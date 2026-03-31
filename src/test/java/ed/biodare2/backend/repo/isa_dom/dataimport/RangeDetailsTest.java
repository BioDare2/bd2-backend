/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class RangeDetailsTest {
    
    public RangeDetailsTest() {
    }

    @Test
    public void serializesTimeToJSONAndBack() throws JacksonException {

        TimeColumnProperties prop = new TimeColumnProperties();
        prop.timeType = TimeType.IMG_NUMBER;
        prop.firstRow = 2;
        prop.timeOffset = 3;
        prop.imgInterval = 4;
        
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(prop);
        assertNotNull(json);
        //System.out.println(json);
        
        TimeColumnProperties cpy = (TimeColumnProperties) mapper.readValue(json, RangeDetails.class);        
        assertEquals(prop,cpy);
    }
    
    @Test
    public void serializesDataToJSONAndBack() throws JacksonException {

        DataColumnProperties prop = new DataColumnProperties();
        prop.dataLabel = "TOC3";
        
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(prop);
        assertNotNull(json);
        //System.out.println(json);
        
        DataColumnProperties cpy = (DataColumnProperties) mapper.readValue(json, RangeDetails.class);        
        assertEquals(prop,cpy);
    }    
    
    @Test
    public void readsTimeUIJSON() throws JacksonException {
        ObjectMapper mapper = new ObjectMapper();
        
        String json = "{\"firstRow\":3,\"timeType\":\"IMG_NUMBER\",\"timeOffset\":2,\"imgInterval\":1.5}";
        
        TimeColumnProperties cpy = (TimeColumnProperties)mapper.readValue(json, RangeDetails.class);
        
        assertEquals(TimeType.IMG_NUMBER,cpy.timeType);
        assertEquals(3,cpy.firstRow);        
        assertEquals(2,cpy.timeOffset,1E-6);        
        assertEquals(1.5,cpy.imgInterval,1E-6);        
    }
}
