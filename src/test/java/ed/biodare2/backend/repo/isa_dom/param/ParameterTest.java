/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.param;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class ParameterTest {
    
    public ParameterTest() {
    }
    
    //@Test
    public void labelBecomsNameIfEmpty() {
        Parameter org = new Parameter("param1");
        assertEquals("param1",org.label);
        
        org = new Parameter("param1",null,null,null);
        assertEquals("param1",org.label);
    }
    
    public void getLabelBecomesNameIfEmpty() {
        Parameter org = new Parameter("param1");
        assertEquals("param1",org.getLabel());
        
        org = new Parameter("param1",null,null,null);
        assertEquals("param1",org.getLabel());
    }    
    
    @Test
    public void labelStaysNnullIfEmpty() {
        Parameter org = new Parameter("param1");
        assertEquals(null,org.label);
        
        org = new Parameter("param1",null,null,null);
        assertEquals(null,org.label);
    }
    

    @Test
    public void serializesToJSONAndBack() throws JacksonException {
        
        Parameter org = new Parameter("param1", "value1", "a label", "a unit");
        
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        Parameter cpy = mapper.readValue(json, Parameter.class);
        assertNotNull(cpy);
        assertEquals(org.name,cpy.name);
        assertEquals(org.value,cpy.value);
        assertEquals(org.label,cpy.label);
        assertEquals(org.unit,cpy.unit);
        
        org = new Parameter("param2");
        
        json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        cpy = mapper.readValue(json, Parameter.class);
        assertNotNull(cpy);
        assertEquals(org.name,cpy.name);
        assertEquals(org.value,cpy.value);
        assertEquals(org.label,cpy.label);
        assertEquals(org.unit,cpy.unit);
    }
 
    
    @Test
    public void serializesToJSONDontPrintNullss() throws JacksonException {
        
        Parameter org = new Parameter("param2","val",null,null);
        
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        assertTrue(json.contains("name"));
        assertTrue(json.contains("value"));
        assertFalse(json.contains("label"));
        assertFalse(json.contains("unit"));
    }
}
