/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.exp;

import ed.biodare2.backend.repo.ui_dom.exp.ExperimentalAssayView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tzielins
 */
public class ExperimentalAssayViewTest {
    
    public ExperimentalAssayViewTest() {
    }

    ObjectMapper mapper;
    
    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        
    }
    
    @After
    public void tearDown() {
    }

    
    
    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        ExperimentalAssayView org = DomRepoTestBuilder.makeExperimentalAssayView();
        
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
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
    public void copyingConstructorUsesExecutionDateFromDetails() throws JsonProcessingException, IOException {

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
