/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.jobcentre2;

import ed.biodare.jobcentre2.client.JobCentreClientException;
import ed.biodare.jobcentre2.client.JobCentreEndpointClient;
import ed.biodare.jobcentre2.dom.JobStatus;
import static ed.biodare.jobcentre2.dom.RhythmicityConstants.*;
import ed.biodare.jobcentre2.dom.TSDataSetJobRequest;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public abstract class JC2Service {

    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    final JobCentreEndpointClient client;
    final JC2ServiceParameters parameters;
    
    public JC2Service(JobCentreEndpointClient client, JC2ServiceParameters parameters
    ) {

        this.parameters = parameters;
        this.client = client;
        
        if (parameters.testClient) {
            verifyClientCanConnect(this.client);
        }
    }

    protected void verifyClientCanConnect(JobCentreEndpointClient client) {

        try {
            Map<String, String> resp = client.getServiceStatus();
            if (resp == null) throw new IllegalStateException("JC2 null status reposne");
            if (!resp.containsKey("status")) throw new IllegalStateException("JC2 does not report any status");
            String status = resp.get("status");
            log.info("JC2 status: {}", status);
        } catch (JobCentreClientException e) {
            log.error("Cannot connect to JC2 {}", e.getMessage(),e);
            throw new IllegalStateException("Cannot connecto to JC2 "+e.getMessage(),e);
        }
    }

    public UUID submitJob(TSDataSetJobRequest jobRequest) throws JC2HandlingException {
        
        
        try {
            addCallBack(jobRequest);
            return client.submitJob(jobRequest);
        } catch (JobCentreClientException e) {
            log.error("Could not submit the job, {}",e.getMessage(),e);
            throw new JC2HandlingException("Job was not accepted by the server: "+e.getMessage(),e);
        }
    }
    
    public JobStatus getJobStatus(UUID jobId) throws JC2HandlingException {
        
        try {
            return client.getJobStatus(jobId);
        } catch (JobCentreClientException e) {
            log.error("Could not read job status job, {}",e.getMessage(),e);
            throw new JC2HandlingException("JobStatus could not be read from the server: "+e.getMessage(),e);
        }
    }

    protected void addCallBack(TSDataSetJobRequest jobRequest) {
        
        String callBack = parameters.backendURL+resultsHandlerEndpoint();
        
        jobRequest.callBackParameters.put(SENDER_TYPE_KEY, REST_SENDER);
        jobRequest.callBackParameters.put(ENDPOINT_KEY, callBack);
        jobRequest.callBackParameters.put(USER_KEY, parameters.ppaUsername);
        jobRequest.callBackParameters.put(PASSWORD_KEY, parameters.ppaPassword);
        
    }
    
    protected abstract String resultsHandlerEndpoint();
}
