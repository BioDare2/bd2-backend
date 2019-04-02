/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa;

import ed.biodare2.backend.repo.isa_dom.ppa.PPARequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.robust.dom.data.DetrendingType;
import ed.robust.ppa.PPAMethod;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class PPARequestTest {
    
    public PPARequestTest() {
    }

    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        PPARequest org = DomRepoTestBuilder.makePPARequest();
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        PPARequest cpy = mapper.readValue(json, PPARequest.class);        
        assertEquals(org,cpy);
    }
    
    @Test
    public void readsUIJSON() throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        
        String json = "{\"windowStart\":5,\"windowEnd\":100,\"periodMin\":18,\"periodMax\":35,\"method\":\"MFourFit\",\"detrending\":\"POLY_DTR\",\"methodN\":\"MFourFit\",\"detrendingN\":\"POLY_DTR\"}";
        
        PPARequest cpy = mapper.readValue(json, PPARequest.class);
        
        assertEquals(5,cpy.windowStart,1E-6);
        assertEquals(100,cpy.windowEnd,1E-6);
        assertEquals(18,cpy.periodMin,1E-6);
        assertEquals(35,cpy.periodMax,1E-6);
        assertEquals(DetrendingType.POLY_DTR,cpy.detrending);
        assertEquals(PPAMethod.MFourFit,cpy.method);        
    }

    @Test
    public void isValidGivesTrueForValidObject() {
        PPARequest req = DomRepoTestBuilder.makePPARequest();
        assertTrue(req.isValid());
    }
    
    @Test
    public void isValidGivesFalseForNegativeValues() {
        PPARequest req;
        
        req = DomRepoTestBuilder.makePPARequest();
        req.windowStart = -1;
        assertFalse(req.isValid());
        
        req = DomRepoTestBuilder.makePPARequest();
        req.windowEnd = -1;
        assertFalse(req.isValid());
        
        req = DomRepoTestBuilder.makePPARequest();
        req.periodMin = -1;
        assertFalse(req.isValid());
        
        req = DomRepoTestBuilder.makePPARequest();
        req.periodMax = -1;
        assertFalse(req.isValid());
        
    }
    
    @Test
    public void notValidForMixtupEnds() {
        PPARequest req;
        
        req = DomRepoTestBuilder.makePPARequest();
        req.windowStart = 10;
        req.windowEnd = 2;
        assertFalse(req.isValid());
        
        req = DomRepoTestBuilder.makePPARequest();
        req.periodMin = 30;
        req.periodMax = 10;
        assertFalse(req.isValid());
        
        
    }    
    
}
