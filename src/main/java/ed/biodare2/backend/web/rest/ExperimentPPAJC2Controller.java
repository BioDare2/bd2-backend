/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.features.ppa.PPAHandlingException;
import ed.biodare2.backend.features.ppa.PPAJC2Handler;
import ed.biodare2.backend.handlers.ArgumentException;
import ed.biodare2.backend.handlers.ExperimentHandler;
import ed.biodare2.backend.repo.isa_dom.ppa.PPARequest;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.security.BioDare2User;

import ed.biodare2.backend.security.PermissionsResolver;
import ed.biodare2.backend.web.tracking.ExperimentTracker;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
@RequestMapping("api/experiment/{expId}/ppa2")
public class ExperimentPPAJC2Controller extends ExperimentController {
    
    final PPAJC2Handler ppaHandler;
    
    @Autowired
    public ExperimentPPAJC2Controller(ExperimentHandler handler,
                                      PPAJC2Handler ppaHandler,
                                      PermissionsResolver permissionsResolver,
                                      ExperimentTracker tracker) {        
        
        super(handler,permissionsResolver,tracker);
        this.ppaHandler = ppaHandler;
    }
    
    
    @RequestMapping(method = RequestMethod.PUT)
    public Map<String,String> newPPA(@PathVariable long expId,@Valid @RequestBody PPARequest ppaRequest,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("new PPA:{} exp:{}; {}",ppaRequest.method,expId,user);
        
        AssayPack exp = getExperimentForWrite(expId,user);
        
        
        try {
            UUID analysisId;
            
            analysisId = ppaHandler.newPPA(exp,ppaRequest);            
            tracker.ppaNew(exp,analysisId,ppaRequest.method,user);
            
            Map<String,String> resp = new HashMap<>();
            resp.put("analysis", analysisId.toString());
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
    
}
