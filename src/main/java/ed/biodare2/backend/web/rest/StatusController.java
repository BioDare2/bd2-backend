/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    
    
}
