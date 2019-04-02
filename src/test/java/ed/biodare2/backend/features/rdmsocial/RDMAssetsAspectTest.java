/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rdmsocial;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import static ed.biodare2.BioDare2TestUtils.assertFieldsEquals;
import ed.biodare2.backend.repo.system_dom.EntityType;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tzielins
 */
public class RDMAssetsAspectTest {
    
    public RDMAssetsAspectTest() {
    }
    
    ObjectMapper mapper;
    
    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    
    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

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
