/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tzielins
 */
@RestController
@RequestMapping(value = "api/status")
public class StatusController extends BioDare2Rest {

    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    final DBSystemInfoRep systemInfos;
    
    boolean isShutingDown = false;
    LocalDateTime shutDownTime;
            
    @Autowired
    public StatusController(DBSystemInfoRep systemInfos) {        
        this.systemInfos = systemInfos;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public Map<String,String> status(@NotNull @AuthenticationPrincipal UserDetails currentUser) {
        String userName = (currentUser != null ? currentUser.getUsername() : "NO USER");
        log.debug("Server status {}",userName);
        
        try {
            long infos = systemInfos.count();
            Map<String,String> res = new HashMap<>();
            res.put("status","running");
            res.put("user",userName);
            res.put("experiments", ""+infos);
            return res;
        
        } catch (Exception e) {
            log.error("Cannot get satus {}",e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        }        
    }  
    
    @RequestMapping(value = "shutdown",  method = RequestMethod.GET)
    public Map<String,String> shutdown(@NotNull @AuthenticationPrincipal UserDetails currentUser) {
        
        Map<String,String> res = new HashMap<>();
        if (isShutingDown) {
            res.put("shutdown",shutdownMessage(shutDownTime));
        }
        return res;        
    }   
    
    @RequestMapping(value = "shutdown/set/{inMinutes}",  method = RequestMethod.GET)
    public Map<String,String> setShutdown(@PathVariable int inMinutes, @NotNull @AuthenticationPrincipal UserDetails currentUser) {
        log.debug("Server shutdown is being set in {} by {}",inMinutes, currentUser.getUsername());
        
        if (!currentUser.getUsername().equals("test")) {
            throw new InsufficientRightsException("Unauthrised to set shutdown");
        }
        
        if (inMinutes < 1 || inMinutes > 60) {
            throw new HandlingException("In minutes must be (1,60)");
        }
        
        isShutingDown = true;
        shutDownTime = LocalDateTime.now().plusMinutes(inMinutes);

        Map<String,String> res = new HashMap<>();
        res.put("shutdown",shutdownMessage(shutDownTime));
        return res;        
    }     
    
    @RequestMapping(value = "shutdown/reset",  method = RequestMethod.GET)
    public Map<String,String> resetShutdown(@NotNull @AuthenticationPrincipal UserDetails currentUser) {
        log.debug("Server shutdown reset by {}",currentUser.getUsername());
        
        if (!currentUser.getUsername().equals("test")) {
            throw new InsufficientRightsException("Unauthrised to reset shutdown");
        }
        
        
        isShutingDown = false;

        Map<String,String> res = new HashMap<>();
        return res;        
    }     

    String shutdownMessage(LocalDateTime shutDownTime) {
        
        Duration duration = Duration.between(LocalDateTime.now(), shutDownTime);
        long minutes = duration.toMinutes();
        if (minutes <=0) return "BioDare shuts down now";
        return "BioDare shuts down in "+minutes+" minutes";
    }
    
    
}
