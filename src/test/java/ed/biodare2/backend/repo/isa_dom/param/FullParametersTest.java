/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.param;

import ed.biodare2.backend.repo.isa_dom.param.FullParameters;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
public class FullParametersTest {
    
    public FullParametersTest() {
    }
    
    FullParameters params;
    
    @Before
    public void setUp() {
        params = DomRepoTestBuilder.makeParameters();
    }
    
    @After
    public void tearDown() {
    }
    
    

    @Test
    public void deserilizesJSJson() throws JsonProcessingException, IOException {
        String json = "[{\"name\":\"p1\",\"value\":\"2\",\"label\":\"A param\",\"unit\":\"m/s\"},{\"name\":\"p2\",\"value\":\"cos\"}]";
        ObjectMapper mapper = new ObjectMapper();
        
        FullParameters cpy = mapper.readValue(json, FullParameters.class);
        assertNotNull(cpy.parameters.get("p1"));
        assertEquals("2",cpy.parameters.get("p1").value);
    }
    
    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        FullParameters org = params;
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        FullParameters cpy = mapper.readValue(json, FullParameters.class);        
        assertEquals(org.parameters,cpy.parameters);
    }
    
}
