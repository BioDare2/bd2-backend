/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.sorting;

import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.robust.dom.data.DetrendingType;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
@CacheConfig(cacheNames = {"TSSort"})
public class TSSorter {
    
    final TSDataHandler dataHandler;
    long lastCleared;
    
    @Autowired
    public TSSorter(TSDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }
    
    @CacheEvict(allEntries=true)
    public void clear(AssayPack exp) {
        // only so that method wont be removed by compiler
        lastCleared = System.currentTimeMillis();
    }
    
    @Cacheable(key="{#exp.getId(),#sorting}",unless="#result == null || #result.isEmpty()") 
    public List<Long> sortedTSIds(AssayPack exp, TSSortParams sorting) {
        
        Optional<List<DataTrace>> data = dataHandler.getDataSet(exp, DetrendingType.LIN_DTR);
        //System.out.println("Sorter data: "+data.map( d -> d.size()));
        return data.map( ds -> sort(ds, sorting)).orElse(List.of());
    }
    
    protected List<Long> sort(List<DataTrace> data, TSSortParams sorting) {
        // I run some simple tests for performance and for less than 10K parralel made it wors
        // check the ignored unit tests
        
        switch (sorting.sort) {
            case NONE: return tracesToIds(data.stream(), sorting.ascending);
            case ID: return tracesToIds(data.stream()
                                            .sorted(Comparator.comparing(d -> d.dataId)), sorting.ascending);
            case NR: return tracesToIds(data.stream()
                                            .sorted(Comparator.comparing(d -> d.traceNr)), sorting.ascending);
            case LABEL: return tracesToIds(data.stream()
                                            .sorted(Comparator.comparing(d -> d.details.dataLabel)), sorting.ascending);
            default: throw new IllegalArgumentException("Unsupported sorting: "+sorting.sort);
        }
    }
    
    protected List<Long> tracesToIds(Stream<DataTrace> traces, boolean ascending) {
        
        List<Long> ids = traces.map( d -> d.dataId).collect(Collectors.toList());
        if (!ascending) Collections.reverse(ids);
        return ids;
    }
}
