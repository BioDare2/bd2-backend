/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.security.dao.db;

import ed.biodare2.Fixtures;
import ed.biodare2.SimpleRepoTestConfig;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@Import({SimpleRepoTestConfig.class})
public class EntityACLTest {
    
    //@MockBean // neede cause main apps needs it and JPA profile does not set it
    //Jackson2ObjectMapperBuilder builder;
    
    @Autowired
    Fixtures fixtures;
    
    //@Autowired
    //UserAccountRep users;
    
    //@Autowired
    //UserGroupRep groups; 
    
    //@Autowired //@PersistenceContext
    //TestEntityManager EM;  
    
    //@PersistenceContext
    //EntityManager EM;  
    
    @Autowired
    TestEntityManager testEM;    
    
    
    //@PersistenceUnit
    //EntityManagerFactory EMF;
    
    public EntityACLTest() {
    }

    @Test
    //@Transactional
    public void aclCanBeSaved() {
        
        EntityACL acl = new EntityACL();
        acl.setOwner(fixtures.user1);
        acl.setSuperOwner(fixtures.user1.getSupervisor());
        
        acl.getAllowedToRead().addAll(fixtures.user1.getDefaultToRead());
        acl.getAllowedToWrite().add(fixtures.otherGroup);
        
        
        //EM.persist(acl);
        //EM.flush();
        
        EntityACL s = testEM.persistFlushFind(acl);
        assertNotNull(s);
        assertTrue(true);
    }
    
}
