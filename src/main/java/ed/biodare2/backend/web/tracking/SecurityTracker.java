/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.tracking;

import ed.biodare2.backend.security.BioDare2User;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static ed.biodare2.backend.web.tracking.TargetType.*;
import static ed.biodare2.backend.web.tracking.ActionType.*;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 *
 * @author tzielins
 */
@Component
public class SecurityTracker extends AbstractTracker {
    
    final Logger security = LoggerFactory.getLogger("security");

    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        
        Authentication auth = event.getAuthentication();
        String remote = extractRemote(auth);
        
        log(USER,LOGIN,auth.getName(),remote);
    }    
    
    @EventListener
    public void handleAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        
        Authentication auth = event.getAuthentication();
        if (auth != null) {
        
            String remote = extractRemote(auth);
            String cat = event.getClass().getSimpleName();


            log(USER,FAILURE,auth.getName(),remote,cat);
        } else {
            log(USER,FAILURE,"null","");
        }
        
    }  
    
    public void handleLogout(Authentication auth) {
        if (auth != null) {
            String remote = extractRemote(auth);
            log(USER,LOGOUT,auth.getName(),remote);
        } else {
            log(USER,LOGOUT,"null","");
        }
    }
    
    public void handleLock(Authentication auth) {
            String remote = extractRemote(auth);
            log(USER,LOCK,auth.getName(),remote);
            
    } 
    
    protected final String extractRemote(Authentication auth) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String remoteAddr = request.getHeader("X-Forwarded-For");
        if (remoteAddr == null || remoteAddr.isEmpty()) {
            if (auth.getDetails() instanceof WebAuthenticationDetails) {
                remoteAddr = ((WebAuthenticationDetails) auth.getDetails()).getRemoteAddress();
            }
        } else {
            // X-Forwarded-For can contain multiple IP addresses, take the first one
            remoteAddr = remoteAddr.split(",")[0].trim();
        }
        return remoteAddr;
    }
    
    public void userPasswordReset(BioDare2User account, Authentication auth) {
        log(USER,RESET,account.getLogin(),extractRemote(auth));
    }    
    
    /*
    public void anonymous(BD2AnonymousUserAuthenticationToken authentication, HttpServletRequest httpServletRequest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void userLogout(Authentication auth, HttpServletRequest request) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }*/

    void log(TargetType target, ActionType action, String login, String remote) {
        
        //operations.info("{}\t{}\t{}\t{}",target,action,login,remote);
        security.info("{}\t{}\t{}\t{}",target,action,login,remote);
    }

    void log(TargetType target, ActionType action, String login, String remote, String cat) {
        //operations.info("{}\t{}\t{}\t{}\t{}",target,action,login,remote,cat);
        security.info("{}\t{}\t{}\t{}\t{}",target,action,login,remote,cat);
    }


    
    
}
