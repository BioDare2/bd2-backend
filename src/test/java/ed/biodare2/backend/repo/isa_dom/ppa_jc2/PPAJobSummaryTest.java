/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa_jc2;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import ed.biodare.jobcentre2.dom.State;
import ed.robust.ppa.PPAMethod;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 *
 * @author tzielins
 */
public class PPAJobSummaryTest {
    
    public PPAJobSummaryTest() {
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

        PPAJobSummary org = makePPAJobSummary();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        PPAJobSummary cpy = mapper.readValue(json, PPAJobSummary.class); 
        // [TODO find reflective eq] assertReflectionEquals(org,cpy); 
        
    }

    public static PPAJobSummary makePPAJobSummary() {
        
        PPAJobSummary job = new PPAJobSummary();
        UUID uuid = UUID.randomUUID();
        job.jobId = uuid;
        
        job.attentionCount = 2;
        job.closed = true;
        job.completed = LocalDateTime.now();
        job.dataSetId ="10050_LIN_DTR";
        job.dataSetType = "LIN_DTR";
        job.dataSetTypeName = "linear dtr";
        job.dataWindow = "min-120";
        job.dataWindowEnd = 120;
        job.dataWindowStart = 0;
        job.failures = 2;
        job.lastError = "";
        job.max_period = 35;
        job.min_period = 18;
        job.message = "";
        job.method = PPAMethod.MESA;
        job.modified = LocalDateTime.now();
        job.needsAttention = true;
        job.state = State.FINISHED;
        job.submitted = LocalDateTime.now();
        job.summary = "linear dtr min-120 p(18.0-35.0)";
        
        return job;
    }
}
