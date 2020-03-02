/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

import ed.biodare.jobcentre2.dom.RhythmicityConstants;
import ed.biodare.jobcentre2.dom.TSData;
import ed.biodare.jobcentre2.dom.TSDataSetJobRequest;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeDataTraces;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeRhythmicityRequest;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityRequest;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class RhythmicityUtilsTest {
    
    public RhythmicityUtilsTest() {
    }
    
    double EPS = 1E-6;
    
    RhythmicityUtils instance;
    
    @Before
    public void setUp() {
        
        instance = new RhythmicityUtils();
    }

    /**
     * Test of prepareJobRequest method, of class RhythmicityUtils.
     */
    @Test
    public void testPrepareJobRequest() {
        long expId = 123L;
        RhythmicityRequest request = makeRhythmicityRequest();
        request.windowStart = 2;
        List<DataTrace> dataSet = makeDataTraces(1,5,48);
        TSDataSetJobRequest result = instance.prepareJobRequest(expId, request, dataSet);
        
        assertNotNull(result);
        assertEquals("123", result.externalId);
        assertEquals("BD2EJTK",result.method);
        assertEquals("BD2_CLASSIC", result.parameters.get(RhythmicityConstants.PRESET_KEY));
        assertEquals("100000", result.parameters.get(RhythmicityConstants.NULL_SIZE_KEY));
        assertEquals(""+request.periodMin, result.parameters.get(RhythmicityConstants.PERIOD_MIN_KEY));
        assertEquals(""+request.periodMax, result.parameters.get(RhythmicityConstants.PERIOD_MAX_KEY));
        
        assertEquals(5, result.data.size());
        assertEquals(2.0, result.data.get(0).trace.getFirst().getTime(),1E-6);
    }

    @Test
    public void prepareDataTrimsData() {

        List<DataTrace> dataSet = makeDataTraces(1,2,48);
        double windowStart = 2;
        double windowEnd = 5;
        
        List<TSData> data = instance.prepareData(dataSet, windowStart, windowEnd);
        
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
        
        List<TSData> data = instance.prepareData(dataSet, windowStart, windowEnd);
        
        assertEquals(2, data.get(0).trace.getFirst().getTime(),EPS);
        assertEquals(47, data.get(0).trace.getLast().getTime(),EPS);
        
    } 
    
    @Test
    public void completeRequestChangesPeriodMinMax() {
        RhythmicityRequest request = makeRhythmicityRequest();
        request.periodMin = 1;
        request.periodMax = 20;
        request.preset = "EJTK_CLASSIC";
        
        instance.completeRequest(request);
        assertEquals(24, request.periodMin, EPS);
        assertEquals(24, request.periodMax, EPS);
        
        
        request.preset = "BD2_CLASSIC";
        
        instance.completeRequest(request);
        assertEquals(18, request.periodMin, EPS);
        assertEquals(35, request.periodMax, EPS);
        
        request.periodMin = 1;
        request.periodMax = 20;
        request.preset = "BD2_SPREAD";
        
        instance.completeRequest(request);
        assertEquals(1, request.periodMin, EPS);
        assertEquals(20, request.periodMax, EPS);
        
    }
    
    @Test
    public void completeRequestChangesCOS24Presets() {
        RhythmicityRequest request = makeRhythmicityRequest();
        request.periodMin = 1;
        request.periodMax = 20;
        request.preset = "COS_1H";
        
        instance.completeRequest(request);
        assertEquals(24, request.periodMin, EPS);
        assertEquals(24, request.periodMax, EPS);
        request.preset = "COS_1H";
        
        request.periodMin = 1;
        request.periodMax = 20;
        request.preset = "COS_2H";
        
        instance.completeRequest(request);
        assertEquals(24, request.periodMin, EPS);
        assertEquals(24, request.periodMax, EPS);
        request.preset = "COS_2H";
        
        request.periodMin = 1;
        request.periodMax = 20;
        request.preset = "COS_4H";
        
        instance.completeRequest(request);
        assertEquals(24, request.periodMin, EPS);
        assertEquals(24, request.periodMax, EPS);
        request.preset = "COS_4H";
        

        
    }    
    
    @Test
    public void prepareDataLeavesDataIfWindows0() {
        
        
        List<DataTrace> dataSet = makeDataTraces(1,2,48);
        double windowStart = 0;
        double windowEnd = 0;
        
        List<TSData> data = instance.prepareData(dataSet, windowStart, windowEnd);
        
        assertSame(dataSet.get(0).trace, data.get(0).trace);
        assertSame(dataSet.get(1).trace, data.get(1).trace);
        
    }    
    
}
