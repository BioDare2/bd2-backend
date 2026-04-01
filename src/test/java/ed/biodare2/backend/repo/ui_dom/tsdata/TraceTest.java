/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.tsdata;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import ed.robust.dom.data.TimeSeries;
import ed.robust.util.timeseries.TSGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 *
 * @author Zielu
 */
public class TraceTest {
    
    public TraceTest() {
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

        Trace org = makeTrace("cos1");
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println("Trace:\n\n"+json+"\n");
        
        Trace cpy = mapper.readValue(json, Trace.class);        
        // [TODO find reflective eq] assertReflectionEquals(org,cpy); 
        //assertEquals(org,cpy);
    }
    
    @Test
    public void canConvertEmptyTS() {
        Trace trace = new Trace();
        trace.setTimeseries(new TimeSeries());
        assertNotNull(trace);
        assertEquals(Double.NaN,trace.max,1E-6);
    }

    protected Trace makeTrace(String label) {
        Trace trace = new Trace();
        trace.label = label;
        trace.setTimeseries(TSGenerator.makeCos(100, 1, 24, 2));
        return trace;
    }
}
