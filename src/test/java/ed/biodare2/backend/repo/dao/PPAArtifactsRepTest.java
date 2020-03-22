/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import static ed.biodare2.backend.repo.dao.PPAArtifactsRep.JOB_SIMPLE_SUMMARY_FILE;
import ed.biodare2.backend.repo.dao.PPAArtifactsRep.ExpJobKey;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobIndResults;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.MockExperimentPack;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummaryTest;
import static ed.biodare2.backend.repo.isa_dom.ppa2.PPAResultsGroupSummaryTest.makePPAReplicateSet;
import static ed.biodare2.backend.repo.isa_dom.ppa2.PPASimpleResultEntryTest.makePPASimpleResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPASimpleStats;
import static ed.biodare2.backend.repo.isa_dom.ppa2.PPASimpleStatsTest.makeSimpleStats;
import ed.biodare2.backend.testutil.PPATestSeeder;
import ed.robust.dom.data.TimeSeries;

import ed.robust.dom.jobcenter.JobSummary;
import ed.robust.dom.param.Parameters;
import ed.robust.dom.tsprocessing.FFT_PPA;
import ed.robust.dom.tsprocessing.GenericPPAResult;
import ed.robust.dom.tsprocessing.MESA_PPA;
import ed.robust.dom.tsprocessing.PPA;
import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.dom.tsprocessing.PPAStats;
import ed.robust.dom.tsprocessing.ResultsEntry;
import ed.robust.dom.tsprocessing.StatsEntry;
import ed.robust.dom.tsprocessing.WeightingType;
import ed.robust.jobcenter.dom.job.JobHandle;
import ed.robust.jobcenter.dom.job.JobRequest;
import ed.robust.jobcenter.dom.job.JobResult;
import ed.robust.jobcenter.dom.job.TaskResult;
import ed.robust.jobcenter.dom.state.State;
import ed.robust.ppa.PPAMethod;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
public class PPAArtifactsRepTest {
    

