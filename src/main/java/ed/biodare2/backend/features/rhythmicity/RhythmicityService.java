/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

import ed.biodare.jobcentre2.client.JobCentreEndpointClient;
import ed.biodare2.backend.features.jobcentre2.JC2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@Service
public class RhythmicityService extends JC2Service {

    @Autowired
    public RhythmicityService(@Qualifier(value = "rhythmicityClient") JobCentreEndpointClient client,
            RhythmicityServiceParameters parameters
    ) {
        super(client, parameters);
    }


    @Override
    protected String resultsHandlerEndpoint() {
        return "/api/services/rhythmicity/results/{externalId}";
    }
}
