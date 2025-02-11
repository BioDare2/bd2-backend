/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
//import ed.biodare2.BioDare2TestConfiguration;
import ed.biodare2.SimpleRepoTestConfig;
//import ed.biodare2.BioDare2TestConfiguration;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.data.TimeSeries;
import ed.robust.util.timeseries.TSGenerator;
import ed.robust.util.timeseries.TimeSeriesFileHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.fail;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext //("Need it to prevent some pprimery key confirlicts with entityacl in integration tests")
@Import(SimpleRepoTestConfig.class)
public class ExpTestSeederTest {
    

    
    
    
    @Autowired
    ExpTestSeeder seeder;
    
    
    @Autowired
    ObjectMapper mapper;    
        
    
    @Autowired
    TSDataHandler tsHandler;
   
    
    
    
    @Test
    //@Ignore("Cause was clashing with restint tests, dont know why")
    //@Transactional
    public void seedingDataWorks() {
        AssayPack exp = seeder.insertExperiment();
        List<DataTrace> data = seeder.getData();
        seeder.seedData(data, exp);
        
        Optional<List<DataTrace>> saved = tsHandler.getDataSet(exp, DetrendingType.LIN_DTR);
        assertTrue(saved.isPresent());
        assertEquals(data.size(), saved.get().size());
    }
    
     
    
     
    
     

    

    @Test
    public void getDataWorks() {
        List<DataTrace> data = seeder.getData();
        assertNotNull(data);
        assertEquals(12,data.size());
    }    
    
    
    @Test
    public void getResourceFileWorks() {
        String name = "171.fit.ser";
        Path file = seeder.getResourceFile(name);
        assertTrue(Files.exists(file));
    }
    
    
    @Test    
    @Ignore("It is only to generate test data for the artefacts")
    public void generateTestDataForPPASeeds() throws Exception {
        Path file = Paths.get("/home/dthedie/Temp/ppaTest.csv");
        
        List<TimeSeries> data = new ArrayList<>();
        List<String> heads = new ArrayList<>();
        
        int N = 120;
        double step = 1;
        
        TimeSeries ts = TSGenerator.makeCos(N, step, 25, 1, 1);
        data.add(ts);
        heads.add("Cos");
        
        ts = TSGenerator.makeCos(N, step, 25.5, 1.5, 1.5);
        data.add(ts);
        heads.add("Cos");        
        
        ts = TSGenerator.makeCos(N, step, 26, 2, 2);
        data.add(ts);
        heads.add("Cos");        
        
        ts = TSGenerator.makeGausian(N, step, 28, 2, 12, 4);
        data.add(ts);
        heads.add("Pulse");        
        
        ts = TSGenerator.makeGausian(N, step, 28.2, 2, 12.5, 5);
        data.add(ts);
        heads.add("Pulse");        
        
        ts = TSGenerator.makeGausian(N, step, 27.8, 2, 11.5, 6);
        data.add(ts);
        heads.add("Pulse"); 
        
        ts = TSGenerator.makeCos(N, step, 20, 18, 2);
        ts = TSGenerator.sum(ts, TSGenerator.makeCos(N, step, 28, 18, 0.8));
        data.add(ts);
        heads.add("Complex"); 

        ts = TSGenerator.makeCos(N, step, 20, 18, 2);
        ts = TSGenerator.sum(ts, TSGenerator.makeCos(N, step, 28, 18.5, 0.4));
        data.add(ts);
        heads.add("Complex"); 
        
        ts = TSGenerator.makeCos(N, step, 20, 18, 2);
        ts = TSGenerator.sum(ts, TSGenerator.makeCos(N, step, 28, 19, 0.2));
        data.add(ts);
        heads.add("Complex"); 
        
        ts = TSGenerator.makeLine(N, step, 0.5/N, 1);
        ts = TSGenerator.addWalkingNoise(ts, 0.2, 0L);
        data.add(ts);
        heads.add("Noise"); 
        
        ts = TSGenerator.makeLine(N, step, 0.5/N, 1);
        ts = TSGenerator.addWalkingNoise(ts, 0.4, 1L);
        data.add(ts);
        heads.add("Noise"); 
        
        ts = TSGenerator.makeLine(N, step, 0.5/N, 1);
        ts = TSGenerator.addWalkingNoise(ts, 0.8, 2L);
        data.add(ts);
        heads.add("Noise"); 
        
        TimeSeriesFileHandler.saveToText(data, file.toFile(), ",", heads);
        
        fail();
        
    }
    
}
