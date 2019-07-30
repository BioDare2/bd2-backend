/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare.jobcentre2.dom.TSDataSetJobRequest;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.features.rhythmicity.RhythmicityHandler;
import ed.biodare2.backend.features.rhythmicity.RhythmicityService;
import ed.biodare2.backend.features.rhythmicity.dao.RhythmicityArtifactsRep;
import ed.biodare2.backend.handlers.ExperimentHandler;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeRhythmicityRequest;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityRequest;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.PermissionsResolver;
import ed.biodare2.backend.web.tracking.ExperimentTracker;
import java.util.Map;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SimpleRepoTestConfig.class)
public class ExperimentRhythmicityControllerTest extends ExperimentBaseIntTest {
    
    final String serviceRoot = "/api/experiment";
    
    
    @MockBean
    RhythmicityService rhythmicityService;
    

    @Autowired
    RhythmicityArtifactsRep rhythmicityRep;
    
    /**
     * Test of newRhythmicity method, of class ExperimentRhythmicityController.
     */
    @Test
    public void testNewRhythmicity() throws Exception {
        
        UUID jobId = UUID.randomUUID();
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();                
        insertData(pack);
        
        RhythmicityRequest rhythmicityRequest = makeRhythmicityRequest();
        
        String orgJSON = mapper.writeValueAsString(rhythmicityRequest);
        
        when(rhythmicityService.submitJob(any())).thenReturn(jobId);
        
        assertFalse(exp.characteristic.hasRhythmicityJobs);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(serviceRoot+'/'+exp.getId()+"/rhythmicity")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("newPPA JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        assertNotNull(info);
        assertNotNull(info.get("analysis"));  
        assertEquals(jobId.toString(), info.get("analysis"));

        pack = expBoundles.findOne(pack.getId()).get();
        exp = pack.getAssay();
        assertTrue(exp.characteristic.hasRhythmicityJobs);
        
        assertTrue(rhythmicityRep.findOne(jobId, exp.getId()).isPresent());
        
    }
    
}
