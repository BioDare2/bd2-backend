/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.sorting;

import ed.biodare2.backend.features.ppa.PPAJC2Handler;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleResultEntry;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.tsprocessing.PhaseType;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.ToDoubleFunction;
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
    final PPAJC2Handler ppaHandler;
    long lastCleared;
    
    @Autowired
    public TSSorter(TSDataHandler dataHandler, PPAJC2Handler ppaHandler) {
        this.dataHandler = dataHandler;
        this.ppaHandler = ppaHandler;
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
        return data.map( ds -> sort(exp, ds, sorting)).orElse(List.of());
    }
    
    protected List<Long> sort(AssayPack exp, List<DataTrace> data, TSSortParams sorting) {
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
            case PERIOD:
            case PHASE:
            case AMP:
            case ERR: return ppaSort(exp, data, sorting);
            default: throw new IllegalArgumentException("Unsupported sorting: "+sorting.sort);
        }
    }
    
    protected List<Long> tracesToIds(Stream<DataTrace> traces, boolean ascending) {
        
        List<Long> ids = traces.map( d -> d.dataId).collect(Collectors.toList());
        if (!ascending) Collections.reverse(ids);
        return ids;
    }

    protected List<Long> ppaSort(AssayPack exp, List<DataTrace> data, TSSortParams sorting) {
        if (sorting.jobId == null) throw new IllegalArgumentException("Missing job id");
        
        PPAJobSimpleResults results = ppaHandler.getPPAJobSimpleResults(exp, sorting.jobId);
        
        return  ppaSort(results.results, sorting);
    }
    
    protected List<Long> ppaSort(List<PPASimpleResultEntry> results, TSSortParams sorting) {
        
        return  results.stream()
                    .sorted(ppaComparator(sorting.sort, sorting.ascending))
                    .map( r -> r.dataId)
                    .collect(Collectors.toList());
    }    
    
    protected Comparator<PPASimpleResultEntry> ppaComparator(TSSortOption sort, boolean ascending) {
        
        Comparator<PPASimpleResultEntry> comp;
        switch (sort) {
            case PERIOD: comp = new InvalidPPAAsLastComparator( r -> r.period); break;
            case PHASE: comp = new InvalidPPAAsLastComparator( r -> r.phaseToZero.get(PhaseType.ByFit)); break;
            case AMP: comp = new InvalidPPAAsLastComparator( r -> r.amplitude.get(PhaseType.ByFit)); break;
            case ERR: comp = new InvalidPPAAsLastComparator( r -> r.ERR); break;
            default: throw new IllegalArgumentException("Unsupported ppa sort by: "+sort);
        }
        if (!ascending) comp = comp.reversed();
        return comp;
    }
    

    
    protected static class InvalidPPAAsLastComparator implements Comparator<PPASimpleResultEntry> {

        final ToDoubleFunction<PPASimpleResultEntry> extractor;
        
        InvalidPPAAsLastComparator(ToDoubleFunction<PPASimpleResultEntry> extractor) {
            this.extractor = extractor;
        }

        @Override
        public int compare(PPASimpleResultEntry o1, PPASimpleResultEntry o2) {
            
            if (o1.failed) return o2.failed ? 0 : 1; 
            if (o2.failed) return -1;
            
            if (o1.ignored && !o2.ignored) return 1;
            if (!o1.ignored && o2.ignored) return -1;
            
            return Double.compare(extractor.applyAsDouble(o1),extractor.applyAsDouble(o2));
        }
        
    }
}
