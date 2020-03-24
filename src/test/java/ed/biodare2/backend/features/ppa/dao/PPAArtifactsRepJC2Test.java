/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare2.backend.features.ppa.dao.PPAArtifactsRepJC2.ExpJobKey;
import static ed.biodare2.backend.features.ppa.dao.PPAArtifactsRepJC2.JOB_SIMPLE_SUMMARY_FILE;
import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAFullResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobIndResults;
import static ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobIndResultsTest.makePPAJobIndResults;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummaryTest;
import static ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAResultsGroupSummaryTest.makePPAReplicateSet;
import static ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleResultEntryTest.makePPASimpleResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleStats;
import static ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleStatsTest.makeSimpleStats;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.MockExperimentPack;
import ed.biodare2.backend.testutil.PPATestSeeder;
import ed.robust.dom.data.TimeSeries;

import ed.robust.dom.tsprocessing.PPAStats;
import ed.robust.dom.tsprocessing.StatsEntry;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 *
 * @author tzielins
 */
public class PPAArtifactsRepJC2Test {
    

    static double EPS = 1E-6;
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    ExperimentsStorage expStorage;
    PPAArtifactsRepJC2 ppaRep;
    Path expDir;
    PPATestSeeder seeder;
    
    
    @Before
    public void setUp() throws IOException {
        
        expDir = testFolder.newFolder().toPath();
        expStorage = mock(ExperimentsStorage.class);
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        
        seeder = new PPATestSeeder();
        ppaRep = new PPAArtifactsRepJC2(expStorage, mapper);
        
    } 
    
    @Test
    public void clearAllPPAArtefactsDeletesJobsFolder() throws IOException {
        
        AssayPack exp = new MockExperimentPack(1);
        Path ppaDir = expDir.resolve(PPAArtifactsRepJC2.PPA_DIR);        
        
        assertFalse(Files.exists(ppaDir));

        Path jobs = ppaDir.resolve(PPAArtifactsRepJC2.JOBS_DIR);
        Path job = jobs.resolve("12");
        
        Files.createDirectories(job);

        assertTrue(Files.exists(ppaDir));
        assertTrue(Files.exists(jobs));
        assertTrue(Files.exists(job));
        
        ppaRep.clearAllPPAArtefacts(exp);
        
        assertTrue(Files.exists(ppaDir));
        assertFalse(Files.exists(jobs));
        assertFalse(Files.exists(job));
                
        
    }
    

    @Test
    public void clearAllPPAArtefactsInvalidatesJobsCache() throws IOException {
        
        AssayPack exp = new MockExperimentPack(1);

        PPAJobSummary job = PPAJobSummaryTest.makePPAJobSummary();
        
        job.parentId = exp.getId();
        ppaRep.saveJobSummary(job, exp);
        
        Optional<PPAJobSummary> cpy1 = ppaRep.getJobSummary(exp, job.jobId);
        assertTrue(cpy1.isPresent());
        
        ppaRep.clearAllPPAArtefacts(exp);
        
        Optional<PPAJobSummary> cpy2 = ppaRep.getJobSummary(exp, job.jobId);
        assertFalse(cpy2.isPresent());
        assertNotSame(cpy1,cpy2);
                
        assertTrue(ppaRep.getJobsSummaries(exp).isEmpty());
        
    }
    

    
    
    @Test
    public void saveFitsSaveFitsUnderJobDir() throws Exception {
        Map<Long, TimeSeries> fits = new HashMap<>();
        fits.put(2L,new TimeSeries());
        
        UUID jobId = UUID.randomUUID();
        AssayPack exp = new MockExperimentPack(4);
        
        ppaRep.saveFits(fits, jobId, exp);
        
        //Path ppaDir = expDir.resolve(PPAArtifactsRep.PPA_DIR);
        Path jobDir = ppaRep.getJobDir(exp.getId(), jobId);
        assertTrue(Files.isDirectory(jobDir));
        
        Path fitFile = jobDir.resolve("fit."+jobId+".ser");
        assertTrue(Files.isRegularFile(fitFile));
    }
    
