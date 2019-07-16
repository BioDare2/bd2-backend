/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

import ed.biodare.jobcentre2.client.JobCentreClientException;
import ed.biodare.jobcentre2.client.JobCentreEndpointClient;
import ed.biodare.jobcentre2.client.JobCentreEndpointDirections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@Service
public class RhythmicityService {

    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    final JobCentreEndpointClient client;
    final RhythmicityServiceParameters parameters;
    
    @Autowired
    public RhythmicityService(@Qualifier(value = "rhythmicityClient") JobCentreEndpointClient client,
            RhythmicityServiceParameters parameters
    ) {

        this.parameters = parameters;
        this.client = client;
        
        if (parameters.testClient) {
            testClient(this.client);
        }
    }

    void testClient(JobCentreEndpointClient client) {

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
}
