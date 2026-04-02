/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

import ed.biodare.jobcentre2.client.JobCentreEndpointClient;
import ed.biodare2.SimpleRepoTestConfig;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@SpringBootTest
@Import(SimpleRepoTestConfig.class)
public class RhythmicityServiceConfigurationTest {
    
    public RhythmicityServiceConfigurationTest() {
    }
    
    @Autowired
    RhythmicityServiceConfiguration instance;
    
    @Autowired
    RhythmicityServiceParameters params;
    
    @Autowired
    @Qualifier(value = "rhythmicityClient")        
    JobCentreEndpointClient client;

    @Test
    public void testParamsAreSet() {
        assertNotNull(params);
        assertEquals("http://localhost:9000", params.backendURL.toString());
        assertEquals("http://localhost:8085/api/rhythmicity", params.directions.endpoint);
    }
    
    @Test
    public void testClientIsProvided() {
        assertNotNull(client);
    }
}
