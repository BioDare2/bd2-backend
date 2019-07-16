/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.rhythmicity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.robust.dom.data.DetrendingType;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@RunWith(SpringRunner.class)
@JsonTest
public class RhythmicityRequestTest {
    
    public RhythmicityRequestTest() {
    }
    
    @Autowired
    ObjectMapper mapper;
    
    RhythmicityRequest instance;
    
    @Before
    public void setUp() {
        
        instance = DomRepoTestBuilder.makeRhythmicityRequest();
    }

    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        RhythmicityRequest org = instance;
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        RhythmicityRequest cpy = mapper.readValue(json, RhythmicityRequest.class);        
        assertEquals(org,cpy);
    }
    
    @Test
    public void readsUIJSON() throws JsonProcessingException, IOException {
        
        String json = "{\"windowStart\":5,\"windowEnd\":100,\"periodMin\":18,\"periodMax\":35,\"method\":\"MFourFit\",\"detrending\":\"POLY_DTR\",\"methodN\":\"MFourFit\",\"detrendingN\":\"POLY_DTR\"}";
        
        RhythmicityRequest cpy = mapper.readValue(json, RhythmicityRequest.class);
        
        assertEquals(5,cpy.windowStart,1E-6);
        assertEquals(100,cpy.windowEnd,1E-6);
        assertEquals(18,cpy.periodMin,1E-6);
        assertEquals(35,cpy.periodMax,1E-6);
        assertEquals(DetrendingType.POLY_DTR,cpy.detrending);
        assertEquals("",cpy.method);        
        assertEquals("",cpy.preset);        
    }

    @Test
    public void isValidGivesTrueForValidObject() {
        
        assertTrue(instance.isValid());
    }
    
    @Test
    public void isValidGivesFalseForNegativeValues() {
        RhythmicityRequest req;
        
        req = DomRepoTestBuilder.makeRhythmicityRequest();
        req.windowStart = -1;
        assertFalse(req.isValid());
        
        req = DomRepoTestBuilder.makeRhythmicityRequest();
        req.windowEnd = -1;
        assertFalse(req.isValid());
        
        req = DomRepoTestBuilder.makeRhythmicityRequest();
        req.periodMin = -1;
        assertFalse(req.isValid());
        
        req = DomRepoTestBuilder.makeRhythmicityRequest();
        req.periodMax = -1;
        assertFalse(req.isValid());
        
    }
    
    @Test
    public void notValidForMixtupEnds() {
        RhythmicityRequest req;
        
        req = DomRepoTestBuilder.makeRhythmicityRequest();
        req.windowStart = 10;
        req.windowEnd = 2;
        assertFalse(req.isValid());
        
        req = DomRepoTestBuilder.makeRhythmicityRequest();
        req.periodMin = 30;
        req.periodMax = 10;
        assertFalse(req.isValid());
        
        
    }    
    
}
