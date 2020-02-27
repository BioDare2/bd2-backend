/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import static ed.biodare2.backend.web.rest.AbstractIntTestBase.APPLICATION_JSON_UTF8;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.Mockito.*;
/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(SimpleRepoTestConfig.class)
public class StatusControllerTest {
    
    @Autowired
    MockMvc mockMvc;
    
    // @Resource(name = "DomMapper" )        
    @Autowired
    ObjectMapper mapper;    
    
    @MockBean
    DBSystemInfoRep systemInfos;
    
    public StatusControllerTest() {
    }
    
    @Before
    public void setUp() {
        
        when(systemInfos.count()).thenReturn(2L);
    }

    @Test
    public void testGivesStatus() throws Exception {
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/status")
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)                
                ;//.with(SecurityMockMvcRequestPostProcessors.authentication(authentication));

        
        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                //.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertNotNull(resp);        
        
        Map<String, String> status = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<Map<String, String>>() { });
        
        assertFalse(status.isEmpty());
        
        assertEquals("running", status.get("status"));
        assertTrue(status.containsKey("user"));
        assertEquals("2", status.get("experiments"));
        
        
    }

    @Test
    public void givesBadResponseOnError() throws Exception {
        
        when(systemInfos.count()).thenThrow(IllegalStateException.class);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/status")
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)                
                ;//.with(SecurityMockMvcRequestPostProcessors.authentication(authentication));

        
        MvcResult resp = mockMvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andReturn();

        assertNotNull(resp);        
        
        
        
    }
    
}
