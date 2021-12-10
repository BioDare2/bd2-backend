/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.datahandling;

import ed.robust.dom.data.TimeSeries;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class HourlyInterpolatingBinnerTest {
    
    public HourlyInterpolatingBinnerTest() {
    }
    
    HourlyInterpolatingBinner instance;
    
    @Before
    public void setUp() {
        instance = new HourlyInterpolatingBinner();
    }

    @Test
    public void binToHourSimpleCases() {
        
        TimeSeries in = new TimeSeries();
        TimeSeries exp = new TimeSeries();
        
        assertEquals(exp, instance.binToHour(in));

        in.add(0, 1);
        exp.add(0, 1);
        assertEquals(exp, instance.binToHour(in));
        
        in.add(1, 2);
        exp.add(1, 2);
        
        assertEquals(exp, instance.binToHour(in));

        in.add(1.1,4);
        in.add(1.5, 4);
        
        exp = new TimeSeries();
        exp.add(0, 1);
        exp.add(1, 3);
        exp.add(2, 4);
        
        assertEquals(exp, instance.binToHour(in));
        
        in.add(3, 5);
        exp.add(3, 5);
        
        assertEquals(exp, instance.binToHour(in));
        
        in.add(3.9, 6);
        exp.add(4, 6);
        assertEquals(exp, instance.binToHour(in));
    }
    
    @Test
    public void binToHourSimpleCases2() {
        
        TimeSeries in = new TimeSeries();
        TimeSeries exp = new TimeSeries();
        

        in.add(0, 1);
        in.add(0.1, 2);
        in.add(0.25, 3);
        in.add(0.4, 2);
        exp.add(0, 2);
        assertEquals(exp, instance.binToHour(in));
        
    }  
    
    @Test
    public void binToHourWithGaps() {
        
        TimeSeries in = new TimeSeries();
        TimeSeries exp = new TimeSeries();
        
        assertEquals(exp, instance.binToHour(in));

        in.add(0, 1);
        in.add(2, 2);
        in.add(5, 4);
        
        exp.add(0, 1);
        exp.add(1, 1.5);
        exp.add(2, 2);
        exp.add(3, 3);
        exp.add(4, 3);
        exp.add(5, 4);
        assertEquals(exp, instance.binToHour(in));
        
        in = new TimeSeries();
        in.add(0.4, 1);
        in.add(1.8, 2);
        in.add(4.9, 4);
        in.add(5.4, 4);
        assertEquals(exp, instance.binToHour(in));
    }    
    
    @Test
    public void addMissingBinsAddsEntries() {
        
        TimeSeries in = new TimeSeries();
        TimeSeries exp = new TimeSeries();

        instance.addMissingBins(in, 1, 2, 1, 2);
        exp.add(1,1.5);
        assertEquals(exp, in);
        
        in = new TimeSeries();
        instance.addMissingBins(in, 2, 4, 1, 2);
        exp = new TimeSeries();
        exp.add(2, 1.5);
        exp.add(3, 1.5);
        assertEquals(exp, in);
        
    }
    
}
