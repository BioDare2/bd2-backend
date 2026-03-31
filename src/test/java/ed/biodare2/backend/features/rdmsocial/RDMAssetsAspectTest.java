/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rdmsocial;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import static ed.biodare2.BioDare2TestUtils.assertFieldsEquals;
import ed.biodare2.backend.repo.system_dom.EntityType;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class RDMAssetsAspectTest {
    
    public RDMAssetsAspectTest() {
    }
    
    ObjectMapper mapper;
    
    @BeforeEach
    public void setUp() {
	ObjectMapper mapper = JsonMapper.builder()
	    .enable(SerializationFeature.INDENT_OUTPUT)
	    .build();	
    }
    
    @Test
    public void serializesToJSONAndBack() throws JacksonException, IOException {

        RDMAssetsAspect org = new RDMAssetsAspect();
        org.cohort = RDMCohort.ADIVSE;
        org.entityType = EntityType.EXP_ASSAY;
        org.measurementAdded = true;
        org.measurementAddedAtUpdate = 1;
        org.measurementAddedAtWarning = 2;
        org.measurementWarnings = 3;
        org.parentId = 2;
        org.updates = 1;
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        RDMAssetsAspect cpy = mapper.readValue(json, RDMAssetsAspect.class);        
        assertFieldsEquals(org,cpy);
        
    }    
}
