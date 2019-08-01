/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare.jobcentre2.dom.JobResults;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare.jobcentre2.dom.TSResult;
import ed.biodare.rhythm.ejtk.BD2eJTKRes;
import ed.biodare.rhythm.ejtk.patterns.AsymCosine;
import ed.biodare2.backend.features.rhythmicity.dao.RhythmicityArtifactsRep;
import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeBD2EJTKResults;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeRhythmicityJobSummary;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@RunWith(SpringRunner.class)
@JsonTest
public class RhythmicityArtifactsRepTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    @Autowired
    ObjectMapper mapper;
    
    ExperimentsStorage expStorage;
    Path expDir;
    
    RhythmicityArtifactsRep instance;
    
    public RhythmicityArtifactsRepTest() {
    }
    
    @Before
    public void setUp() throws Exception {
        
        expDir = testFolder.newFolder().toPath();
        expStorage = mock(ExperimentsStorage.class);
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);
        
        instance = new RhythmicityArtifactsRep(expStorage, mapper);
    }

    /**
     * Test of saveJobDetails method, of class RhythmicityArtifactsRep.
     */
    @Test
    public void testGetJobDirCreatesCorrectFolder() {
        
        long expId = 123;
        UUID jobId = UUID.randomUUID();
        
        Path exp = expDir.resolve("RHYTHMICITY/JOBS").resolve(jobId.toString());
        
        Path dir = instance.getJobDir(expId, jobId);
        
        assertEquals(exp, dir);
        assertTrue(Files.exists(dir));
    }
    
    @Test
    public void saveJobSaveThatReadCanRead() {
        
        long expId = 123;
        UUID jobId = UUID.randomUUID();        
        RhythmicityJobSummary job = makeRhythmicityJobSummary(jobId, expId);
        
        Optional<RhythmicityJobSummary> res = instance.readJobDetails(jobId, expId);
        assertTrue(res.isEmpty());
        
        instance.saveJobDetails(job, expId);
        
        res = instance.readJobDetails(jobId, expId);
        assertTrue(res.isPresent());
        assertEquals(job, res.get());
    }
    
    @Test
    public void saveResultsSavesThatReadCanRead() {
        
        long expId = 123;
        UUID jobId = UUID.randomUUID();        
        JobResults<TSResult<BD2eJTKRes>> results = makeBD2EJTKResults(jobId, expId); 
        
        
        Optional<JobResults<TSResult<BD2eJTKRes>>> res = instance.readJobResults(jobId, expId);
        assertTrue(res.isEmpty());
        
        instance.saveJobResults(results, jobId, expId);
        
        res = instance.readJobResults(jobId, expId);
        assertTrue(res.isPresent());
        assertEquals(results, res.get());
    }    


    @Test
    public void getJobsReadsJobsInCorrectOrder() {
        
        long expId = 123;
        
        List<RhythmicityJobSummary> res = instance.getJobs(expId);
        assertEquals(List.of(), res);
        
        
        RhythmicityJobSummary job1 = makeRhythmicityJobSummary(UUID.randomUUID(), expId);
        job1.jobStatus.submitted = LocalDateTime.now().minusHours(1);        
        instance.saveJobDetails(job1, expId);
        
        RhythmicityJobSummary job2 = makeRhythmicityJobSummary(UUID.randomUUID(), expId);
        job2.jobStatus.submitted = LocalDateTime.now();        
        instance.saveJobDetails(job2, expId);
        
        RhythmicityJobSummary job3 = makeRhythmicityJobSummary(UUID.randomUUID(), expId);
        job3.jobStatus.submitted = LocalDateTime.now().minusHours(2);        
        instance.saveJobDetails(job3, expId);
        
        List<RhythmicityJobSummary> exp = List.of(job2, job1, job3);
        res = instance.getJobs(expId);
        
        assertEquals(exp, res);
    }


    
}
