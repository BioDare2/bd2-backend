/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.contribution;

import ed.biodare2.backend.repo.isa_dom.contribution.Funding;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class FundingTest {
    
    public FundingTest() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @Test
    public void serializesToJSONAndBack() throws JacksonException {

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
