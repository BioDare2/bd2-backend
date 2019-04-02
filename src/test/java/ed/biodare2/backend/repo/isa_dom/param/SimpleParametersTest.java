/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.param;

import ed.biodare2.backend.repo.isa_dom.param.SimpleParameters;
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
public class SimpleParametersTest {
    
    public SimpleParametersTest() {
    }
    
    SimpleParameters params;
    
    @Before
    public void setUp() {
        params = new SimpleParameters();
        params.set("param1","val1");
        params.set("param2",null);
    }
    
    @After
    public void tearDown() {
    }
    

    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        SimpleParameters org = params;
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        SimpleParameters cpy = mapper.readValue(json, SimpleParameters.class);        
        assertEquals(org.parameters,cpy.parameters);
    }
    
}
