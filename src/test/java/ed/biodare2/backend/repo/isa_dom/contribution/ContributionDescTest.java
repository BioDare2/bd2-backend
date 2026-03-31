/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.contribution;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class ContributionDescTest {
    
    public ContributionDescTest() {
    }
    
    @Test
    public void serializesToJSONAndBack() throws JacksonException {

        ContributionDesc org = DomRepoTestBuilder.makeContributionDesc();
	ObjectMapper mapper = JsonMapper
	    .builder()
	    .enable(SerializationFeature.INDENT_OUTPUT)
	    .build();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        ContributionDesc cpy = mapper.readValue(json, ContributionDesc.class);        
        assertEquals(org.authors,cpy.authors);
        assertEquals(org.curators,cpy.curators);
        assertEquals(org.fundings,cpy.fundings);
        assertEquals(org.institutions,cpy.institutions);
        assertEquals(org,cpy);
    }
    
}
