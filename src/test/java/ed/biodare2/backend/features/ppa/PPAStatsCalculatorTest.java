/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.robust.dom.tsprocessing.CosComponent;
import ed.robust.dom.tsprocessing.FFT_PPA;
import ed.robust.dom.tsprocessing.PPA;
import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.dom.tsprocessing.PPAStats;
import ed.robust.dom.tsprocessing.PhaseType;
import ed.robust.dom.tsprocessing.WeightedStat;
import ed.robust.dom.tsprocessing.WeightingType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Zielu
 */
public class PPAStatsCalculatorTest {
    
    public PPAStatsCalculatorTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void calculatePhaseStatsCircGivesCorrectStats() {
        
        List<PPAResult> results = new ArrayList<>();
        WeightedStat periodStats = null;
        
        FFT_PPA result = new FFT_PPA();
        PPA byMe = new PPA(24, 12, 1);
        PPA byFit = new PPA(24, 6, 1);
        PPA byFP = new PPA(24, 3, 1);
        PPA byAP = new PPA(24, 1, 1);
        CosComponent cos = new CosComponent(byMe, byFP, byAP, byFit);
        result.addCOS(cos);
        results.add(result);
        
        result = new FFT_PPA();
        byMe = new PPA(48, 24, 1);
        byFit = new PPA(48, 12, 1);
        byFP = new PPA(48, 6, 1);
        byAP = new PPA(48, 2, 1);
        cos = new CosComponent(byMe, byFP, byAP, byFit);
        result.addCOS(cos);
        results.add(result);
        
        result = new FFT_PPA();
        byMe = new PPA(20, 10, 1);
        byFit = new PPA(20, 5, 1);
        byFP = new PPA(20, 2.5, 1);
        byAP = new PPA(20, 1.0*20.0/24.0, 1);
        cos = new CosComponent(byMe, byFP, byAP, byFit);
        result.addCOS(cos);
        results.add(result);
        
        Map<PhaseType,WeightedStat> res = PPAStatsCalculator.calculatePhaseStatsCirc(results, periodStats);
        
        assertEquals(12,res.get(PhaseType.ByMethod).getMean(WeightingType.None),1E-6);
        assertEquals(3,res.get(PhaseType.ByMethod).getN(WeightingType.None),1E-6);
        
        assertEquals(6,res.get(PhaseType.ByFit).getMean(WeightingType.None),1E-6);
        assertEquals(3,res.get(PhaseType.ByFit).getN(WeightingType.None),1E-6);
    
        assertEquals(3,res.get(PhaseType.ByFirstPeak).getMean(WeightingType.None),1E-6);
        assertEquals(3,res.get(PhaseType.ByFirstPeak).getN(WeightingType.None),1E-6);
        
        assertEquals(1,res.get(PhaseType.ByAvgMax).getMean(WeightingType.None),1E-6);
        assertEquals(3,res.get(PhaseType.ByAvgMax).getN(WeightingType.None),1E-6);
        
    }
    
    @Test
    public void calculateStatsGivesCorrectStats() {
        
        List<PPAResult> results = new ArrayList<>();
        
        FFT_PPA result = new FFT_PPA();
        PPA byMe = new PPA(24, 12, 1);
        PPA byFit = new PPA(24, 6, 2);
        PPA byFP = new PPA(24, 3, 3);
        PPA byAP = new PPA(24, 1, 4);
        CosComponent cos = new CosComponent(byMe, byFP, byAP, byFit);
        result.addCOS(cos);
        results.add(result);
        
        result = new FFT_PPA();
        byMe = new PPA(48, 24, 2);
        byFit = new PPA(48, 12, 3);
        byFP = new PPA(48, 6, 4);
        byAP = new PPA(48, 2, 5);
        cos = new CosComponent(byMe, byFP, byAP, byFit);
        result.addCOS(cos);
        results.add(result);
        
        result = new FFT_PPA();
        byMe = new PPA(20, 10, 3);
        byFit = new PPA(20, 5, 4);
        byFP = new PPA(20, 2.5, 5);
        byAP = new PPA(20, 1.0*20.0/24.0, 6);
        cos = new CosComponent(byMe, byFP, byAP, byFit);
        result.addCOS(cos);
        results.add(result);
        
        PPAStats res = PPAStatsCalculator.calculateStats(results);
        
        assertEquals(12,res.getPhaseCirc(PhaseType.ByMethod,WeightingType.None).getMean(),1E-6);
        assertEquals(6,res.getPhaseCirc(PhaseType.ByFit,WeightingType.None).getMean(),1E-6);
        assertEquals(3,res.getPhaseCirc(PhaseType.ByFirstPeak,WeightingType.None).getMean(),1E-6);
        assertEquals(1,res.getPhaseCirc(PhaseType.ByAvgMax,WeightingType.None).getMean(),1E-6);
    
        assertEquals((12+24+10)/3.0,res.getPhase(PhaseType.ByMethod,WeightingType.None).getMean(),1E-2);
        assertEquals((6+12+5)/3.0,res.getPhase(PhaseType.ByFit,WeightingType.None).getMean(),1E-2);
        assertEquals((3+6+2.5)/3.0,res.getPhase(PhaseType.ByFirstPeak,WeightingType.None).getMean(),1E-2);
        assertEquals((1+2+(1.0*20.0/24.0))/3.0,res.getPhase(PhaseType.ByAvgMax,WeightingType.None).getMean(),1E-2);
    
        assertEquals((24+48+20)/3.0,res.getPeriod(WeightingType.None).getMean(),1E-2);
        
        assertEquals((1+2+3)/3.0,res.getAmplitude(PhaseType.ByMethod,WeightingType.None).getMean(),1E-2);
        assertEquals((2+3+4)/3.0,res.getAmplitude(PhaseType.ByFit,WeightingType.None).getMean(),1E-2);
        assertEquals((3+4+5)/3.0,res.getAmplitude(PhaseType.ByFirstPeak,WeightingType.None).getMean(),1E-2);
        assertEquals((4+5+6)/3.0,res.getAmplitude(PhaseType.ByAvgMax,WeightingType.None).getMean(),1E-2);
    
    }
    
    @Test
    public void calculateStatsCanHandleEmptyCollection() {
        List<PPAResult> results = new ArrayList<>();
        PPAStats res = PPAStatsCalculator.calculateStats(results);
        assertNotNull(res);
        assertNotNull(res.getAmpStatsByAvgMax());
        assertNotNull(res.getAmpStatsByFirstPeak());
        assertNotNull(res.getAmpStatsByFit());
        assertNotNull(res.getAmpStatsByMethod());
        assertNotNull(res.getGOF());
        assertNotNull(res.getJoinedError());
        assertNotNull(res.getPeriodStats());
        assertNotNull(res.getPhaseStatsByAvgMax());
        assertNotNull(res.getPhaseStatsByAvgMaxCirc());
        assertNotNull(res.getPhaseStatsByFirstPeak());
        assertNotNull(res.getPhaseStatsByFirstPeakCirc());
    }
}
