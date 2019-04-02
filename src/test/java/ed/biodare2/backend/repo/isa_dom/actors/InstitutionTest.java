/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.actors;

import ed.biodare2.backend.repo.isa_dom.actors.Institution;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.conditions.Environments;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class InstitutionTest {
    
    public InstitutionTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    

    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

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