    @Test
    public void saveFitsSavesFitsThatCanBeRead() throws Exception {
         Map<Long, TimeSeries> fits = new HashMap<>();
        fits.put(2L,new TimeSeries());
        
        UUID jobId = UUID.randomUUID();
        AssayPack exp = new MockExperimentPack(4);
        
        ppaRep.saveFits(fits, jobId, exp);
        
        Optional<Map<Long, TimeSeries>> res = ppaRep.getFits(jobId, exp);
        assertTrue(res.isPresent());
        
        assertNotNull(res.get().get(2L));
    }
    
    
    @Test
    public void jobDirKeyGivesDifferentHashForLargeLongs() {
        
        Set<Integer> hashes = new HashSet<>();
        Set<ExpJobKey> keys = new HashSet<>();
        
        int count = 0;
        
        UUID jobId = UUID.randomUUID();
        for (long expId = Integer.MAX_VALUE-2; expId < Integer.MAX_VALUE+3;expId++) {
                count++;
                ExpJobKey key = new ExpJobKey(expId,jobId);
                keys.add(key);
                hashes.add(key.hashCode());
        }
        
        assertEquals(count,keys.size());
        assertEquals(count,hashes.size());
    }

    @Test
    public void getPPADirCreatesTheDirIfMissing() throws Exception {

        Path exp = expDir.resolve(PPAArtifactsRepJC2.PPA_DIR);
        assertFalse(Files.isDirectory(exp));
        
        Path resp = ppaRep.getPPADir(8);
        assertEquals(exp,resp);
        assertTrue(Files.isDirectory(exp));        
    }
    
    @Test
    public void getJobDirGetsSubfolderOfJobs() throws Exception {
        Path ppa = expDir.resolve(PPAArtifactsRepJC2.PPA_DIR);
        UUID jobId = UUID.randomUUID();
        
        Path exp = ppa.resolve("JOBS").resolve(jobId.toString());
        Path resp = ppaRep.getJobDir(3, jobId);
        assertEquals(exp,resp);
        
    }
    
    
    @Test
    public void getJobDirCreatesTheFolderIfMissing() throws Exception {
        Path ppa = expDir.resolve(PPAArtifactsRepJC2.PPA_DIR);
        UUID jobId = UUID.randomUUID();
        
        Path resp = ppaRep.getJobDir(4, jobId);
        assertTrue(Files.isDirectory(resp));
    }
    
    @Test
    public void getJobDirCachesTheValue() throws Exception {
        Path ppa = expDir.resolve(PPAArtifactsRepJC2.PPA_DIR);
        UUID jobId = UUID.randomUUID();
        
        //Path exp = ppa.resolve("JOBS/12");
        Path resp1 = ppaRep.getJobDir(5, jobId);
        Path resp2 = ppaRep.getJobDir(5, jobId);
        assertSame(resp1,resp2);
        
    }
    
    
    
    @Test
    public void jobGroupedResultsFileGivesCorrectFileInJobFolder()  throws Exception {

        Path ppa = expDir.resolve(PPAArtifactsRepJC2.PPA_DIR);
        UUID jobId = UUID.randomUUID();
        Path exp = ppa.resolve("JOBS").resolve(jobId.toString()).resolve(ppaRep.JOB_GROUPED_RESULTS_FILE);
        Path resp = ppaRep.jobGroupedResultsFile(6, jobId);
        assertEquals(exp,resp);
        
                
    }
    
    @Test
    public void canSaveAndRetrieveJobGrouppedResults() {
        UUID jobId = UUID.randomUUID();
        PPAJobResultsGroups results = new PPAJobResultsGroups(jobId);
        results.groups.add(makePPAReplicateSet("cos1"));
        results.groups.add(makePPAReplicateSet("cos2"));
        
        AssayPack exp = new MockExperimentPack(1);
        
        ppaRep.saveJobResultsGroups(results, exp, jobId);
        
        
        PPAJobResultsGroups cpy = ppaRep.getJobResultsGroups(exp, jobId);
        assertEquals(cpy.jobId,jobId);
        assertReflectionEquals(results,cpy); 
        
    }
    
