/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.conditions;

import ed.biodare2.backend.repo.isa_dom.conditions.Environment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.param.FullParameters;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class EnvironmentTest {
    
    public EnvironmentTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    public static Environment makeInstance() {
        Environment org = new Environment();
        org.name ="LL";
        org.description = "Desc";
        return org;
    }

    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        Environment org = makeInstance();
        
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        Environment cpy = mapper.readValue(json, Environment.class); 
        assertEquals(org.name ,cpy.name);
        assertEquals(org.description ,cpy.description);
        assertEquals(org,cpy);
        
    }
    
}
