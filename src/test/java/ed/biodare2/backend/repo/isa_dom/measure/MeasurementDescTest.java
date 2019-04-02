/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.measure;

import ed.biodare2.backend.repo.isa_dom.measure.MeasurementDesc;
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
public class MeasurementDescTest {
    
    public MeasurementDescTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        MeasurementDesc org = DomRepoTestBuilder.makeMeasurementDesc();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
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
