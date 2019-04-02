/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.contribution;

import ed.biodare2.backend.repo.isa_dom.contribution.ContributionDesc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
public class ContributionDescTest {
    
    public ContributionDescTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        ContributionDesc org = DomRepoTestBuilder.makeContributionDesc();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
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
