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
import ed.robust.dom.tsprocessing.PhaseType;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
/**
 *
 * @author tzielins
 */
public class PPAResultsGroupSummaryTest {
    
    public PPAResultsGroupSummaryTest() {
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
        PPAResultsGroupSummary org = makePPAReplicateSet("cos1");
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println("Rep Set:\n\n"+json+"\n");
        
        PPAResultsGroupSummary cpy = mapper.readValue(json, PPAResultsGroupSummary.class);        
        // [TODO find reflective eq] assertReflectionEquals(org,cpy); 
        //assertEquals(org,cpy);

    }

    public static PPAResultsGroupSummary makePPAReplicateSet(String label) {
        
        PPAResultsGroupSummary set = new PPAResultsGroupSummary();
        set.label = label;
        set.memberDataId = 12;
        set.bioId = 5;
        set.envId =6;
        set.excluded = 2;
        set.failures = 1;
        
        set.periods.add(24.0);
        set.periods.add(24.1);
        set.periods.add(25.0);
        
        set.phasesToZero.put(PhaseType.ByAvgMax, Arrays.asList(0.0, 0.5, 3.0));
        set.phasesToZero.put(PhaseType.ByFirstPeak, Arrays.asList(1.0, 1.5, 3.0));
        set.phasesToZero.put(PhaseType.ByFit, Arrays.asList(2.0, 2.5, 2.0));
        set.phasesToZero.put(PhaseType.ByMethod, Arrays.asList(3.0, 3.5, 3.0));
        
        set.phasesToWindow.put(PhaseType.ByAvgMax, Arrays.asList(1.0, 0.5, 3.0));
        set.phasesToWindow.put(PhaseType.ByFirstPeak, Arrays.asList(2.0, 1.5, 3.0));
        set.phasesToWindow.put(PhaseType.ByFit, Arrays.asList(3.0, 2.5, 2.0));
        set.phasesToWindow.put(PhaseType.ByMethod, Arrays.asList(4.0, 3.5, 3.0));
        
        set.amplitudes.put(PhaseType.ByAvgMax, Arrays.asList(12.0, 12.5));
        set.amplitudes.put(PhaseType.ByFirstPeak, Arrays.asList(12.0, 11.5));
        set.amplitudes.put(PhaseType.ByFit, Arrays.asList(13.0, 12.5));
        set.amplitudes.put(PhaseType.ByMethod, Arrays.asList(14.0, 13.5));
        
        return set;
    }
}