    static double EPS = 1E-6;
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    ExperimentsStorage expStorage;
    PPAArtifactsRep ppaRep;
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
        ppaRep = new PPAArtifactsRep(expStorage, mapper);
        
    } 
    
    @Test
    public void clearAllPPAArtefactsDeletesJobsFolder() throws IOException {
        
        AssayPack exp = new MockExperimentPack(1);
        Path ppaDir = expDir.resolve(PPAArtifactsRep.PPA_DIR);        
        
        assertFalse(Files.exists(ppaDir));

        Path jobs = ppaDir.resolve(PPAArtifactsRep.JOBS_DIR);
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
    public void saveRequestPutsRequestContentInTheFileUnderJob() throws IOException {
        
        AssayPack exp = new MockExperimentPack(1);
        //ppaRep.createContainers(exp);
        
        //Path reqDir = expDir.resolve(PPAArtifactsRep.PPA_DIR).resolve(PPAArtifactsRep.REQUESTS_DIR);
        
        JobRequest request = new JobRequest();
        request.setMethod(PPAMethod.NLLS.name());
        request.setExternalId(""+exp.getId());
        request.setParams(new Parameters());
        Path reqFile = ppaRep.saveJobRequest(request, 123, exp);
        assertTrue(Files.isRegularFile(reqFile));
        assertTrue(Files.size(reqFile) > 0);
        assertEquals("req.123.xml",reqFile.getFileName().toString());
        
        Path parent = reqFile.getParent();
        assertEquals("123",parent.getFileName().toString());
        assertEquals("JOBS",parent.getParent().getFileName().toString());
    }
    
    
    @Test
    public void saveFitsSaveFitsUnderJobDir() throws Exception {
        Map<Long, TimeSeries> fits = new HashMap<>();
        fits.put(2L,new TimeSeries());
        
        long jobId = 3;
        AssayPack exp = new MockExperimentPack(4);
        
        ppaRep.saveFits(fits, jobId, exp);
        
        //Path ppaDir = expDir.resolve(PPAArtifactsRep.PPA_DIR);
        Path jobDir = ppaRep.getJobDir(exp, jobId);
        assertTrue(Files.isDirectory(jobDir));
        
        Path fitFile = jobDir.resolve("fit.3.ser");
        assertTrue(Files.isRegularFile(fitFile));
    }
    
    @Test
    public void saveFitsSavesFitsThatCanBeRead() throws Exception {
         Map<Long, TimeSeries> fits = new HashMap<>();
        fits.put(2L,new TimeSeries());
        
        long jobId = 3;
        AssayPack exp = new MockExperimentPack(4);
        
        ppaRep.saveFits(fits, jobId, exp);
        
        Optional<Map<Long, TimeSeries>> res = ppaRep.getFits(jobId, exp);
        assertTrue(res.isPresent());
        
        assertNotNull(res.get().get(2L));
    }
    
    @Test
    public void saveJobRawResultsSavesThemToTheFileInJobDir() throws IOException {
        
        
        AssayPack exp = new MockExperimentPack(1);
        
        //Path reqDir = expDir.resolve(PPAArtifactsRep.PPA_DIR).resolve(PPAArtifactsRep.REQUESTS_DIR);
        JobResult<PPAResult> jRes = new JobResult<>(123,State.SUCCESS,"OK");
        
        PPAResult pRes = new MESA_PPA(new PPA(24, 1, 2));
        //pRes.addCOS(2, 0, 24, 0, 1, 0);
        
        TaskResult<PPAResult> tRes = new TaskResult(1L, pRes);
        jRes.addResult(tRes);
        
        
        Path file = ppaRep.saveJobRawResults(jRes, 23L, exp, false);
        assertTrue(Files.isRegularFile(file));
        assertTrue(Files.size(file) > 0);  
        
        Path parent = file.getParent();
        assertEquals("23",parent.getFileName().toString());
        assertEquals("JOBS",parent.getParent().getFileName().toString());
        
    }
    
    @Test
    public void JobDirKeyGivesDifferentHashForLargeLongs() {
        
        Set<Integer> hashes = new HashSet<>();
        Set<ExpJobKey> keys = new HashSet<>();
        
        int count = 0;
        
        for (long expId = Integer.MAX_VALUE-2; expId < Integer.MAX_VALUE+3;expId++) {
            for (long jobId = Integer.MAX_VALUE-2; jobId< Integer.MAX_VALUE+3;jobId++ ) {
                count++;
                ExpJobKey key = new ExpJobKey(expId,""+jobId);
                keys.add(key);
                hashes.add(key.hashCode());
            }
        }
        
        assertEquals(count,keys.size());
        assertEquals(count,hashes.size());
    }

    @Test
    public void getPPADirCreatesTheDirIfMissing() throws Exception {

        Path exp = expDir.resolve(PPAArtifactsRep.PPA_DIR);
        assertFalse(Files.isDirectory(exp));
        
        Path resp = ppaRep.getPPADir(8);
        assertEquals(exp,resp);
        assertTrue(Files.isDirectory(exp));        
    }
    
    @Test
    public void getJobDirGetsSubfolderOfJobs() throws Exception {
        Path ppa = expDir.resolve(PPAArtifactsRep.PPA_DIR);
        long jobId = 12;
        
        Path exp = ppa.resolve("JOBS/12");
        Path resp = ppaRep.getJobDir(3, jobId);
        assertEquals(exp,resp);
        
    }
    
    
    @Test
    public void getJobDirCachesTheValue() throws Exception {
        Path ppa = expDir.resolve(PPAArtifactsRep.PPA_DIR);
        long jobId = 12;
        
        Path resp = ppaRep.getJobDir(4, jobId);
        assertTrue(Files.isDirectory(resp));
    }
    
    @Test
    public void getJobDirCreatesTheFolderIfMissing() throws Exception {
        Path ppa = expDir.resolve(PPAArtifactsRep.PPA_DIR);
        long jobId = 13;
        
        //Path exp = ppa.resolve("JOBS/12");
        Path resp1 = ppaRep.getJobDir(5, jobId);
        Path resp2 = ppaRep.getJobDir(5, jobId);
        assertSame(resp1,resp2);
        
    }
    
    
    
    @Test
    public void jobGroupedResultsFileGivesCorrectFileInJobFolder()  throws Exception {

        Path ppa = expDir.resolve(PPAArtifactsRep.PPA_DIR);
        long jobId = 13;
        Path exp = ppa.resolve("JOBS/13").resolve(ppaRep.JOB_GROUPED_RESULTS_FILE);
        Path resp = ppaRep.jobGroupedResultsFile(6, jobId);
        assertEquals(exp,resp);
        
                
    }
    
    @Test
    public void canSaveAndRetrieveJobGrouppedResults() {
        PPAJobResultsGroups results = new PPAJobResultsGroups();
        results.groups.add(makePPAReplicateSet("cos1"));
        results.groups.add(makePPAReplicateSet("cos2"));
        
        AssayPack exp = new MockExperimentPack(1);
        long jobId = 20;
        
        ppaRep.saveJobResultsGroups(results, exp, jobId);
        
        
        PPAJobResultsGroups cpy = ppaRep.getJobResultsGroups(exp, jobId);
        assertEquals(cpy.jobId,20);
        assertReflectionEquals(results,cpy); 
        
    }
    
    @Test
    public void canSaveAndRetrieveJobSimpleStats() {
        
        PPAJobSimpleStats stats = new PPAJobSimpleStats(2);
        
        PPASimpleStats stat = makeSimpleStats();
        stats.stats.add(stat);

        stat = makeSimpleStats();
        stat.memberDataId++;
        stats.stats.add(stat);
        
        assertNotNull(stats);
        
        AssayPack exp = new MockExperimentPack(1);
        long jobId = 20;
        
        ppaRep.saveJobSimpleStats(stats, exp, jobId);
        
        
        PPAJobSimpleStats cpy = ppaRep.getJobSimpleStats(exp, jobId);
        assertEquals(cpy.jobId,20);
        assertReflectionEquals(stats,cpy); 
        
    }

    
    @Test
    public void canSaveAndRetrieveJobFullStats() {
        
        AssayPack exp = new MockExperimentPack(1);
        long jobId = 20;

        StatsEntry stats = new StatsEntry();
        stats.setJobId(jobId);
        PPAStats ppaStat = new PPAStats();
        stats.add(ppaStat);
        
        
        ppaRep.saveJobFullStats(stats, exp, jobId);
        
        StatsEntry cpy = ppaRep.getJobFullStats(exp, jobId);
        assertEquals(jobId,cpy.getJobId());
        assertReflectionEquals(stats,cpy); 
        
        
    }
    
    @Test
    public void canSaveAndRetrieveJobSimpleResults() {
        
        PPAJobSimpleResults org = new PPAJobSimpleResults(3);
        org.results.add(makePPASimpleResultEntry());        
        AssayPack exp = new MockExperimentPack(1);
        long jobId = 20;
        
        ppaRep.saveJobSimpleResults(org, exp, jobId);
        
        
        PPAJobSimpleResults cpy = ppaRep.getJobSimpleResults(exp, jobId);
        assertEquals(cpy.jobId,20);
        assertReflectionEquals(org,cpy); 
        
    }
    
    
    @Test
    public void savesAndRetrieveJobSimpleSummary() {
        AssayPack exp = new MockExperimentPack(1);
        PPAJobSummary job = PPAJobSummaryTest.makePPAJobSummary();
        
        ppaRep.saveJobSummary(job, exp);
        
        Optional<PPAJobSummary> cpy = ppaRep.getJobSummary(exp, job.jobId);
        assertReflectionEquals(job,cpy.get()); 
        
        Path file = expDir.resolve("PPA/JOBS").resolve(""+job.jobId).resolve(JOB_SIMPLE_SUMMARY_FILE);
        assertTrue(Files.exists(file));
    }
    
    @Test
    public void getJobSummaryUsesTheCache() {
        AssayPack exp = new MockExperimentPack(1);
        PPAJobSummary job = PPAJobSummaryTest.makePPAJobSummary();
        
        ppaRep.saveJobSummary(job, exp);
        
        Optional<PPAJobSummary> cpy1 = ppaRep.getJobSummary(exp, job.jobId);
        Optional<PPAJobSummary> cpy2 = ppaRep.getJobSummary(exp, job.jobId);

        assertSame(cpy1,cpy2);
    }
    
    @Test
    public void savingJobSummaryUpdatesTheCache() {
        AssayPack exp = new MockExperimentPack(1);
        PPAJobSummary job = PPAJobSummaryTest.makePPAJobSummary();
        
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
        
        ppaRep.saveJobSummary(job, exp);
        
        Optional<PPAJobSummary> cpy = ppaRep.getJobSummary(new ExpJobKey(exp.getId(), job.id));
        assertReflectionEquals(job,cpy.get()); 
        
        Path file = expDir.resolve("PPA/JOBS").resolve(""+job.jobId).resolve(JOB_SIMPLE_SUMMARY_FILE);
        assertTrue(Files.exists(file));
    }    
    
    @Test
    public void canSaveAndReadJobFullDescToTheFile() throws IOException {
        
        AssayPack exp = new MockExperimentPack(1);
        
        Path ppaDir = expDir.resolve(PPAArtifactsRep.PPA_DIR);    

        
        JobSummary job = new JobSummary();
        job.setJob(new JobHandle(1234));
        
        job.setParams(new Parameters());
        
        Path file = ppaDir.resolve("JOBS").resolve(""+job.getJobId()).resolve(ppaRep.JOB_FULL_FILE);
        assertFalse(Files.exists(file));
        ppaRep.saveJobFullDescription(job, exp);
                
        assertTrue(Files.exists(file));

        JobSummary cpy = ppaRep.getJobFullDescription(exp, job.getJobId()).get();
        assertReflectionEquals(job,cpy); 
    }  

    
    
    
    @Test
    public void deleteJobDirDeletesJobSubfolderAndItsContent() throws Exception {
        
        AssayPack exp = new MockExperimentPack(1);
        Path ppaDir = expDir.resolve(PPAArtifactsRep.PPA_DIR);        

        String jobId = "3";
        Path jobDir = ppaDir.resolve(PPAArtifactsRep.JOBS_DIR).resolve(jobId);
        Files.createDirectories(jobDir);
        
        Path file = jobDir.resolve("cos.xml");
        Files.createFile(file);
        
        ppaRep.deleteJobDir(jobId, exp);
        
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
    public void getJobSummariesGivesSummariesOrderedByJobIdDescBasedOnlyOnSummaryFiles() throws Exception {
        AssayPack exp = new MockExperimentPack(1);
        PPAJobSummary s1 = new PPAJobSummary();
        s1.jobId = 3;
        s1.state = State.FAILED;
        
        PPAJobSummary s2 = new PPAJobSummary();
        s2.jobId = 5;
        s2.state = State.FINISHED;
        
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
        s1.jobId = 3;
        s1.state = State.FAILED;
        
        PPAJobSummary s2 = new PPAJobSummary();
        s2.jobId = 5;
        s2.state = State.FINISHED;
        
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
        
        s2 = new PPAJobSummary();
        s2.jobId = 5;
        s2.state = State.FINISHED;

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
        
        long jobId = 123;

        //job
        JobSummary job = new JobSummary();
        JobHandle jh = new JobHandle(jobId);
        job.setJob(jh);
        
        //raw results
        JobResult<PPAResult> jRes = new JobResult<>(jobId,State.SUCCESS,"OK");
        PPAResult pRes = new MESA_PPA(new PPA(24, 1, 2));        
        TaskResult<PPAResult> tRes = new TaskResult(1L, pRes);
        jRes.addResult(tRes);
        Path resFile = ppaRep.saveJobRawResults(jRes, jobId, exp, false);
        assertTrue(Files.isRegularFile(resFile));
        
        //fits
        Map<Long, TimeSeries> fits = new HashMap<>();
        fits.put(2L,new TimeSeries());
        ppaRep.saveFits(fits, jobId, exp);        
        assertTrue(ppaRep.getFits(jobId, exp).isPresent());
        
        
        //stats
        StatsEntry stats = new StatsEntry();
        stats.setJobId(jobId);
        PPAStats ppaStat = new PPAStats();
        stats.add(ppaStat);
        ppaRep.saveJobFullStats(stats, exp,jobId);
        assertEquals(jobId,ppaRep.getJobFullStats(exp, jobId).getJobId());
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
        
        ppaRep.deleteJobArtefacts(exp,job.getJobId());
        
        //check that don't exists or came back as defaults.
        assertFalse(Files.isRegularFile(resFile));
        assertFalse(ppaRep.getFits(jobId, exp).isPresent());
        assertEquals(0,ppaRep.getJobResultsGroups(exp, jobId).periodMax,EPS);
        assertEquals(0,ppaRep.getJobSimpleStats(exp, jobId).stats.size());
    }
    
    @Test
    public void deleteJobArtifactsRemovesJobFromCache() {
        
        
        AssayPack exp = new MockExperimentPack(1);
        
        PPAJobSummary job = PPAJobSummaryTest.makePPAJobSummary();
        
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

        Path ppa = expDir.resolve(PPAArtifactsRep.PPA_DIR);
        long jobId = 13;
        Path exp = ppa.resolve("JOBS/13").resolve(ppaRep.JOB_FULL_RESULTS_FILE);
        Path resp = ppaRep.jobIndResultsFile(7, jobId);
        assertEquals(exp,resp);
        
                
    }
    
    @Test
    public void canSaveAndReadBackJobIndResults() {
        AssayPack exp = new MockExperimentPack(1);
        long jobId = 20;
        
        List<ResultsEntry> results = new ArrayList<>();
        
        ResultsEntry ent = new ResultsEntry();
        ent.setDataId(1);
        ent.setJobId(jobId);
        GenericPPAResult ppa = new GenericPPAResult(24, 12, 3);
        ent.setResult(ppa);
        results.add(ent);
        
        ent = new ResultsEntry();
        ent.setDataId(2);
        ent.setJobId(jobId);
        ppa = new GenericPPAResult(25, 12, 3);
        ppa.setNeedsAttention(true);
        ent.setResult(ppa);
        results.add(ent);
        
        ppaRep.saveJobIndResults(results, exp, jobId);
        
        List<ResultsEntry> read = ppaRep.getJobIndResults(exp, jobId);
        assertNotNull(read);
        assertEquals(results.size(),read.size());
        
        assertEquals(results.get(0).getResult().getPeriod(),read.get(0).getResult().getPeriod(),EPS);
        assertEquals(results.get(1).getResult().getPeriod(),read.get(1).getResult().getPeriod(),EPS);

        assertEquals(1,read.get(0).dataId);
        assertEquals(2,read.get(1).dataId);
        
    }
    
    @Test
    public void canReadPreJC2PPAJobSummary() throws Exception {
        String fName = "preJC2_PPA_JOB_SUMMARY.json";
        File f = new File(this.getClass().getResource(fName).toURI());
        
        Optional<PPAJobSummary> res = ppaRep.readJobSummary(f.toPath());
        assertTrue(res.isPresent());
        
        PPAJobSummary job = res.get();
        assertEquals(1017, job.jobId);
        assertEquals("10311_NO_DTR", job.dataSetId);
        assertEquals("NO_DTR", job.dataSetType);
        assertEquals(State.FINISHED, job.state);
    }
    
    @Test
    public void canReadPreJC2PPAJobSimpleResults() throws Exception {
        String fName = "preJC2_PPA_SIMPLE_RESULTS.json";
        File f = new File(this.getClass().getResource(fName).toURI());
        
        PPAJobSimpleResults res = ppaRep.simpleResultsReader.readValue(f);

        
        assertEquals(1017, res.jobId);
        assertEquals(10, res.results.size());
        assertEquals(1, res.results.get(0).dataId);
        assertEquals(23.47, res.results.get(0).period, EPS);
        
    }    
    
    @Test
    public void canReadPreJC2PPAJobSimpleStats() throws Exception {
        String fName = "preJC2_PPA_SIMPLE_STATS.json";
        File f = new File(this.getClass().getResource(fName).toURI());
        
        PPAJobSimpleStats res = ppaRep.simpleStatsReader.readValue(f);

        
        assertEquals(1017, res.jobId);
        assertEquals(5, res.stats.size());
        assertEquals(1, res.stats.get(0).memberDataId);
        assertEquals(2, res.stats.get(0).N);
        assertEquals(23.7, res.stats.get(0).period, EPS);
        
    }    
    
    @Test
    public void canReadPreJC2PPAJobResultsGroups() throws Exception {
        String fName = "preJC2_PPA_GROUPED_RESULTS.json";
        File f = new File(this.getClass().getResource(fName).toURI());
        
        PPAJobResultsGroups res = ppaRep.groupSummaryReader.readValue(f);

        
        assertEquals(1017, res.jobId);
        assertEquals(5, res.groups.size());
        assertEquals(1, res.groups.get(0).memberDataId);
        assertEquals(2, res.groups.get(0).periods.size());
        assertEquals(List.of(23.47,23.93), res.groups.get(0).periods);
        
    }    
    
    @Test
    public void canReadPreJC2PPAJobIndResults() throws Exception {
        String fName = "preJC2_PPA_RESULTS.xml";
        File f = new File(this.getClass().getResource(fName).toURI());
        
        PPAJobIndResults res = ppaRep.xmlUtil.readFromFile(f.toPath(), PPAJobIndResults.class);

        
        assertEquals(10, res.results.size());
        assertEquals(1017, res.results.get(0).jobId);
        assertEquals(1, res.results.get(0).dataId);
        assertEquals(23.47, res.results.get(0).getResult().getPeriod(),EPS);
        assertTrue(res.results.get(0).getResult() instanceof FFT_PPA);
        
    } 
    
    @Test
    public void canReadPreJC2JobSummary() throws Exception {
        String fName = "preJC2_PPA_JOB_FULL.xml";
        File f = new File(this.getClass().getResource(fName).toURI());
        
        JobSummary job = ppaRep.xmlUtil.readFromFile(f.toPath(), JobSummary.class);
        
        assertEquals(1017, job.getJobId());
        assertEquals("10311_NO_DTR", job.getParams().getString(job.DATA_SET_ID));
        assertEquals("NO_DTR", job.getDataSetType());
        assertEquals(State.FINISHED, job.getStatus().getState());
    } 
    
    @Test
    public void canReadPreJC2StatsEntry() throws Exception {
        String fName = "preJC2_PPA_FULL_STATS.xml";
        File f = new File(this.getClass().getResource(fName).toURI());
        
        StatsEntry res = ppaRep.xmlUtil.readFromFile(f.toPath(), StatsEntry.class);

        
        assertEquals(1017, res.getJobId());
        assertEquals(5, res.getStats().size());
        assertEquals(1, res.getStats().get(0).getMemberDataId());
        assertEquals(2, res.getStats().get(0).getPeriodStats().getN(WeightingType.None));
        assertEquals(23.7, res.getStats().get(0).getPeriodStats().getMean(WeightingType.None), EPS);
        
    }      
    
}
