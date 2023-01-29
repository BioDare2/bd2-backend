/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.ppa;

import ed.biodare2.backend.repo.ui_dom.ppa.PPAFitPack;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ed.biodare2.backend.repo.ui_dom.shared.SimpleOption;
import ed.biodare2.backend.repo.ui_dom.tsdata.Trace;
import ed.biodare2.backend.repo.ui_dom.tsdata.TraceSet;
import ed.robust.util.timeseries.TSGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
//import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 *
 * @author tzielins
 */
public class PPAFitPackTest {
    
    public PPAFitPackTest() {
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

        PPAFitPack org = makePack();
        
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println("FitPack:\n\n"+json+"\n");
        
        PPAFitPack cpy = mapper.readValue(json, PPAFitPack.class);        
        // [TODO find reflective eq] assertReflectionEquals(org,cpy); 
        //assertEquals(org,cpy);
        
        
    }
    
    protected PPAFitPack makePack() {
        PPAFitPack pack = new PPAFitPack();
        pack.traces = makeFits();
        pack.options = makeOptions();
        return pack;
    }
    
    protected TraceSet makeFits() {
        TraceSet set = new TraceSet();
        set.title = "1. WT";
        
        set.traces.add(makeTrace("1. WT"));
        set.traces.add(makeTrace("Fit"));
        return set;
    }    

    protected Trace makeTrace(String label) {
        Trace trace = new Trace();
        trace.label = label;
        trace.setTimeseries(TSGenerator.makeCos(100, 1, 24, 2));
        return trace;
    }

    protected List<SimpleOption> makeOptions() {
        List<SimpleOption> list = new ArrayList<>();
        list.add(new SimpleOption("24000","24.00"));
        list.add(new SimpleOption("25100","25.10"));
        list.add(new SimpleOption("120700","2120.70"));
        return list;
    }


    
}
