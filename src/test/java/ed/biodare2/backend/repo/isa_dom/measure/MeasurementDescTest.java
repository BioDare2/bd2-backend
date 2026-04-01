/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.measure;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class MeasurementDescTest {
    
    public MeasurementDescTest() {
    }
    
    @Test
    public void serializesToJSONAndBack() throws JacksonException {

        MeasurementDesc org = DomRepoTestBuilder.makeMeasurementDesc();

	ObjectMapper mapper = JsonMapper
	    .builder()
	    .enable(SerializationFeature.INDENT_OUTPUT)
	    .build();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        MeasurementDesc cpy = mapper.readValue(json, MeasurementDesc.class);        
        assertEquals(org.technique,cpy.technique);
        assertEquals(org.equipment,cpy.equipment);
        assertEquals(org.description,cpy.description);
        assertEquals(org.parameters,cpy.parameters);
        assertEquals(org,cpy);
    }
}
