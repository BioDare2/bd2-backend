/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare.jobcentre2.client.JobCentreEndpointClient;
import ed.biodare2.backend.features.jobcentre2.JC2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class PPAJC2AnalysisService extends JC2Service {

    @Autowired
    public PPAJC2AnalysisService(@Qualifier(value = "ppaClient") JobCentreEndpointClient client,
            PPAServiceParameters parameters
    ) {
        super(client, parameters);
    }


    @Override
    protected String resultsHandlerEndpoint() {
        return "/api/services/ppa2/results/{externalId}";
    }
    
}
