/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.features.tsdata.datahandling.DataProcessingException;
import ed.biodare2.backend.features.tsdata.dataimport.ImportException;
import ed.biodare2.backend.repo.ui_dom.shared.Page;
import ed.biodare2.backend.handlers.ExperimentDataHandler;
import ed.biodare2.backend.handlers.ExperimentHandler;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.PermissionsResolver;
import ed.biodare2.backend.repo.isa_dom.dataimport.FileImportRequest;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeSeriesMetrics;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.ui_dom.tsdata.Trace;
import ed.biodare2.backend.repo.ui_dom.tsdata.TraceSet;
import ed.biodare2.backend.web.tracking.ExperimentTracker;
import ed.robust.dom.data.DetrendingType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tzielins
 */
@RestController
@RequestMapping("api/experiment/{expId}/data")
public class ExperimentDataController extends ExperimentController {

    final ExperimentDataHandler dataHandler;
    
    @Autowired
    public ExperimentDataController(ExperimentHandler handler,ExperimentDataHandler dataHandler,PermissionsResolver permissionsResolver,ExperimentTracker tracker) {        
        super(handler,permissionsResolver,tracker);
        this.dataHandler = dataHandler;
    }
    
    
    @RequestMapping(value = "ts-import", method = RequestMethod.POST)
    public Map<String,Object> importTimeSeries(@PathVariable long expId,@RequestBody FileImportRequest importRequest,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("import TimeSeries; exp:{} file:{}; {}",expId,importRequest.fileId,user);
        
        if (user.isAnonymous())
            throw new LogginRequiredException("Loggin to import data");
        
        AssayPack exp = getExperimentForWrite(expId,user);
        
        try {
            int seriesNr = dataHandler.importTimeSeries(exp,importRequest, user);
            tracker.dataImport(exp,user);
            Map<String,Object> resp = new HashMap<>();
            resp.put("imported", seriesNr);
            return resp;
            
        } catch(InsufficientRightsException e) {
            log.error("Insufficient rights: {} {}",user.getLogin(), e.getMessage());
            throw e;
        } catch(WebMappedException e) {
            log.error("Cannot import timeseries {} {}",expId,e.getMessage(),e);
            throw e;
        } catch (ImportException| DataProcessingException e) {
            log.error("Cannot import timeseries {} {}",expId,e.getMessage());
            throw new HandlingException(e.getMessage(),e);
        } catch (Exception e) {
            log.error("Cannot import timeseries {} {}",expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }      

    @RequestMapping(value = "bd1-import", method = RequestMethod.POST)
    public Map<String,Object> importBD1data(@PathVariable long expId,@NotNull @RequestBody DataBundle data,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("import BD1 TimeSeries; exp:{} traces: {}; {}",expId,data.data.size(),user);
        
        if (true)
            throw new UnsupportedOperationException("Import not supported at the moment");
        
        if (user.isAnonymous())
            throw new LogginRequiredException("Loggin to import BD1 data");
        
        
        AssayPack exp = getExperimentForWrite(expId,user);
        
        try {
            int seriesNr;
            if (false) {
                seriesNr = data.data.size();
            } else {
                seriesNr = dataHandler.importBD1Data(exp,data, user);
            }
            
            tracker.dataImport(exp,user);
            Map<String,Object> resp = new HashMap<>();
            resp.put("imported", seriesNr);
            return resp;
            
        } catch(WebMappedException e) {
            log.error("Cannot import BD1 timeseries {} {}",expId,e.getMessage(),e);
            throw e;
        } catch (ImportException| DataProcessingException e) {
            log.error("Cannot import BD1 timeseries {} {}",expId,e.getMessage());
            throw new HandlingException(e.getMessage(),e);
        } catch (Exception e) {
            log.error("Cannot import BD1 timeseries {} {}",expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }  

    @RequestMapping(value = "metrics", method = RequestMethod.GET)
    public TimeSeriesMetrics getTSDataMetrics(@PathVariable long expId,
            @NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get TimeSeriesMetrics; exp:{}; {}",expId,user);
        
        AssayPack exp = getExperimentForRead(expId,user);
        
        try {
            
            TimeSeriesMetrics metrics = dataHandler.getTSDataMetrics(exp).orElseThrow(()-> new NotFoundException("Data metrics not found"));
            return metrics;
            
        } catch(InsufficientRightsException e) {
            log.error("Insufficient rights: {} {}",user.getLogin(), e.getMessage());
            throw e;
        } catch(WebMappedException e) {
            log.error("Cannot get data metrics {} {}",expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot get data metrics {} {}",expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }      
    
    
    @RequestMapping(value = "{detrending}", method = RequestMethod.GET)
    public TraceSet getTSData(@PathVariable long expId, @PathVariable DetrendingType detrending, 
            @RequestParam(name="pageIndex", defaultValue = "0") int pageIndex,
            @RequestParam(name="pageSize", defaultValue = "100") int pageSize,
            @NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get TimeSeries; exp:{} {}; {}",expId,detrending,user);
        
        AssayPack exp = getExperimentForRead(expId,user);
        
        if (detrending == null) detrending = DetrendingType.LIN_DTR;
        
        try {
            Page page = new Page(pageIndex, pageSize);
            TraceSet resp = dataHandler.getTSData(exp,detrending,page).orElseThrow(()-> new NotFoundException("DataSet not found"));
            tracker.dataView(exp,detrending,user);
            return resp;
            
        } catch(InsufficientRightsException e) {
            log.error("Insufficient rights: {} {}",user.getLogin(), e.getMessage());
            throw e;
        } catch(WebMappedException e) {
            log.error("Cannot get timeseries {} {} {}",expId,detrending,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot get timeseries {} {} {}",expId,detrending,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }      
    
    @RequestMapping(value = "{detrending}/binned", method = RequestMethod.GET)
    public TraceSet getBinnedTSData(@PathVariable long expId, @PathVariable DetrendingType detrending, 
            @RequestParam(name="pageIndex", defaultValue = "0") int pageIndex,
            @RequestParam(name="pageSize", defaultValue = "100") int pageSize,
            @NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get binned TimeSeries; exp:{} {}; {}",expId,detrending,user);
        
        AssayPack exp = getExperimentForRead(expId,user);
        
        if (detrending == null) detrending = DetrendingType.LIN_DTR;
        
        try {
            Page page = new Page(pageIndex, pageSize);
            TraceSet resp = dataHandler.getBinnedTSData(exp,detrending,page).orElseThrow(()-> new NotFoundException("DataSet not found"));
            tracker.dataView(exp,detrending,user);
            return resp;
            
        } catch(InsufficientRightsException e) {
            log.error("Insufficient rights: {} {}",user.getLogin(), e.getMessage());
            throw e;
        } catch(WebMappedException e) {
            log.error("Cannot get timeseries {} {} {}",expId,detrending,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot get timeseries {} {} {}",expId,detrending,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }      
    
    @RequestMapping(value = "{detrending}/export", method = RequestMethod.GET)
    public void exportTSData(@PathVariable long expId,@PathVariable DetrendingType detrending, @RequestParam Map<String,String> displayProperties, @NotNull @AuthenticationPrincipal BioDare2User user,HttpServletResponse response) {
        log.debug("export TimeSeries; exp:{} {}; {}",expId,detrending,user);
        
        AssayPack exp = getExperimentForRead(expId,user);
        
        verifyCanRead(user,exp);
        
        if (detrending == null) detrending = DetrendingType.LIN_DTR;
        
        Path data = null;
        try {
            // display properties are not implemented
            data = dataHandler.exportData(exp,detrending,displayProperties);

            String contentType = "text/comma-separated-values";
            String fileName = expId+"."+detrending.name()+".data.csv";
            sendFile(data,fileName,contentType,false,response);
            
            tracker.dataDownload(exp,detrending,user);
        } catch(InsufficientRightsException e) {
            log.error("Insufficient rights: {} {}",user.getLogin(), e.getMessage());
            throw e;
        } catch(WebMappedException e) {
            log.error("Cannot export timeseries {} {} {}",expId,detrending,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot export timeseries {} {} {}",expId,detrending,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } finally {
            try {
                if (data != null)
                    Files.delete(data);
            } catch (IOException e) {
                log.error("Could not delete tmp data file: "+e.getMessage(),e);
            }
        }

        
    }      
    
}
