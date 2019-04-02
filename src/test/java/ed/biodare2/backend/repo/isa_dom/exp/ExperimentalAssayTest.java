/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.exp;

import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class ExperimentalAssayTest {
    
    public ExperimentalAssayTest() {
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

        ExperimentalAssay org = DomRepoTestBuilder.makeExperimentalAssay();
        
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println("Test Experiment JSON:\n\n"+json+"\n");
        
        ExperimentalAssay cpy = mapper.readValue(json, ExperimentalAssay.class);        
        assertEquals(org.getId(), cpy.getId());
        assertEquals(org.generalDesc,cpy.generalDesc);
        assertEquals(org.contributionDesc,cpy.contributionDesc);
        assertEquals(org.experimentalDetails,cpy.experimentalDetails);
        assertEquals(org.characteristic,cpy.characteristic);
        assertEquals(org.provenance,cpy.provenance);
        
        
    }
    
}
