/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.exp;

import ed.biodare2.backend.repo.isa_dom.exp.ExperimentCharacteristic;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
/**
 *
 * @author tzielins
 */
public class ExperimentCharacteristicTest {
    
    public ExperimentCharacteristicTest() {
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

        ExperimentCharacteristic org = new ExperimentCharacteristic();
        org.hasDataFiles = true;
        org.hasPPAJobs = false;
        org.hasTSData = true;
        org.hasAttachments = true;
        org.attachmentsSize = 2;
        
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println("ExperimentFeatures JSON:\n"+json+"\n");
        
        ExperimentCharacteristic cpy = mapper.readValue(json, ExperimentCharacteristic.class);        
        assertEquals(org.hasDataFiles, cpy.hasDataFiles);
        assertEquals(org,cpy);
        assertReflectionEquals(org, cpy);
        
        //can read legacy parts
        String str = "{\n" +
"  \"hasAttachments\" : true,\n" +
"  \"hasTSData\" : true,\n" +
"  \"hasPPAJobs\" : false,\n" +
"  \"hasDataFiles\" : true,\n" +
"  \"attachmentsSize\" : 2\n" +
"}";
        cpy = mapper.readValue(str, ExperimentCharacteristic.class); 
        assertReflectionEquals(org, cpy);
    }
    
}
