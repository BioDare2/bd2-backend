/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class GeneralDescTest {
    
    public GeneralDescTest() {
    }
    
    @Test
    public void serializesToJSONAndBack() throws JacksonException {

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
