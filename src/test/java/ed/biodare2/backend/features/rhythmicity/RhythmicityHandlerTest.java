/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

import ed.biodare.jobcentre2.dom.JobResults;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare.jobcentre2.dom.TSResult;
import ed.biodare.rhythm.ejtk.BD2eJTKRes;
import ed.biodare2.backend.features.rhythmicity.dao.RhythmicityArtifactsRep;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.handlers.ExperimentHandler;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeBD2EJTKResults;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeDataTraces;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeRhythmicityJobSummary;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityRequest;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.MockExperimentPack;
import ed.biodare2.backend.web.rest.HandlingException;
import ed.robust.dom.data.DetrendingType;
import java.time.LocalDate;
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
        
        instance.handleResults(exp, results);
        
        assertEquals(State.SUCCESS, job.jobStatus.state);
        assertEquals(LocalDate.now(), job.jobStatus.completed.toLocalDate());
        
        verify(rhythmicityRep).saveJobResults(results, job, exp);
        verify(rhythmicityRep).saveJobDetails(job, exp);
        verify(dataHandler).getDataSet(exp, detrending);
        
    }
    
}
