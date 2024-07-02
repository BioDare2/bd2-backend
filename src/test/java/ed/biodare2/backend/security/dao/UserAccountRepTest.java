/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.security.dao;

import ed.biodare2.Fixtures;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.features.subscriptions.SubscriptionType;
import ed.biodare2.backend.security.dao.db.UserAccount;
import java.time.LocalDate;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Zielu
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@Import({SimpleRepoTestConfig.class})
public class UserAccountRepTest {
    
    @Autowired
    Fixtures fixtures;
    
    
    @Autowired
    UserAccountRep repository;
    
    //@MockBean // neede cause main apps needs it and JPA profile does not set it
    //Jackson2ObjectMapperBuilder builder;
   
    //List<UserAccount> created = new ArrayList<>();
    
    public UserAccountRepTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    //@Transactional
    public void setUp() {
        //initAccounts(repository,created);
    }
    
    @After
    //@Transactional
    public void tearDown() {
        //clearAccounts(repository,created);
    }
    
    
    @Test
    public void testSetup() {
        
        List<UserAccount> all = repository.findAll();
        
        assertNotNull(all);
        assertFalse(all.isEmpty());
        
        UserAccount acc = new UserAccount();
        acc.setLogin("setup_test");
        acc.setFirstName("First Name");
        acc.setLastName("Last Name");
        acc.setEmail("test@test.ed");
        acc.setPassword("xxx");
        acc.setInstitution("University of Edinburgh");
        
        acc = repository.save(acc);
        //created.add(acc);
        
        UserAccount res = repository.findById(acc.getId()).get();
        assertNotNull(res);
        
    }

    @Test
    public void testFindByLogin() {
        //System.out.println("findByLogin");
        String login = fixtures.user1.getLogin(); //"test1";
        Optional<UserAccount> result = repository.findByLogin(login);
        
        
        assertTrue(result.isPresent());
        
        login = "not-here";
        result = repository.findByLogin(login);
        assertFalse(result.isPresent());        
    }

    @Test
    public void findByActivationDateIsNullWorks() {
        
        UserAccount acc1 = new UserAccount();
        acc1.setLogin("fbyad1");
        acc1.setFirstName("Test");
        acc1.setLastName("User1");
        acc1.setEmail("fbyad1@test.ed");
        acc1.setPassword("test");
        acc1.setInstitution("University of Edinburgh");
        acc1.setActivationDate(LocalDate.now());
        acc1 = repository.saveAndFlush(acc1);
        
        UserAccount acc2 = new UserAccount();
        acc2.setLogin("fbyad2");
        acc2.setFirstName("Test");
        acc2.setLastName("User1");
        acc2.setEmail("fbyad2@test.ed");
        acc2.setPassword("test");
        acc2.setInstitution("University of Edinburgh");
        acc2.setActivationDate(null);  
        acc2 = repository.saveAndFlush(acc2);
        
        List<UserAccount> accs = repository.findByActivationDateIsNull();
        assertFalse(accs.contains(acc1));
        assertTrue(accs.contains(acc2));
        
        accs.forEach( u -> assertNull(u.getActivationDate()));
    }
    
    @Test
    public void findByLoginOrEmailOrIntial() {
        
        UserAccount acc1 = new UserAccount();
        acc1.setLogin("fbylei1");
        acc1.setFirstName("Test");
        acc1.setLastName("User1");
        acc1.setEmail("fbylei1@test.ed");
        acc1.setInitialEmail("fbylei1@intial.test.ed");
        acc1.setPassword("test");
        acc1.setInstitution("University of Edinburgh");
        acc1.setActivationDate(LocalDate.now());
        acc1 = repository.saveAndFlush(acc1);
        
        String id = "aha";
        
        assertTrue(repository.findByLoginOrEmailOrInitialEmail(id, id, id).isEmpty());

        id = acc1.getLogin();
        assertSame(acc1,repository.findByLoginOrEmailOrInitialEmail(id, "X", "X").get(0));
        
        id = acc1.getEmail();
        assertSame(acc1,repository.findByLoginOrEmailOrInitialEmail("X", id, "X").get(0));
        
        id = acc1.getInitialEmail();
        assertSame(acc1,repository.findByLoginOrEmailOrInitialEmail("X", "X", id).get(0));
    }
    
    @Test
    public void findBySubscriptionKind() {
        
        SubscriptionType subscription = SubscriptionType.FREE;
        
        List<UserAccount> res = repository.findBySubscriptionKind(subscription);
        assertTrue(res.size() > 1);
        
        subscription = SubscriptionType.FREE_NO_PUBLISH;
        res = repository.findBySubscriptionKind(subscription);
        //demo user
        assertEquals(1, res.size());        
    }    
    /*
    public static void initAccounts(UserAccountRep repository,List<UserAccount> created) {
        
        UserAccount acc = new UserAccount();
        acc.setLogin("test1");
        acc.setFirstName("Test");
        acc.setLastName("User1");
        acc.setEmail("test1@test.ed");
        acc.setPassword("test");
        acc.setInstitution("University of Edinburgh");

        created.add(repository.save(acc));
        
        
        acc = new UserAccount();
        acc.setLogin("test2");
        acc.setFirstName("Test");
        acc.setLastName("User2");
        acc.setEmail("test2@test.ed");
        acc.setPassword("test");
        acc.setInstitution("University of Edinburgh");

        created.add(repository.save(acc));        
        
        acc = new UserAccount();
        acc.setLogin("boss1");
        acc.setFirstName("Boss");
        acc.setLastName("User1");
        acc.setEmail("boss1@test.ed");
        acc.setPassword("test");
        acc.setInstitution("University of Edinburgh");

        created.add(repository.save(acc));        
        
    }

    public static void clearAccounts(UserAccountRep repository,List<UserAccount> created) {
        
        created.forEach(repository::delete);
        
        created.clear();
    }
*/
    
}
