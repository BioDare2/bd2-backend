/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.jobcentre2;

import ed.biodare.jobcentre2.client.JobCentreEndpointClient;
import ed.biodare.jobcentre2.dom.JobStatus;
import ed.biodare.jobcentre2.dom.TSDataSetJobRequest;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author tzielins
 */
public class JC2ServiceTest {
    
    public JC2ServiceTest() {
    }
    
    JC2Service instance;
    JobCentreEndpointClient client;
    JC2ServiceParameters parameters;
    
    static class TestingJC2Service extends JC2Service {

        public TestingJC2Service(JobCentreEndpointClient client, JC2ServiceParameters parameters) {
            super(client, parameters);
        }

        
        @Override
        protected String resultsHandlerEndpoint() {
            return "/api/jc2";
        }
        
    }
    
    @Before
    public void setUp() throws Exception {
        
        parameters = new JC2ServiceParameters();
        parameters.backendURL = new URL("http://localhost:9000");
        parameters.ppaUsername = "user1";
        parameters.ppaPassword = "password1";
        parameters.testClient = false;
        
        client = mock(JobCentreEndpointClient.class);
                
        
        instance = new TestingJC2Service(client, parameters);
    }

    @Test
    public void testAddCallBacks() throws Exception {
        
        TSDataSetJobRequest jobRequest = new TSDataSetJobRequest();
        
        assertTrue(jobRequest.callBackParameters.isEmpty());
        
        instance.addCallBack(jobRequest);
        Map<String,String> callBack = jobRequest.callBackParameters;
        
        assertFalse(callBack.isEmpty());
        
        Map<String,String> exp = Map.of("SENDER_TYPE","REST_SENDER",
                "ENDPOINT","http://localhost:9000/api/jc2",
                "USER","user1", 
                "PASSWORD","password1");
        
        assertEquals(exp, callBack);
                
    }
    
    @Test
    public void testSumbitSendsOverWithCallBack() throws Exception {
        
        TSDataSetJobRequest jobRequest = new TSDataSetJobRequest();
                
        UUID jobId = UUID.randomUUID();        
        
        when(client.submitJob(jobRequest)).thenReturn(jobId);
        
        UUID res = instance.submitJob(jobRequest);
        assertEquals(jobId, res);
        verify(client).submitJob(jobRequest);
        
        assertTrue(jobRequest.callBackParameters.containsKey("ENDPOINT"));
    }
    
    @Test
    public void testGetJobsStatusReadsStatus() throws Exception {
        
        UUID jobId = UUID.randomUUID();        
        JobStatus status = new JobStatus(jobId);
        
        when(client.getJobStatus(jobId)).thenReturn(status);
        
        JobStatus res = instance.getJobStatus(jobId);
        assertEquals(status, res);
        verify(client).getJobStatus(jobId);
        
    }    
    
}
