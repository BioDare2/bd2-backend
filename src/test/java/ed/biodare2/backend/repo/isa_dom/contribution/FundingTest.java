/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.contribution;

import ed.biodare2.backend.repo.isa_dom.contribution.Funding;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class FundingTest {
    
    public FundingTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    

    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        Funding org = DomRepoTestBuilder.makeFunding("UoE","1234");
        ObjectMapper mapper = new ObjectMapper();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        Funding cpy = mapper.readValue(json, Funding.class);        
        assertEquals(org.grantNr,cpy.grantNr);
        assertEquals(org.institution,cpy.institution);
        assertEquals(org,cpy);
    }
    
}
