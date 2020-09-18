/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.sorting;

import ed.biodare.jobcentre2.dom.TSResult;
import ed.biodare.rhythm.ejtk.BD2eJTKRes;
import ed.biodare2.backend.features.ppa.PPAJC2Handler;
import ed.biodare2.backend.features.rhythmicity.RhythmicityHandler;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import static ed.biodare2.backend.features.tsdata.sorting.TSSortOption.*;
import ed.biodare2.backend.features.tsdata.sorting.TSSorter.InvalidPPAAsLastComparator;
import ed.biodare2.backend.features.tsdata.sorting.TSSorter.PPAStatusComparator;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeBD2EJTKResult;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeDataTraces;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.ValuesByPhase;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.robust.dom.tsprocessing.PhaseType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import static org.mockito.Mockito.*;

/**
 *
 * @author tzielins
 */
public class TSSorterTest {
    
    public TSSorterTest() {
    }
    
    TSSorter sorter;
    TSDataHandler dataHandler;
    PPAJC2Handler ppaHandler;
    RhythmicityHandler rhythmicityHandler;
    
    AssayPack experiment;
    
    @Before
    public void setUp() {
        
        dataHandler = mock(TSDataHandler.class);
        ppaHandler = mock(PPAJC2Handler.class);
        rhythmicityHandler = mock(RhythmicityHandler.class);
        sorter = new TSSorter(dataHandler, ppaHandler, rhythmicityHandler);
        
        experiment = mock(AssayPack.class);
        when(experiment.getId()).thenReturn(5670L);
        //when(experiment.getName()).thenReturn("Fake Test Exp");         
    }
    
    @Test
    public void testGivesSortedIdByTracesProperties() {
        
        List<DataTrace> traces = makeDataTraces(1,5);
        assertEquals(5, traces.size());
        traces.get(0).traceNr = 10;
        traces.get(1).traceNr = 11;
        traces.get(2).traceNr = 12;
        
        traces.get(0).details.dataLabel = "c1";
        traces.get(1).details.dataLabel = "c2";
        traces.get(2).details.dataLabel = "d";
        traces.get(3).details.dataLabel = "aba";
        traces.get(4).details.dataLabel = "a";        
        Collections.shuffle(traces);
        
        TSSortParams sorting = new TSSortParams(TSSortOption.NONE, true, null);
        List<Long> sorted = sorter.sort(experiment, traces, sorting);        
        assertEquals(traces.stream().map(s -> s.dataId).collect(Collectors.toList()), sorted);
        
        sorting = new TSSortParams(TSSortOption.ID, false, null);
        sorted = sorter.sort(experiment, traces, sorting);        
        List<Long> exp = List.of(5L, 4L, 3L, 2L, 1L);
        assertEquals(exp, sorted);
        
        sorting = new TSSortParams(TSSortOption.LABEL, true, null);
        sorted = sorter.sort(experiment,traces, sorting);        
        exp = List.of(5L, 4L, 1L, 2L, 3L);
        assertEquals(exp, sorted);
    }
    
