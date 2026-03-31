/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

/**
 *
 * @author tzielins
 */
public class TimeColumnPropertiesTest {
    
    public TimeColumnPropertiesTest() {
    }

    ObjectMapper mapper;
    
    @BeforeEach
    public void setUp() {
	mapper = JsonMapper
	    .builder()
	    .enable(SerializationFeature.INDENT_OUTPUT)
	    .build();
    }
    
    @Test
    public void serializesToJSONAndBack() throws JacksonException {

        TimeColumnProperties org = DomRepoTestBuilder.makeTimeColumnProperties();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        System.out.println("TimeColumnProperties JSON:\n\n"+json+"\n");
        
        TimeColumnProperties cpy = mapper.readValue(json, TimeColumnProperties.class);        
        assertEquals(org.firstRow,cpy.firstRow);
        assertEquals(org.timeOffset,cpy.timeOffset,1E-6);
        assertEquals(org.imgInterval,cpy.imgInterval,1E-6);
        assertEquals(org.timeType,cpy.timeType);
        assertEquals(org,cpy);
    }
}
