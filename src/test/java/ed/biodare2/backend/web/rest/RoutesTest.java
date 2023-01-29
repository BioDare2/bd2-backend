/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.SimpleRepoTestConfig;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SimpleRepoTestConfig.class)
public class RoutesTest extends AbstractIntTestBase {
    

    
    @LocalServerPort
    int port = 0;
    
    String server;
    
    Routes routes;
    
    public RoutesTest() {
    }
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        server = "localhost:"+port;
        String name = "BioDare2";
        routes = new Routes(name, server);
    }
    
    @Test
    public void accountPointsToAccount() throws Exception {
        String url = routes.getEntityPath(currentUser);
        assertTrue(url.startsWith(server));
        url = url.substring(server.length());
        // System.out.println(url);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(url)
                .accept(APPLICATION_JSON_UTF8);
                
        MvcResult resp = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.login", is(currentUser.getLogin())))
                .andReturn();  
        
        assertNotNull(resp);
        // System.out.println(resp.getResponse().getContentAsString());
    }
    
    //@Test
    public void testPortInjected() {
        System.out.println("Port: "+port);
        assertEquals(port, 9000);
        
    }
    
}
