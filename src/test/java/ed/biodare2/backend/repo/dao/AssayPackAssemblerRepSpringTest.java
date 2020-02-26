/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.MapperConfiguration;
import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import static ed.biodare2.backend.repo.dao.MockReps.testAssayPack;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
/**
 *
 * @author Zielu
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.NONE)
@Import(SimpleRepoTestConfig.class)
public class AssayPackAssemblerRepSpringTest {
    
    @EnableCaching
    //@SpringBootApplication
    @TestConfiguration
    @ComponentScan(basePackages = "ed.biodare2.backend.repo.dao",useDefaultFilters=false,
        includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {AssayPackAssembler.class})
    )
    @Import(MapperConfiguration.class)    
    public static class Config {
    
    }    
    
    String cacheName = "AssayPack";
    
    @Autowired
    AssayPackAssembler assembler;
    
    @Autowired
    Environment env;
    
    @MockBean
    ExperimentalAssayRep experiments;
    
    @MockBean
    SystemInfoRep systemInfos;
    
    @MockBean
    DBSystemInfoRep dbSystemInfos; 
    
    AssayPackAssembler.AssayPackImpl testPack;    
    
    @Autowired
    CacheManager cacheManager;
            

    
    @Before
    public void setup() throws Exception {

        testPack = testAssayPack();
        
        MockReps.configureMock(dbSystemInfos, testPack.getDbSystemInfo());
        MockReps.configureMock(systemInfos, testPack.getSystemInfo());
        MockReps.configureMock(experiments, testPack.getAssay());
        
    }
    
    @Test
    public void wiringWorks() {
        assertNotNull(assembler);
        
        assertTrue(env.getProperty("spring.cache.cache-names","").contains(cacheName));
        
    }
    
    @Test
    public void caffeineIsUsed() {
        
        assertTrue(env.getProperty("spring.cache.cache-names","").contains(cacheName));
        
        assertNotNull(cacheManager);
        
        assertNotNull(cacheManager.getCache(cacheName));
        assertTrue(com.github.benmanes.caffeine.cache.Cache.class.isInstance(cacheManager.getCache(cacheName).getNativeCache()));
        //System.out.println(.getNativeCache().getClass().getName());
    }    
    
    @Test
    @Transactional()
    public void cachingWorks() {


        long id = testPack.expId;
        
        testPack.readOnly = false;
        AssayPack s  = assembler.save(testPack);
        assertNotNull(s);
        
        
        AssayPack g1 = assembler.findOne(id).get();
        AssayPack g2 = assembler.findOne(id).get();
        
        assertSame(g1,g2);
        assertSame(s,g1);
        
        AssayPack test2 = MockReps.testAssayPack();
        assertNotEquals(test2.getId(),testPack.getId());
        
        MockReps.configureMock(dbSystemInfos,test2.getDbSystemInfo());
        MockReps.configureMock(systemInfos, test2.getSystemInfo());
        MockReps.configureMock(experiments, test2.getAssay());  
        
        AssayPack g3 = assembler.findOne(test2.getId()).get();
        assertNotSame(g3,g1);
        
        //should be cached even if mocks no longer return it
        g2 = assembler.findOne(id).get();
        assertSame(g2,g1);
        
        
    }    
}
