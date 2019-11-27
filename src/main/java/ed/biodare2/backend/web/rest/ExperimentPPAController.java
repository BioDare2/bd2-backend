/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.handlers.ArgumentException;
import ed.biodare2.backend.handlers.ExperimentHandler;
import ed.biodare2.backend.handlers.PPAHandler;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.PermissionsResolver;
import ed.biodare2.backend.repo.isa_dom.ppa.PPARequest;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.ui_dom.ppa.PPAFitPack;
import ed.biodare2.backend.repo.ui_dom.ppa.PPAResultsGroup;
import ed.biodare2.backend.repo.ui_dom.ppa.PPASelectGroup;
import ed.biodare2.backend.features.ppa.PPAHandlingException;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.web.tracking.ExperimentTracker;
import ed.robust.dom.tsprocessing.PhaseType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tzielins
 */
@RestController
@RequestMapping("api/experiment/{expId}/ppa")
public class ExperimentPPAController extends ExperimentController {

    final PPAHandler ppaHandler;
            
    @Autowired
    public ExperimentPPAController(ExperimentHandler handler,PPAHandler ppaHandler,
            PermissionsResolver permissionsResolver,ExperimentTracker tracker) {        
        super(handler,permissionsResolver,tracker);
        this.ppaHandler = ppaHandler;
    }
    
    
    
