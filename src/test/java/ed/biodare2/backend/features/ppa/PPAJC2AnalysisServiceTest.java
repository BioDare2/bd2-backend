/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare.jobcentre2.client.JobCentreEndpointClient;
import ed.biodare.jobcentre2.dom.TSDataSetJobRequest;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.ppa.PPARequest;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author tzielins
 */
public class PPAJC2AnalysisServiceTest {
    
    public PPAJC2AnalysisServiceTest() {
    }
    
    PPAJC2AnalysisService instance;
    JobCentreEndpointClient client;
    PPAServiceParameters parameters;
    PPAUtilsJC2 utils;
    
    @Before
    public void setUp() throws Exception {
        
        parameters = new PPAServiceParameters();
        parameters.backendURL = new URL("http://localhost:9000");
        parameters.ppaUsername = "user1";
        parameters.ppaPassword = "password1";
        parameters.testClient = false;
        
        client = mock(JobCentreEndpointClient.class);
                
        utils = new PPAUtilsJC2();
        instance = new PPAJC2AnalysisService(client, parameters);
    }

    
    @Test
    public void resultsHandlerEndpoint() {
        String exp = "/api/services/ppa2/results/{externalId}";
        
        assertEquals(exp, instance.resultsHandlerEndpoint());
    }    
    
    @Test
    public void testSumbitSendsOverWithCallBack() throws Exception {
        
        PPARequest req = DomRepoTestBuilder.makePPARequest();
        
        long expId = 123;
        TSDataSetJobRequest jobRequest =utils.prepareJC2JobRequest(expId, req, List.of());
        assertFalse(jobRequest.callBackParameters.containsKey("ENDPOINT"));
                
        UUID jobId = UUID.randomUUID();        
        
        when(client.submitJob(jobRequest)).thenReturn(jobId);
        
        UUID res = instance.submitJob(jobRequest);
        assertEquals(jobId, res);
        verify(client).submitJob(jobRequest);
        
        assertTrue(jobRequest.callBackParameters.containsKey("ENDPOINT"));
        assertEquals("http://localhost:9000/api/services/ppa2/results/{externalId}",
                jobRequest.callBackParameters.get("ENDPOINT"));
    }    
}
