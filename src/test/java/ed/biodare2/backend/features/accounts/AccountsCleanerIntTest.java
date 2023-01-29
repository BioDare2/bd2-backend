/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.accounts;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.features.rdmsocial.RDMUserAspect;
import ed.biodare2.backend.handlers.UsersHandler;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import jakarta.persistence.EntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.Assert.*;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(SimpleRepoTestConfig.class)
public class AccountsCleanerIntTest {
    
    @Autowired
    UsersHandler handler;

    @Autowired
    AccountsCleaner cleaner;
    
    @Autowired
    UserAccountRep users;
    
    @Autowired
    EntityManager em;
    
    @Test
    @Transactional
    public void removeNonActivatedDeletesAccountsWithDependencies() throws Exception {
        
        Map<String,String> details = new HashMap<>();
        details.put("login","rnoactive");
        details.put("email","rnoactive@rnoactive.ed.ac.uk");
        details.put("password", "rnoactive.ed.ac.uk");
        details.put("firstName","TomeK");
        details.put("lastName"," ZedW'S");
        details.put("institution","Inst"); 
        details.put("terms","true");        
        
        UserAccount acc1 = (UserAccount)handler.register(details);
        
        acc1 = users.findById(acc1.getId()).get();
        acc1.setRegistrationDate(LocalDate.now().minusDays(10));
        users.saveAndFlush(acc1);
        
        RDMUserAspect rdm = acc1.getRdmAspect();
        
        cleaner.removeNonActivated();
        
        em.flush();
        rdm = em.find(RDMUserAspect.class, rdm.getId());
        assertNull(rdm);
        try {
            acc1 = users.findById(acc1.getId()).get();
            assertNull(acc1);
        } catch (NoSuchElementException e) {}
        
    }    
    
}
