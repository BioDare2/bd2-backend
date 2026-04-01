/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.MapperConfiguration;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import java.nio.file.Path;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.context.TestConfiguration;
// import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
/**
 *
 * @author Zielu
 */
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
@Import(SimpleRepoTestConfig.class)
public class ExperimentalAssayRepSpringTest {
    
    @EnableCaching
    //@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,JpaRepositoriesAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
    @TestConfiguration
    @ComponentScan(basePackages = "ed.biodare2.backend.repo.dao",useDefaultFilters=false,
        includeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {ExperimentalAssayRep.class})
    )
    @Import(MapperConfiguration.class)
    //@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,JpaRepositoriesAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
    public static class Config {
    }    
    
    final String cacheName = "ExperimentalAssay";

    @TempDir
    Path testFolder;
    
    Path bdStorageDir;
    ExperimentalAssay exp;
    
    @Autowired
    ExperimentalAssayRep rep;
    
    @Autowired
    Environment env;
    
    @MockitoBean
    ExperimentsStorage expStorage; 
    
    @Autowired
    CacheManager cacheManager;
            
    @BeforeEach
    public void setup() throws Exception {
        bdStorageDir = testFolder.resolve("test");
        when(expStorage.getExperimentDir(anyLong())).thenReturn(bdStorageDir.resolve(""+returnsFirstArg()));
        when(expStorage.getExperimentsDir()).thenReturn(bdStorageDir);
        
        exp = DomRepoTestBuilder.makeExperimentalAssay();
    }
    
    @Test
    public void wiringWorks() {
        assertNotNull(rep);
        
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
    public void cachingWorks() {


        Optional<ExperimentalAssay> o1 = rep.findOne(exp.getId());
        assertFalse(o1.isPresent());
        
        ExperimentalAssay s  = rep.save(exp);
        assertNotNull(s);
        
        ExperimentalAssay g1 = rep.findOne(exp.getId()).get();
        ExperimentalAssay g2 = rep.findOne(exp.getId()).get();
        
        assertSame(g1,g2);
        assertSame(s,g1);
        
    }    
}
