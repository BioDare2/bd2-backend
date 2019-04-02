/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare2.backend.repo.isa_dom.dataimport.DataColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class FakeIdExtractorTest {
    
    public FakeIdExtractorTest() {
    }

    protected List<DataTrace> fakeTraces() {
        
        List<DataTrace> traces = new ArrayList<>();
        DataTrace trace;
        trace = new DataTrace();
        trace.details = new DataColumnProperties("T1");
        traces.add(trace);
        
        trace = new DataTrace();
        trace.details = new DataColumnProperties("WT");
        traces.add(trace);
        
        trace = new DataTrace();
        trace.details = new DataColumnProperties("T1");
        traces.add(trace);
        
        trace = new DataTrace();
        trace.details = new DataColumnProperties("T2");
        traces.add(trace);
        
        
        trace = new DataTrace();
        trace.details = new DataColumnProperties("WT");
        traces.add(trace);
        
        String REFS = "ABCDEFG";
        int nr = 1;
        for (DataTrace t : traces) {
            t.traceRef = REFS.substring(nr-1,nr);
            t.traceNr = nr++;
            t.dataId = t.traceNr;
            t.rawDataId = t.traceNr+1;
        }
        
        return traces;
    }
    

    @Test
    public void throwsExceptionOnUnitializedList() {
        
        FakeIdExtractor instance = new FakeIdExtractor(Collections.emptyList());
        
        DataTrace trace1;
        trace1 = new DataTrace();
        trace1.traceNr = 1;
        trace1.dataId = trace1.traceNr;
        trace1.traceRef = "A";
        trace1.details = new DataColumnProperties("T1");

        try {
            long id1 = instance.getBioId(trace1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {};
        
    }
    
    @Test
    public void givesDifferentIdsForDifferentTracesLabels() {
        
        List<DataTrace> traces = fakeTraces();
        
        DataTrace trace1,trace2;
        trace1 = traces.get(0);
        trace2 = traces.get(1);
        assertNotEquals(trace1.details.dataLabel, trace2.details.dataLabel);
        
        FakeIdExtractor instance = new FakeIdExtractor(traces);
        
        long id1 = instance.getBioId(trace1);
        long id2 = instance.getBioId(trace2);
        
        assertNotEquals(id1, id2);
        
        assertEquals(trace1.details.dataLabel, traces.get(2).details.dataLabel);
        id2 = instance.getBioId(traces.get(2));
        assertEquals(id1,id2);
        
        id1 = instance.getCondId(trace1);
        id2 = instance.getCondId(trace2);
        
        assertNotEquals(id1, id2);
        
        assertEquals(trace2.details.dataLabel, traces.get(4).details.dataLabel);
        id1 = instance.getCondId(traces.get(4));
        assertEquals(id1,id2);  
        
    }
    
    @Test
    public void instancesBasedOnSameInputGivesTheSameIds() {
        
        List<DataTrace> traces = fakeTraces();
        FakeIdExtractor instance1 = new FakeIdExtractor(traces);
        FakeIdExtractor instance2 = new FakeIdExtractor(traces);
        
        for (DataTrace trace : traces) {
            assertEquals(instance1.getBioId(trace),instance2.getBioId(trace));
            assertEquals(instance1.getCondId(trace),instance2.getCondId(trace));
        }        
    }
    
    @Test
    public void idsMapsBackToLabels() {
        
        List<DataTrace> traces = fakeTraces();
        FakeIdExtractor instance1 = new FakeIdExtractor(traces);
        FakeIdExtractor instance2 = new FakeIdExtractor(traces);
        
        for (DataTrace trace : traces) {
            assertEquals(trace.details.dataLabel,instance2.getBioLabel(instance1.getBioId(trace)));
            assertEquals(trace.details.dataLabel,instance2.getCondLabel(instance1.getCondId(trace)));
        }        
    }
    
    @Test
    public void dataRefBasedOnTraceNrAndItsRefByDataId() {
        
        List<DataTrace> traces = fakeTraces();
        FakeIdExtractor instance1 = new FakeIdExtractor(traces);
        
        String exp = "2. [B]";
        String res = instance1.getDataRef(traces.get(1).dataId);
        
        assertEquals(exp,res);
        
    }    
    
}