    @RequestMapping(method = RequestMethod.PUT)
    public Map<String,Object> newPPA(@PathVariable long expId,@Valid @RequestBody PPARequest ppaRequest,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("new PPA:{} exp:{}; {}",ppaRequest.method,expId,user);
        
        AssayPack exp = getExperimentForWrite(expId,user);
        
        
        try {
            long analysisId;
            
            if (false) analysisId = 1;
            else analysisId = ppaHandler.newPPA(exp,ppaRequest);            
            tracker.ppaNew(exp,analysisId,ppaRequest.method,user);
            
            Map<String,Object> resp = new HashMap<>();
            resp.put("analysis", analysisId);
            return resp;
            
        } catch (ArgumentException| PPAHandlingException e) {
            log.error("Cannot start ppa {} {}",expId,e.getMessage(),e);
            throw new HandlingException(e.getMessage());
        } catch(WebMappedException e) {
            log.error("Cannot start ppa {} {}",expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot start ppa {} {}",expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }      
    
    @RequestMapping(value = "jobs", method = RequestMethod.GET)
    public ListWrapper<PPAJobSummary> getPPAJobs(@PathVariable long expId,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get PPAJobs; exp:{}; {}",expId,user);
        
        
        AssayPack exp = getExperimentForRead(expId,user);
        
        
        try {
            ListWrapper<PPAJobSummary> resp = new ListWrapper<>(ppaHandler.getPPAJobs(exp));
            resp.data.forEach( j -> {
                if (j.parentId == 0) j.parentId = expId;
            });
            tracker.ppaList(exp,user);
            return resp;
            
        } catch(WebMappedException e) {
            log.error("Cannot retrieve PPA jobs {} {}",expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot retrieve PPA jobs {} {}",expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    } 
    
    
    @RequestMapping(value = "job/{jobId}", method = RequestMethod.GET)
    public PPAJobSummary getPPAJob(@PathVariable long expId,@PathVariable long jobId,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get PPAJob:{} exp:{}; {}",jobId,expId,user);
        
        
        AssayPack exp = getExperimentForRead(expId,user);
        
        
        try {
            PPAJobSummary res = ppaHandler.getPPAJob(exp,jobId);
            if (res.parentId == 0) res.parentId = expId;
            tracker.ppaJob(exp,res,user);
            return res;
            
        } catch (WebMappedException e) {
            log.error("Cannot retrieve PPA job {} {} {}",jobId,expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot retrieve PPA job {} {} {}",jobId,expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }  
    
    @RequestMapping(value = "job/{jobId}/export/{phaseType}", method = RequestMethod.GET)
    public void exportPPAJob(@PathVariable long expId,@PathVariable long jobId,@PathVariable PhaseType phaseType,@NotNull @AuthenticationPrincipal BioDare2User user,HttpServletResponse response) {
        log.debug("export PPAJob:{} exp:{}; {}",jobId,expId,user);
        
        
        AssayPack exp = getExperimentForRead(expId,user);
        if (phaseType == null) phaseType = PhaseType.ByFit;
        
        Path results = null;
        try {
            results = ppaHandler.exportPPAJob(exp,jobId,phaseType);

            String contentType = "text/csv";
            String fileName = expId+"_job"+jobId+".ppa_data.csv";
            sendFile(results,fileName,contentType,false,response);
            tracker.ppaJobDownload(exp,jobId,user);
        } catch (WebMappedException e) {
            log.error("Cannot export PPA job results {} {} {}",jobId,expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot export PPA job results {} {} {}",jobId,expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } finally {
            try {
                if (results != null)
                    Files.delete(results);
            } catch (IOException e) {
                log.error("Could not delete tmp results file: "+e.getMessage(),e);
            }
        }
                
    }      
    
    
    @RequestMapping(value = "job/{jobId}", method = RequestMethod.DELETE)
    public PPAJobSummary deletePPAJob(@PathVariable long expId,@PathVariable long jobId,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("delete PPAJob:{} exp:{}; {}",jobId,expId,user);
        
        
        AssayPack exp = getExperimentForWrite(expId,user);
        
        
        try {
            PPAJobSummary job = ppaHandler.deletePPAJob(exp, jobId);
            tracker.ppaDeleteJob(exp,job,user);
            return job;
        } catch (WebMappedException e) {
            log.error("Cannot delete PPA job {} {} {}",jobId,expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot delete PPA job {} {} {}",jobId,expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }  
    
    @RequestMapping(value = "job/{jobId}/results/grouped", method = RequestMethod.GET)
    public PPAJobResultsGroups getPPAJobResultsGrouped(@PathVariable long expId,@PathVariable long jobId,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get PPAJobResultsGrouped; job:{} exp: {}; {}",jobId,expId,user);
        
        
        AssayPack exp = getExperimentForRead(expId, user);
        
        
        try {
            PPAJobResultsGroups resp = ppaHandler.getPPAJobResultsGrouped(exp,jobId);
            tracker.ppaJobGroupedResults(exp,jobId,user);
            return resp;
            
        } catch (WebMappedException e) {
            log.error("Cannot retrieve PPA results groupped {} {} {}",jobId,expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot retrieve PPA results groupped {} {} {}",jobId,expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }     
    
    
    @RequestMapping(value = "job/{jobId}/results/simple", method = RequestMethod.GET)
    public PPAJobSimpleResults getPPAJobSimpleResults(@PathVariable long expId,@PathVariable long jobId,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get PPAJobSimpleResults; job:{} exp: {}; {}",jobId,expId,user);
        
        
        AssayPack exp = getExperimentForRead(expId, user);
        
        
        try {
            PPAJobSimpleResults resp = ppaHandler.getPPAJobSimpleResults(exp,jobId);
            tracker.ppaJobSimpleResults(exp,jobId,user);
            return resp;
            
        } catch (WebMappedException e) {
            log.error("Cannot retrieve PPA simple results {} {} {}",jobId,expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot retrieve PPA simple results {} {} {}",jobId,expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }     


    @RequestMapping(value = "job/{jobId}/results/select", method = RequestMethod.GET)
    public ListWrapper<PPASelectGroup> getPPAForSelect(@PathVariable long expId,@PathVariable long jobId,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get PPAForSelect; job:{} exp: {}; {}",jobId,expId,user);
        
        
        AssayPack exp = getExperimentForWrite(expId,user);
        
        try {
            ListWrapper<PPASelectGroup> resp = new ListWrapper(ppaHandler.getPPAForSelect(exp,jobId));
            tracker.ppaForSelect(exp,jobId,user);
            return resp;
            
        } catch (WebMappedException e) {
            log.error("Cannot retrieve PPA results for select {} {} {}",jobId,expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot retrieve PPA results for select {} {} {}",jobId,expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }      
    
    
 
    
   
    @RequestMapping(value = "job/{jobId}/results/select", method = RequestMethod.POST)
    public Map<String,Object> doPPASelection(@PathVariable long expId,@PathVariable long jobId,@RequestBody Map<String,String> selectionParams,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("update PPASelection; job:{} exp:{}; {}",jobId,expId,user);
        
        
        AssayPack exp = getExperimentForWrite(expId,user);
        
        
        try {
            int needsAttention =  ppaHandler.doPPASelection(exp,jobId,selectionParams);
            tracker.ppaSelect(exp,jobId,user);
            
            Map<String,Object> resp = new HashMap<>();
            resp.put("needsAttention", needsAttention);
            return resp;
            
        } catch (WebMappedException e) {
            log.error("Cannot do PPA results select {} {} {}",jobId,expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot do PPA results select {} {} {}",jobId,expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }  
    
    
    @RequestMapping(value = "job/{jobId}/stats/simple", method = RequestMethod.GET)
    public PPAJobSimpleStats getPPAJobSimpleStats(@PathVariable long expId,@PathVariable long jobId,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get PPAJobSimpleStats; job:{} exp: {}; {}",jobId,expId,user);
        
        
        AssayPack exp = getExperimentForRead(expId,user);
     
        
        try {
            PPAJobSimpleStats stats = ppaHandler.getPPAJobSimpleStats(exp,jobId);
            tracker.ppaJobStats(exp,jobId,user);
            return stats;
        } catch (WebMappedException e) {
            log.error("Cannot retrieve PPA simple stats {} {} {}",jobId,expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot retrieve PPA simple stats {} {} {}",jobId,expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        }         
    } 
    
    
    @RequestMapping(value = "job/{jobId}/fits/{dataId}/{selectable}", method = RequestMethod.GET)
    public PPAFitPack getDataFit(@PathVariable long expId,@PathVariable long jobId,
            @PathVariable long dataId, @PathVariable(required = false) Boolean selectable,
            @NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get PPAFit; job:{} data:{} exp:{}; {}",jobId,dataId,expId,user);
        
        if (selectable == null) selectable = false;
        
        AssayPack exp = getExperimentForRead(expId,user);
        
        try {
            PPAFitPack resp = ppaHandler.getDataFit(exp,jobId,dataId,selectable);        
            tracker.ppaFit(exp,jobId,dataId,user);
            return resp;
        } catch (WebMappedException e) {
            log.error("Cannot retrieve PPA fit {} {} {}",jobId,expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot retrieve PPA fit {} {} {}",jobId,expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }     
   
    @RequestMapping(value = "results", method = RequestMethod.GET)
    public ListWrapper<PPAResultsGroup> getJoinedPPAResults(@PathVariable long expId,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get JoinedPPAResults; exp:{}; {}",expId,user);
        
        
        AssayPack exp = getExperimentForRead(expId,user);
        
        
        
        try {
            ListWrapper<PPAResultsGroup> resp = new ListWrapper(ppaHandler.getJoinedPPAResults(exp));
            tracker.ppaResults(exp,user);
            return resp;
            
        } catch (WebMappedException e) {
            log.error("Cannot retrieve joined PPA results {} {}",expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot retrieve joined PPA results {} {}",expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }      
    
    
    @RequestMapping(value = "export", method = RequestMethod.GET)
    public void exportPPA(@PathVariable long expId,@NotNull @AuthenticationPrincipal BioDare2User user,HttpServletResponse response) {
        log.debug("export PPA; exp:{}; {}",expId,user);
        
        
        AssayPack exp = getExperimentForRead(expId,user);
        
        
        Path results = null;
        try {
            results = ppaHandler.packResults(exp);

            String contentType = "application/zip";
            String fileName = expId+".ppa_data.zip";
            sendFile(results,fileName,contentType,false,response);
            tracker.ppaDownload(exp,user);
        } catch (WebMappedException e) {
            log.error("Cannot export joined PPA results {} {}",expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot export joined PPA results {} {}",expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } finally {
            try {
                if (results != null)
                    Files.delete(results);
            } catch (IOException e) {
                log.error("Could not delete tmp results file: "+e.getMessage(),e);
            }
        }
        
        
    }      
    
    

    
    

    

    
    
    
}
