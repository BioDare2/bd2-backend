/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa_jc2;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import static ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleResultEntryTest.makePPASimpleResultEntry;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
/**
 *
 * @author tzielins
 */
public class PPAJobSimpleResultsTest {
    
    public PPAJobSimpleResultsTest() {
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
    public void serializesToJSONAndBack() throws Exception {
        UUID id = UUID.randomUUID();
        PPAJobSimpleResults org = new PPAJobSimpleResults(id);
        org.results.add(makePPASimpleResultEntry());
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        PPAJobSimpleResults cpy = mapper.readValue(json, PPAJobSimpleResults.class); 
        // [TODO find reflective eq] assertReflectionEquals(org,cpy); 
    }
}
