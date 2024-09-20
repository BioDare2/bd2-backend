/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.subscriptions;

import ed.biodare2.Fixtures;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.repo.system_dom.FeaturesAvailability;
import ed.biodare2.backend.repo.system_dom.ServiceLevel;
import static ed.biodare2.backend.repo.system_dom.ServiceLevel.*;
import static ed.biodare2.backend.features.subscriptions.SubscriptionType.*;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tzielins
 */
public class ServiceLevelResolverTest {
    
    public ServiceLevelResolverTest() {
    }
    
    ServiceLevelResolver instance;
    Fixtures fixtures;
    
    @Before
    public void init() {
        instance = new ServiceLevelResolver();
        fixtures = Fixtures.build();
    }

    @Test
    public void correctlyConvertsSubscriptionsToServiceLevel() {
        
        AccountSubscription subscription = new AccountSubscription();
        
        List<SubscriptionType> types = Arrays.asList(    
                FREE,
                FULL_WELCOME,
                FULL_INDIVIDUAL,
                FULL_GROUP,
                FULL_INHERITED,
                EMBARGO_06,
                EMBARGO_10,
                EMBARGO_20);
        
        List<ServiceLevel> exps = Arrays.asList(
                FULL_GRATIS,
                FULL_GRATIS,
                FULL_SUBSCRIBED,
                FULL_SUBSCRIBED,
                FULL_SUBSCRIBED,
                FULL_GRATIS,
                FULL_GRATIS,
                FULL_GRATIS);
        
        for (int i = 0;i<types.size();i++) {
            subscription.kind = types.get(i);
            ServiceLevel exp = exps.get(i);
            
            ServiceLevel res = instance.subscriptionToServiceLevel(subscription);
            assertEquals(exp,res);
        }
    }
    
    @Test
    public void correctlyConvertsSubscriptionsToEmbargoDuration() {
        
        AccountSubscription subscription = new AccountSubscription();
        
        List<SubscriptionType> types = Arrays.asList(    
                FREE,
                FULL_WELCOME,
                FULL_INDIVIDUAL,
                FULL_GROUP,
                FULL_INHERITED,
                EMBARGO_06,
                EMBARGO_10,
                EMBARGO_20);
        
        List<Integer> exps = Arrays.asList(
                FeaturesAvailability.DEFAULT_EMBARGO,
                FeaturesAvailability.DEFAULT_EMBARGO,
                10,
                10,
                10,
                6,
                10,
                20);
        
        for (int i = 0;i<types.size();i++) {
            subscription.kind = types.get(i);
            int exp = exps.get(i);
            
            int res = instance.subscriptionToEmbargo(subscription);
            assertEquals(exp,res);
        }
    }    
    
  
    @Test
    public void throwsOnSystemSubscriptionsToServiceLevel() {
        
        AccountSubscription subscription = new AccountSubscription();
        subscription.kind = SYSTEM;
        
        try {
            instance.subscriptionToServiceLevel(subscription);
            fail("Excpetion expected");
        } catch (IllegalArgumentException e) {};
    }
    
    @Test
    public void buildForExperimentCreatesCorrectAvailability() {
        
        BioDare2User user = fixtures.user1;
        user.getSubscription().kind = FULL_WELCOME;
        
        FeaturesAvailability res = instance.buildForExperiment(user);
        assertNotNull(res);
        assertEquals(FULL_GRATIS,res.serviceLevel);
        
        
    }
    
    @Test
    public void serviceForOpenPreservesHighValuesSubscriptions() {
        FeaturesAvailability f = new FeaturesAvailability();
        f.serviceLevel = FULL_SUBSCRIBED;
        
        instance.setServiceForOpen(f);
        assertEquals(FULL_SUBSCRIBED, f.serviceLevel);
        
        f.serviceLevel = FULL_PURCHASED;        
        instance.setServiceForOpen(f);
        assertEquals(FULL_PURCHASED, f.serviceLevel);
    }
    
    @Test
    public void serviceForOpenSetsFullOpenForBasicLevels() {
        FeaturesAvailability f = new FeaturesAvailability();
        
        instance.setServiceForOpen(f);
        assertEquals(FULL_FOR_OPEN, f.serviceLevel);
        
        f = new FeaturesAvailability();
        f.serviceLevel = BASIC;
        instance.setServiceForOpen(f);
        assertEquals(FULL_FOR_OPEN, f.serviceLevel);
        
        f = new FeaturesAvailability();
        f.serviceLevel = FULL_GRATIS;
        instance.setServiceForOpen(f);
        assertEquals(FULL_FOR_OPEN, f.serviceLevel);
        
    }
    
}
