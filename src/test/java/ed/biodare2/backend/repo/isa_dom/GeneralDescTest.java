/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom;

import ed.biodare2.backend.repo.isa_dom.GeneralDesc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class GeneralDescTest {
    
    public GeneralDescTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    

    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        GeneralDesc org = DomRepoTestBuilder.makeGeneralDesc();
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        GeneralDesc cpy = mapper.readValue(json, GeneralDesc.class);        
        assertEquals(org.name,cpy.name);
        assertEquals(org.purpose,cpy.purpose);
        assertEquals(org.description,cpy.description);
        assertEquals(org.comments,cpy.comments);
        assertEquals(org,cpy);
    }
    
}
