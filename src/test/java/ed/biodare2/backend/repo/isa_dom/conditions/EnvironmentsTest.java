/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.conditions;

import ed.biodare2.backend.repo.isa_dom.conditions.Environments;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class EnvironmentsTest {
    
    public EnvironmentsTest() {
    }
    
    Environments envs;
    
    @Before
    public void setUp() {
        envs = DomRepoTestBuilder.makeEnvironments();
        
    }
    
    
    @After
    public void tearDown() {
    }

    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        Environments org = envs;
        
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        Environments cpy = mapper.readValue(json, Environments.class); 
        assertEquals(org.environments ,cpy.environments);
        assertEquals(org,cpy);
        
    }
    
}
