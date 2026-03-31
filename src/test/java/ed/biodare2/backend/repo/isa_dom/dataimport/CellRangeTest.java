/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import ed.biodare2.backend.repo.isa_dom.dataimport.CellRange;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class CellRangeTest {
    
    public CellRangeTest() {
    }

    @Test
    public void serializesToJSONAndBack() throws JacksonException {

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
    public void readsUIJSON() throws JacksonException {
        ObjectMapper mapper = new ObjectMapper();
        
        String json = "{\"first\":{\"col\":8,\"row\":1},\"last\":{\"col\":10,\"row\":1}}";
        
        CellRange cpy = mapper.readValue(json, CellRange.class);
        assertEquals(1,cpy.first.row);
        assertEquals(8,cpy.first.col);
        assertEquals(1,cpy.last.row);
        assertEquals(10,cpy.last.col);
    }
}
