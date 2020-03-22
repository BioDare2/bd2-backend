/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare.jobcentre2.client.JobCentreEndpointClient;
import ed.biodare2.SimpleRepoTestConfig;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Import(SimpleRepoTestConfig.class)
public class PPAServiceConfigurationTest {
    
    public PPAServiceConfigurationTest() {
    }
    
    @Autowired
    PPAServiceConfiguration instance;
    
    @Autowired
    PPAServiceParameters params;
    
    @Autowired
    @Qualifier(value = "ppaClient")        
    JobCentreEndpointClient client;
    
    @Before
    public void setUp() {
    }

    @Test
    public void testParamsAreSet() {
        assertNotNull(params);
        assertEquals("http://localhost:9000", params.backendURL.toString());
        assertEquals("http://localhost:8085/api/period", params.directions.endpoint);
    }
    
    @Test
    public void testClientIsProvided() {
        assertNotNull(client);
    }
    
}
