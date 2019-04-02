/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import ed.biodare2.backend.repo.isa_dom.dataimport.CellRange;
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
public class CellRangeTest {
    
    public CellRangeTest() {
    }

    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        CellRange org = DomRepoTestBuilder.makeCellRange();
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        CellRange cpy = mapper.readValue(json, CellRange.class);        
        assertEquals(org.first,cpy.first);
        assertEquals(org.last,cpy.last);
    }
    
    @Test
    public void readsUIJSON() throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        
        String json = "{\"first\":{\"col\":8,\"row\":1},\"last\":{\"col\":10,\"row\":1}}";
        
        CellRange cpy = mapper.readValue(json, CellRange.class);
        assertEquals(1,cpy.first.row);
        assertEquals(8,cpy.first.col);
        assertEquals(1,cpy.last.row);
        assertEquals(10,cpy.last.col);
    }
    
}