    @Test
    public void canSaveAndRetrieveJobSimpleStats() {
        UUID jobId = UUID.randomUUID();
        PPAJobSimpleStats stats = new PPAJobSimpleStats(jobId);
        
        PPASimpleStats stat = makeSimpleStats();
        stats.stats.add(stat);

        stat = makeSimpleStats();
        stat.memberDataId++;
        stats.stats.add(stat);
        
        assertNotNull(stats);
        
        AssayPack exp = new MockExperimentPack(1);
        
        ppaRep.saveJobSimpleStats(stats, exp, jobId);
        
        
        PPAJobSimpleStats cpy = ppaRep.getJobSimpleStats(exp, jobId);
        assertEquals(cpy.jobId,jobId);
        assertReflectionEquals(stats,cpy); 
        
    }

    
    @Test
    public void canSaveAndRetrieveJobFullStats() {
        
        AssayPack exp = new MockExperimentPack(1);
        UUID jobId = UUID.randomUUID();

        StatsEntry stats = new StatsEntry();
        stats.setUuid(jobId);
        PPAStats ppaStat = new PPAStats();
        stats.add(ppaStat);
        
        
        ppaRep.saveJobFullStats(stats, exp, jobId);
        
        StatsEntry cpy = ppaRep.getJobFullStats(exp, jobId);
        assertEquals(jobId,cpy.getUuid());
        assertReflectionEquals(stats,cpy); 
        
        
    }
    
    @Test
    public void canSaveAndRetrieveJobSimpleResults() {
        
        UUID jobId = UUID.randomUUID();
        PPAJobSimpleResults org = new PPAJobSimpleResults(jobId);
        org.results.add(makePPASimpleResultEntry());        
        AssayPack exp = new MockExperimentPack(1);
        
        ppaRep.saveJobSimpleResults(org, exp, jobId);
        
        
        PPAJobSimpleResults cpy = ppaRep.getJobSimpleResults(exp, jobId);
        assertEquals(cpy.jobId,jobId);
        assertReflectionEquals(org,cpy); 
        
    }
    
    
    @Test
    public void savesAndRetrieveJobSimpleSummary() {
        AssayPack exp = new MockExperimentPack(1);
        PPAJobSummary job = PPAJobSummaryTest.makePPAJobSummary();
        
        job.parentId = exp.getId();
        ppaRep.saveJobSummary(job, exp);
        
        Optional<PPAJobSummary> cpy = ppaRep.getJobSummary(exp, job.jobId);
        assertReflectionEquals(job,cpy.get()); 
        
        Path file = expDir.resolve("PPA3/JOBS").resolve(""+job.jobId).resolve(JOB_SIMPLE_SUMMARY_FILE);
        assertTrue(Files.exists(file));
    }
    
    @Test
    public void getJobSummaryUsesTheCache() {
        AssayPack exp = new MockExperimentPack(1);
        PPAJobSummary job = PPAJobSummaryTest.makePPAJobSummary();
        
        job.parentId = exp.getId();
        ppaRep.saveJobSummary(job, exp);
        
        Optional<PPAJobSummary> cpy1 = ppaRep.getJobSummary(exp, job.jobId);
        Optional<PPAJobSummary> cpy2 = ppaRep.getJobSummary(exp, job.jobId);

        assertSame(cpy1,cpy2);
    }
    
    @Test
    public void savingJobSummaryUpdatesTheCache() {
        AssayPack exp = new MockExperimentPack(1);
        PPAJobSummary job = PPAJobSummaryTest.makePPAJobSummary();
        job.parentId = exp.getId();
        
        Optional<PPAJobSummary> cpy1 = ppaRep.getJobSummary(exp, job.jobId);
        ppaRep.saveJobSummary(job, exp);
        
        Optional<PPAJobSummary> cpy2 = ppaRep.getJobSummary(exp, job.jobId);
        assertSame(job,cpy2.get());
        assertNotSame(cpy1, cpy2);
    }
    
    
    
    @Test
    public void savesAndRetrieveJobSimpleSummaryOutsideCache() {
        AssayPack exp = new MockExperimentPack(1);
        PPAJobSummary job = PPAJobSummaryTest.makePPAJobSummary();

        job.parentId = exp.getId();
        ppaRep.saveJobSummary(job, exp);
        
        Optional<PPAJobSummary> cpy = ppaRep.getJobSummary(new ExpJobKey(exp.getId(), job.jobId));
        assertReflectionEquals(job,cpy.get()); 
        
        Path file = expDir.resolve("PPA3/JOBS").resolve(""+job.jobId).resolve(JOB_SIMPLE_SUMMARY_FILE);
        assertTrue(Files.exists(file));
    }    
    

    
    
    
    @Test
    public void deleteJobDirDeletesJobSubfolderAndItsContent() throws Exception {
        
        AssayPack exp = new MockExperimentPack(1);
        Path ppaDir = expDir.resolve(PPAArtifactsRepJC2.PPA_DIR);        

        UUID jobId = UUID.randomUUID();
        Path jobDir = ppaDir.resolve(PPAArtifactsRepJC2.JOBS_DIR).resolve(jobId.toString());
        Files.createDirectories(jobDir);
        
        Path file = jobDir.resolve("cos.xml");
        Files.createFile(file);
        
        ppaRep.deleteJobDir(jobId, exp.getId());
        
        assertFalse(Files.exists(file));
        assertFalse(Files.exists(jobDir));
    }
    
