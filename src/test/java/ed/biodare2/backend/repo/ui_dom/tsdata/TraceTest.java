/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.tsdata;

import ed.biodare2.backend.repo.ui_dom.tsdata.Trace;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.ui_dom.security.SecuritySummary;
import ed.robust.dom.data.TimeSeries;
import ed.robust.util.timeseries.TSGenerator;
import java.io.IOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
//import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 *
 * @author Zielu
 */
public class TraceTest {
    
    public TraceTest() {
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

        Trace org = makeTrace("cos1");
        
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
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
