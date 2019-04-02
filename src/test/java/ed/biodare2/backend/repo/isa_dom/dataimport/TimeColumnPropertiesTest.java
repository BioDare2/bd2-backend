/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import ed.biodare2.backend.repo.isa_dom.dataimport.TimeColumnProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import java.io.IOException;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tzielins
 */
public class TimeColumnPropertiesTest {
    
    public TimeColumnPropertiesTest() {
    }

    ObjectMapper mapper;
    
    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        
    }
    
    @After
    public void tearDown() {
    }

    
    
    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        TimeColumnProperties org = DomRepoTestBuilder.makeTimeColumnProperties();
        
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
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
