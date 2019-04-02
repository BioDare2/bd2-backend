/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.handlers.ExperimentHandler;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.PermissionsResolver;

import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.features.rdmsocial.RDMAssayGUIAspects;
import ed.biodare2.backend.features.rdmsocial.RDMSocialHandler;
import ed.biodare2.backend.web.tracking.ExperimentTracker;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tzielins
 */
@RestController
@RequestMapping("api/experiment/{expId}/rdm")
public class ExperimentRDMSocialController extends ExperimentController {

    final RDMSocialHandler rdmSocialHandler;
    
    @Autowired
    public ExperimentRDMSocialController(ExperimentHandler handler,RDMSocialHandler rdmSocialHandler,PermissionsResolver permissionsResolver,ExperimentTracker tracker) {        
        super(handler,permissionsResolver,tracker);
        this.rdmSocialHandler = rdmSocialHandler;
    }
    
    
    @RequestMapping(value = "gui-aspects", method = RequestMethod.GET)
    public RDMAssayGUIAspects getGUIAspects(@PathVariable long expId,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("get guiaspects; exp:{}; {}",expId,user);
        
        AssayPack exp = getExperimentForRead(expId,user);

        try {
            return rdmSocialHandler.getAssayGuiAspects(exp, user);
        } catch(WebMappedException e) {
            log.error("Cannot get guiaspects experiment {} {}",expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot get guiaspects experiment {} {}",expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
            
    }     
    
    @RequestMapping(value = "register/warning/{cathegory}", method = RequestMethod.PUT)
    public void registerWarning(@PathVariable long expId,@NotNull @PathVariable String cathegory,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("put registerWarning; exp:{} {}; {}",expId,cathegory,user);
        

        AssayPack exp = getExperimentForWrite(expId,user);
        
        try {
            if (!"MEASUREMENT".equals(cathegory))
                throw new HandlingException("Unsuported warning cathegory: "+cathegory);
            rdmSocialHandler.registerMeasurementWarning(exp, user);
        } catch(WebMappedException e) {
            log.error("Cannot register gui warning experiment {} {}",expId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot register gui warning experiment {} {}",expId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }     
    
    
}
