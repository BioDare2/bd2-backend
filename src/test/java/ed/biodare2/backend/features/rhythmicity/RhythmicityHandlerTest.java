/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

import ed.biodare.jobcentre2.dom.JobResults;
import ed.biodare.jobcentre2.dom.JobStatus;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare.jobcentre2.dom.TSData;
import ed.biodare.jobcentre2.dom.TSDataSetJobRequest;
import ed.biodare.jobcentre2.dom.TSResult;
import ed.biodare.rhythm.ejtk.BD2eJTKRes;
import static ed.biodare2.backend.features.rhythmicity.RhythmicityHandler.HOURS_BEFORE_CAN_REPEAT;
import static ed.biodare2.backend.features.rhythmicity.RhythmicityHandler.MAX_DATA_SET_SIZE;
import ed.biodare2.backend.features.rhythmicity.dao.RhythmicityArtifactsRep;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.handlers.ExperimentHandler;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeBD2EJTKResults;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeDataTraces;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeRhythmicityJobSummary;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeRhythmicityRequest;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeTimeSeries;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityRequest;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.MockExperimentPack;
import ed.biodare2.backend.web.rest.HandlingException;
import ed.robust.dom.data.DetrendingType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class RhythmicityHandlerTest {
    
    public RhythmicityHandlerTest() {
    }
    
    ExperimentHandler experimentHandler;
    RhythmicityArtifactsRep rhythmicityRep;
    
    TSDataHandler dataHandler;
    RhythmicityService rhythmicityService;
    
    RhythmicityHandler instance;
    
    @Before
    public void setUp() {
        
        experimentHandler = mock(ExperimentHandler.class);
        
        rhythmicityRep = mock(RhythmicityArtifactsRep.class);
    
        dataHandler = mock(TSDataHandler.class);
        
        rhythmicityService = mock(RhythmicityService.class);
    
        instance = new RhythmicityHandler(experimentHandler, dataHandler, rhythmicityRep, rhythmicityService);        
    }
    

 

    @Test
    public void testWaitForJobWaits() {

        long expId = 123;
        UUID jobId = UUID.randomUUID();        
        RhythmicityJobSummary job = makeRhythmicityJobSummary(jobId, expId);

        when(rhythmicityRep.findJob(jobId, expId)).thenReturn(Optional.empty(), Optional.of(job));
        instance.JOB_WAITING_TIME = 10;
        
        RhythmicityJobSummary found = instance.waitForJob(jobId, expId);
        
        assertSame(job, found);
    }
    
    @Test
    public void testWaitForJobThrowExceptionOnMissing() {

        long expId = 123;
        UUID jobId = UUID.randomUUID();        

        when(rhythmicityRep.findJob(jobId, expId)).thenReturn(Optional.empty());
        instance.JOB_WAITING_TIME = 10;
        
        try {
            RhythmicityJobSummary found = instance.waitForJob(jobId, expId);
            fail("Exception expected not "+found);
        } catch (HandlingException e) {};
        
    }    
    
    @Test
    public void testHandleResults() {
        
        long expId = 123;
        AssayPack exp = new MockExperimentPack(expId);
        
        UUID jobId = UUID.randomUUID();        
        RhythmicityJobSummary job = makeRhythmicityJobSummary(jobId, expId);
        job.jobStatus.state = State.SUBMITTED;
        job.jobStatus.completed = null;
        
        DetrendingType detrending = DetrendingType.valueOf(job.parameters.get(job.DATA_SET_TYPE));
        List<DataTrace> dataSet = makeDataTraces(1, 1);    
        when(dataHandler.getDataSet(exp, detrending)).thenReturn(Optional.of(dataSet));  
        
        when(rhythmicityRep.findJob(jobId, expId)).thenReturn(Optional.empty(), Optional.of(job));
        
        JobResults<TSResult<BD2eJTKRes>> results = makeBD2EJTKResults(jobId, expId); 
        // as will be testing if set by the handler
        results.parentId = -1;
        instance.handleResults(exp, results);
        
        assertEquals(State.SUCCESS, job.jobStatus.state);
        assertEquals(LocalDate.now(), job.jobStatus.completed.toLocalDate());
        assertEquals(results.parentId, expId);
        
        verify(rhythmicityRep).saveJobResults(results);
        verify(rhythmicityRep).saveJobDetails(job);
        verify(dataHandler).getDataSet(exp, detrending);
        
    }
    
    @Test
    public void testGetResults() throws Exception {
        
        long expId = 123;
        AssayPack exp = new MockExperimentPack(expId);
        UUID jobId = UUID.randomUUID();        
        RhythmicityJobSummary job = makeRhythmicityJobSummary(jobId, expId);

        when(rhythmicityRep.findJob(jobId, expId)).thenReturn(Optional.of(job));
        
        JobResults<TSResult<BD2eJTKRes>> results = makeBD2EJTKResults(jobId, expId); 
        when(rhythmicityRep.findJobResults(jobId, expId)).thenReturn(Optional.of(results));
        
        DetrendingType detrending = DetrendingType.valueOf(job.parameters.get(job.DATA_SET_TYPE));
        List<DataTrace> dataSet = makeDataTraces(1, results.results.size());    
        when(dataHandler.getDataSet(exp, detrending)).thenReturn(Optional.of(dataSet));         
        
        assertNull(results.results.get(0).label);
        JobResults<TSResult<BD2eJTKRes>> resp = instance.getRhythmicityResults(exp, jobId);
        
        assertSame(results, resp);
        assertNotNull(results.results.get(0).label);
        
        verify(rhythmicityRep).findJob(jobId, expId);
        verify(rhythmicityRep).findJobResults(jobId, expId);
        
    }
    
    @Test
    public void newRhythmicitySubmitsJob() throws Exception {
        
        long expId = 123;
        AssayPack exp = new MockExperimentPack(expId);    
        
        RhythmicityRequest rhythmicityRequest = makeRhythmicityRequest();        
        
        List<DataTrace> dataSet = makeDataTraces(1, 1);    
        when(dataHandler.getDataSet(exp, rhythmicityRequest.detrending)).thenReturn(Optional.of(dataSet));     
        
        UUID uid = UUID.randomUUID();
        when(rhythmicityService.submitJob(any())).thenReturn(uid);
        
        UUID res = instance.newRhythmicity(exp, rhythmicityRequest);
        
        assertEquals(uid, res);
        verify(rhythmicityRep).saveJobDetails(any());
        verify(experimentHandler).updateHasRhythmicityJobs(exp,true);
        
    }
    
    @Test
    public void newRhythmicityDoesNotSubmitJobIfSimilarIsRunning() throws Exception {
        
        long expId = 123;
        AssayPack exp = new MockExperimentPack(expId);    
        
        RhythmicityRequest rhythmicityRequest = makeRhythmicityRequest();        
        
        
        List<DataTrace> dataSet = makeDataTraces(1, 1);    
        when(dataHandler.getDataSet(exp, rhythmicityRequest.detrending)).thenReturn(Optional.of(dataSet));     

        RhythmicityUtils utils = new RhythmicityUtils();
        utils.completeRequest(rhythmicityRequest);
        TSDataSetJobRequest jobRequest = utils.prepareJobRequest(exp.getId(), rhythmicityRequest, dataSet);
        
        RhythmicityJobSummary job1 = utils.prepareNewJobSummary(jobRequest, rhythmicityRequest, exp.getId());                
        UUID uid1 = UUID.randomUUID();
        
        job1.jobId = uid1;
        job1.jobStatus = new JobStatus(uid1, State.SUBMITTED); 

        when(rhythmicityRep.getJobs(exp)).thenReturn(List.of(job1));
        
        UUID uid2 = UUID.randomUUID();
        when(rhythmicityService.submitJob(any())).thenReturn(uid2);        
        
        try {
           UUID res = instance.newRhythmicity(exp, rhythmicityRequest);
           fail("Expected exception got jID: "+res);
        } catch (RhythmicityHandlingException e) {
            String msg = "Similar job is currently running ("
                    + uid1 + ")";            
            assertEquals(msg, e.getMessage());
        }
    
    }  
    
    @Test
    public void jobsSummariesGeneratedFromSameInputAreSimilar() {
        
        long expId = 123;
        RhythmicityRequest rhythmicityRequest = makeRhythmicityRequest();        
        rhythmicityRequest.preset = "AAA";
        
        List<DataTrace> dataSet = makeDataTraces(1, 1);    

        RhythmicityUtils utils = new RhythmicityUtils();
        TSDataSetJobRequest jobRequest = utils.prepareJobRequest(expId, rhythmicityRequest, dataSet);
        RhythmicityJobSummary job1 = utils.prepareNewJobSummary(jobRequest, rhythmicityRequest, expId);                
        
        RhythmicityJobSummary job2 = utils.prepareNewJobSummary(jobRequest, rhythmicityRequest, expId);                
        
        assertTrue(instance.isSimilarJob(job1, job2));
        
        rhythmicityRequest.preset = "XXX";
        jobRequest = utils.prepareJobRequest(expId, rhythmicityRequest, dataSet);
        job2 = utils.prepareNewJobSummary(jobRequest, rhythmicityRequest, expId);   
        System.out.println(job1.parameters);
        System.out.println(job2.parameters);
        assertFalse(instance.isSimilarJob(job1, job2));
        
        rhythmicityRequest.preset = "AAA";
        jobRequest = utils.prepareJobRequest(expId, rhythmicityRequest, dataSet);
        job2 = utils.prepareNewJobSummary(jobRequest, rhythmicityRequest, expId);                
        assertTrue(instance.isSimilarJob(job1, job2));
        
        rhythmicityRequest.windowStart = 10;
        jobRequest = utils.prepareJobRequest(expId, rhythmicityRequest, dataSet);
        job2 = utils.prepareNewJobSummary(jobRequest, rhythmicityRequest, expId);                        
        assertFalse(instance.isSimilarJob(job1, job2));
    }
    
    @Test
    public void findSimilarRunningJobTakesIntoAccountStateAndSubmissionDate() {
        
        long expId = 123;
        AssayPack exp = new MockExperimentPack(expId); 
        RhythmicityRequest rhythmicityRequest = makeRhythmicityRequest();        
        
        List<DataTrace> dataSet = makeDataTraces(1, 1);    

        RhythmicityUtils utils = new RhythmicityUtils();
        TSDataSetJobRequest jobRequest = utils.prepareJobRequest(expId, rhythmicityRequest, dataSet);
        RhythmicityJobSummary job1 = utils.prepareNewJobSummary(jobRequest, rhythmicityRequest, expId);
        
        
        RhythmicityJobSummary job2 = utils.prepareNewJobSummary(jobRequest, rhythmicityRequest, expId);                
        
        when(rhythmicityRep.getJobs(exp)).thenReturn(List.of(job1));
        
        assertTrue(instance.findSimilarRunningJob(job2, exp).isPresent());
        
        job1.jobStatus.state = State.FAILED;
        assertFalse(instance.findSimilarRunningJob(job2, exp).isPresent());
        
        job1.jobStatus.state = State.SUBMITTED;
        assertTrue(instance.findSimilarRunningJob(job2, exp).isPresent());

        job1.jobStatus.submitted = LocalDateTime.now().minusHours(HOURS_BEFORE_CAN_REPEAT+1);
        assertFalse(instance.findSimilarRunningJob(job2, exp).isPresent());
    }
    
    @Test
    public void checkRequestSanityChecksMethodsAndPresets() throws Exception {
        
        TSDataSetJobRequest jobRequest  = new TSDataSetJobRequest();
        TSData data = new TSData(2,  makeTimeSeries(24));
        jobRequest.data = List.of(data);
        jobRequest.externalId = "123";
        jobRequest.parameters.put("PRESET", "EJTK_CLASSIC");
        
        try {
            instance.checkRequestSanity(jobRequest);
            fail("Exception expected");
        } catch (RhythmicityHandlingException e) {
            String msg = "Unsupported method: "+null;
            assertEquals(msg, e.getMessage());
        }
        
        jobRequest.method = "XXX";
        try {
            instance.checkRequestSanity(jobRequest);
            fail("Exception expected");
        } catch (RhythmicityHandlingException e) {
            String msg = "Unsupported method: XXX";
            assertEquals(msg, e.getMessage());
        }
        
        jobRequest.method = "BD2EJTK";
        instance.checkRequestSanity(jobRequest);
        
        jobRequest.parameters.put("PRESET", "XXX");
        try {
            instance.checkRequestSanity(jobRequest);
            fail("Exception expected");
        } catch (RhythmicityHandlingException e) {
            String msg = "Unsupported preset: XXX";
            assertEquals(msg, e.getMessage());
        }
    }
 
    
    @Test
    public void checkRequestSanityChecksData() throws Exception {
        
        TSDataSetJobRequest jobRequest  = new TSDataSetJobRequest();
        TSData data = new TSData(2,  makeTimeSeries(24));
        jobRequest.data = List.of(data);
        jobRequest.externalId = "123";
        jobRequest.parameters.put("PRESET", "EJTK_CLASSIC");
        jobRequest.method = "BD2EJTK";
        
        instance.checkRequestSanity(jobRequest);
        
        jobRequest.data = List.of();
        try {
            instance.checkRequestSanity(jobRequest);
            fail("Exception expected");
        } catch (RhythmicityHandlingException e) {
            String msg = "Empty data set";
            assertEquals(msg, e.getMessage());
        }
        
        jobRequest.data = new ArrayList<>(MAX_DATA_SET_SIZE);
        for (int i = 1; i<= MAX_DATA_SET_SIZE+1; i++) jobRequest.data.add(data);
        
        try {
            instance.checkRequestSanity(jobRequest);
            fail("Exception expected");
        } catch (RhythmicityHandlingException e) {
            String msg = "BioDare can only test up to 50000 timeseries, got: 50001";
            assertEquals(msg, e.getMessage());
        }
        
        jobRequest.data = List.of(data);
        instance.checkRequestSanity(jobRequest);
        
        data.trace = makeTimeSeries(6*24);
        try {
            instance.checkRequestSanity(jobRequest);
            fail("Exception expected");
        } catch (RhythmicityHandlingException e) {
            String msg = "BioDare can only test data with up to 120 time points, got: 144";
            assertEquals(msg, e.getMessage());
        }
    }
    
}
