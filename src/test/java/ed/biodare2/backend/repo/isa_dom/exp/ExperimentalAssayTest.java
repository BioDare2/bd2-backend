/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.exp;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class ExperimentalAssayTest {
    
    public ExperimentalAssayTest() {
    }
    
    ObjectMapper mapper;
    
    @BeforeEach
    public void setUp() {
	mapper = JsonMapper
	    .builder()
	    .enable(SerializationFeature.INDENT_OUTPUT)
	    .build();
    }
    
    @Test
    public void serializesToJSONAndBack() throws JacksonException {

        ExperimentalAssay org = DomRepoTestBuilder.makeExperimentalAssay();
        
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
