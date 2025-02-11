/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest.onto;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.features.onto.species.SpeciesService;
import ed.biodare2.backend.security.dao.UserAccountRep;
import static ed.biodare2.backend.web.rest.AbstractIntTestBase.APPLICATION_JSON_UTF8;
import java.util.Arrays;
import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SimpleRepoTestConfig.class)
@AutoConfigureMockMvc
public class OntologyControllerTest {
    
    @MockitoBean
    SpeciesService species;
    
    @Autowired
    MockMvc mvc;    
    
    public OntologyControllerTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void speciesGetsListOfSpecies() throws Exception {
        
        when(species.findAll()).thenReturn(Arrays.asList("Species1","Last One"));
        
        
        MvcResult res = mvc.perform(get("/api/onto/species").accept(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]", is("Species1")))                
                .andReturn();
        
        // System.out.println("Species:\n"+res.getResponse().getContentAsString());
                
    }
    
}
