/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.param;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class FullParametersTest {
    
    public FullParametersTest() {
    }
    
    FullParameters params;
    
    @BeforeEach
    public void setUp() {
        params = DomRepoTestBuilder.makeParameters();
    }
    
    @Test
    public void deserilizesJSJson() throws JacksonException {
        String json = "[{\"name\":\"p1\",\"value\":\"2\",\"label\":\"A param\",\"unit\":\"m/s\"},{\"name\":\"p2\",\"value\":\"cos\"}]";
	ObjectMapper mapper = JsonMapper.builder().build();
        
        FullParameters cpy = mapper.readValue(json, FullParameters.class);
        assertNotNull(cpy.parameters.get("p1"));
        assertEquals("2",cpy.parameters.get("p1").value);
    }
    
    @Test
    public void serializesToJSONAndBack() throws JacksonException {

        FullParameters org = params;

	ObjectMapper mapper = JsonMapper
	    .builder()
	    .enable(SerializationFeature.INDENT_OUTPUT)
	    .build();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        FullParameters cpy = mapper.readValue(json, FullParameters.class);        
        assertEquals(org.parameters,cpy.parameters);
    }
    
}
