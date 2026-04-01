/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.security;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

/**
 *
 * @author tzielins
 */
public class SecuritySummaryTest {
    
    public SecuritySummaryTest() {
    }

    ObjectMapper mapper;
    
    @BeforeEach
    public void setUp() {
	mapper = JsonMapper
	    .builder()
	    .enable(SerializationFeature.INDENT_OUTPUT)
	    .build();
    }
    
    @Test
    public void serializesToJSONAndBack() throws JacksonException {

        SecuritySummary org = DomRepoTestBuilder.makeSecuritySummary();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println("SecuritySummary:\n\n"+json+"\n");
        
        SecuritySummary cpy = mapper.readValue(json, SecuritySummary.class);        
        assertEquals(org.canRead, cpy.canRead);
        assertEquals(org,cpy);
    }
}
