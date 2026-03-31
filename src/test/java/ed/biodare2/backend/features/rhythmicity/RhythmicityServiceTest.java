/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

import ed.biodare.jobcentre2.client.JobCentreEndpointClient;
import ed.biodare.jobcentre2.dom.JobStatus;
import ed.biodare.jobcentre2.dom.TSDataSetJobRequest;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeRhythmicityJobSummary;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeRhythmicityRequest;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityRequest;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class RhythmicityServiceTest {
    
    public RhythmicityServiceTest() {
    }
    
    RhythmicityService instance;
    JobCentreEndpointClient client;
    RhythmicityServiceParameters parameters;
    RhythmicityUtils utils;
    
    @Before
    public void setUp() throws Exception {
        
        parameters = new RhythmicityServiceParameters();
        parameters.backendURL = new URL("http://localhost:9000");
        parameters.ppaUsername = "user1";
        parameters.ppaPassword = "password1";
        parameters.testClient = false;
        
        client = mock(JobCentreEndpointClient.class);
        utils = new RhythmicityUtils();
                
        
        instance = new RhythmicityService(client, parameters);
    }

    
    @Test
    public void resultsHandlerEndpoint() {
        String exp = "/api/services/rhythmicity/results/{externalId}";
        
        assertEquals(exp, instance.resultsHandlerEndpoint());
    }    
    
    @Test
    public void testSumbitSendsOverWithCallBack() throws Exception {
        
        RhythmicityRequest uiReq = makeRhythmicityRequest();
        
        long expId = 123;
        TSDataSetJobRequest jobRequest = utils.prepareJobRequest(expId, uiReq, List.of());
        assertFalse(jobRequest.callBackParameters.containsKey("ENDPOINT"));
                
        UUID jobId = UUID.randomUUID();        
        
        when(client.submitJob(jobRequest)).thenReturn(jobId);
        
        UUID res = instance.submitJob(jobRequest);
        assertEquals(jobId, res);
        verify(client).submitJob(jobRequest);
        
        assertTrue(jobRequest.callBackParameters.containsKey("ENDPOINT"));
        assertEquals("http://localhost:9000/api/services/rhythmicity/results/{externalId}",
                jobRequest.callBackParameters.get("ENDPOINT"));
    }
    
 
    
}
