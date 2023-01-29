/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa_jc2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ed.robust.dom.tsprocessing.PhaseType;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
//import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 *
 * @author tzielins
 */
public class PPASimpleResultEntryTest {
    
    public PPASimpleResultEntryTest() {
    }
    
    ObjectMapper mapper;
    
    
    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }
    
    public static PPASimpleResultEntry makePPASimpleResultEntry() {
        PPASimpleResultEntry entry = new PPASimpleResultEntry();
        
        entry.ERR = 0.5;
        entry.GOF = 0.2;
        
        double val = 1;
        for (PhaseType phase : PhaseType.values()) {
            entry.amplitude.put(phase, val++);
            entry.phaseToWindow.put(phase,val++);
            entry.phaseToWindowCirc.put(phase,val++);
            entry.phaseToZero.put(phase,val++);
            entry.phaseToZeroCirc.put(phase,val++);
        }
        entry.amplitudeErr = 2;
        entry.bioId = 3;
        entry.dataId = 4;
        entry.dataRef = "4. [D]";
        entry.dataType = "LIN_DTR";
        entry.envId = 5;
        entry.failed = false;
        entry.circadian = true;
        entry.ignored = false;
        entry.jobId = UUID.randomUUID();
        entry.jobSummary = "linear dtr min-max p(18.0-35.0)";
        entry.label = "WT";
        entry.message = null;
        entry.attention = false;
        entry.orgId = "D";
        entry.period = 24;
        entry.periodErr = 0.6;
        entry.phaseCircErr = 1;
        entry.phaseErr = 0.8;
        entry.rawId = 1;
        return entry;
    }    

    @Test
    public void serializesToJSONAndBack() throws Exception {
        PPASimpleResultEntry org = makePPASimpleResultEntry();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        PPASimpleResultEntry cpy = mapper.readValue(json, PPASimpleResultEntry.class); 
        // [TODO find reflective eq] assertReflectionEquals(org,cpy); 
    }
    
}
