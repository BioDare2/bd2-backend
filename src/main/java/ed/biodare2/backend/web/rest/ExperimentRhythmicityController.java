/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare.jobcentre2.dom.JobResults;
import ed.biodare.jobcentre2.dom.TSResult;
import ed.biodare.rhythm.ejtk.BD2eJTKRes;
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
import ed.biodare2.backend.features.rhythmicity.RhythmicityHandler;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityRequest;
import ed.biodare2.backend.web.tracking.ExperimentTracker;
import ed.robust.dom.tsprocessing.PhaseType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
@RequestMapping("api/experiment/{expId}/rhythmicity")
public class ExperimentRhythmicityController extends ExperimentController {

    //final PPAHandler ppaHandler;
    final RhythmicityHandler rhythmicityHandler;
            
    @Autowired
    public ExperimentRhythmicityController(ExperimentHandler experiments,
            RhythmicityHandler rhythmicityHandler,
            PermissionsResolver permissionsResolver, ExperimentTracker tracker) {        
        super(experiments,permissionsResolver,tracker);
        //this.ppaHandler = ppaHandler;
        this.rhythmicityHandler = rhythmicityHandler;
    }
    
    
    
    @RequestMapping(method = RequestMethod.PUT)
    public Map<String,String> newRhythmicity(@PathVariable long expId,
            @Valid @RequestBody RhythmicityRequest rhythmicityRequest,
            @NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("new RHYTHM:{} {} exp:{}; {}",rhythmicityRequest.method, rhythmicityRequest.preset,expId,user);
        
        AssayPack exp = getExperimentForWrite(expId,user);
        
        
        try {
            UUID analysisId;
            
            
            //analysisId = ppaHandler.newPPA(exp,ppaRequest);            
            analysisId = rhythmicityHandler.newRhythmicity(exp,rhythmicityRequest);            
            tracker.rhythmicityNew(exp,analysisId.toString(),rhythmicityRequest.method,user);
            
            Map<String,String> resp = new HashMap<>();
            resp.put("analysis", analysisId.toString());
            return resp;
            
        } catch (ArgumentException e) {
            log.error("Cannot start rhythmicity test {} {}",expId,e.getMessage(),e);
            throw new HandlingException(e.getMessage());
        } catch(WebMappedException e) {
            log.error("Cannot start rhythmicity test {} {}",expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot start rhythmicity test {} {}",expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }      
    
    
    @RequestMapping(value = "jobs", method = RequestMethod.GET)
    public ListWrapper<RhythmicityJobSummary> getRhythmicityJobs(@PathVariable long expId,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get RhythmicityJobs; exp:{}; {}",expId,user);
        
        
        AssayPack exp = getExperimentForRead(expId,user);
        
        
        try {
            ListWrapper<RhythmicityJobSummary> resp = new ListWrapper<>(rhythmicityHandler.getRhythmicityJobs(exp));
            tracker.ppaList(exp,user);
            return resp;
            
        } catch(WebMappedException e) {
            log.error("Cannot retrieve RhythmicityJobs jobs {} {}",expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot retrieve RhythmicityJobs jobs {} {}",expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    } 
    
    
    @RequestMapping(value = "job/{jobId}", method = RequestMethod.GET)
    public RhythmicityJobSummary getRhythmicityJob(@PathVariable long expId,@PathVariable UUID jobId,@NotNull @AuthenticationPrincipal BioDare2User user) {
        // log.debug("get getRhythmicityJob:{} exp:{}; {}",jobId,expId,user);
        // we dont log it as it is beeing called a lot from UI before the analysis finishes
        
        AssayPack exp = getExperimentForRead(expId,user);
        
        
        try {
            RhythmicityJobSummary res = rhythmicityHandler.getRhythmicityJob(exp,jobId);
            // tracker.rhythmicityJob(exp,res,user);
            return res;
            
        } catch (WebMappedException e) {
            log.error("Cannot retrieve Rhythmicity job {} {} {}",jobId,expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot retrieve Rhythmicity job {} {} {}",jobId,expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }  
    
    @RequestMapping(value = "job/{jobId}/results", method = RequestMethod.GET)
    public JobResults<TSResult<BD2eJTKRes>> getRhythmicityResults(@PathVariable long expId,@PathVariable UUID jobId,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get getRhythmicityResults; job:{} exp: {}; {}",jobId,expId,user);
        
        
        AssayPack exp = getExperimentForRead(expId, user);
        
        
        try {
            JobResults<TSResult<BD2eJTKRes>> resp = rhythmicityHandler.getRhythmicityResults(exp,jobId);
            tracker.rhythmicityResults(exp,jobId,user);
            return resp;
            
        } catch (WebMappedException e) {
            log.error("Cannot retrieve rhythmicity results {} {} {}",jobId,expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot retrieve rhythmicity results {} {} {}",jobId,expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }    
    
    /*
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
    
    */

    
    

    

    
    
    
}
