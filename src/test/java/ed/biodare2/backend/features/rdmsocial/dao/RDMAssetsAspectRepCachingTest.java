/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rdmsocial.dao;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.features.rdmsocial.RDMAssetsAspect;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 *
 * @author tzielins
 */
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
@Import(SimpleRepoTestConfig.class)
public class RDMAssetsAspectRepCachingTest {
    
    @EnableCaching
    public static class Config {    
    }
    
    final String cacheName = "RDMAssetsAspect";

    @TempDir
    Path testFolder;
    
    @Autowired
    Environment env;
    

    @Autowired
    RDMAssetsAspectRep rep;
    
    @Autowired
    CacheManager cacheManager;
    
    
    @MockitoBean
    ExperimentsStorage expStorage;    
    
    Path expDir;
    
    @BeforeEach
    public void setup() throws IOException {
        expDir = testFolder.resolve("test");
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);
    }
    
    @Test
    public void cachingWorks() {
        long parentId = 3;
        Optional<RDMAssetsAspect> o1 = rep.findByParent(parentId, EntityType.EXP_ASSAY);
        assertFalse(o1.isPresent());
        
        RDMAssetsAspect in = new RDMAssetsAspect();
        in.entityType = EntityType.EXP_ASSAY;
        in.parentId = parentId;
        
        RDMAssetsAspect s  = rep.save(in);
        assertNotNull(s);
        
        RDMAssetsAspect c1 = rep.findByParent(parentId, EntityType.EXP_ASSAY).get();
        RDMAssetsAspect c2 = rep.findByParent(parentId, EntityType.EXP_ASSAY).get();
        
        assertSame(c1,c2);
        assertSame(s,c1);
        
        in = new RDMAssetsAspect();
        in.entityType = EntityType.EXP_ASSAY;
        in.parentId = parentId;
        rep.save(in);
        
        c1 = rep.findByParent(parentId, EntityType.EXP_ASSAY).get();
        assertNotSame(c1,c2);
        assertSame(in,c1);
    }  
    
    @Test
    public void wiringWorks() {
        assertNotNull(rep);

        System.out.println(env.getProperty("spring.cache.cache-names"));
        assertTrue(env.getProperty("spring.cache.cache-names","").contains(cacheName));
    }  
    
    @Test
    public void caffeineIsUsed() {
        assertTrue(env.getProperty("spring.cache.cache-names","").contains(cacheName));
        assertNotNull(cacheManager);
        assertNotNull(cacheManager.getCache(cacheName));
        assertTrue(com.github.benmanes.caffeine.cache.Cache.class.isInstance(cacheManager.getCache(cacheName).getNativeCache()));
    }    
}
