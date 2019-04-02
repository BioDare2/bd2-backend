/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.security.dao.db;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.security.dao.UserGroupRep;
import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
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
@DataJpaTest
@Import({SimpleRepoTestConfig.class})
public class UserAccountTest {
    
    //@MockBean // neede cause main apps needs it and JPA profile does not set it
    //Jackson2ObjectMapperBuilder builder;
    //@Autowired
    //UserAccountRep users;
    
    @Autowired
    UserGroupRep groups; 
    
    //@PersistenceContext
    //EntityManager testEM;              
    
    @Autowired
    TestEntityManager testEM;    
    
    
    @PersistenceUnit
    EntityManagerFactory EMF;
    

    
    public UserAccountTest() {
    }

    //@Transactional
    @Test
    public void savesPIUser() {
        
        UserAccount pi = new UserAccount();
        pi.login = "PITest1";
        pi.firstName = "PI";
        pi.lastName = "Test";
        pi.setEmail("cos@cos.pl");
        pi.password = "xxx";
        pi.setInstitution("University of Edinburgh");        
        pi.setSupervisor(pi);
        
        //pi = users.save(pi);
        
        //testEM.flush();
        //assertNotNull(pi);
        
        UserAccount s = testEM.persistFlushFind(pi);
        assertEquals(s,pi);
        
    }
    

    
    //@Transactional
    @Test
    public void savesUserWithSuper() {
        
        UserAccount pi = new UserAccount();
        pi.login = "PITest2";
        pi.firstName = "PI";
        pi.lastName = "Test";
        pi.setEmail("cos@cos.pl");
        pi.password = "xxx";
        pi.setInstitution("University of Edinburgh");  
        pi.setSupervisor(pi);
        
        //pi = users.save(pi);
        //testEM.flush();
        pi = testEM.persistAndFlush(pi);
        
        pi = testEM.find(UserAccount.class, pi.getId());
        assertNotNull(pi);
        
        UserAccount u = new UserAccount();
        u.login = "UTest2";
        u.firstName = "User";
        u.lastName = "Test";
        u.setEmail("email@mail.com");
        u.password = "xxx";
        u.setInstitution("University of Edinburgh");  
        u.setSupervisor(pi);
        
        UserAccount s = testEM.persistFlushFind(u);
        assertEquals(u,s);
        
        /*
        u = users.save(u);
        testEM.flush();
        u = testEM.find(UserAccount.class, u.getId());        
        assertNotNull(u);        
        */
    }    
    
    //@Transactional
    @Test
    public void duplicateLoginsCauseException() {
        
        UserAccount pi = new UserAccount();
        pi.login = "PITest2";
        pi.firstName = "PI";
        pi.lastName = "Test";
        pi.setEmail("cos@cos.pl");
        pi.password = "xxx";
        pi.setInstitution("University of Edinburgh");  
        pi.setSupervisor(pi);
        
        //users.save(pi);        
        //testEM.flush();
        
        testEM.persistAndFlush(pi);        
        
        pi = new UserAccount();
        pi.login = "PITest2";
        pi.firstName = "PI";
        pi.lastName = "Test";
        pi.setEmail("cos@cos.pl");
        pi.password = "xxx";
        pi.setInstitution("University of Edinburgh");  
        pi.setSupervisor(pi);
        
        //users.save(pi);

        try {
            testEM.persistAndFlush(pi);   
            //EM.flush();
            testEM.flush();
            fail("Exception expected");
        } catch (javax.persistence.PersistenceException e) {};
        
        
    }     
    
    @Test
    public void addGroupAddsDependingOnGroupType() {
        
        UserGroup g1 = UserGroup.testInstance(1);
        g1.setName("g1ToWrite1");
        g1.system = false;
        
        
        UserGroup g2 = UserGroup.testInstance(2);
        g2.setName("g2ToRead1");
        g2.system = true;
        
        UserAccount pi = UserAccount.testInstance(1);
        pi.login = "PITest31";
        pi.firstName = "PI";
        pi.lastName = "Test";
        pi.setEmail("cos@cos.pl");
        pi.password = "xxx";
        pi.setInstitution("University of Edinburgh");  
        pi.setSupervisor(pi);
        
        pi.addGroup(g1);
        pi.addGroup(g2);
        
        System.out.println(pi.getGroups());
        assertTrue(pi.getGroups().contains(g1));
        assertFalse(pi.getGroups().contains(g2));
        assertFalse(pi.getSystemGroups().contains(g1));
        assertTrue(pi.getSystemGroups().contains(g2));
        
    }
    
    
    //@Transactional
    @Test
    public void savesUserWithGroups() {
        
        UserGroup g1 = new UserGroup();
        g1.setName("g1ToWrite1");
        g1 = groups.save(g1);
        
        UserGroup g2 = new UserGroup();
        g2.setName("g2ToRead1");
        g2 = groups.save(g2);
        
        UserGroup g3 = new UserGroup();
        g3.setName("g3Special1");
        g3.system = true;
        g3 = groups.save(g3);
        
        //EM.flush();
        testEM.flush();
        
        UserAccount pi = new UserAccount();
        pi.login = "PITest31";
        pi.firstName = "PI";
        pi.lastName = "Test";
        pi.setEmail("cos@cos.pl");
        pi.password = "xxx";
        pi.setInstitution("University of Edinburgh");  
        pi.setSupervisor(pi);
        
        pi.addGroup(g1);
        pi.addGroup(g2);
        pi.addGroup(g3);
        
        pi.addDefaultToWrite(g1);
        pi.addDefaultToRead(g2);
        
        //pi = users.save(pi);
        //testEM.flush();
        //assertNotNull(pi);
        pi = testEM.persistFlushFind(pi);
        
    }
    

