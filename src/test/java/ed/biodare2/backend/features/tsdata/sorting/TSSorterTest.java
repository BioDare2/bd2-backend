/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.sorting;

import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeDataTraces;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
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
    
    @Before
    public void setUp() {
        
        dataHandler = mock(TSDataHandler.class);
        sorter = new TSSorter(dataHandler);
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
        List<Long> sorted = sorter.sort(traces, sorting);        
        assertEquals(traces.stream().map(s -> s.dataId).collect(Collectors.toList()), sorted);
        
        sorting = new TSSortParams(TSSortOption.ID, false, null);
        sorted = sorter.sort(traces, sorting);        
        List<Long> exp = List.of(5L, 4L, 3L, 2L, 1L);
        assertEquals(exp, sorted);
        
        sorting = new TSSortParams(TSSortOption.LABEL, true, null);
        sorted = sorter.sort(traces, sorting);        
        exp = List.of(5L, 4L, 1L, 2L, 3L);
        assertEquals(exp, sorted);
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
