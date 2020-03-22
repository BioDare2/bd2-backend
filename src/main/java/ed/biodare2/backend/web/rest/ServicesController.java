/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare.jobcentre2.dom.JobResults;
import ed.biodare.jobcentre2.dom.PPAJobResults;
import ed.biodare.jobcentre2.dom.TSResult;
import ed.biodare.rhythm.ejtk.BD2eJTKRes;
import ed.biodare2.backend.features.ppa.PPAJC2Handler;
import ed.biodare2.backend.features.rhythmicity.RhythmicityHandler;
import ed.biodare2.backend.handlers.ArgumentException;
import ed.biodare2.backend.handlers.ExperimentHandler;
import ed.biodare2.backend.handlers.PPAHandler;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.jobcenter.dom.job.JobResult;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
@RequestMapping(value = "api/services")
public class ServicesController extends BioDare2Rest {

    
    final ExperimentHandler experiments;
    final PPAHandler ppaHandler;
    final PPAJC2Handler ppaHandler2;
    final RhythmicityHandler rhythmicityHandler;
            
    @Autowired
    public ServicesController(ExperimentHandler experiments,PPAHandler ppaHandler,
            PPAJC2Handler ppaHandler2,
            RhythmicityHandler rhythmicityHandler) {        
        this.experiments = experiments;
        this.ppaHandler = ppaHandler;
        this.ppaHandler2 = ppaHandler2;
        this.rhythmicityHandler = rhythmicityHandler;
    }
    
    @RequestMapping(value = "status", method = RequestMethod.GET)
    public Map<String,String> status(@NotNull @AuthenticationPrincipal UserDetails currentUser) {
        String userName = (currentUser != null ? currentUser.getUsername() : "NO USER");
        log.debug("Services status {}",userName);
        
        Map<String,String> res = new HashMap<>();
        res.put("services","running");
        res.put("user",userName);
        return res;
    }  
    
    @RequestMapping(value = "ppa/results/{jobId}/{externalId}", method = RequestMethod.POST)
    public void handlePPAResults(@PathVariable long jobId,@PathVariable String externalId,@RequestBody JobResult<PPAResult> results) {
        if (results == null) throw new HandlingException("Null results recieved");
        log.debug("handle PPA results; job:{}, exp: {}, results size: {}",jobId,externalId,results.getTaskResults().size());
        
        
        //Account user = principalToAccount(principal);
        long expId = Long.parseLong(externalId);
        AssayPack exp = experiments.getExperiment(expId)
                        .orElseThrow(()-> new NotFoundException("Experiment "+expId+" not found"));        
        
        try {
            ppaHandler.handleResults(jobId,exp,results);
            
        } catch (HandlingException | ServerSideException e) {
            log.error("Cannot process results {}",e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot process results, system error {}",e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }      
    

    @RequestMapping(value = "rhythmicity/results/{expId}", method = RequestMethod.POST)
    public void handleRhythmicityResults(@PathVariable long expId,
            @RequestBody JobResults<TSResult<BD2eJTKRes>> results) {
        
        if (results == null) throw new HandlingException("Null results recieved");
        log.debug("handle rhythmiciy results; job:{}, exp: {}, results size: {}",results.jobId,expId,results.results.size());
        
        
        AssayPack exp = experiments.getExperiment(expId)
                        .orElseThrow(()-> new NotFoundException("Experiment "+expId+" not found"));        
        
        try {
            rhythmicityHandler.handleResults(exp,results);
            
        } catch (HandlingException | ServerSideException e) {
            log.error("Cannot process results {}",e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot process results, system error {}",e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }      

    @RequestMapping(value = "ppa2/results/{expId}", method = RequestMethod.POST)
    public void handlePPA2Results(@PathVariable long expId,
            @RequestBody PPAJobResults results) {
        
        if (results == null) throw new HandlingException("Null results recieved");
        log.debug("handle ppa2 results; job:{}, exp: {}, results size: {}",results.jobId,expId,results.results.size());
        
        
        AssayPack exp = experiments.getExperiment(expId)
                        .orElseThrow(()-> new NotFoundException("Experiment "+expId+" not found"));        
        
        try {
            ppaHandler2.handleResults(exp,results);
            
        } catch (HandlingException | ServerSideException e) {
            log.error("Cannot process ppa results {}",e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot process ppa results, system error {}",e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }      
    
    @RequestMapping(value = "ppa/results", method = RequestMethod.GET)
    public String pppStatus() {
        //log.debug("status asked");
        return "Receiving available";
    }    
    
}
