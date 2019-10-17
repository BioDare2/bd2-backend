/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare2.backend.repo.isa_dom.dataimport.CellCoordinates;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRole;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBlock;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeType;
import ed.robust.dom.data.TimeSeries;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author tzielins
 */
public class TSDataImporter {
    
    protected void insertNumbers(List<DataBlock> blocks) {
        
        int blockId = 1;
        int serId = 1;
        
        for (DataBlock block : blocks) {
            block.blockNr = blockId++;
            for (DataTrace trace : block.traces)
                trace.traceNr = serId++;
        }
    }
    
    protected void insertIds(List<DataBlock> blocks) {
        
        for (DataBlock block : blocks) {
            for (DataTrace trace : block.traces) {
                trace.dataId = trace.traceNr;
                trace.rawDataId = trace.dataId;
            }
        }
    }    

    protected DataBundle makeBundle(List<DataBlock> blocks) {
        
        DataBundle bundle = new DataBundle();
        bundle.blocks = blocks;
        
        bundle.backgrounds = blocks.stream().filter(b -> b.role.equals(CellRole.BACKGROUND))
                                            .flatMap(b -> b.traces.stream().filter( dt -> !dt.trace.isEmpty()))
                                            .collect(Collectors.toList());
        
        bundle.data = blocks.stream().filter(b -> b.role.equals(CellRole.DATA))
                                            .flatMap(b -> b.traces.stream().filter( dt -> !dt.trace.isEmpty()))
                                            .collect(Collectors.toList());
        return bundle;
    } 
    
    protected List<Double> processTimes(List<Double> times, TimeType timeType, double timeOffset, double imgInterval) throws ImportException {
        times = trim(times);
        
        if (times.isEmpty()) 
            throw new ImportException("No time values found");
        
        if (times.stream().anyMatch( v -> v == null))
            throw new ImportException("Time values cannot contain gaps or non numerical values");
        
        times = convertTimes(times,timeType, imgInterval);
        
        if (timeOffset != 0)
            times = times.stream().map( v -> v + timeOffset).collect(Collectors.toList());
        
        if (times.stream().anyMatch( v -> v<0))
            throw new ImportException("Times values cannot be < 0");
        
        return times;
    } 
    
    protected List<Double> trim(List<Double> values) {
        
        if (values.isEmpty()) return values;
        
        int lastNotNull = values.size()-1;
        for (;lastNotNull>=0;lastNotNull--) {
            if (values.get(lastNotNull) != null) break;
        }
        
        return values.subList(0, lastNotNull+1);
    } 
    
    protected List<Double> convertTimes(List<Double> times, TimeType timeType, double imgInterval) throws ImportException {
        
        switch (timeType) {
            case TIME_IN_HOURS: return times;
            case TIME_IN_MINUTES: return times.stream().map(v -> v != null ? v/60.0 : null).collect(Collectors.toList());
            case TIME_IN_SECONDS: return times.stream().map(v -> v != null ? v/(60.0*60) : null).collect(Collectors.toList());
            case IMG_NUMBER: {
                    if (times.get(0) < 1.0) throw new ImportException("Image nr must be 1-based");
                    if (imgInterval <= 0) throw new ImportException("Image interval must be >0, got: "+imgInterval);
                    return times.stream().map( nr -> nr != null ? (nr-1)*imgInterval :  null).collect(Collectors.toList());
                    }
            default: throw new ImportException("Unsuported time column type: "+timeType);
        }
    }   
    
    protected TimeSeries makeSerie(List<Double> times, List<Double> values) {
        

        final int size = Math.min(times.size(),values.size());
        
        TimeSeries serie = new TimeSeries();
        for (int i =0;i<size;i++) {
            Double v = values.get(i);
            if (v != null)
                serie.add(times.get(i),v);
        }
        return serie;
    } 
    
    
    
}
