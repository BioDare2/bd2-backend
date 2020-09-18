/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.sorting;

import ed.biodare.jobcentre2.dom.JobResults;
import ed.biodare.jobcentre2.dom.TSResult;
import ed.biodare.rhythm.ejtk.BD2eJTKRes;
import ed.biodare2.backend.features.ppa.PPAJC2Handler;
import ed.biodare2.backend.features.rhythmicity.RhythmicityHandler;
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
    final RhythmicityHandler rhythmicityHandler;
    long lastCleared;
    
    @Autowired
    public TSSorter(TSDataHandler dataHandler, PPAJC2Handler ppaHandler, RhythmicityHandler rhythmicityHandler) {
        this.dataHandler = dataHandler;
        this.ppaHandler = ppaHandler;
        this.rhythmicityHandler = rhythmicityHandler;
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
            
            case R_PERIOD:
            case R_PEAK:
            case R_TAU:
            case R_PVALUE: return rhythmSort(exp, data, sorting);
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
                    /*.peek( r -> {
                        System.out.println(r.period+"\t"+r.phaseToZero.get(PhaseType.ByFit)+"\t"+r.ERR+"\t"+roundToDecy(r.ERR));                    
                    })*/
                    .map( r -> r.dataId)
                    .collect(Collectors.toList());
    }    
    
    protected List<Long> rhythmSort(AssayPack exp, List<DataTrace> data, TSSortParams sorting) {
        if (sorting.jobId == null) throw new IllegalArgumentException("Missing job id");
        
        JobResults<TSResult<BD2eJTKRes>> results = rhythmicityHandler.getRhythmicityResults(exp, sorting.jobId);
        
        return  rhythmSort(results.results, sorting);
    }
    
    protected List<Long> rhythmSort(List<TSResult<BD2eJTKRes>> results, TSSortParams sorting) {
        
        return  results.stream()
                    .sorted(rhythmComparator(sorting.sort, sorting.ascending))
                    /*.peek( r -> {
                        System.out.println(r.period+"\t"+r.phaseToZero.get(PhaseType.ByFit)+"\t"+r.ERR+"\t"+roundToDecy(r.ERR));                    
                    })*/
                    .map( r -> r.id)
                    .collect(Collectors.toList());
    }    
    
   
    
    protected Comparator<PPASimpleResultEntry> ppaComparator(TSSortOption sort, boolean ascending) {
        
        final Comparator<PPASimpleResultEntry> status = new PPAStatusComparator();
        final Comparator<PPASimpleResultEntry> period = Comparator.comparing(r -> roundToHalf(r.period));
        final Comparator<PPASimpleResultEntry> phase = Comparator.comparing(r -> roundToHalf(r.phaseToZero.get(PhaseType.ByFit)));
        final Comparator<PPASimpleResultEntry> amp = Comparator.comparing(r -> smartRound(r.amplitude.get(PhaseType.ByFit)));
        final Comparator<PPASimpleResultEntry> err = Comparator.comparing(r -> roundToDecy(r.ERR));
                
        Comparator<PPASimpleResultEntry> comp;
        switch (sort) {
            case PERIOD: comp = status.thenComparing(period).thenComparing(phase).thenComparing(err); break;
            case PHASE: comp = status.thenComparing(phase).thenComparing(period).thenComparing(err); break;
            case AMP: comp = status.thenComparing(amp).thenComparing(phase).thenComparing(err); break;
            case ERR: comp = status.thenComparing(err).thenComparing(phase); break;
            default: throw new IllegalArgumentException("Unsupported ppa sort by: "+sort);
        }
        if (!ascending) comp = comp.reversed();
        return comp;
    }
    
    protected Comparator<TSResult<BD2eJTKRes>> rhythmComparator(TSSortOption sort, boolean ascending) {
        
        // R_PVALUE not implemented as messy to choose between empP and P and
        // they antiproporcional to tau
        final Comparator<TSResult<BD2eJTKRes>> period = Comparator.comparing(r -> roundToHalf(r.result.pattern.period));
        final Comparator<TSResult<BD2eJTKRes>> peak = Comparator.comparing(r -> roundToHalf(r.result.pattern.peak));
        final Comparator<TSResult<BD2eJTKRes>> tau = Comparator
                                                        .comparing( (TSResult<BD2eJTKRes> r) -> roundToCenty(r.result.tau))
                                                        .reversed();
                
        Comparator<TSResult<BD2eJTKRes>> comp;
        switch (sort) {
            case R_PERIOD: comp = period.thenComparing(peak).thenComparing(tau); break;
            case R_PEAK: comp = peak.thenComparing(tau).thenComparing(period); break;
            case R_TAU: comp = tau.thenComparing(peak); break;
            default: throw new IllegalArgumentException("Unsupported rhythm sort by: "+sort);
        }
        if (!ascending) comp = comp.reversed();
        return comp;
    }     
    
    final protected double roundToHalf(double val) {
        double v = Math.floor(val);
        double r = val - v;
        if (r >= 0.25 && r< 0.75) v+=0.5;
        else if (r >=0.75) v+=1;
        return v;
    }
    
    final protected double roundToDecy(double val) {
        return Math.round(val*10)/10.0;        
    }
    
    final protected double roundToCenty(double val) {
        return Math.round(val*100)/100.0;        
    }    
    
    final protected double smartRound(double val) {
        if (Math.abs(val) >= 30)
            return Math.round(val);
        if (Math.abs(val) >= 10)
            return roundToHalf(val);
        if (Math.abs(val) >= 0.5)
            return roundToDecy(val);        
        if (Math.abs(val) >= 0.05)
            return roundToCenty(val);
        return val;
    }
    
    protected static class PPAStatusComparator implements Comparator<PPASimpleResultEntry> {


        @Override
        public int compare(PPASimpleResultEntry o1, PPASimpleResultEntry o2) {
            
            if (o1.failed) return o2.failed ? 0 : 1; 
            if (o2.failed) return -1;
            
            if (o1.ignored) return o2.ignored ? 0 : 1;
            if (o2.ignored) return -1;
            
            return 0;
        }
        
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
