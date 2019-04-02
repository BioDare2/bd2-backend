/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import ed.biodare2.backend.repo.isa_dom.dataimport.CellRangeDescription;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRole;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tzielins
 */
public class CellRangeDescriptionTest {
    
    public CellRangeDescriptionTest() {
    }
    
    ObjectMapper mapper;
    
    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        
    }    

    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        CellRangeDescription org = DomRepoTestBuilder.makeCellRangeDescription();
        
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        System.out.println(json);
        
        CellRangeDescription cpy = mapper.readValue(json, CellRangeDescription.class);        
        assertEquals(org.range,cpy.range);
        assertEquals(org.role,cpy.role);
        assertEquals(org.details,cpy.details);
        
        org.role=CellRole.TIME;
        org.details = DomRepoTestBuilder.makeTimeColumnProperties();
        
        json = mapper.writeValueAsString(org);
        assertNotNull(json);
        System.out.println(json);
        
        cpy = mapper.readValue(json, CellRangeDescription.class);        
        assertEquals(org.range,cpy.range);
        assertEquals(org.role,cpy.role);
        TimeColumnProperties prop = (TimeColumnProperties)cpy.details;
        
        //System.out.println(prop.timeType);
        //System.out.println(prop.firstRow);
        //.out.println(prop.timeOffset);
        //System.out.println(prop.imgInterval);
        
        assertEquals(org.details,cpy.details);
        
    } 
    
    @Test
    public void readsUIJSON() throws JsonProcessingException, IOException {
        
        String json = "{\"range\":{\"first\":{\"col\":6,\"row\":1},\"last\":{\"col\":9,\"row\":1}},\"role\":\"DATA\",\"details\":{\"dataLabel\":\"WT\"}}";
        
        CellRangeDescription cpy = mapper.readValue(json, CellRangeDescription.class);
        assertEquals(1,cpy.range.first.row);
        assertEquals(6,cpy.range.first.col);  
        assertEquals(1,cpy.range.last.row);
        assertEquals(9,cpy.range.last.col);  
        assertEquals(cpy.role,CellRole.DATA);
        
        
        DataColumnProperties details = (DataColumnProperties)cpy.details;
        assertEquals("WT",details.dataLabel);
        assertEquals(CellRole.DATA,cpy.role);
    } 
    
    @Test
    public void readsUIJSONWithTime() throws JsonProcessingException, IOException {
        
        String json = "{\"range\":{\"first\":{\"col\":1,\"row\":1},\"last\":{\"col\":1,\"row\":1}},\n" +
"\"role\":\"TIME\",\n" +
"\"details\":{\"firstRow\":3,\"timeType\":\"IMG_NUMBER\",\"timeOffset\":2,\"imgInterval\":1.5}}";
        
        CellRangeDescription cpy = mapper.readValue(json, CellRangeDescription.class);
        assertEquals(1,cpy.range.first.row);
        assertEquals(1,cpy.range.first.col); 
        assertEquals(cpy.role,CellRole.TIME);
        
        TimeColumnProperties details = (TimeColumnProperties) cpy.details;
        assertEquals(TimeType.IMG_NUMBER,details.timeType);
        assertEquals(1.5,details.imgInterval,1E-6);
        assertEquals(2, details.timeOffset,1E-6);
        assertEquals(3,details.firstRow);
        
        
    }    
}
