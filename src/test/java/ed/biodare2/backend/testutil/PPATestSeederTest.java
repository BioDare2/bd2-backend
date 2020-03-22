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
import ed.biodare2.backend.features.ppa.PPAUtils;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.repo.dao.PPAArtifactsRep;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPASimpleResultEntry;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.jobcenter.JobSummary;
import ed.robust.dom.tsprocessing.ResultsEntry;
import ed.robust.dom.tsprocessing.StatsEntry;
import ed.robust.util.timeseries.TSGenerator;
import ed.robust.util.timeseries.TimeSeriesFileHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext //("Need it to prevent some pprimery key confirlicts with entityacl in integration tests")
@Import(SimpleRepoTestConfig.class)
public class PPATestSeederTest {
    

    
    
    
    @Autowired
    PPATestSeeder seeder;
    
    @Autowired
    PPAArtifactsRep ppaRep; 
    
    @Autowired
    ObjectMapper mapper;    
        
    
    @Autowired
    TSDataHandler tsHandler;
   
    
    @Test
    public void getJobsReadsTheJobs() {
        List<JobSummary> jobs = seeder.getJobs();
        assertEquals(2,jobs.size());
    }
    
    @Test
    //@Ignore("Cause was clashing with restint tests, dont know why")
    //@Transactional
    public void seedJustJobSavesIt() {
        AssayPack exp = seeder.insertExperiment();
        JobSummary job = seeder.getJobs().get(0);
        seeder.seedJustJob(job, exp);
        
        Optional<JobSummary> res = ppaRep.findJob(job.getJobId(), exp);
        assertTrue(res.isPresent());
        assertEquals(job.getSubmitted(),res.get().getSubmitted());
        assertNotNull(job.getSubmitted());
        
        assertTrue(ppaRep.getJobFullDescription(exp, job.getJobId()).isPresent());
        assertTrue(ppaRep.getJobSummary(exp, job.getJobId()).isPresent());
    }
    
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
    //@Ignore("Cause was clashing with restint tests, dont know why")
    //@Transactional
    public void seedingJobArtifactsWorks() {
        AssayPack exp = seeder.insertExperiment();
        JobSummary job = seeder.getJobs().get(0);
        
        seeder.seedJobArtifacts(job, exp);
        
        long jobId = job.getJobId();
        
        List<ResultsEntry> indRes = ppaRep.getJobIndResults(exp, jobId);
        assertEquals(12,indRes.size());
        
        PPAJobSimpleResults simpleRes = ppaRep.getJobSimpleResults(exp,jobId);
        assertEquals(12,simpleRes.results.size());
        
        PPAJobResultsGroups grouped = ppaRep.getJobResultsGroups(exp, jobId);
        assertEquals(4, grouped.groups.size());
        
        PPAJobSimpleStats simpleStats = ppaRep.getJobSimpleStats(exp, jobId);
        assertEquals(4, simpleStats.stats.size());
        
        StatsEntry fullStats = ppaRep.getJobFullStats(exp, jobId);
        assertEquals(4, fullStats.getStats().size());
        
        Map<Long,TimeSeries> fits = ppaRep.getFits(jobId, exp).get();
        assertEquals(12,fits.size());

    }    
    
    
    
    @Test
    public void getJobSimpleStatsWorks() {
        JobSummary job = seeder.getJob();
        PPAJobSimpleStats stats = seeder.getJobSimpleStats(job);
        assertNotNull(stats);
        //assertEquals(job.getJobId(),stats.jobId);
        assertEquals(4,stats.stats.size());
    }
    
    @Test
    public void getJobFullStatsWorks() {
        JobSummary job = seeder.getJob();
        StatsEntry stats = seeder.getJobFullStats(job);
        assertNotNull(stats);
        assertEquals(job.getJobId(),stats.getJobId());
        assertEquals(4,stats.getStats().size());
    }    
    
    @Test
    public void getJobIndResultsWorks() {
        JobSummary job = seeder.getJob();
        List<ResultsEntry> entries = seeder.getJobIndResults(job);
        assertNotNull(entries);
        assertEquals(12,entries.size());
    }
    
    @Test
    public void getJobSimpleResultsWorks() {
        JobSummary job = seeder.getJob();
        PPAJobSimpleResults res = seeder.getJobSimpleResults(job);
        assertNotNull(res);
        //assertEquals(job.getJobId(),res.jobId);
        assertEquals(12,res.results.size());
    }    

    @Test
    public void getJobResultsGroupsWorks() {
        JobSummary job = seeder.getJob();
        PPAJobResultsGroups groups = seeder.getJobResultsGroups(job);
        assertNotNull(groups);
        //assertEquals(job.getJobId(),groups.jobId);
        assertEquals(4,groups.groups.size());
    }
    
    @Test
    public void getFitsWorks() {
        JobSummary job = seeder.getJob();
        Map<Long, TimeSeries> fits = seeder.getFits(job);
        assertNotNull(fits);
        assertEquals(12,fits.size());
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
    
    //@Test
    @Ignore
    public void createSimpleResults() {

        Path workDir = Paths.get("E:/Temp");
        
        seeder.getJobs().forEach( job -> {
        
            Path file = workDir.resolve(job.getJobId()+".PPA_SIMPLE_RESULTS.json");
            List<ResultsEntry> entries = seeder.getJobIndResults(job);
            
            PPAJobSimpleResults res = new PPAJobSimpleResults(""+job.getJobId());
        
            final double windowStart = job.getDataWindowStart();        
            entries.forEach( entry -> {
                PPASimpleResultEntry simple = PPAUtils.simplifyResultsEntry(entry,windowStart);
                res.results.add(simple);
            });        
            try {
                mapper.writeValue(file.toFile(), res);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }            
            
        });
    
    }
    
    @Test    
    @Ignore("It is only to generate test data for the artefacts")
    public void generateTestDataForPPASeeds() throws Exception {
        Path file = Paths.get("E:/Temp/ppaTest.csv");
        
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
