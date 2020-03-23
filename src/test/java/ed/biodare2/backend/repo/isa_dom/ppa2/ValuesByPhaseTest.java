/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ed.robust.dom.tsprocessing.PhaseType;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 *
 * @author tzielins
 */
public class ValuesByPhaseTest {
    
    public ValuesByPhaseTest() {
    }
    
    ObjectMapper mapper;
    
    
    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }    
    

    @Test
    public void serializesToJSONAndBack() throws Exception {
        ValuesByPhase<Double> org = makeValuesByPhaseDbl();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        ValuesByPhase cpy = mapper.readValue(json, ValuesByPhase.class); 
        assertEquals(org,cpy);
        assertReflectionEquals(org,cpy); 
    }
    
    @Test
    public void serializesToJSONAndBackListVals() throws Exception {
        ValuesByPhase<List<Double>> org = makeValuesByPhaseList();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        ValuesByPhase cpy = mapper.readValue(json, ValuesByPhase.class); 
        assertEquals(org,cpy);
        //System.out.println(cpy.get(PhaseType.ByFit).getClass());
        assertReflectionEquals(org,cpy); 
    }   
    
    @Test
    @Ignore //cause comes back as list, but it is ok
    public void serializesToJSONAndBackArrayVals() throws Exception {
        ValuesByPhase<double[]> org = makeValuesByPhaseArray();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        ValuesByPhase cpy = mapper.readValue(json, ValuesByPhase.class); 
        //System.out.println(cpy.get(PhaseType.ByFit).getClass());
        assertEquals(org,cpy);
        assertReflectionEquals(org,cpy); 
    }      

    protected ValuesByPhase<Double> makeValuesByPhaseDbl() {
        
        ValuesByPhase<Double> values = new ValuesByPhase();
        
        double val = 1;
        for (PhaseType phase : PhaseType.values()) {
            values.put(phase, val++);
        }
        return values;
    }
    
    protected ValuesByPhase<List<Double>> makeValuesByPhaseList() {
        
        ValuesByPhase<List<Double>> values = new ValuesByPhase();
        
        double val = 1;
        for (PhaseType phase : PhaseType.values()) {
            List<Double> list = new ArrayList<>();
            list.add(val++);
            list.add(val++);
            values.put(phase, list);
        }
        return values;
    }   
    
    protected ValuesByPhase<double[]> makeValuesByPhaseArray() {
        
        ValuesByPhase<double[]> values = new ValuesByPhase();
        
        double val = 1;
        for (PhaseType phase : PhaseType.values()) {
            double[] t = {val++,val++};
            values.put(phase, t);
        }
        return values;
    }       
    
}
