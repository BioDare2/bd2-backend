/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.listeners;

import ed.biodare2.Fixtures;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Import(SimpleRepoTestConfig.class)
@Ignore //[TODO DB TEST]
public class AccountsLockerTest {
    

    
    @Autowired
    AccountsLocker instance;
    
    @Autowired
    UserAccountRep users;
    
    @Autowired
    Fixtures fixtures;
    
    @Autowired
    EntityManagerFactory emf;
    
    public AccountsLockerTest() {
    }

    @Test
    public void increaseAttemptsOnBadCredentials() {
        
        EntityManager em = emf.createEntityManager();
        UserAccount user = em.find(UserAccount.class,fixtures.demoUser.getId());
        
        int prev = user.getFailedAttempts();
        
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user,user.getPassword());
        
        AuthenticationFailureBadCredentialsEvent event = new AuthenticationFailureBadCredentialsEvent(auth, new BadCredentialsException("bad"));
        
        instance.handleBadCredentials(event);
        
        //em.getTransaction().begin();
        em.refresh(user);
        
        assertEquals(prev+1,user.getFailedAttempts());
        
    }
    
    @Test
    public void locksAccountIfLimitReachedOnBadCredentials() {
        
        EntityManager em = emf.createEntityManager();
        UserAccount user = em.find(UserAccount.class,fixtures.demoUser1.getId());
        
        em.getTransaction().begin();
        assertFalse(user.isLocked());
        user.setFailedAttempts(instance.MAX_ATTEMPTS-1);
        em.getTransaction().commit();
        
        
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user,user.getPassword());
        
        AuthenticationFailureBadCredentialsEvent event = new AuthenticationFailureBadCredentialsEvent(auth, new BadCredentialsException("bad"));
        
        instance.handleBadCredentials(event);
        
        //em.getTransaction().begin();
        em.refresh(user);
        assertTrue(user.isLocked());
        
    }   
    
    @Test
    public void resetsFailedOnSuccessfulLogging() {
        
        EntityManager em = emf.createEntityManager();
        UserAccount user = em.find(UserAccount.class,fixtures.demoUser1.getId());
        
        em.getTransaction().begin();
        user.setFailedAttempts(instance.MAX_ATTEMPTS-1);
        em.getTransaction().commit();
        
        
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user,user.getPassword());
        
        AuthenticationSuccessEvent event = new AuthenticationSuccessEvent(auth);
        
        instance.handleSuccessLogin(event);
        
        //em.getTransaction().begin();
        em.refresh(user);
        assertEquals(0,user.getFailedAttempts());
        
    }  
    
    @Test
    public void updatesLastLoginOnSuccessfulLogging() {
        
        EntityManager em = emf.createEntityManager();
        UserAccount user = em.find(UserAccount.class,fixtures.demoUser1.getId());
        
        em.getTransaction().begin();
        user.setLastLogin(LocalDateTime.now().minusDays(1));
        user.setLastLoginAddress("xxx");
        em.getTransaction().commit();
        
        
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user,user.getPassword());
        
        AuthenticationSuccessEvent event = new AuthenticationSuccessEvent(auth);
        
        instance.handleSuccessLogin(event);
        
        //em.getTransaction().begin();
        em.refresh(user);
        assertEquals(0,user.getFailedAttempts());
        assertEquals(LocalDate.now(),user.getLastLogin().toLocalDate());
        assertEquals("unknown",user.getLastLoginAddress());
    }     
    
}
