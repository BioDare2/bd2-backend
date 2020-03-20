/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;

import ed.biodare2.Fixtures;
import ed.biodare2.backend.features.rhythmicity.RhythmicityHandler;
import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.repo.dao.AssetsParamRep;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.dao.FileAssetRep;
import ed.biodare2.backend.repo.dao.MockReps;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataExporter;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.repo.ui_dom.shared.Page;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeDataTraces;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.dataimport.FileImportRequest;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder;
import static ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder.emptySystemInfo;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.repo.ui_dom.tsdata.TraceSet;
import ed.biodare2.backend.util.json.TimeSeriesModule;
import ed.robust.dom.data.DetrendingType;
//import ed.biodare2.backend.util.json.TimeSeriesModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author tzielins
 */
public class ExperimentDataHandlerTest {
    
    public ExperimentDataHandlerTest() {
    }
    
    ExperimentDataHandler handler;
    ExperimentPackHub experiments;
    
    TSImportHandler importHandler;
    TSDataHandler dataHandler;
    TSDataExporter dataExporter;
    FileAssetRep fileAssets;
    AssetsParamRep assetsParams;
    PPAHandler ppaHandler;
    RhythmicityHandler rhythmicityHandler;
    UserAccount user;
    
    ExperimentalAssay testExp;
    MockReps.ExperimentPackTestImp testBoundle;
    Fixtures fixtures;
    
    @Before
    public void setUp() throws Exception {
        
        fixtures = Fixtures.build();
        
        user = fixtures.demoUser;
        
        testExp = DomRepoTestBuilder.makeExperimentalAssay();
        
        SystemInfo info = SystemDomTestBuilder.makeSystemInfo();
        info.parentId = testExp.getId();
        info.entityType = EntityType.EXP_ASSAY;

        DBSystemInfo dbSystemInfo = emptySystemInfo(testExp.getId());
        dbSystemInfo.setEntityType(EntityType.EXP_ASSAY);
        dbSystemInfo.setAcl(new EntityACL());
        
        testBoundle = new MockReps.ExperimentPackTestImp();
        testBoundle.expId = testExp.getId();
        testBoundle.assay = testExp;
        testBoundle.systemInfo = info;
        testBoundle.dbSystemInfo = dbSystemInfo;        
        
        importHandler = mock(TSImportHandler.class);
        dataHandler = mock(TSDataHandler.class);
        dataExporter = mock(TSDataExporter.class);
        when(dataExporter.export(any(), any(), any(), any())).thenReturn(Files.createTempFile(null, null));
        
        fileAssets = mock(FileAssetRep.class);
        assetsParams = mock(AssetsParamRep.class);
        ppaHandler = mock(PPAHandler.class);
        
        rhythmicityHandler = mock(RhythmicityHandler.class);
        
        experiments = MockReps.mockHub();
        
        //handler = new ExperimentHandler(boundles,experiments,systemInfos,dbSystemInfos,idGenerator,routes,importHandler,dataHandler,fileAssets,securityResolver);
        handler = new ExperimentDataHandler(
                experiments,
                importHandler,dataHandler,dataExporter,
                ppaHandler,
                rhythmicityHandler,
                fileAssets,
                assetsParams
        );
    }    

    @Test
    public void importsTimeSeriesAndUpdatesExperimentStatus() throws Exception {
        
        AssayPack boundle = testBoundle;
        ExperimentalAssay exp = boundle.getAssay();        
        exp.characteristic.hasTSData = false;
        exp.characteristic.hasPPAJobs = true;
        boundle.getSystemInfo().experimentCharacteristic.hasTSData = false;
        boundle.getSystemInfo().experimentCharacteristic.hasPPAJobs = true;
        
        FileImportRequest req = new FileImportRequest();
        
        DataBundle dataBoundle = new DataBundle();
        DataTrace trace = new DataTrace();
        trace.traceNr = 1;
        trace.dataId = 1;
        trace.rawDataId = 1;
        dataBoundle.data.add(trace);
        
        when(importHandler.importTimeSeries(any(), any())).thenReturn(dataBoundle);
        when(dataHandler.handleNewData(any(), any())).thenReturn(1);
        
        long prevDV = boundle.getSystemInfo().currentDataVersion;
        long prevEV = boundle.getSystemInfo().currentDescVersion;
        
        int res = handler.importTimeSeries(boundle, req, user);
        
        verify(importHandler).importTimeSeries(eq(req), eq(user));
        verify(dataHandler).handleNewData(eq(boundle), eq(dataBoundle));
        verify(ppaHandler).clearPPA(eq(boundle));
        verify(rhythmicityHandler).clear(eq(boundle));
        verify(experiments).save(eq(boundle));
        
        assertEquals(1,res);
        
        assertTrue(exp.characteristic.hasTSData);
        assertTrue(boundle.getSystemInfo().experimentCharacteristic.hasTSData);
        assertFalse(exp.characteristic.hasPPAJobs);
        assertFalse(boundle.getSystemInfo().experimentCharacteristic.hasPPAJobs);
        
        assertEquals(prevDV+1,boundle.getSystemInfo().currentDataVersion);
        assertEquals(prevEV,boundle.getSystemInfo().currentDescVersion);
    }
    
 