    @Test
    public void invalidPPAAsLastComparatorWorks() {
        
        PPASimpleResultEntry o1 = new PPASimpleResultEntry();
        o1.period = 24;
        o1.ERR = 0.5;
        
        PPASimpleResultEntry o2 = new PPASimpleResultEntry();
        o2.period = 20;
        o2.ERR = 0.6;
        
        PPASimpleResultEntry o3 = new PPASimpleResultEntry();
        o3.period = 20;
        o3.ERR = 0.6;
        
        
        InvalidPPAAsLastComparator c1 = new TSSorter.InvalidPPAAsLastComparator(p -> p.period);
        InvalidPPAAsLastComparator c2 = new TSSorter.InvalidPPAAsLastComparator(p -> p.ERR);
        
        assertTrue(c1.compare(o1, o2) > 0);
        assertTrue(c1.compare(o3, o2) == 0);
        assertTrue(c2.compare(o1, o2) < 0);
        assertTrue(c2.compare(o3, o2) == 0);
        
        o1.failed = true;
        assertTrue(c1.compare(o1, o2) > 0);
        assertTrue(c2.compare(o1, o2) > 0);
        
        o1.failed = true;
        o2.failed = true;
        assertTrue(c1.compare(o1, o2) == 0);
        assertTrue(c2.compare(o1, o2) == 0);
        assertTrue(c1.compare(o3, o2) < 0);
        assertTrue(c2.compare(o3, o2) < 0);
        
        o1.failed = false;
        assertTrue(c1.compare(o1, o2) < 0);
        assertTrue(c2.compare(o1, o2) < 0);
        
        o1.failed = true;
        o2.failed = false;
        o2.ignored = true;
        assertTrue(c1.compare(o1, o2) > 0);
        assertTrue(c2.compare(o1, o2) > 0);
        
        o1.failed = false;
        o1.ignored = true;
        o2.failed = true;
        o2.ignored = false;
        assertTrue(c1.compare(o1, o2) < 0);
        assertTrue(c2.compare(o1, o2) < 0);
        
        o1.failed = false;
        o1.ignored = true;
        o2.failed = false;
        o2.ignored = true;
        assertTrue(c1.compare(o1, o2) > 0);
        assertTrue(c2.compare(o1, o2) < 0);
        assertTrue(c1.compare(o3, o2) < 0);
        assertTrue(c2.compare(o3, o2) < 0);

        o3.ignored = true;
        assertTrue(c1.compare(o3, o2) == 0);
    }
    
    
    @Test
    public void ppaStatusComparatorWorks() {
        
        PPASimpleResultEntry o1 = new PPASimpleResultEntry();
        o1.period = 24;
        o1.ERR = 0.5;
        
        PPASimpleResultEntry o2 = new PPASimpleResultEntry();
        o2.period = 20;
        o2.ERR = 0.6;
                
        PPASimpleResultEntry o3 = new PPASimpleResultEntry();
        o3.period = 20;
        o3.ERR = 0.6;
        
        
        PPAStatusComparator c1 = new PPAStatusComparator();
        
        assertTrue(c1.compare(o1, o2) == 0);
        assertTrue(c1.compare(o2, o3) == 0);
        
        o2.failed = true;
        assertTrue(c1.compare(o1, o2) < 0);
        assertTrue(c1.compare(o2, o3) > 0);
        
        o1.failed = true;
        o2.failed = true;
        assertTrue(c1.compare(o1, o2) == 0);
        assertTrue(c1.compare(o2, o3) > 0);
        
        o1.failed = true;
        o2.ignored = true;
        o2.failed = false;
        assertTrue(c1.compare(o1, o2) > 0);
        assertTrue(c1.compare(o2, o3) > 0);

        o1.failed = false;
        o2.ignored = true;
        o2.failed = false;
        assertTrue(c1.compare(o1, o2) < 0);
        assertTrue(c1.compare(o2, o3) > 0);
        
        o3.ignored = true;
        assertTrue(c1.compare(o2, o3) == 0);
        
    }
    
    @Test
    public void roundToDecy() {
        
        assertEquals(11.2, sorter.roundToDecy(11.19),1E-6);
        assertEquals(-1.0, sorter.roundToDecy(-.96),1E-6);
    }
    
    @Test
    public void roundToHalf() {
        
        assertEquals(11.0, sorter.roundToHalf(11.19),1E-6);
        assertEquals(-1.5, sorter.roundToHalf(-1.35),1E-6);
        assertEquals(2.0, sorter.roundToHalf(1.79),1E-6);
    }    
    
    @Test
    public void smartRound() {
        
        assertEquals(41.0, sorter.smartRound(41.2678),1E-6);
        assertEquals(10.5, sorter.smartRound(10.3678),1E-6);
        assertEquals(-1.4, sorter.smartRound(-1.3678),1E-6);
        assertEquals(0.42, sorter.smartRound(0.4178),1E-6);
        assertEquals(0.03678, sorter.smartRound(0.03678),1E-6);
    }     
    
