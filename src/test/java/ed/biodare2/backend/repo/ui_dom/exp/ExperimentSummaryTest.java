/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.exp;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class ExperimentSummaryTest {
    
    public ExperimentSummaryTest() {
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

        ExperimentSummary org = new ExperimentSummary(DomRepoTestBuilder.makeExperimentalAssay());
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println("ExperimentSummary JSON:\n\n"+json+"\n");
        
        ExperimentSummary cpy = mapper.readValue(json, ExperimentSummary.class);        
        assertEquals(org.id, cpy.id);
        assertEquals(org.generalDesc,cpy.generalDesc);
        assertEquals(org.features,cpy.features);
        assertEquals(org.provenance,cpy.provenance);
        assertEquals(org.authors,cpy.authors);
        
    }
    
    @Test
    public void hasAuthorsAsString()  {
        ExperimentalAssay assay = DomRepoTestBuilder.makeExperimentalAssay();
        ExperimentSummary org = new ExperimentSummary(assay);
        assertNotNull(org.authors);
        assertTrue(org.authors.contains(assay.contributionDesc.authors.get(0).getName()));
    }
}
