/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.sorting;

import ed.biodare2.backend.features.ppa.PPAJC2Handler;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.features.tsdata.sorting.TSSorter.InvalidPPAAsLastComparator;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeDataTraces;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleResultEntry;
import ed.biodare2.backend.repo.system_dom.AssayPack;
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
    
    AssayPack experiment;
    
    @Before
    public void setUp() {
        
        dataHandler = mock(TSDataHandler.class);
        ppaHandler = mock(PPAJC2Handler.class);
        sorter = new TSSorter(dataHandler, ppaHandler);
        
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
    public void createsCorrectComparator() {
        
        PPASimpleResultEntry o1 = new PPASimpleResultEntry();
        o1.period = 24;
        o1.ERR = 0.5;
        
        PPASimpleResultEntry o2 = new PPASimpleResultEntry();
        o2.period = 20;
        o2.ERR = 0.6;
        
        PPASimpleResultEntry o3 = new PPASimpleResultEntry();
        o3.period = 20;
        o3.ERR = 0.6;
        
        Comparator<PPASimpleResultEntry> c1;
        
        try {
            c1 = sorter.ppaComparator(TSSortOption.NR, true);
            fail("Exception expected");
        } catch (IllegalArgumentException e) {}
        
        c1 = sorter.ppaComparator(TSSortOption.PERIOD, true);
        assertTrue(c1.compare(o1, o2) > 0);
        assertTrue(c1.compare(o3, o2) == 0);
        
        c1 = sorter.ppaComparator(TSSortOption.PERIOD, false);
        assertTrue(c1.compare(o1, o2) < 0);
        assertTrue(c1.compare(o3, o2) == 0);

        c1 = sorter.ppaComparator(TSSortOption.ERR, true);
        assertTrue(c1.compare(o1, o2) < 0);
        assertTrue(c1.compare(o3, o2) == 0);
        
        o2.ignored = true;
        c1 = sorter.ppaComparator(TSSortOption.PERIOD, true);
        assertTrue(c1.compare(o1, o2) < 0);
        assertTrue(c1.compare(o3, o2) < 0);
        
        
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
