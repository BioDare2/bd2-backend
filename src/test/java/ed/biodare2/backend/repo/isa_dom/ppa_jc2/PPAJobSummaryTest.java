/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa_jc2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ed.biodare.jobcentre2.dom.State;
import ed.robust.ppa.PPAMethod;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 *
 * @author tzielins
 */
public class PPAJobSummaryTest {
    
    public PPAJobSummaryTest() {
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

        PPAJobSummary org = makePPAJobSummary();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        PPAJobSummary cpy = mapper.readValue(json, PPAJobSummary.class); 
        assertReflectionEquals(org,cpy); 
        
    }

    public static PPAJobSummary makePPAJobSummary() {
        
        PPAJobSummary job = new PPAJobSummary();
        UUID uuid = UUID.randomUUID();
        job.jobId = uuid;
        
        job.attentionCount = 2;
        job.closed = true;
        job.completed = new Date();
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
        job.modified = new Date();
        job.needsAttention = true;
        job.state = State.FINISHED;
        job.submitted = new Date();
        job.summary = "linear dtr min-120 p(18.0-35.0)";
        
        return job;
    }
    
}
