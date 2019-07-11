/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

import ed.biodare.jobcentre2.client.JobCentreEndpointClient;
import ed.biodare.jobcentre2.client.JobCentreEndpointDirections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import ed.biodare.jobcentre2.client.JobCentreEndpointDirections;


/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@Service
public class JobCentre2Client {
    
    //JobCentreEndpointClient instance;
    RestTemplateBuilder builder;
    
    @Autowired
    public JobCentre2Client(RestTemplateBuilder builder) {
        
        setUp();
        instance = new JobCentreEndpointClient(builder, directions);
    };
    
    JobCentreEndpointClient instance;

    JobCentreEndpointDirections directions;
    
    String endpoint = "http://localhost:8080/api/rhythmicity";
    
    public void setUp() {
        
        directions = new JobCentreEndpointDirections();
        directions.endpoint = endpoint;
        directions.user = "user";
        directions.password = "password";
        
        //instance = new JobCentreEndpointClient(builder, directions);
    }
    
    

    
    
}
