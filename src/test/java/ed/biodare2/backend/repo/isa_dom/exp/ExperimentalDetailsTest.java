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
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import java.time.LocalDate;
import java.time.Month;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

/**
 *
 * @author tzielins
 */
public class ExperimentalDetailsTest {
    
    public ExperimentalDetailsTest() {
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
    public void deserializesJSJSON() throws JacksonException {

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
