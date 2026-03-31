/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.exp;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
//import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
/**
 *
 * @author tzielins
 */
public class ExperimentCharacteristicTest {
    
    public ExperimentCharacteristicTest() {
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

        ExperimentCharacteristic org = new ExperimentCharacteristic();
        org.hasDataFiles = true;
        org.hasPPAJobs = false;
        org.hasTSData = true;
        org.hasAttachments = true;
        org.attachmentsSize = 2;
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println("ExperimentFeatures JSON:\n"+json+"\n");
        
        ExperimentCharacteristic cpy = mapper.readValue(json, ExperimentCharacteristic.class);        
        assertEquals(org.hasDataFiles, cpy.hasDataFiles);
        assertEquals(org,cpy);
        // [TODO find reflective eq] assertReflectionEquals(org, cpy);
        
        //can read legacy parts
        String str = "{\n" +
"  \"hasAttachments\" : true,\n" +
"  \"hasTSData\" : true,\n" +
"  \"hasPPAJobs\" : false,\n" +
"  \"hasDataFiles\" : true,\n" +
"  \"attachmentsSize\" : 2\n" +
"}";
        cpy = mapper.readValue(str, ExperimentCharacteristic.class); 
        // [TODO find reflective eq] assertReflectionEquals(org, cpy);
    }
}
