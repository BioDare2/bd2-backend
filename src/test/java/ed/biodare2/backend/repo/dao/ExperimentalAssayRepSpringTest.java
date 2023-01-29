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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@RunWith(SpringRunner.class)
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
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); 
    
    Path bdStorageDir;
    ExperimentalAssay exp;
    
    @Autowired
    ExperimentalAssayRep rep;
    
    @Autowired
    Environment env;
    
    @MockBean
    ExperimentsStorage expStorage; 
    
    @Autowired
    CacheManager cacheManager;
            

    
    @Before
    public void setup() throws Exception {
        bdStorageDir = testFolder.newFolder().toPath();
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
