/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
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
// import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
/**
 *
 * @author Zielu
 */
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
@Import(SimpleRepoTestConfig.class)
public class SystemInfoRepSpringTest {
    
    @TempDir
    Path testFolder;
    
    Path bdStorageDir;
    SystemInfo info;
    
    @Autowired
    SystemInfoRep rep;
    
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
        
        info = SystemDomTestBuilder.makeSystemInfo();
    }
    
    @Test
    public void wiringWorks() {
        assertNotNull(rep);
        
        assertTrue(env.getProperty("spring.cache.cache-names","").contains("SystemInfo"));
        
    }
    
    @Test
    public void caffeineIsUsed() {
        
        assertTrue(env.getProperty("spring.cache.cache-names","").contains("SystemInfo"));
        
        assertNotNull(cacheManager);
        
        assertNotNull(cacheManager.getCache("SystemInfo"));
        assertTrue(com.github.benmanes.caffeine.cache.Cache.class.isInstance(cacheManager.getCache("SystemInfo").getNativeCache()));
        //System.out.println(.getNativeCache().getClass().getName());
    }    
    
    @Test
    public void cachingWorks() {


        Optional<SystemInfo> o1 = rep.findByParent(info.parentId,info.entityType);
        assertFalse(o1.isPresent());
        
        SystemInfo s  = rep.save(info);
        assertNotNull(s);
        
        SystemInfo g1 = rep.findByParent(info.parentId,info.entityType).get();
        SystemInfo g2 = rep.findByParent(info.parentId,info.entityType).get();
        
        assertSame(g1,g2);
        assertSame(s,g1);
        
        SystemInfo info2 =  SystemDomTestBuilder.makeSystemInfo();
        assertNotEquals(info.parentId,info2.parentId);
        Optional<SystemInfo> o2 = rep.findByParent(info2.parentId,info2.entityType);
        assertFalse(o2.isPresent());
        
        SystemInfo s2  = rep.save(info2);
        SystemInfo g3 = rep.findByParent(info2.parentId,info2.entityType).get();
        assertSame(s2,g3);   
        
        g1 = rep.findByParent(info.parentId,info.entityType).get();
        assertNotSame(g1,g3);
        assertSame(g1,g2);
        
        
        /*
        SystemInfo info2 =  SystemDomTestBuilder.makeSystemInfo();
        info2.parentId = info.parentId;
        info2.entityType = EntityType.INVESTIGATION;
        
        Optional<SystemInfo> o2 = rep.findByParent(info2.parentId,info2.entityType);
        assertFalse(o2.isPresent());
        
        SystemInfo s2  = rep.save(info2);
        SystemInfo g3 = rep.findByParent(info2.parentId,info2.entityType).get();
        assertSame(s2,g3);   
        
        g1 = rep.findByParent(info.parentId,info.entityType).get();
        assertNotSame(g1,g3);*/
        
    }    
}
