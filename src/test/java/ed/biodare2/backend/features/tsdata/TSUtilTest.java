/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata;

import ed.biodare.jobcentre2.dom.TSData;
import static ed.biodare2.backend.features.tsdata.TSUtil.*;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeDataTraces;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class TSUtilTest {
    
    public TSUtilTest() {
    }
    double EPS = 1E-6;
    
    @Test
    public void prepareDataTrimsData() {

        List<DataTrace> dataSet = makeDataTraces(1,2,48);
        double windowStart = 2;
        double windowEnd = 5;
        
        List<TSData> data = prepareTSData(dataSet, windowStart, windowEnd);
        
        assertEquals(2, data.get(0).trace.getFirst().getTime(),EPS);
        assertEquals(5, data.get(0).trace.getLast().getTime(),EPS);
        assertEquals(2, data.get(1).trace.getFirst().getTime(),EPS);
        assertEquals(5, data.get(1).trace.getLast().getTime(),EPS);
        
    }
    
    @Test
    public void prepareDataTrimsStartOfData() {

        List<DataTrace> dataSet = makeDataTraces(1,2,48);
        double windowStart = 2;
        double windowEnd = 0;
        
        assertEquals(0, dataSet.get(0).trace.getFirst().getTime(),EPS);
        assertEquals(47, dataSet.get(0).trace.getLast().getTime(),EPS);
        
        List<TSData> data = prepareTSData(dataSet, windowStart, windowEnd);
        
        assertEquals(2, data.get(0).trace.getFirst().getTime(),EPS);
        assertEquals(47, data.get(0).trace.getLast().getTime(),EPS);
        
    }    
    
    @Test
    public void prepareDataTrimsEndOfData() {

        List<DataTrace> dataSet = makeDataTraces(1,2,48);
        double windowStart = 0;
        double windowEnd = 15;
        
        assertEquals(0, dataSet.get(0).trace.getFirst().getTime(),EPS);
        assertEquals(47, dataSet.get(0).trace.getLast().getTime(),EPS);
        
        List<TSData> data = prepareTSData(dataSet, windowStart, windowEnd);
        
        assertEquals(0, data.get(0).trace.getFirst().getTime(),EPS);
        assertEquals(15, data.get(0).trace.getLast().getTime(),EPS);
        
    }    
}
