/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.actors;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class InstitutionTest {
    
    public InstitutionTest() {
    }
    
    @BeforeEach
    public void setUp() {
    }

    @Test
    public void serializesToJSONAndBack() throws JacksonException {

        Institution org = DomRepoTestBuilder.makeInstitution("UoE");
        
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        Institution cpy = mapper.readValue(json, Institution.class); 
        assertEquals(org.id ,cpy.id);
        assertEquals(org.name ,cpy.name);
        assertEquals(org.address ,cpy.address);
        assertEquals(org.longName ,cpy.longName);
        assertEquals(org.web ,cpy.web);        
        assertEquals(org,cpy);
    }
}
