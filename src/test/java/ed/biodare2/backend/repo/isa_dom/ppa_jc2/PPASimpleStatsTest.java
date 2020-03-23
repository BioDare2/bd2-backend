/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa_jc2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ed.robust.dom.tsprocessing.PhaseType;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 *
 * @author tzielins
 */
public class PPASimpleStatsTest {
    
    public PPASimpleStatsTest() {
    }
    
    ObjectMapper mapper;
    
    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }    
    
    public static PPASimpleStats makeSimpleStats() {
        
        PPASimpleStats org = new PPASimpleStats();

        org.memberDataId = 1;
        org.rawId = 1;
        org.bioId = 2;
        org.envId = 3;
              
        org.label = "Cos";
        
        org.ERR = 1;
        org.GOF = 0;
        org.N = 23;
        org.period = 24;
        org.periodStd = 0.5;
        
        org.amplitude.put(PhaseType.ByFit,2.0);
        org.amplitude.put(PhaseType.ByMethod,2.1);
        
        org.phaseToZero.put(PhaseType.ByFit,3.0);
        org.phaseToZero.put(PhaseType.ByMethod,3.1);
        
        org.phaseToWindow.put(PhaseType.ByAvgMax,4.0);
        org.phaseToWindow.put(PhaseType.ByFirstPeak,4.1);
        
        org.phaseToWindowCirc.put(PhaseType.ByAvgMax,5.0);
        org.phaseToWindowCirc.put(PhaseType.ByFirstPeak,5.1);        

        org.phaseStd.put(PhaseType.ByFit,3.0);
        org.phaseCircStd.put(PhaseType.ByMethod,3.1);
        
        return org;
    }
    
    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        PPASimpleStats org = makeSimpleStats();

        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        PPASimpleStats cpy = mapper.readValue(json, PPASimpleStats.class); 
        assertReflectionEquals(org,cpy); 
        
    }    
    
}
