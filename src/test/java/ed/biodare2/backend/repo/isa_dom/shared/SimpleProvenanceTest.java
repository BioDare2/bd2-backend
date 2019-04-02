/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.shared;

import ed.biodare2.backend.repo.isa_dom.shared.SimpleProvenance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.ui_dom.exp.ExperimentalAssayView;
import java.io.IOException;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tzielins
 */
public class SimpleProvenanceTest {
    
    public SimpleProvenanceTest() {
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

        SimpleProvenance org = DomRepoTestBuilder.makeSimpleProvenance();
        
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
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
