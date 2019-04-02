/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.subscriptions;

import static ed.biodare2.BioDare2TestUtils.count;
import ed.biodare2.Fixtures;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.security.dao.db.UserAccount;
import java.time.LocalDate;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
//@Import({SimpleRepoTestConfig.class, IdGeneratorsConfiguration.class})
@Import({SimpleRepoTestConfig.class})
@DataJpaTest
public class AccountSubscriptionTest {
    
    //@PersistenceContext
    //EntityManager EM; 
    
    @Autowired
    TestEntityManager testEM;     
    
    @PersistenceUnit
    EntityManagerFactory EMF;
    
    @Autowired
    Fixtures fixtures;
    
    public AccountSubscriptionTest() {
    }

    
    @Test
    public void savesAndRetrievesSubscriptionByAccount() {
        
        EntityManager em2 = EMF.createEntityManager();
        
        em2.getTransaction().begin();
        
        UserAccount u = new UserAccount();
        u.setEmail("sub.test@ed.ac.uk");
        u.setFirstName("Test");
        u.setLastName("Subscription");
        u.setInitialEmail(u.getEmail());
        u.setInstitution("Inst");
        u.setLogin("sub.test");
        u.setSupervisor(u);
        u.setPassword("aaa");  
        
        em2.persist(u);
        em2.getTransaction().commit();
        
        em2.getTransaction().begin();
        
        AccountSubscription sub = makeSubscription();
        assertNotNull(u);
        assertNull(u.getSubscription());
        
        u.setSubscription(sub);
        em2.getTransaction().commit();
        
        u = testEM.find(UserAccount.class, u.getId());
        assertNotNull(u);
        assertNotNull(u.getSubscription());
        
        AccountSubscription res = u.getSubscription();        
        assertEquals(sub.getKind(),res.getKind());
        assertEquals(sub.getRenewDate(),res.getRenewDate());
        
    }
    
    @Test
    public void removesOrphanedSubscription() {
        
        EntityManager em2 = EMF.createEntityManager();
        
        em2.getTransaction().begin();
        
        UserAccount u = new UserAccount();
        u.setEmail("sub.test@ed.ac.uk");
        u.setFirstName("Test");
        u.setLastName("Subscription");
        u.setInitialEmail(u.getEmail());
        u.setInstitution("Inst");
        u.setLogin("sub.test2");
        u.setSupervisor(u);
        u.setPassword("aaa");
        
        AccountSubscription sub = makeSubscription();
        u.setSubscription(sub);
        
        em2.persist(u);
        em2.getTransaction().commit();
        
        //u = EM.find(UserAccount.class, u.getId());
        
        long us = count(UserAccount.class,testEM.getEntityManager());
        long ss = count(AccountSubscription.class,testEM.getEntityManager());
        
        em2.getTransaction().begin();
        em2.remove(u);
        em2.getTransaction().commit();
        
        assertEquals(us-1,count(UserAccount.class,testEM.getEntityManager()));
        assertEquals(ss-1,count(AccountSubscription.class,testEM.getEntityManager()));
        
    }
    
   
    protected AccountSubscription makeSubscription() {
        AccountSubscription sub = new AccountSubscription();
        sub.setStartDate(LocalDate.now());
        sub.setRenewDate(LocalDate.now().plusYears(1));
        sub.setKind(SubscriptionType.FULL_INDIVIDUAL);
        return sub;
    }
    
}
