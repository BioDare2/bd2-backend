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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

/**
 *
 * @author tzielins
 */
public class ExperimentalAssayViewTest {
    
    public ExperimentalAssayViewTest() {
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

        ExperimentalAssayView org = DomRepoTestBuilder.makeExperimentalAssayView();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        // System.out.println("ExperimentAssayView JSON:\n\n"+json+"\n");
        
        ExperimentalAssayView cpy = mapper.readValue(json, ExperimentalAssayView.class);        
        assertEquals(org.id, cpy.id);
        assertEquals(org.generalDesc,cpy.generalDesc);
        assertEquals(org.contributionDesc,cpy.contributionDesc);
        assertEquals(org.experimentalDetails,cpy.experimentalDetails);
        assertEquals(org.features,cpy.features);
        assertEquals(org.provenance,cpy.provenance);
        assertEquals(org.security,cpy.security);
        
        
    }
    
    @Test
    public void copyingConstructorUsesExecutionDateFromDetails() throws JacksonException {

        ExperimentalAssay assay = DomRepoTestBuilder.makeExperimentalAssay();
        LocalDate date = LocalDate.now().minus(1, ChronoUnit.DAYS);
        assay.experimentalDetails.executionDate = date;
        
        ExperimentalAssayView cpy = new ExperimentalAssayView(assay);        
        assertEquals(date, cpy.generalDesc.executionDate);
        assertEquals(assay.contributionDesc,cpy.contributionDesc);
        assertEquals(assay.experimentalDetails,cpy.experimentalDetails);
        assertEquals(assay.characteristic,cpy.features);
        assertEquals(assay.species,cpy.species);
        assertEquals(assay.dataCategory,cpy.dataCategory);
    }    
}
