/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.datahandling;

import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import ed.biodare2.backend.MapperConfiguration;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.data.TimeSeries;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
// import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
/**
 *
 * @author Zielu
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
public class TSDataHandlerSpringTest {
    
    @EnableCaching
    @SpringBootApplication
    @Import(MapperConfiguration.class)
    @EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,JpaRepositoriesAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
    public static class Config {


    }    
    final String cacheName = "TSData";
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); 
    Path bdStorageDir;
    
    AssayPack exp;
    
    @Autowired
    TSDataHandler handler;    
    
    @MockitoBean
    ExperimentsStorage expStorage;
    

    
    @Autowired
    Environment env;
    
    @Autowired
    CacheManager cacheManager;
            

    
    @Before
    public void setup() throws Exception {
        bdStorageDir = testFolder.newFolder().toPath();
        Files.createDirectories(bdStorageDir.resolve("1"));
        Files.createDirectories(bdStorageDir.resolve("2"));

        when(expStorage.getExperimentDir(eq(1L))).thenReturn(bdStorageDir.resolve("1"));
        when(expStorage.getExperimentDir(eq(2L))).thenReturn(bdStorageDir.resolve("2"));
        when(expStorage.getExperimentsDir()).thenReturn(bdStorageDir);
        
        exp = mock(AssayPack.class);
        when(exp.getId()).thenReturn(1L);

    }
    
    DataBundle makeBoundle() {
        DataBundle dataBoundle = new DataBundle();
        DataTrace trace = new DataTrace();
        trace.traceRef = "A"; 
        trace.traceNr = 1;
        trace.dataId = trace.traceNr;
        trace.rawDataId = trace.dataId;
        TimeSeries serie = new TimeSeries();
        serie.add(1,1);
        serie.add(2,2);
        serie.add(3,3);
        trace.trace = serie;
        dataBoundle.data.add(trace);
        
        trace = new DataTrace();
        trace.traceRef = "B";
        trace.traceNr = 2;        
        trace.dataId = trace.traceNr;
        trace.rawDataId = trace.dataId;
        serie = new TimeSeries();
        serie.add(1.1,2);
        serie.add(1.2,2);
        serie.add(2.2,3);
        serie.add(3.0,4);
        serie.add(3.3,4);
        trace.trace = serie;
        dataBoundle.data.add(trace);  
        return dataBoundle;
    }
    
    @Test
    public void wiringWorks() {
        assertNotNull(handler);
        
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
    public void cachingWorks() throws Exception {

        DataBundle db1 = makeBoundle();
        handler.handleNewData(exp, db1);
        
        DetrendingType detrending = DetrendingType.LIN_DTR;
        List<DataTrace> set1 = handler.getDataSet(exp, detrending).get();
        
        List<DataTrace> set2 = handler.getDataSet(exp, detrending).get();   
        
        assertSame(set1,set2);
        
        set2 = handler.getDataSet(exp, DetrendingType.POLY_DTR).get(); 
        
        assertNotSame(set1,set2);
        
        
    }   


    @Test
    public void binnedCachingWorks() throws Exception {

        DataBundle db1 = makeBoundle();
        handler.handleNewData(exp, db1);
        
        DetrendingType detrending = DetrendingType.LIN_DTR;
        List<DataTrace> set1 = handler.getHourlyDataSet(exp, detrending).get();
        
        List<DataTrace> set2 = handler.getHourlyDataSet(exp, detrending).get();  
        
        assertSame(set1,set2);
        
        set2 = handler.getHourlyDataSet(exp, DetrendingType.POLY_DTR).get(); 
        
        assertNotSame(set1,set2);
        
        
    }   
    
    @Test
    public void binnedCachingDoesNotInterfereWorks() throws Exception {

        DataBundle db1 = makeBoundle();
        handler.handleNewData(exp, db1);
        
        DetrendingType detrending = DetrendingType.LIN_DTR;
        List<DataTrace> set1 = handler.getDataSet(exp, detrending).get();
        
        List<DataTrace> binset1 = handler.getHourlyDataSet(exp, detrending).get();
        
        List<DataTrace> set2 = handler.getDataSet(exp, detrending).get();   
        List<DataTrace> binset2 = handler.getHourlyDataSet(exp, detrending).get();          
        
        assertSame(set1,set2);
        assertSame(binset1,binset2);
        
        assertNotSame(set1,binset1);
        assertNotEquals(set1,binset1);
        
        
    }     
    
    @Test
    public void handleNewDataInvalidesDataAndBinnedCache() throws Exception {

        DataBundle db1 = makeBoundle();
        handler.handleNewData(exp, db1);
        
        DetrendingType detrending = DetrendingType.LIN_DTR;
        List<DataTrace> set1 = handler.getDataSet(exp, detrending).get();
        
        List<DataTrace> binset1 = handler.getHourlyDataSet(exp, detrending).get();
        
        List<DataTrace> set2 = handler.getDataSet(exp, detrending).get();   
        List<DataTrace> binset2 = handler.getHourlyDataSet(exp, detrending).get();          
        
        assertSame(set1,set2);
        assertSame(binset1,binset2);
        
        handler.handleNewData(exp, db1);
        
        set2 = handler.getDataSet(exp, detrending).get();   
        binset2 = handler.getHourlyDataSet(exp, detrending).get();          

        assertNotSame(set1,set2);
        assertNotSame(binset1,binset2);
        
        
    }     
    
    @Test
    public void handleNewDataInvalidesCache() throws Exception {

        DataBundle db1 = makeBoundle();
        handler.handleNewData(exp, db1);
        
        AssayPack exp2 = mock(AssayPack.class);
        when(exp2.getId()).thenReturn(2L);  
        DataBundle db2 = makeBoundle();
        handler.handleNewData(exp2, db2);        
        
        DetrendingType detrending = DetrendingType.LIN_DTR;
        List<DataTrace> set1 = handler.getDataSet(exp, detrending).get();
        List<DataTrace> set2 = handler.getDataSet(exp, detrending).get();   
        assertSame(set1,set2);
        
        set2 = handler.getDataSet(exp2, detrending).get();   
        
        assertNotSame(set1,set2);
        
        handler.handleNewData(exp2, db2); 
        set2 = handler.getDataSet(exp, detrending).get();
        assertNotSame(set1,set2);
        set1 = handler.getDataSet(exp, detrending).get();
        assertSame(set1,set2);
       
        
         
        
    }   
    
}
