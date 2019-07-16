/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

//import ed.biodare.jobcentre2.client.JobCentreEndpointClient;
//import ed.biodare.jobcentre2.client.JobCentreEndpointDirections;
import ed.biodare2.SimpleRepoTestConfig;
import java.util.Map;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
//@Import(SimpleRepoTestConfig.class)
public class JobCentre2ClientTest {
    
    //JobCentreEndpointClient instance;
    
    
    @Autowired
    JobCentre2Client client;        
    //JobCentreEndpointClient instance;

    //JobCentreEndpointDirections directions;
    
    String endpoint = "http://localhost:8080/api/rhythmicity";
    
    @Before
    public void setUp() {
        /*
        directions = new JobCentreEndpointDirections();
        directions.endpoint = endpoint;
        directions.user = "user";
        directions.password = "password";
        */
        //instance = new JobCentreEndpointClient(builder, directions);
    }
    
    /*
    JobCentreEndpointClient instance() {
        return new JobCentreEndpointClient(builder, directions);
    }*/
    
    @Test
    public void testgetServiceStatus() throws Exception {
        
        try {
            Map<String,String> result = client.instance.getServiceStatus();            
            assertTrue(result.containsKey("status"));
        } catch (Exception e) {
            System.out.println("E: "+e.getMessage());
            e.printStackTrace();
            throw e;
        }
    } 
    
    @Test
    public void contextLoads() {
    }    
    
}
