/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ed.biodare2.backend.features.auto_publish;

import ed.biodare2.backend.features.subscriptions.SubscriptionType;
import ed.biodare2.backend.handlers.ExperimentHandler;
import ed.biodare2.backend.repo.isa_dom.openaccess.OpenAccessLicence;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.web.rest.HandlingException;
import java.time.LocalDate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@Service
public class ExpPublishingHandler {
    
    final ExperimentHandler experimentHandler;    
    final UserAccountRep users;    

    public ExpPublishingHandler(ExperimentHandler experimentHandler, UserAccountRep users) {
        this.experimentHandler = experimentHandler;
        this.users = users;
    }
    
    
    
    boolean isSuitableForPublishing(AssayPack exp) {
        
        final EntityACL acl = exp.getACL();        
        if (isNoPublishUser(acl.getCreator())) return false;
        if (isNoPublishUser(acl.getOwner())) return false;
        return true;
    }
    
    boolean isSuitableForPublishing(AssayPack exp, LocalDate cutOff) {
        
        if (exp.getAssay().provenance.created.toLocalDate().isBefore(cutOff))
            return isSuitableForPublishing(exp);

        return false;
    }
    
    
    boolean isNoPublishUser(BioDare2User user) {
                
        return user.getSubscription().getKind().equals(SubscriptionType.FREE_NO_PUBLISH);
    }
    
    @Transactional
    public boolean attemptAutoPublishing(AssayPack exp, LocalDate cutoff) {
        
        if (!isSuitableForPublishing(exp, cutoff)) return false;
        
        BioDare2User system = getSystemUser();
        OpenAccessLicence licence = getDefaultLicence();
        
        addPublishingComment(exp);
        experimentHandler.publish(exp, licence, system);
        
        return true;
        
    }

    OpenAccessLicence getDefaultLicence() {
        return OpenAccessLicence.CC_BY;
    }

    BioDare2User getSystemUser() {
        
        return users.findByLogin("system").orElseThrow(() -> {
            return new HandlingException("Account: 'system' not found, cannot publish");
        });
    }

    void addPublishingComment(AssayPack exp) {
        
        String comments = exp.getAssay().generalDesc.comments;
        
        String message = "Automatically published by BioDare2 system on "+LocalDate.now().toString();
        
        if (comments == null) {
            comments = message;
        } else {
            comments = comments.concat("\n     ")+message;
        }
        
        exp.getAssay().generalDesc.comments = comments;
        
    }
    
    
    
}
