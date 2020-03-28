/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

import ed.biodare2.backend.features.ppa.PPAJC2Handler;
import ed.biodare2.backend.features.rhythmicity.RhythmicityHandler;
import ed.biodare2.backend.web.rest.NotFoundException;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.repo.dao.AssetsParamRep;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.dao.FileAssetRep;
import ed.biodare2.backend.features.tsdata.datahandling.DataProcessingException;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataExporter;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.features.tsdata.dataimport.ImportException;
import ed.biodare2.backend.repo.ui_dom.shared.Page;
import ed.biodare2.backend.repo.isa_dom.assets.AssetType;
import ed.biodare2.backend.repo.isa_dom.assets.FileAsset;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.dataimport.FileImportRequest;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeSeriesMetrics;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.OperationType;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.repo.ui_dom.tsdata.Trace;
import ed.biodare2.backend.repo.ui_dom.tsdata.TraceSet;
import ed.robust.dom.data.DetrendingType;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@Service
public class ExperimentDataHandler extends BaseExperimentHandler {
    
    final ExperimentPackHub experiments;    
    final TSImportHandler importHandler;
    final FileAssetRep fileAssets;
    final AssetsParamRep assetsParams;
    final TSDataHandler dataHandler;
    final TSDataExporter dataExporter;
    final PPAJC2Handler ppaHandler;
    final RhythmicityHandler rhythmicityHandler;
    
    public static final String TSAssetName = "ts_data_file_1";

    public ExperimentDataHandler(ExperimentPackHub experiments, 
            TSImportHandler importHandler, 
            TSDataHandler dataHandler,
            TSDataExporter dataExporter,
            PPAJC2Handler ppaHandler,
            RhythmicityHandler rhythmicityHandler,
            FileAssetRep fileAssets, 
            AssetsParamRep assetsParams
            ) {
        this.experiments = experiments;
        this.importHandler = importHandler;
        this.dataExporter = dataExporter;
        this.fileAssets = fileAssets;
        this.assetsParams = assetsParams;
        this.dataHandler = dataHandler;
        this.ppaHandler = ppaHandler;
        this.rhythmicityHandler = rhythmicityHandler;
    }
    
    public Optional<TimeSeriesMetrics> getTSDataMetrics(AssayPack exp) {
        
        return dataHandler.getMetrics(exp);
    }    
    
    public Optional<TraceSet> getTSData(AssayPack exp,DetrendingType detrending, Page page) throws ServerSideException {
        
        final int toSkip = page.pageIndex*page.pageSize;
        return dataHandler.getDataSet(exp, detrending)
                .map( ds -> {
                    List<Trace> traces  = ds.stream()
                        .skip(toSkip)
                        .limit(page.pageSize)
                        .map(this::toUITrace)
                        .collect(Collectors.toList());
                    
                    TraceSet set = new TraceSet();
                    set.totalTraces = ds.size();
                    set.traces = traces;
                    set.currentPage = page;
                    set.currentPage.length = ds.size();
                    
                    return set;
                });
    }
    
    protected Trace toUITrace(DataTrace data) {
        Trace trace = new Trace();
        trace.label = data.traceNr+".["+data.traceRef+"] "+data.details.dataLabel;
        trace.setTimeseries(data.trace);
        return trace;
    }
    
    @Transactional
    public int importTimeSeries(AssayPack exp, FileImportRequest importRequest, BioDare2User user) throws ImportException, DataProcessingException, ServerSideException {

        try {
        exp = experiments.enableWriting(exp);
            
        DataBundle rawData = importHandler.importTimeSeries(importRequest,user);
        int imported = dataHandler.handleNewData(exp,rawData);
        
        FileAsset asset = fileAssets.storeFileUpload(importRequest.fileId,TSAssetName,AssetType.TS_DATA,exp,user);
        assetsParams.storeParams(asset, importRequest.importParameters, exp);
        
        ppaHandler.clearPPA(exp);
        rhythmicityHandler.clear(exp);
        
        registerDataImport(exp, true, user);
        
        copySystemFeatures(exp.getSystemInfo(),exp.getAssay());        
        exp = experiments.save(exp);
        
        return imported;
        } catch (IOException e) {
            throw new ServerSideException(e.getMessage(),e);
        }
    } 
    
    
    @Transactional
    public int importBD1Data(AssayPack exp, DataBundle data, BioDare2User user) throws ImportException, DataProcessingException, ServerSideException {
        try {
        exp = experiments.enableWriting(exp);
            
        DataBundle rawData = removeEmptySeries(data);
        
        if (rawData.data.isEmpty()) {
            String msg = "No valid series found in the set";
            if (!data.data.isEmpty())
                msg += " but there were "+data.data.size()+" empty series";
            throw new ImportException(msg);
        }
        
        int imported = dataHandler.handleNewData(exp,rawData);
        
        
        ppaHandler.clearPPA(exp);
        registerDataImport(exp, false, user);
        
        copySystemFeatures(exp.getSystemInfo(),exp.getAssay());        
        exp = experiments.save(exp);
        
        return imported;
        } catch (IOException e) {
            throw new ServerSideException(e.getMessage(),e);
        }
    }
    
    protected AssayPack registerDataImport(AssayPack exp, boolean fromFile, BioDare2User user) throws ImportException, DataProcessingException {
        
        SystemInfo systemInfo = exp.getSystemInfo();
        
        systemInfo.currentDataVersion++;
        systemInfo.experimentCharacteristic.hasDataFiles = fromFile;
        systemInfo.experimentCharacteristic.hasTSData = true;
        systemInfo.experimentCharacteristic.hasPPAJobs = false;
        
        updateProvenance(systemInfo.provenance, user, OperationType.DATA_UPLOAD, systemInfo.getVersionId());
        
        return exp;
    }

    public Path exportData(AssayPack exp, DetrendingType detrending, Map<String,String> displayProperties) throws IOException, NotFoundException {
        
        List<DataTrace> dataSet = dataHandler.getDataSet(exp, detrending).orElseThrow(()-> new NotFoundException("DataSet not found"));

        // applying displayProperties not implemented
        // dataSet = render(dataSet,displayProperties);
        Path file = dataExporter.export(dataSet,exp,detrending);
        
        return file;
    }

    protected List<DataTrace> render(List<DataTrace> dataSet, Map<String, String> displayProperties) {
        throw new UnsupportedOperationException("Applying displayProperties to the data on the server not implemented");
    }

    protected DataBundle removeEmptySeries(DataBundle data) {
        DataBundle cleaned = new DataBundle();
        cleaned.backgrounds = data.backgrounds.stream().filter( this::noEmptyTrace).collect(Collectors.toList());
        cleaned.data = data.data.stream().filter( this::noEmptyTrace).collect(Collectors.toList());
        cleaned.blocks = data.blocks;
        cleaned.detrending = data.detrending;
        return cleaned;
    }
    
    protected boolean noEmptyTrace(DataTrace trace) {
        return (trace.trace != null && !trace.trace.isEmpty());
    }




    
    
}
