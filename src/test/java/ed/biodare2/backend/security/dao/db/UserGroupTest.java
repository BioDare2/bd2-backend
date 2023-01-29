/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.security.dao.db;

import ed.biodare2.SimpleRepoTestConfig;
import jakarta.persistence.PersistenceException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
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
public class UserGroupTest {
    
    //@MockBean // neede cause main apps needs it and JPA profile does not set it
    //Jackson2ObjectMapperBuilder builder;

    //@Autowired
    //UserAccountRep users;
    
    //@Autowired
    //UserGroupRep groups; 
    
    //@PersistenceContext
    //EntityManager EM; 
    
    @Autowired
    TestEntityManager testEM;    
    
    //@PersistenceUnit
    //EntityManagerFactory EMF;
    
    public UserGroupTest() {
    }

    @Test
    //@Transactional
    public void canSaveGroup() {
        
        UserGroup g = new UserGroup();
        g.name = "GT1";
        g.longName = "Long name";
        
        //g = groups.save(g);
        //EM.flush();
        g = testEM.persistFlushFind(g);
        assertNotNull(g);
    }
    
    @Test
    //@Transactional
    public void rejectsNamesDuplicates() {
        
        UserGroup g = new UserGroup();
        g.name = "GT1";
        g.longName = "Long name";
        
        //g = groups.save(g);
        //EM.flush();
        g = testEM.persistFlushFind(g);

        assertNotNull(g);
        
        UserGroup g2 = new UserGroup();
        g2.name = g.name;
        g2.longName = "Long name";
        
        //g2 = groups.save(g2);
        try {
            g2 = testEM.persistFlushFind(g2);

            //EM.flush();
            fail("Exception expected");
        } catch (PersistenceException e) {};
        
    }
    
    
    @Ignore("Currently groups do not have members list as was not usre of the semantics for system and non sistem groups")
    @Test
    //@Transactional
    public void relationsWorks() {
    /*    
        UserAccount pi = new UserAccount();
        pi.login = "PITest1";
        pi.firstName = "PI";
        pi.lastName = "Test";
        pi.email = "cos@cos.pl";
        pi.password = "xxx";
        pi.setInstitution("University of Edinburgh");  
        pi.setSupervisor(pi);

        UserGroup g = new UserGroup();
        g.name = "GT2";
        g.longName = "Long name";
        
        UserGroup g2 = new UserGroup();
        g2.name = "GT3";
        g2.longName = "Long name";

        pi.addGroup(g);
        pi.addGroup(g2);
        pi.addDefaultToRead(g);
        pi.addDefaultToWrite(g2);
        EntityManager em2 = EMF.createEntityManager();
        em2.getTransaction().begin();
        em2.persist(g);
        em2.persist(g2);
        em2.persist(pi);
        em2.getTransaction().commit();
        
        UserGroup gr = groups.findByName(g.name).get();
        assertNotNull(gr);
        //assertEquals(1,gr.getMembers().size());
        //assertTrue(gr.getMembers().contains(pi));
        fail("Have to uncomment methods above to have sensible test");
        
        em2.getTransaction().begin();
        em2.remove(g);
        em2.remove(g2);
        em2.remove(pi);
        em2.getTransaction().commit();
     */   
    }    
}
