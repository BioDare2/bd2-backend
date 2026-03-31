/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.conditions;

import ed.biodare2.backend.repo.isa_dom.conditions.Environments;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class EnvironmentsTest {
    
    public EnvironmentsTest() {
    }
    
    Environments envs;
    
    @BeforeEach
    public void setUp() {
        envs = DomRepoTestBuilder.makeEnvironments();
    }
    
    @Test
    public void serializesToJSONAndBack() throws JacksonException {

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
