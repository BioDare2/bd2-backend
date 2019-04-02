/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.Fixtures;
import ed.biodare2.SimpleRepoTestConfig;
//import ed.biodare2.backend.SimpleTestConfiguration;
import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.UserGroupRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import static org.unitils.reflectionassert.ReflectionAssert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
//@DataJpaTest(showSql = false)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestEntityManager //need this to get entity manager
@Import(SimpleRepoTestConfig.class)
public class SystemCopierTest {
    
    //@Autowired
    SystemCopier copier;
    
    @Autowired
    DBSystemInfoRep dbSystemInfos;
    
    @Autowired
    UserAccountRep accounts;  
    
    @Autowired
    UserGroupRep groups;  
    
    
    @Autowired
    TestEntityManager entityManager;    
    
    @Autowired
    EntityManagerFactory EMF;
    
    Fixtures fixture;
    DBSystemInfo dbSysInfo;
    
    public SystemCopierTest() {
    }
    
    @Before
    //@Transactional
    public void setup() {
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        //mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        //DBSystemInfoRep dbSystemInfos = mock(DBSystemInfoRep.class);
        fixture = Fixtures.build();//Fixtures.build(accounts,groups);

        

        
        copier = new SystemCopier(dbSystemInfos,mapper);
        

    }
    
    @After
    public void clean() {
      
    }
 

    @Test
    public void testCopySystemInfo() {
        
        SystemInfo org = SystemDomTestBuilder.makeSystemInfo();
        
        SystemInfo cpy = copier.copy(org);
        assertNotSame(org,cpy);
        assertEquals(org,cpy);
        assertReflectionEquals(org,cpy);

    }
    
    @Test
    public void testCopyExperimentalAssay() {
        ExperimentalAssay org = DomRepoTestBuilder.makeExperimentalAssay();
        ExperimentalAssay cpy = copier.copy(org);
        
        assertNotSame(org,cpy);
        //assertEquals(org,cpy);
        assertEquals(org.getId(),cpy.getId());
        assertReflectionEquals(org,cpy);        
    }
    
    
    protected DBSystemInfo insertDBSysInfo() {
        
        EntityManager EM = EMF.createEntityManager();
        EM.getTransaction().begin();
        System.out.println("\n\nBefore user");
        
        UserAccount user = new UserAccount();
        user.setLogin("auser");
        user.setFirstName("auser");
        user.setLastName("auser");
        user.setPassword("pass");
        user.setEmail("bioadare@ed.ac.uk");
        user.setInstitution("UoE");
        
        EM.persist(user);
        EM.flush();
        
        //user = EM.find(UserAccount.class, user.getId());
        //assertNotNull(user);
        
        //user.getGroups().forEach(EM::persist);        
        //EM.persist(user);
        
        DBSystemInfo org = SystemDomTestBuilder.makeDBSystemInfo(SystemDomTestBuilder.makeSystemInfo());
        org.getAcl().setCreator(user);
        org.getAcl().setOwner(user);
        org.getAcl().setSuperOwner(user);
        
        System.out.println("\n\nBefore insert");
        EM.persist(org);
        EM.flush();
        EM.getTransaction().commit();
        
        //org = dbSystemInfos.save(org);
        dbSysInfo = org; 
        return dbSysInfo;
    }
    
    @Test
    public void testCopyDBSytemInfo() {
        
        DBSystemInfo org = insertDBSysInfo();
        //DBSystemInfo org = dbSysInfo;
        
        long p =org.getParentId();
        org.setParentId(p+1);
        
        System.out.println("\n\n\nCopy");
        DBSystemInfo cpy = copier.copy(org);
        assertEquals(org.getInnerId(),cpy.getInnerId());        
        assertEquals(p,cpy.getParentId());
        assertNotSame(org,cpy);
        
        //fail("ON purpose");
        /*
        UserAccount user = fixture.demoUser;
        user.getGroups().forEach(entityManager::persist);
        
        user = entityManager.persistAndFlush(user);
        
        DBSystemInfo org = SystemDomTestBuilder.makeDBSystemInfo(SystemDomTestBuilder.makeSystemInfo());
        org.getAcl().setCreator(user);
        org.getAcl().setOwner(user);
        org.getAcl().setSuperOwner(user);
        
        org = entityManager.persistAndFlush(org);
        
        
        
        DBSystemInfo cpy = copier.copy(org);
        assertNotSame(org,cpy);
        assertEquals(org.getInnerId(),cpy.getInnerId());
                */
    }
    
}
