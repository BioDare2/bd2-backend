/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import ed.biodare2.backend.repo.isa_dom.dataimport.CellRangeDescription;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRole;
import ed.biodare2.backend.repo.isa_dom.dataimport.ExcelTSImportParameters;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class ExcelTSImportParametersTest {
    
    public ExcelTSImportParametersTest() {
    }

    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        ExcelTSImportParameters org = DomRepoTestBuilder.makeExcelTSImportParameters();
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        System.out.println(json);
        
        ExcelTSImportParameters cpy = mapper.readValue(json, ExcelTSImportParameters.class);        
        assertEquals(org.timeColumn,cpy.timeColumn);
        assertEquals(org.dataBlocks,cpy.dataBlocks);
    } 
    
    @Test
    public void readsUIJSON() throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        
        String json = "{\"_class_name\":\".ExcelTSImportParameters\",\n" +
"\"timeColumn\":{\"range\":{\"first\":{\"col\":1,\"row\":1},\"last\":{\"col\":1,\"row\":1}},\"role\":\"TIME\",\"details\":{\"firstRow\":3,\"timeType\":\"IMG_NUMBER\",\"timeOffset\":2,\"imgInterval\":1.5}},\n" +
"\"dataBlocks\":[{\"range\":{\"first\":{\"col\":1,\"row\":1},\"last\":{\"col\":1,\"row\":1}},\"role\":\"TIME\",\"details\":{\"firstRow\":3,\"timeType\":\"IMG_NUMBER\",\"timeOffset\":2,\"imgInterval\":1.5}},\n" +
"{\"range\":{\"first\":{\"col\":2,\"row\":1},\"last\":{\"col\":3,\"row\":1}},\"role\":\"IGNORED\",\"details\":{}},\n" +
"{\"range\":{\"first\":{\"col\":4,\"row\":1},\"last\":{\"col\":5,\"row\":1}},\"role\":\"BACKGROUND\",\"details\":{}},\n" +
"{\"range\":{\"first\":{\"col\":6,\"row\":1},\"last\":{\"col\":9,\"row\":1}},\"role\":\"DATA\",\"details\":{\"dataLabel\":\"WT\"}},\n" +
"{\"range\":{\"first\":{\"col\":10,\"row\":1},\"last\":{\"col\":13,\"row\":1}},\"role\":\"DATA\",\"details\":{\"dataLabel\":\"toc1\"}},\n" +
"{\"range\":{\"first\":{\"col\":14,\"row\":1},\"last\":{\"col\":17,\"row\":1}},\"role\":\"DATA\",\"details\":{\"dataLabel\":\"toc2\"}},\n" +
"{\"range\":{\"first\":{\"col\":18,\"row\":1},\"last\":{\"col\":21,\"row\":1}},\"role\":\"DATA\",\"details\":{\"dataLabel\":\"toc3\"}},\n" +
"{\"range\":{\"first\":{\"col\":22,\"row\":1},\"last\":{\"col\":25,\"row\":1}},\"role\":\"DATA\",\"details\":{\"dataLabel\":\"cca1\"}}]}";
        
        ExcelTSImportParameters cpy = mapper.readValue(json, ExcelTSImportParameters.class);
        CellRangeDescription timeCol = cpy.timeColumn;
        TimeColumnProperties timeProp = (TimeColumnProperties) timeCol.details;
        
        assertEquals(TimeType.IMG_NUMBER,timeProp.timeType);
        assertEquals(2.0,timeProp.timeOffset,1E-06);
        assertEquals(1.5,timeProp.imgInterval,1E-06);
        assertEquals(3,timeProp.firstRow);
        
        assertEquals(8,cpy.dataBlocks.size());
        assertEquals(CellRole.TIME,cpy.dataBlocks.get(0).role);
        assertEquals(CellRole.IGNORED,cpy.dataBlocks.get(1).role);
        assertEquals(CellRole.BACKGROUND,cpy.dataBlocks.get(2).role);
        assertEquals(CellRole.DATA,cpy.dataBlocks.get(3).role);
        assertEquals(CellRole.DATA,cpy.dataBlocks.get(4).role);
        assertEquals(CellRole.DATA,cpy.dataBlocks.get(5).role);
        assertEquals(CellRole.DATA,cpy.dataBlocks.get(6).role);
        assertEquals(CellRole.DATA,cpy.dataBlocks.get(7).role);
    } 
    
}
