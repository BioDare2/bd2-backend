/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.param;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class SimpleParametersTest {
    
    public SimpleParametersTest() {
    }
    
    SimpleParameters params;
    
    @BeforeEach
    public void setUp() {
        params = new SimpleParameters();
        params.set("param1","val1");
        params.set("param2",null);
    }
    
    @Test
    public void serializesToJSONAndBack() throws JacksonException {

        SimpleParameters org = params;
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        SimpleParameters cpy = mapper.readValue(json, SimpleParameters.class);        
        assertEquals(org.parameters,cpy.parameters);
    }
}
