/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.robust.dom.tsprocessing.Statistics;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Zielu
 */
public class StatsUtilTest {
    
    static double EPS = 1E-6;
    
    public StatsUtilTest() {
    }
    
    @Before
    public void setUp() {
    }
    
 

    @Test
    public void calculateStatsCanHandleEmptySets() {
        double[] values = new double[0];
        StatsUtil.StatsType statsType = StatsUtil.StatsType.FULL;
        
        Statistics stat = StatsUtil.calculateStat(values, statsType);
        
        assertEquals(0, stat.getN());
        assertEquals(Double.NaN, stat.getMean(), EPS);
        assertEquals(Double.NaN, stat.getStdErr(), EPS);
        assertEquals(Double.NaN, stat.getStdDev(), EPS);
        assertEquals(Double.NaN, stat.getMedian(), EPS);
        assertEquals(Double.NaN, stat.getMin(), EPS);
        assertEquals(Double.NaN, stat.getMax(), EPS);
        assertEquals(0, stat.getSum(), EPS);
        assertEquals(Double.NaN, stat.getKurtosis(), EPS);
        assertEquals(Double.NaN, stat.getSkewness(), EPS);

    }
    
   @Test
    public void calculateErrorWeightedStatCanHandleEmptySets() {
        double[] values = new double[0];
        double[] errs = new double[0];
        StatsUtil.StatsType statsType = StatsUtil.StatsType.FULL;
        
        Statistics stat = StatsUtil.calculateErrorWeightedStat(values, errs, statsType);
        
        assertEquals(0, stat.getN());
        assertEquals(Double.NaN, stat.getMean(), EPS);
        assertEquals(Double.NaN, stat.getStdErr(), EPS);
        assertEquals(Double.NaN, stat.getStdDev(), EPS);
        assertEquals(Double.NaN, stat.getMedian(), EPS);
        assertEquals(Double.NaN, stat.getMin(), EPS);
        assertEquals(Double.NaN, stat.getMax(), EPS);
        assertEquals(0, stat.getSum(), EPS);
        assertEquals(Double.NaN, stat.getKurtosis(), EPS);
        assertEquals(Double.NaN, stat.getSkewness(), EPS);

    }    
    
}