    @Test
    public void createsPPAComparator() {
        
        try {
            Comparator<PPASimpleResultEntry> c1;
            c1 = sorter.ppaComparator(NR, true);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {}
        
        for (TSSortOption sort: List.of(PERIOD, PHASE, AMP, ERR)) {
            Comparator<PPASimpleResultEntry> c1 = sorter.ppaComparator(sort, true);
            assertNotNull(c1);
        }    
        
   
        
    }
    
    @Test
    public void createRhythmComparator() {
        
        try {
            Comparator<PPASimpleResultEntry> c1;
            c1 = sorter.ppaComparator(NR, true);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {}
        
 
        // R_PVALUE not implemented as too tricky to choose which and they ar related to tau
        for (TSSortOption sort: List.of(R_PERIOD, R_PEAK, R_TAU)) {
            Comparator<TSResult<BD2eJTKRes>> c1 = sorter.rhythmComparator(sort, true);
            assertNotNull(c1);
        }         
        
    }
    


    @Test
    public void byPeriodComparatorSortsByPeriodPhaseError() {
        
        PPASimpleResultEntry o1 = new PPASimpleResultEntry();
        o1.period = 24;
        o1.ERR = 0.5;
        o1.phaseToZero = new ValuesByPhase<>();
        o1.phaseToZero.put(PhaseType.ByFit, 1.0);
        
        PPASimpleResultEntry o2 = new PPASimpleResultEntry();
        o2.period = 20;
        o2.ERR = 0.6;
        o2.phaseToZero.put(PhaseType.ByFit, 1.0);
        
        PPASimpleResultEntry o3 = new PPASimpleResultEntry();
        o3.period = 20;
        o3.ERR = 0.6;
        o3.phaseToZero.put(PhaseType.ByFit, 3.0);
        
        PPASimpleResultEntry o4 = new PPASimpleResultEntry();
        o4.period = 20;
        o4.ERR = 0.7;
        o4.phaseToZero.put(PhaseType.ByFit, 1.0);        
        
        Comparator<PPASimpleResultEntry> c1 = sorter.ppaComparator(TSSortOption.PERIOD, true);
        
        assertTrue(c1.compare(o1, o2) > 0);
        assertTrue(c1.compare(o3, o2) > 0);
        assertTrue(c1.compare(o4, o2) > 0);
        
        c1 = sorter.ppaComparator(TSSortOption.PERIOD, false);
        
        assertTrue(c1.compare(o1, o2) < 0);
        assertTrue(c1.compare(o3, o2) < 0);
        assertTrue(c1.compare(o4, o2) < 0);
    }
    
    @Test
    public void byPeriodComparatorAppliesRounding() {
        
        PPASimpleResultEntry o1 = new PPASimpleResultEntry();
        o1.period = 24.3;
        o1.ERR = 0.5;
        o1.phaseToZero = new ValuesByPhase<>();
        o1.phaseToZero.put(PhaseType.ByFit, 1.1);
        
        PPASimpleResultEntry o2 = new PPASimpleResultEntry();
        o2.period = 24.6;
        o2.ERR = 0.53;
        o2.phaseToZero.put(PhaseType.ByFit, 0.9);
        
        PPASimpleResultEntry o3 = new PPASimpleResultEntry();
        o3.period = 24.8;
        o3.ERR = 0.69;
        o3.phaseToZero.put(PhaseType.ByFit, 3.7);
        
        PPASimpleResultEntry o4 = new PPASimpleResultEntry();
        o4.period = 25;
        o4.ERR = 0.71;
        o4.phaseToZero.put(PhaseType.ByFit, 3.4);        
        
        Comparator<PPASimpleResultEntry> c1 = sorter.ppaComparator(TSSortOption.PERIOD, true);
        
        assertTrue(c1.compare(o1, o2) == 0);
        assertTrue(c1.compare(o3, o2) > 0);
        assertTrue(c1.compare(o4, o3) == 0);
        

    }    
    
    @Test
    public void byPeriodComparatorSortsFailedLast() {
        
        PPASimpleResultEntry o1 = new PPASimpleResultEntry();
        o1.period = 20;
        o1.ERR = 0.6;
        o1.phaseToZero.put(PhaseType.ByFit, 1.0);
        
        PPASimpleResultEntry o2 = new PPASimpleResultEntry();
        o2.period = 20;
        o2.ERR = 0.6;
        o2.phaseToZero.put(PhaseType.ByFit, 1.0);
        
        PPASimpleResultEntry o3 = new PPASimpleResultEntry();
        o3.period = 20;
        o3.ERR = 0.6;
        o3.phaseToZero.put(PhaseType.ByFit, 1.0);
        
        Comparator<PPASimpleResultEntry> c1 = sorter.ppaComparator(PERIOD, true);
        
        assertTrue(c1.compare(o1, o2) == 0);
        assertTrue(c1.compare(o3, o2) == 0);

        o2.ignored = true;
        o3.failed = true;
        assertTrue(c1.compare(o1, o2) < 0);
        assertTrue(c1.compare(o3, o2) > 0);
        assertTrue(c1.compare(o3, o1) > 0);
    }   
    
    @Test
    public void byPhaseComparatorSortsByPhasePeriodError() {
        
        PPASimpleResultEntry o1 = new PPASimpleResultEntry();
        o1.period = 24;
        o1.ERR = 0.5;
        o1.phaseToZero = new ValuesByPhase<>();
        o1.phaseToZero.put(PhaseType.ByFit, 1.0);
        
        PPASimpleResultEntry o2 = new PPASimpleResultEntry();
        o2.period = 20;
        o2.ERR = 0.6;
        o2.phaseToZero.put(PhaseType.ByFit, 3.0);
        
        PPASimpleResultEntry o3 = new PPASimpleResultEntry();
        o3.period = 24;
        o3.ERR = 0.6;
        o3.phaseToZero.put(PhaseType.ByFit, 3.0);
        
        PPASimpleResultEntry o4 = new PPASimpleResultEntry();
        o4.period = 20;
        o4.ERR = 0.2;
        o4.phaseToZero.put(PhaseType.ByFit, 3.0);        
        
        Comparator<PPASimpleResultEntry> c1 = sorter.ppaComparator(PHASE, true);
        
        assertTrue(c1.compare(o1, o2) < 0);
        assertTrue(c1.compare(o3, o2) > 0);
        assertTrue(c1.compare(o4, o2) < 0);
        
    }    
    
    @Test
    public void byErrorComparatorSortsByErrorPhase() {
        
        PPASimpleResultEntry o1 = new PPASimpleResultEntry();
        o1.period = 24;
        o1.ERR = 0.2;
        o1.phaseToZero = new ValuesByPhase<>();
        o1.phaseToZero.put(PhaseType.ByFit, 1.0);
        
        PPASimpleResultEntry o2 = new PPASimpleResultEntry();
        o2.period = 20;
        o2.ERR = 0.6;
        o2.phaseToZero.put(PhaseType.ByFit, 3.0);
        
        PPASimpleResultEntry o3 = new PPASimpleResultEntry();
        o3.period = 24;
        o3.ERR = 0.6;
        o3.phaseToZero.put(PhaseType.ByFit, 3.0);
        
        PPASimpleResultEntry o4 = new PPASimpleResultEntry();
        o4.period = 20;
        o4.ERR = 0.6;
        o4.phaseToZero.put(PhaseType.ByFit, 3.6);        
        
        Comparator<PPASimpleResultEntry> c1 = sorter.ppaComparator(ERR, true);
        
        assertTrue(c1.compare(o1, o2) < 0);
        assertTrue(c1.compare(o3, o2) == 0);
        assertTrue(c1.compare(o4, o2) > 0);
        
    }    
    
    @Test
    public void byRPeriodSortsByPeriodPhaseAndTau() {
        
        TSResult<BD2eJTKRes> o1 = makeBD2EJTKResult(1, 0.5, 0.01, 24, 3);
        TSResult<BD2eJTKRes> o2 = makeBD2EJTKResult(2, 0.5, 0.01, 20, 3);
        TSResult<BD2eJTKRes> o3 = makeBD2EJTKResult(3, 0.5, 0.01, 20, 1);
        TSResult<BD2eJTKRes> o4 = makeBD2EJTKResult(4, 0.9, 0.01, 20, 3);
        
        Comparator<TSResult<BD2eJTKRes>> c1 = sorter.rhythmComparator(R_PERIOD, true);
        assertTrue(c1.compare(o1, o2) > 0);
        assertTrue(c1.compare(o3, o2) < 0);
        assertTrue(c1.compare(o4, o2) < 0);
    }
    
    @Test
    public void byRPeakSortsByPeakTauAndPeriod() {
        
        TSResult<BD2eJTKRes> o1 = makeBD2EJTKResult(1, 0.5, 0.01, 20, 3);
        TSResult<BD2eJTKRes> o2 = makeBD2EJTKResult(2, 0.5, 0.01, 20, 1);
        TSResult<BD2eJTKRes> o3 = makeBD2EJTKResult(3, 0.5, 0.01, 24, 1);
        TSResult<BD2eJTKRes> o4 = makeBD2EJTKResult(4, 0.9, 0.01, 20, 1);
        
        Comparator<TSResult<BD2eJTKRes>> c1 = sorter.rhythmComparator(R_PEAK, true);
        assertTrue(c1.compare(o1, o2) > 0);
        assertTrue(c1.compare(o3, o2) > 0);
        assertTrue(c1.compare(o4, o2) < 0);
    }    
    
    @Test
    public void byRTauSortsByTauAndPeak() {
        
        TSResult<BD2eJTKRes> o1 = makeBD2EJTKResult(1, 0.4, 0.01, 21, 1);
        TSResult<BD2eJTKRes> o2 = makeBD2EJTKResult(2, 0.5, 0.01, 22, 1);
        TSResult<BD2eJTKRes> o3 = makeBD2EJTKResult(3, 0.5, 0.01, 23, 2);
        TSResult<BD2eJTKRes> o4 = makeBD2EJTKResult(4, 0.5, 0.01, 24, 1);
        
        Comparator<TSResult<BD2eJTKRes>> c1 = sorter.rhythmComparator(R_TAU, true);
        assertTrue(c1.compare(o1, o2) > 0);
        assertTrue(c1.compare(o3, o2) > 0);
        assertTrue(c1.compare(o4, o2) == 0);
    }  
    
    @Test
    public void byRPeriodAppliesRounding() {
        
        TSResult<BD2eJTKRes> o1 = makeBD2EJTKResult(1, 0.5, 0.01, 24.2, 3.5);
        TSResult<BD2eJTKRes> o2 = makeBD2EJTKResult(2, 0.5, 0.01, 24, 3.6);
        TSResult<BD2eJTKRes> o3 = makeBD2EJTKResult(3, 0.509, 0.01, 24.6, 1);
        TSResult<BD2eJTKRes> o4 = makeBD2EJTKResult(4, 0.51, 0.01, 24.7, 1);
        
        Comparator<TSResult<BD2eJTKRes>> c1 = sorter.rhythmComparator(R_PERIOD, true);
        assertTrue(c1.compare(o1, o2) == 0);
        assertTrue(c1.compare(o3, o4) == 0);
    }    
    
    @Test
    public void rhythmSortResults() {
        
        TSResult<BD2eJTKRes> o1 = makeBD2EJTKResult(1, 0.49, 0.01, 24, 3);
        TSResult<BD2eJTKRes> o4 = makeBD2EJTKResult(2, 0.9, 0.01, 20, 3);
        TSResult<BD2eJTKRes> o2 = makeBD2EJTKResult(3, 0.5, 0.01, 22.4, 3);
        TSResult<BD2eJTKRes> o3 = makeBD2EJTKResult(4, 0.5, 0.01, 22.5, 1);
        
        TSSortParams sorting = new TSSortParams(TSSortOption.R_PERIOD, true, null);
        List<Long> sorted = sorter.rhythmSort(List.of(o1,o2,o3,o4), sorting);
        
        assertEquals(List.of(2L,4L,3L,1L), sorted);
        
        sorting = new TSSortParams(TSSortOption.R_PEAK, true, null);
        sorted = sorter.rhythmSort(List.of(o1,o2,o3,o4), sorting);
        
        assertEquals(List.of(4L,2L,3L,1L), sorted);
        
    }    
    
    @Test
    public void ppaSortsResults() {
        
        PPASimpleResultEntry o1 = new PPASimpleResultEntry();
        o1.dataId = 1;
        o1.period = 24;
        o1.ERR = 0.5;
        
        PPASimpleResultEntry o2 = new PPASimpleResultEntry();
        o2.dataId = 2;
        o2.period = 20;
        o2.ERR = 0.6;
        o2.ignored = true;
        
        PPASimpleResultEntry o3 = new PPASimpleResultEntry();
        o3.dataId = 3;
        o3.period = 20;
        o3.ERR = 0.6;
        
        List<PPASimpleResultEntry> results = List.of(o1,o2,o3);
        TSSortParams sorting = new TSSortParams(TSSortOption.PERIOD, true, null);
        assertEquals(List.of(3L,1L,2L), sorter.ppaSort(results, sorting));
        
        sorting = new TSSortParams(TSSortOption.ERR, true, null);
        assertEquals(List.of(1L,3L,2L), sorter.ppaSort(results, sorting));
        
    }
        
    

    @Test
    @Ignore
    public void testSortingParraleStreamToListPreservesOrder() {
        
        class A {
            UUID id;
            public A(UUID id) {
                this.id = id;
            }
        }
        
        
        for (int i = 0; i<10;i++) {
            List<A> ids = new ArrayList<>();
            while(ids.size() < 20000) ids.add(new A(UUID.randomUUID()));
            
            long sT = System.nanoTime();
                List<A> ss = ids.stream().sorted( Comparator.comparing(a -> a.id.toString())).collect(Collectors.toList());
            long dSS = System.nanoTime()-sT;
            sT = System.nanoTime();
                List<A> ps = ids.parallelStream().sorted( Comparator.comparing(a -> a.id.toString())).collect(Collectors.toList());
            long dPS = System.nanoTime()-sT;
                
            assertEquals(ss, ps);
            System.out.println(dSS/100000+"\t"+dPS/100000);
        }
    }
    
}
