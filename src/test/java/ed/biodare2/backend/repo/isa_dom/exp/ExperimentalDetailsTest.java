/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.exp;

import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tzielins
 */
public class ExperimentalDetailsTest {
    
    public ExperimentalDetailsTest() {
    }

    ObjectMapper mapper;
    
    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);        
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        ExperimentalDetails org = DomRepoTestBuilder.makeExperimentalDetails();
        

        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println("ExperimentalDetails JSON:\n\n"+json+"\n");
        
        ExperimentalDetails cpy = mapper.readValue(json, ExperimentalDetails.class);        
        //assertEquals(org.contributionDesc,cpy.contributionDesc);
        assertEquals(org.experimentalEnvironments,cpy.experimentalEnvironments);
        assertEquals(org.growthEnvironments,cpy.growthEnvironments);
        //assertEquals(org.generalDesc,cpy.generalDesc);
        assertEquals(org.measurementDesc,cpy.measurementDesc);
        assertEquals(org.executionDate,cpy.executionDate);
        assertEquals(org,cpy);
        //assertTrue(org.hasSameValues(cpy));
    }
   
    @Test
    public void deserializesJSJSON() throws JsonProcessingException, IOException {

        String json = "{\"measurementDesc\":{\"parameters\":[{\"name\":\"last\",\"value\":\"a value\"},{\"name\":\"first\",\"value\":\"2\",\"label\":\"first param\",\"unit\":\"a unit\"},\n" +
"{\"name\":\"second\",\"value\":\"3\",\"label\":\"2n param\"},{\"name\":\"empty\"}],\"technique\":\"Luciferase luminescence\",\n" +
"\"equipment\":\"Topcount 1\",\"description\":\" A description\"},\"growthEnvironments\":{\"environments\":[{\"name\":\"LL\",\"description\":\"Desc\"},\n" +
"{\"name\":\"LD\",\"description\":null}]},\"experimentalEnvironments\":{\"environments\":[{\"name\":\"LL\",\"description\":\"Desc\"},\n" +
"{\"name\":\"LD\",\"description\":null}]},\"executionDate\":[2016,9,23]}";
        
        ExperimentalDetails cpy = mapper.readValue(json, ExperimentalDetails.class);        
        assertEquals(LocalDate.of(2016, Month.SEPTEMBER, 23),cpy.executionDate);
        
        //assertEquals(org.contributionDesc,cpy.contributionDesc);
        //assertEquals(org.experimentalEnvironments,cpy.experimentalEnvironments);
        //assertEquals(org.growthEnvironments,cpy.growthEnvironments);
        //assertEquals(org.generalDesc,cpy.generalDesc);
        //assertEquals(org.measurementDesc,cpy.measurementDesc);
        //assertEquals(org.executionDate,cpy.executionDate);
        //assertEquals(org,cpy);
        //assertTrue(org.hasSameValues(cpy));
    }
    
}
