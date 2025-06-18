/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.listeners;

import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.web.tracking.SecurityTracker;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

/**
 *
 * @author tzielins
 */
@Service
public class AccountsLocker {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());
    final int MAX_ATTEMPTS = 5;
    
    final SecurityTracker tracker;
    final UserAccountRep users;
    
    @Autowired
    public AccountsLocker(UserAccountRep users, SecurityTracker tracker) {
        this.tracker = tracker;
        this.users = users;
    }
    
    @EventListener
    @Transactional
    public void handleBadCredentials(AuthenticationFailureBadCredentialsEvent event) {

        Authentication auth = event.getAuthentication();
        //log.info("Locking: "+auth.getName());
        if (auth != null) {
            
            users.findByLogin(auth.getName()).ifPresent( user -> {
                user.setFailedAttempts(user.getFailedAttempts()+1);
                if (user.getFailedAttempts() >= MAX_ATTEMPTS) {
                    user.setLocked(true);
                    tracker.handleLock(auth);
                    log.info("Locked account: "+user.getLogin());
                }
            });
            
        }
    }

    @EventListener
    @Transactional
    public void handleSuccessLogin(AuthenticationSuccessEvent event) {
        
        Authentication auth = event.getAuthentication();
        //log.info("Locking: "+auth.getName());
        if (auth != null) {
            
            if (auth.getPrincipal() instanceof UserAccount) {
                UserAccount user = (UserAccount)auth.getPrincipal();
                if (user.isAnonymous()) return;
                String remote = "unknown";
                

                if (auth.getDetails() instanceof WebAuthenticationDetails) {
                    remote = (( WebAuthenticationDetails)auth.getDetails()).getRemoteAddress();
                }
                String add = remote;
                
                users.findByLogin(auth.getName()).ifPresent( u -> {
                    u.setFailedAttempts(0);
                    u.setLastLogin(LocalDateTime.now());
                    u.setLastLoginAddress(add);
                    log.info("SUCCESS login account: "+u.getLogin());
                });

                
            }
            
        }
    }
    
    @Scheduled(fixedRate = 30*60*1000, initialDelay = 1*60*1000)
    @Transactional
    public void unlockAccounts() {
        log.info("Unlocking accounts");
        users.unlockExpiredAccounts();
    }
    
}