    @Test
    public void getJobSummariesGivesEmptyIfNotPresent() {
        AssayPack exp = new MockExperimentPack(1);
        List<PPAJobSummary> jobs = ppaRep.getJobsSummaries(exp);
        assertTrue(jobs.isEmpty());
    }
    
    @Test
    public void getJobSummariesGivesSummariesOrderedBySubmisionDataDescBasedOnlyOnSummaryFiles() throws Exception {
        AssayPack exp = new MockExperimentPack(1);
        PPAJobSummary s1 = new PPAJobSummary();
        s1.jobId = UUID.randomUUID();
        s1.parentId = exp.getId();
        s1.state = State.FAILED;
        s1.submitted = LocalDateTime.now().minusHours(3);
        
        PPAJobSummary s2 = new PPAJobSummary();
        s2.jobId = UUID.randomUUID();
        s2.parentId = exp.getId();
        s2.state = State.FINISHED;
        s2.submitted = LocalDateTime.now().minusHours(2);
        
        ppaRep.saveJobSummary(s1, exp);
        ppaRep.saveJobSummary(s2, exp);
        
        Path bogus = expDir.resolve("PPA/JOBS/4");
        Files.createDirectories(bogus);
        
        List<PPAJobSummary> jobs = ppaRep.getJobsSummaries(exp);
        assertEquals(2,jobs.size());
        assertReflectionEquals(s2,jobs.get(0)); 
        
    }
    
    @Test
    public void getJobSummariesUsesJobsCache() throws Exception {
        AssayPack exp = new MockExperimentPack(1);
        PPAJobSummary s1 = new PPAJobSummary();
        s1.jobId = UUID.randomUUID();
        s1.parentId = exp.getId();
        s1.state = State.FAILED;
        s1.submitted = LocalDateTime.now().minusHours(3);
        
        PPAJobSummary s2 = new PPAJobSummary();
        s2.jobId = UUID.randomUUID();
        s2.parentId = exp.getId();
        s2.state = State.FINISHED;
        s2.submitted = LocalDateTime.now().minusHours(2);
        
        ppaRep.saveJobSummary(s1, exp);
        ppaRep.saveJobSummary(s2, exp);
        
        Path bogus = expDir.resolve("PPA/JOBS/4");
        Files.createDirectories(bogus);
        
        ppaRep.clearCaches();
        
        List<PPAJobSummary> jobs1 = ppaRep.getJobsSummaries(exp);
        List<PPAJobSummary> jobs2 = ppaRep.getJobsSummaries(exp);
        
        for (int i =0;i<jobs1.size();i++) {
            assertSame("Jobs at:"+i+" not same",jobs1.get(i),jobs2.get(i));
        }
        
        UUID s2Id = s2.jobId;
        s2 = new PPAJobSummary();
        s2.jobId = s2Id;
        s2.parentId = exp.getId();
        s2.state = State.FINISHED;
        s2.submitted = LocalDateTime.now().minusHours(2);

        ppaRep.saveJobSummary(s2, exp);
        
        jobs2 = ppaRep.getJobsSummaries(exp);
        assertNotSame(jobs1.get(0),jobs2.get(0));
        assertSame(jobs1.get(1),jobs2.get(1));        
        
        jobs1 = ppaRep.getJobsSummaries(exp);
        ppaRep.clearCaches();        
        jobs2 = ppaRep.getJobsSummaries(exp);
        
        for (int i =0;i<jobs1.size();i++) {
            assertNotSame("Jobs at:"+i+" should not be same",jobs1.get(i),jobs2.get(i));
        }
        
    }    
    
