/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.features.rdmsocial.RDMAssetsAspect;
import ed.biodare2.backend.features.rdmsocial.RDMCohort;
import ed.biodare2.backend.features.rdmsocial.dao.RDMAssetsAspectRep;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.security.BioDare2User;
import ed.robust.dom.util.Pair;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class RDMSocialStatsControllerTest extends ExperimentBaseIntTest {
    
    final String serviceRoot = "/api/rdm";
    
    @Autowired
    RDMAssetsAspectRep assetsAspects;
    
    public RDMSocialStatsControllerTest() {
    }
    

    /**
     * Test of dataStats method, of class RDMSocialStatsController.
     */
    @Test
    @Ignore("RDMSocial is disabled in the production code")
    public void testWarningStats() throws Exception {
        
        AssayPack pack = insertExperiment();
        
        RDMAssetsAspect aspect =  assetsAspects.findByParent(pack.getId(), EntityType.EXP_ASSAY).orElseThrow();
        
        aspect.cohort = RDMCohort.CONTROL;
        aspect.measurementAdded = true;
        aspect.measurementWarnings = 100;
        aspect.measurementAddedAtWarning = 100;
        assetsAspects.save(aspect);
        
        pack = insertExperiment();        
        aspect =  assetsAspects.findByParent(pack.getId(), EntityType.EXP_ASSAY).orElseThrow();
        
        aspect.cohort = RDMCohort.CONTROL;
        aspect.measurementAdded = true;
        aspect.measurementWarnings = 200;
        aspect.measurementAddedAtWarning = 200;
        assetsAspects.save(aspect);   
        
        for (int i=0;i<5;i++) {
            //as only users with more than 5 are taken into account
            insertExperiment();
        }
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+"/stats")
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("newPPA JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,Integer> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        assertNotNull(info);
        
        assertTrue(info.containsKey("CONTROL_CONTROL_100"));
        
    }
    
}
