/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary;
import ed.robust.dom.tsprocessing.StatsEntry;
import ed.robust.error.RobustFormatException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
/**
 *
 * @author tzielins
 */
public class PPATestSeederJC2Test {
    
    PPATestSeederJC2 seeder;
    
    public static ObjectMapper makeMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();  
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return mapper;
    }
    
    public PPATestSeederJC2Test() {
        seeder = new PPATestSeederJC2();
    }
    
    /*public PPATestSeederJC2(ObjectMapper mapper) {
        this.mapper = mapper;
    }*/
    
    @Test
    public void testFiles() {
        
        Path f = seeder.getJobFile(seeder.fftJob, "PPA_JOB_SUMMARY.json");
        assertTrue(Files.isRegularFile(f));
        
    }
    
    @Test
    public void testJobSummary() throws IOException {
        assertNotNull(seeder.getJobSummary());
    }
    
    @Test
    public void testJobSimpleResults() throws IOException {
        assertNotNull(seeder.getJobSimpleResults(seeder.getJobSummary()));
        
    }
    
    @Test
    public void testJobSimpleStats() throws IOException {
        assertNotNull(seeder.getJobSimpleStats(seeder.getJobSummary()));
        
    } 
    
    @Test
    public void testJobFullStats() throws IOException {
        assertEquals(5, seeder.getJobFullStats(seeder.getJobSummary()).getStats().size());
        
    }    
    
    @Test
    public void testJobFullResults() throws IOException {
        assertEquals(10, seeder.getJobFullResults(seeder.getJobSummary()).results.size());
        
    }     
    
    @Test
    public void testJobResultsGroups() throws IOException {
        assertEquals(5, seeder.getJobResultsGroups(seeder.getJobSummary()).groups.size());
        
    }     
    
    
    @Test
    public void testGetData() throws RobustFormatException, IOException {
        assertEquals(10, seeder.getData().size());
    }
    
    @Test
    public void testGetFits() throws RobustFormatException, IOException {
        assertEquals(10, seeder.getFits(seeder.getJobSummary()).size());
    }    
    
    @Test
    public void testFullStatsCanBeSerializedToJSONAndBack() throws IOException {
        PPAJobSummary job = seeder.getJobSummary();
        StatsEntry stats1 = seeder.getJobFullXMLStats(seeder.job2name(job));
        StatsEntry stats2 = seeder.getJobFullXMLStats(seeder.job2name(job));
        
        assertReflectionEquals(stats1, stats2);
        
        String json = seeder.mapper.writeValueAsString(stats1);
        stats2 = seeder.mapper.readValue(json, StatsEntry.class);
        //System.out.println(json);
        String json2 = seeder.mapper.writeValueAsString(stats2);
        assertEquals(json, json2);
        
        assertReflectionEquals(stats1, stats2);
        
    }    

    
}