    //@Transactional
    @Test
    public void independentContextWorks() {
        
        EntityManager em2 = EMF.createEntityManager();
        UserAccount p2 = new UserAccount();
        p2.login = "PITest4";
        p2.firstName = "PI";
        p2.lastName = "Test";
        p2.setEmail("cos@cos.pl");
        p2.password = "xxx";
        p2.setInstitution("University of Edinburgh");  
        p2.setSupervisor(p2);        
        em2.getTransaction().begin();
        em2.persist(p2);
        
        assertNotNull(p2.getId());
        
        
        UserAccount pi = testEM.find(UserAccount.class, p2.getId());
        assertNull(pi);
        
        em2.getTransaction().commit();
        
        pi = testEM.find(UserAccount.class, p2.getId());
        assertNotNull(pi);
        
    }

    
    //@Transactional
    @Test
    public void retrievesCorrectRelations() {
        
        EntityManager em2 = EMF.createEntityManager();
        em2.getTransaction().begin();
        
        UserGroup g1 = new UserGroup();
        g1.setName("g1ToWrite");
        em2.persist(g1);
        
        UserGroup g2 = new UserGroup();
        g2.setName("g2ToRead");
        em2.persist(g2);
        
        UserGroup g3 = new UserGroup();
        g3.setName("g3Special");
        g3.system = true;
        em2.persist(g3);
        
        UserGroup g4 = new UserGroup();
        g4.setName("g4NotUsed");
        g4.system = true;
        em2.persist(g4);        
        
        UserAccount pi = new UserAccount();
        pi.login = "PITest5";
        pi.firstName = "PI";
        pi.lastName = "Test";
        pi.setEmail("cos@cos.pl");
        pi.password = "xxx";
        pi.setInstitution("University of Edinburgh");  
        pi.setSupervisor(pi);
        
        pi.addGroup(g1);
        pi.addGroup(g2);
        pi.addGroup(g3);
        
        pi.addDefaultToWrite(g1);
        pi.addDefaultToRead(g2);
        
        em2.persist(pi);
        
        String sql = "SELECT COUNT(g) FROM UserAccount u INNER JOIN u.groups g WHERE u.id IN ("+pi.getId()+")";
        System.out.println(sql);
        TypedQuery<Long> query = testEM.getEntityManager().createQuery(sql, Long.class);
        assertEquals(0L,query.getSingleResult().longValue()); 
        
        em2.getTransaction().commit();
        
        UserAccount acc = testEM.find(UserAccount.class,pi.getId());
        assertNotNull(acc);
        assertEquals(2L,query.getSingleResult().longValue());
        
        sql = "SELECT COUNT(g) FROM UserAccount u INNER JOIN u.systemGroups g WHERE u.id IN ("+pi.getId()+")";
        System.out.println(sql);
        query = testEM.getEntityManager().createQuery(sql, Long.class);
        assertEquals(1L,query.getSingleResult().longValue());         
        
        assertTrue(acc.getDefaultToWrite().contains(g1));
        assertTrue(acc.getDefaultToRead().contains(g2));
        //assertEquals(g3, acc.getSystemGroups().stream().filter(g -> g.isSystem()).findFirst().get());
        
        assertTrue(acc.getGroups().contains(g1));
        assertTrue(acc.getGroups().contains(g2));
        assertTrue(acc.getSystemGroups().contains(g3));
        

        
        em2.getTransaction().begin();
        em2.remove(g1);
        em2.remove(g2);
        em2.remove(g3);
        em2.remove(pi);
        em2.getTransaction().commit();
    }
    
    //@Transactional
    @Test
    public void creationDateIsSetToNow() {
        
        
        UserAccount u = new UserAccount();
        u.login = "cdttest";
        u.firstName = "creation";
        u.lastName = "data tests";
        u.setEmail("cos@cos.pl");
        u.password = "xxx";
        u.setInstitution("University of Edinburgh");  
        u.setSupervisor(u);    
        
        LocalDateTime justBeforeNow = LocalDateTime.now().minusSeconds(1);
        
        testEM.persist(u);
        testEM.flush();
        u= testEM.find(UserAccount.class,u.getId());
        assertEquals(u.getCreationDate().toLocalDate(),justBeforeNow.toLocalDate());
    }
    
    //@Transactional
    @Test
    public void modificationDateIsSetToNow() throws InterruptedException {
        
        
        UserAccount u = new UserAccount();
        u.login = "mdttest";
        u.firstName = "creation";
        u.lastName = "data tests";
        u.setEmail("cos@cos.pl");
        u.password = "xxx";
        u.setInstitution("University of Edinburgh");  
        u.setSupervisor(u);    
        
        LocalDateTime justBeforeNow = LocalDateTime.now().minusSeconds(1);
        
        testEM.persist(u);
        testEM.flush();
        u= testEM.find(UserAccount.class,u.getId());
        
        assertEquals(u.getModificationDate().toLocalDate(),justBeforeNow.toLocalDate());
        
        LocalDateTime prev = u.getModificationDate();
        LocalDateTime cre = u.getCreationDate();
        u.setPassword("xxx2");
        Thread.sleep(100);
        
        testEM.flush();
        u= testEM.find(UserAccount.class,u.getId());
        assertTrue(prev.isBefore(u.getModificationDate()));
        assertEquals(cre,u.getCreationDate());
        
    }    
}
