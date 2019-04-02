/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rdmsocial;

import ed.biodare2.BioDare2TestUtils;
//import ed.biodare2.IdGeneratorsConfiguration;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.After;
import org.junit.Before;
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
 * @author Zielu
 */
@RunWith(SpringRunner.class)
//@Import({SimpleRepoTestConfig.class, IdGeneratorsConfiguration.class})
@Import({SimpleRepoTestConfig.class})
@DataJpaTest
public class RDMUserAspectTest {
    
    @Autowired
    TestEntityManager EM;

    @Autowired
    UserAccountRep users;
    
    @Autowired
    EntityManagerFactory emf;
    
    
    public RDMUserAspectTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void savesAspectWithAccount() {
        
        UserAccount u = makeAccount("rdmsawa");
        
        RDMUserAspect a = new RDMUserAspect();
        a.setCohort(RDMCohort.STRICT);
        u.setRdmAspect(a);
        
        u = EM.persistFlushFind(u);
        
        assertNotNull(u);
        assertEquals(RDMCohort.STRICT,u.getRdmAspect().getCohort());
        
    }
    
    @Test
    public void removesWithAccount() {
        EntityManager em = emf.createEntityManager();
        
        UserAccount u = makeAccount("rdmrwa");
        
        RDMUserAspect a = new RDMUserAspect();
        a.setCohort(RDMCohort.STRICT);
        u.setRdmAspect(a);
        
        em.getTransaction().begin();
        em.persist(u);
        em.getTransaction().commit();
        
        long ac = BioDare2TestUtils.count(RDMUserAspect.class, em);
        em.getTransaction().begin();
        em.remove(u);
        em.getTransaction().commit();
        
        assertEquals(ac-1,BioDare2TestUtils.count(RDMUserAspect.class, em));
        
    }
    
    protected UserAccount makeAccount(String login) {
        UserAccount u = new UserAccount();
        u.setEmail(login+"@ed.ac.uk");
        u.setFirstName("Test");
        u.setLastName("Subscription");
        u.setInitialEmail(u.getEmail());
        u.setInstitution("Inst");
        u.setLogin(login);
        u.setSupervisor(u);
        u.setPassword("aaa");  
        return u;
    }
    
}
