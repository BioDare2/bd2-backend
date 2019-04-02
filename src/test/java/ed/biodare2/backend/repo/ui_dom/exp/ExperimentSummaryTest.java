/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.exp;

import ed.biodare2.backend.repo.ui_dom.exp.ExperimentSummary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import java.io.IOException;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tzielins
 */
public class ExperimentSummaryTest {
    
    public ExperimentSummaryTest() {
    }

    ObjectMapper mapper;
    
    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        
    }
    
    @After
    public void tearDown() {
    }

    
    
    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        ExperimentSummary org = new ExperimentSummary(DomRepoTestBuilder.makeExperimentalAssay());
        
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println("ExperimentSummary JSON:\n\n"+json+"\n");
        
        ExperimentSummary cpy = mapper.readValue(json, ExperimentSummary.class);        
        assertEquals(org.id, cpy.id);
        assertEquals(org.generalDesc,cpy.generalDesc);
        assertEquals(org.features,cpy.features);
        assertEquals(org.provenance,cpy.provenance);
        
        
    }
    
}
