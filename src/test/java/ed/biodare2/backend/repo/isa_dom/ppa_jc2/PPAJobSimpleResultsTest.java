/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa_jc2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import static ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleResultEntryTest.makePPASimpleResultEntry;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
/**
 *
 * @author tzielins
 */
public class PPAJobSimpleResultsTest {
    
    public PPAJobSimpleResultsTest() {
    }
    
    ObjectMapper mapper;    
    
    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
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
        assertReflectionEquals(org,cpy); 
    }
    
}
