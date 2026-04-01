/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.shared;

import ed.biodare2.backend.repo.isa_dom.shared.SimpleProvenance;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.ui_dom.exp.ExperimentalAssayView;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

/**
 *
 * @author tzielins
 */
public class SimpleProvenanceTest {
    
    public SimpleProvenanceTest() {
    }

    ObjectMapper mapper;
    
    @BeforeEach
    public void setUp() {
	ObjectMapper mapper = JsonMapper
	    .builder()
	    .enable(SerializationFeature.INDENT_OUTPUT)
	    .build();
    }
    
    @Test
    public void serializesToJSONAndBack() throws JacksonException {

        SimpleProvenance org = DomRepoTestBuilder.makeSimpleProvenance();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println("SimpleProvenance JSON:\n\n"+json+"\n");
        
        SimpleProvenance cpy = mapper.readValue(json, SimpleProvenance.class);        
        assertEquals(org.created, cpy.created);
        assertEquals(org.createdBy,cpy.createdBy);
        assertEquals(org.modified,cpy.modified);
        assertEquals(org.modifiedBy,cpy.modifiedBy);
        assertEquals(org,cpy);
    }
}
