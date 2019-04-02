/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.subscriptions;

import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.repo.system_dom.FeaturesAvailability;
import ed.biodare2.backend.repo.system_dom.ServiceLevel;
import static ed.biodare2.backend.repo.system_dom.ServiceLevel.*;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class ServiceLevelResolver {
    
    public FeaturesAvailability buildForExperiment(BioDare2User user) {
    
        FeaturesAvailability features = new FeaturesAvailability();
        
        features.serviceLevel = subscriptionToServiceLevel(user.getSubscription());
        
        return features;
    }
    
    public void setServiceForOpen(FeaturesAvailability features) {
        
        if (FULL_SUBSCRIBED.equals(features.serviceLevel)) return;
        if (FULL_PURCHASED.equals(features.serviceLevel)) return;
        features.serviceLevel = FULL_FOR_OPEN;
    }

    protected ServiceLevel subscriptionToServiceLevel(AccountSubscription subscription) {
        
        switch (subscription.kind) {
            case FREE: return FULL_GRATIS;
            case FULL_WELCOME: return FULL_GRATIS;
            case FULL_INDIVIDUAL:
            case FULL_INHERITED:
            case FULL_GROUP: return FULL_SUBSCRIBED;
            default: throw new IllegalArgumentException("Unsuported subscription: "+subscription.kind);
        }
    }
    
}
