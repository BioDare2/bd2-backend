/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.features.rdmsocial.RDMAssayGUIAspects;
import ed.biodare2.backend.features.rdmsocial.RDMAssetsAspect;
import ed.biodare2.backend.features.rdmsocial.dao.RDMAssetsAspectRep;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SimpleRepoTestConfig.class)
public class ExperimentRDMSocialContorllerIntTest extends ExperimentBaseIntTest {
 

    
    final String serviceRoot = "/api/experiment";
    
    @Autowired
    RDMAssetsAspectRep assetsAspects;
 
    @Test
    public void getGUIAspectsReturnsAspects() throws Exception {
    
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/rdm/gui-aspects")
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("getGUIAspects JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        RDMAssayGUIAspects aspects = mapper.readValue(resp.getResponse().getContentAsString(), RDMAssayGUIAspects.class);
        assertNotNull(aspects);
        assertFalse(aspects.showMeasurementWarning);
        
    }
    
    
    @Test
    public void registerWarningIncreasesWarningsSize() throws Exception {
    
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        

        RDMAssetsAspect aspect = assetsAspects.findByParent(exp.getId(), EntityType.EXP_ASSAY).get();
        int prev = aspect.measurementWarnings;
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(serviceRoot+"/"+exp.getId()+"/rdm/register/warning/MEASUREMENT")
                .contentType(APPLICATION_JSON_UTF8)
                //.content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                //.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("registerWarning JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        aspect = assetsAspects.findByParent(exp.getId(), EntityType.EXP_ASSAY).get();
        assertEquals(prev+1,aspect.measurementWarnings);
        
        
        
    }
    
    
    
    
    
    
}
