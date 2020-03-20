/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata;

import ed.biodare.jobcentre2.dom.TSData;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.robust.dom.data.TimeSeries;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author tzielins
 */
public class TSUtil {
    
    public static List<TSData> prepareTSData(List<DataTrace> dataSet, double windowStart, double windowEnd) {
        
        return dataSet.stream()
                .map( dt -> trace2TSData(dt, windowStart, windowEnd))
                .collect(Collectors.toList());
    }

    public static TSData trace2TSData(DataTrace trace,double windowStart, double windowEnd) {
        
        TimeSeries serie = trace.trace;
        if (windowStart != 0 || windowEnd != 0) {
            if (windowStart == 0) windowStart = serie.getFirst().getTime();
            if (windowEnd == 0) windowEnd = serie.getLast().getTime();
            serie = trace.trace.subSeries(windowStart, windowEnd);
        }
        
        TSData data = new TSData(trace.dataId, serie);
        return data;
    }
    
}
