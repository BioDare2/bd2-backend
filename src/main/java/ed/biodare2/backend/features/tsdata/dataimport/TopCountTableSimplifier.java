/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare.data.topcount.TopCountReader;
import ed.biodare.data.topcount.err.FormatException;
import ed.biodare2.backend.web.rest.HandlingException;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.util.Pair;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author tzielins
 */
public class TopCountTableSimplifier extends TableSimplifier {

    final TopCountReader topcount = new TopCountReader(false);
    
    @Override
    protected List<List<String>> readTable(Path file, int rows) throws IOException, HandlingException {
        
        try {
    
            Map<Pair<Integer, Integer>, TimeSeries> data = topcount.read(file);
            
            List<TimeSeries> series = linearize(data);
            
            List<List<String>> columns = seriesToColumns(series);
            columns = equalizeColumns(columns,rows);
            
            return pivot(columns,rows);
            
        } catch (FormatException e) {
            throw new HandlingException("Cannot read topcount data: "+e.getMessage(),e);
        }
    }

    protected List<TimeSeries> linearize(Map<Pair<Integer, Integer>, TimeSeries> data) {
        
        List<TimeSeries> series = new ArrayList<>(96);
        for (int row =1;row<9;row++)
            for (int col =1;col<13;col++) {
                series.add(data.getOrDefault(new Pair<>(row,col), new TimeSeries())); 
            }
        
        return series;
    }

    protected List<List<String>> seriesToColumns(List<TimeSeries> series) {
        
        return series.stream().map( ts -> 
                                ts.getValuesList().stream()
                                    .map(d -> Double.toString(d))
                                    .collect(Collectors.toList())
                        )
                        .collect(Collectors.toList());
    }

    protected List<List<String>> equalizeColumns(List<List<String>> columns,int maxLength) {
        
        return columns.stream()
                .map( col -> {
                    if (col.size() > maxLength) 
                        return col.subList(0, maxLength);

                    while(col.size() < maxLength) col.add("");                    
                    return col;
                })
                .collect(Collectors.toList());
        
    }

    protected List<List<String>> pivot(List<List<String>> columns, int rowsNr) {
        
        List<List<String>> rows = new ArrayList<>();
        for (int i =0;i<rowsNr;i++)
            rows.add(new ArrayList<>());
        
        for (List<String> column : columns) {
            for (int i =0;i< rowsNr;i++) {
                rows.get(i).add(column.get(i));
            }
        }
            
        return rows;
    }
    
}
