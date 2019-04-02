/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.SimpleRepoTestConfig;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SimpleRepoTestConfig.class)
public class ScrabControllerTest extends AbstractIntTestBase {
    


    @Test
    public void greetingsGivesGreeting() throws Exception {
    
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/scrab")
                .param("name", "King")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("RESP: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> gr = mapper.readValue(resp.getResponse().getContentAsString(), MAP_TYPE);
        assertTrue(gr.get("greeting").contains("King"));
    }
    
    @Test
    public void postReturnsGreeting() throws Exception {
    
        Map<String,String> org = new HashMap<>();
        org.put("content","Tak tak");
        
        String orgJSON = mapper.writeValueAsString(org);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/api/scrab")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("RESP: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> gr = mapper.readValue(resp.getResponse().getContentAsString(), MAP_TYPE);
        assertTrue(gr.get("greeting").startsWith("Tak tak"));
    }    
}