    /* BD1 import */
    
    DataBundle biodareImport() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.registerModule(new TimeSeriesModule());
        
        try {
            return mapper.readValue(testFile("importdata.json"), DataBundle.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public File testFile(String name) {
        try { 
            return new File(this.getClass().getResource(name).toURI());
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void canImportBD1Boundle() throws Exception {
        DataBundle data = biodareImport();
        assertNotNull(data);
        
        AssayPack boundle = testBoundle;
        ExperimentalAssay exp = boundle.getAssay();        
        exp.characteristic.hasTSData = false;
        exp.characteristic.hasPPAJobs = true;
        boundle.getSystemInfo().experimentCharacteristic.hasTSData = false;
        boundle.getSystemInfo().experimentCharacteristic.hasPPAJobs = true;
        
        
        when(dataHandler.handleNewData(eq(boundle), any())).thenReturn(data.data.size());
        
        long prevDV = boundle.getSystemInfo().currentDataVersion;
        long prevEV = boundle.getSystemInfo().currentDescVersion;
        
        int res = handler.importBD1Data(boundle, data, user);
        
        verify(dataHandler).handleNewData(eq(boundle), any());
        verify(ppaHandler).clearPPA(eq(boundle));
        verify(experiments).save(eq(boundle));
        
        assertEquals(data.data.size(),res);
        
        assertTrue(exp.characteristic.hasTSData);
        assertTrue(boundle.getSystemInfo().experimentCharacteristic.hasTSData);
        assertFalse(exp.characteristic.hasPPAJobs);
        assertFalse(boundle.getSystemInfo().experimentCharacteristic.hasPPAJobs);
        
        assertEquals(prevDV+1,boundle.getSystemInfo().currentDataVersion);
        assertEquals(prevEV,boundle.getSystemInfo().currentDescVersion);
        
    }
    
    @Test
    public void getTSDataGivesPagedData() {
     
        List<DataTrace> data = makeDataTraces(1,100);
        assertEquals(100, data.size());
        
        AssayPack expPack = testBoundle;        
        DetrendingType detrending = DetrendingType.LIN_DTR;
        when(dataHandler.getDataSet(eq(expPack), eq(detrending))).thenReturn(Optional.of(data));
        
        Page page = new Page(3,30);
        
        Optional<TraceSet> oDataset = handler.getTSData(expPack, detrending, page);
        assertTrue(oDataset.isPresent());
        
        TraceSet dataset = oDataset.get();
        assertEquals(10,dataset.traces.size());
        // System.out.println(dataset.get(0).label);
        assertTrue(dataset.traces.get(0).label.startsWith("90.["));
        assertEquals(100, dataset.totalTraces);
        assertEquals(page, dataset.currentPage);
        
    }
    
    @Test
    public void getTSDataGivesEmptyIfBehindPage() {
     
        List<DataTrace> data = makeDataTraces(1,100);
        assertEquals(100, data.size());
        
        AssayPack expPack = testBoundle;        
        DetrendingType detrending = DetrendingType.LIN_DTR;
        when(dataHandler.getDataSet(eq(expPack), eq(detrending))).thenReturn(Optional.of(data));
        
        Page page = new Page(4,30);
        
        Optional<TraceSet> oDataset = handler.getTSData(expPack, detrending, page);
        assertTrue(oDataset.isPresent());
        
        TraceSet dataset = oDataset.get();
        assertEquals(0,dataset.traces.size());
        assertEquals(100,dataset.totalTraces);
        
    }    
}
