/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;


import ed.biodare2.backend.security.BioDare2User;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
//@Service
@Deprecated
class Routes {

    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    final String serviceName;
    final String serverPath;
    
    final String accountPath;
    
    @Autowired
    public Routes(@Value("${service.name:BioDare2}") String serviceName,@Value("${server.path:'localhost:9000'}")String serverPath) {
        this.serviceName = Objects.requireNonNull(serviceName);
        this.serverPath = Objects.requireNonNull(serverPath);
        
        this.accountPath = this.serverPath+"/api/account";
        
        log.debug("Routes Account: {}",accountPath);
    }
    
    public String getServiceName() {
        return serviceName;
    }

    public String getEntityPath(BioDare2User user) {
        return accountPath+"/"+user.getLogin();
    }
    
    
}
