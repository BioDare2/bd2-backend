/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import static ed.biodare2.backend.repo.isa_dom.dataimport.TimeSeriesMetrics.*;
import ed.robust.dom.data.TimeSeries;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class TimeSeriesMetricsTest {
    
    static final double EPS = 1E-6;
    
    public TimeSeriesMetricsTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void createsFromeEmptyTimeSeries() {
        
        TimeSeries data = new TimeSeries();
        
        TimeSeriesMetrics res = fromTimeSeries(data);
        
        TimeSeriesMetrics exp = new TimeSeriesMetrics();
        exp.series = 1;
        exp.uniformMetrics = true;
                
        assertEquals(exp, res);
        
    }
    
    @Test
    public void createsFromeTimeSeries() {
        
        TimeSeries data = new TimeSeries();
        data.add(1, 1);
        data.add(2, 2);
        data.add(4, 0);
        
        TimeSeriesMetrics res = fromTimeSeries(data);
        
        assertEquals(3, res.maxDuration, EPS);
        assertEquals(3, res.minDuration, EPS);
        assertEquals(3, res.avgDuration, EPS);
        assertEquals(3, res.maxPoints, EPS);
        assertEquals(3, res.minPoints, EPS);
        assertEquals(3, res.avgPoints, EPS);
        assertEquals(1.5, res.maxStep, EPS);
        assertEquals(1.5, res.minStep, EPS);
        assertEquals(1.5, res.avgStep, EPS);
        assertEquals(0.667, res.maxPointsPerHour, EPS);
        assertEquals(0.667, res.minPointsPerHour, EPS);
        assertEquals(0.667, res.avgPointsPerHour, EPS);
        assertEquals(1, res.maxFirstTime, EPS);
        assertEquals(1, res.minFirstTime, EPS);
        assertEquals(1, res.avgFirstTime, EPS);
        assertEquals(4, res.maxLastTime, EPS);
        assertEquals(4, res.minLastTime, EPS);
        assertEquals(4, res.avgLastTime, EPS);                
        assertEquals(2, res.maxValue, EPS);
        assertEquals(0, res.minValue, EPS);
        assertEquals(1, res.avgValue, EPS);
                
        assertEquals(1, res.series);
        assertTrue(res.uniformMetrics);
        
    }    
    
    @Test
    public void createsFromeTimeSeriesRoundsToMili() {
        
        TimeSeries data = new TimeSeries();
        data.add(0.9996, 1);
        data.add(2.00001, 2);
        
        TimeSeriesMetrics res = fromTimeSeries(data);
        
        assertEquals(1, res.maxDuration, EPS);
        assertEquals(1, res.minDuration, EPS);
        assertEquals(1, res.avgDuration, EPS);
        assertEquals(2, res.maxPoints, EPS);
        assertEquals(2, res.minPoints, EPS);
        assertEquals(2, res.avgPoints, EPS);
        assertEquals(1, res.maxStep, EPS);
        assertEquals(1, res.minStep, EPS);
        assertEquals(1, res.avgStep, EPS);
        assertEquals(1, res.maxPointsPerHour, EPS);
        assertEquals(1, res.minPointsPerHour, EPS);
        assertEquals(1, res.avgPointsPerHour, EPS);
        assertEquals(1, res.maxFirstTime, EPS);
        assertEquals(1, res.minFirstTime, EPS);
        assertEquals(1, res.avgFirstTime, EPS);
        assertEquals(2, res.maxLastTime, EPS);
        assertEquals(2, res.minLastTime, EPS);
        assertEquals(2, res.avgLastTime, EPS);                
        assertEquals(2, res.maxValue, EPS);
        assertEquals(1, res.minValue, EPS);
        assertEquals(1.5, res.avgValue, EPS);
                
        assertEquals(1, res.series);
        assertTrue(res.uniformMetrics);
        
    }      
    
    @Test
    public void reduces() {
        
        TimeSeries data1 = new TimeSeries();
        data1.add(1,1);
        data1.add(2,2);
        data1.add(3,2);
        data1.add(4,1);
        
        TimeSeriesMetrics one = fromTimeSeries(data1);
        
        TimeSeries data2 = new TimeSeries();
        data2.add(0,4);
        data2.add(2,4);
        
        TimeSeriesMetrics other = fromTimeSeries(data2);
        
        TimeSeriesMetrics res = reduce(one, other);
        
        
        assertEquals(3, res.maxDuration, EPS);
        assertEquals(2, res.minDuration, EPS);
        assertEquals(2.5, res.avgDuration, EPS);
        assertEquals(4, res.maxPoints, EPS);
        assertEquals(2, res.minPoints, EPS);
        assertEquals(3, res.avgPoints, EPS);
        assertEquals(2, res.maxStep, EPS);
        assertEquals(1, res.minStep, EPS);
        assertEquals(1.5, res.avgStep, EPS);
        assertEquals(1, res.maxPointsPerHour, EPS);
        assertEquals(0.5, res.minPointsPerHour, EPS);
        assertEquals(0.75, res.avgPointsPerHour, EPS);
        assertEquals(1, res.maxFirstTime, EPS);
        assertEquals(0, res.minFirstTime, EPS);
        assertEquals(0.5, res.avgFirstTime, EPS);
        assertEquals(4, res.maxLastTime, EPS);
        assertEquals(2, res.minLastTime, EPS);
        assertEquals(3, res.avgLastTime, EPS);                
        assertEquals(4, res.maxValue, EPS);
        assertEquals(1, res.minValue, EPS);
        assertEquals((1.5+4)/2, res.avgValue, EPS);
                
        assertEquals(2, res.series);
        assertFalse(res.uniformMetrics);

    }
    
}