    @Test
    public void deleteJobArtifactsDeletesAllParts() {
        
        
        AssayPack exp = new MockExperimentPack(1);
        
        PPAJobSummary job = PPAJobSummaryTest.makePPAJobSummary();        
        job.parentId = exp.getId();
        ppaRep.saveJobSummary(job, exp);
        
        UUID jobId = job.jobId;
        
        //fits
        Map<Long, TimeSeries> fits = new HashMap<>();
        fits.put(2L,new TimeSeries());
        ppaRep.saveFits(fits, jobId, exp);        
        assertTrue(ppaRep.getFits(jobId, exp).isPresent());
        
        
        //stats
        StatsEntry stats = new StatsEntry();
        stats.setUuid(jobId);
        PPAStats ppaStat = new PPAStats();
        stats.add(ppaStat);
        ppaRep.saveJobFullStats(stats, exp,jobId);
        assertEquals(jobId,ppaRep.getJobFullStats(exp, jobId).getUuid());
        assertEquals(1,ppaRep.getJobFullStats(exp, jobId).getStats().size());
        
        //job Results
        PPAJobResultsGroups results = new PPAJobResultsGroups(jobId);
        results.periodMax = 5;
        ppaRep.saveJobResultsGroups(results, exp, jobId);
        assertEquals(5,ppaRep.getJobResultsGroups(exp, jobId).periodMax,EPS);
        
        //job Stats
        PPAJobSimpleStats jStats = new PPAJobSimpleStats(jobId);
        jStats.stats.add(new PPASimpleStats());
        ppaRep.saveJobSimpleStats(jStats, exp, jobId);
        assertEquals(1,ppaRep.getJobSimpleStats(exp, jobId).stats.size());
        
        ppaRep.deleteJobArtefacts(exp,job.jobId);
        
        //check that don't exists or came back as defaults.
        assertFalse(ppaRep.getJobSummary(exp, jobId).isPresent());
        assertFalse(ppaRep.getFits(jobId, exp).isPresent());
        assertEquals(0,ppaRep.getJobResultsGroups(exp, jobId).periodMax,EPS);
        assertEquals(0,ppaRep.getJobSimpleStats(exp, jobId).stats.size());
    }
    
    @Test
    public void deleteJobArtifactsRemovesJobFromCache() {
        
        
        AssayPack exp = new MockExperimentPack(1);
        
        PPAJobSummary job = PPAJobSummaryTest.makePPAJobSummary();
        
        job.parentId = exp.getId();
        ppaRep.saveJobSummary(job, exp);
        
        Optional<PPAJobSummary> cpy1 = ppaRep.getJobSummary(exp, job.jobId);
        assertTrue(cpy1.isPresent());
        
        ppaRep.deleteJobArtefacts(exp,job.jobId);
        
        Optional<PPAJobSummary> cpy2 = ppaRep.getJobSummary(exp, job.jobId);
        assertFalse(cpy2.isPresent());
        assertNotSame(cpy1,cpy2);
        
        assertTrue(ppaRep.getJobsSummaries(exp).isEmpty());
        
    }
    
    
    @Test
    public void jobIndResultsFileGivesCorrectFileInJobFolder()  throws Exception {

        Path ppa = expDir.resolve(PPAArtifactsRepJC2.PPA_DIR);
        UUID jobId = UUID.randomUUID();
        Path exp = ppa.resolve("JOBS").resolve(jobId.toString()).resolve(ppaRep.JOB_FULL_RESULTS_FILE);
        Path resp = ppaRep.jobIndResultsFile(7, jobId);
        assertEquals(exp,resp);
        
                
    }
    
    @Test
    public void canSaveAndReadBackJobIndResults() {
        AssayPack exp = new MockExperimentPack(1);
        UUID jobId = UUID.randomUUID();
        
        PPAJobIndResults results = makePPAJobIndResults(jobId);

        ppaRep.saveJobIndResults(results.results, exp, jobId);
        
        List<PPAFullResultEntry> read = ppaRep.getJobIndResults(exp, jobId);
        assertNotNull(read);
        assertEquals(results.results.size(),read.size());
        
        assertEquals(results.results.get(0).result.getPeriod(),read.get(0).result.getPeriod(),EPS);
        assertEquals(results.results.get(1).result.getPeriod(),read.get(1).result.getPeriod(),EPS);

        assertEquals(1,read.get(0).dataId);
        assertEquals(2,read.get(1).dataId);
        
    }
    
    
}
