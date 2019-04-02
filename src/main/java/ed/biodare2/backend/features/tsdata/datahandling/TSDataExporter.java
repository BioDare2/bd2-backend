/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.datahandling;

import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.data.TimeSeries;
import ed.robust.util.timeseries.TimeSeriesFileHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class TSDataExporter {

    public Path export(List<DataTrace> dataSet, AssayPack exp, DetrendingType detrending, Map<String, String> displayProperties,Path file) throws IOException {
        
        List<List<String>> setDescription = renderSetDescription(exp,detrending,displayProperties);
        List<List<String>> dataHeaders = renderDataHeaders(dataSet);
        List<TimeSeries> data = renderData(dataSet);
        
        
        save(setDescription,dataHeaders,data,file);
        return file;
        
    }    
    
    public Path export(List<DataTrace> dataSet, AssayPack exp, DetrendingType detrending, Map<String, String> displayProperties) throws IOException {

        Path file = Files.createTempFile(null, null);
        return export(dataSet, exp, detrending, displayProperties, file);
        
    }

    protected List<List<String>> renderSetDescription(AssayPack exp, DetrendingType detrending, Map<String, String> displayProperties) {

        List<List<String>> rows = new ArrayList<>();
        List<String> row;
        
        row = Arrays.asList("Set type:","Experiment Data");
        rows.add(row);
        
        row = Arrays.asList("Data type");
        rows.add(row);
        
        row = Arrays.asList("Detrending:",detrending.longName);
        rows.add(row);         
        
        row = new ArrayList<>();
        row.add("Contributors:");
        row.addAll(exp.getAssay()
                        .contributionDesc
                        .authors.stream()
                        .map(p -> p.getName())
                        .collect(Collectors.toSet()));        
        rows.add(row);
        
        row = Arrays.asList("Experiment Id:",""+exp.getId());
        rows.add(row);

        row = Arrays.asList("Experiment URL:",experimentURL(exp));
        rows.add(row);
        
        row = Arrays.asList("Experiment Name:",exp.getAssay().generalDesc.name);
        rows.add(row);

        return rows;
    }
    
    protected String experimentURL(AssayPack exp) {
        return "https://biodare2.ed.ac.uk/experiment/"+exp.getId();
    }

    protected List<List<String>> renderDataHeaders(List<DataTrace> dataSet) {
        
        List<List<String>> rows = new ArrayList<>();
        List<String> row;
        
        row = new ArrayList<>();
        row.add("Data Nr:");
        row.addAll(dataSet.stream()
                .map(dt -> ""+dt.traceNr)
                .collect(Collectors.toList()));
        rows.add(row);
        
        row = new ArrayList<>();
        row.add("Data Ref:");
        row.addAll(dataSet.stream()
                .map(dt -> dt.traceRef)
                .collect(Collectors.toList()));
        rows.add(row);
        
        row = new ArrayList<>();
        row.add("Label:");
        row.addAll(dataSet.stream()
                .map(dt -> dt.details.dataLabel)
                .collect(Collectors.toList()));
        rows.add(row);
        
        return rows;
    }

    protected List<TimeSeries> renderData(List<DataTrace> dataSet) {
        
        return dataSet.stream()
                .map(dt -> dt.trace)
                .collect(Collectors.toList());
    }

    protected void save(List<List<String>> setDescription, List<List<String>> dataHeaders, List<TimeSeries> data, Path file) throws IOException {
        
        setDescription = escape(setDescription);
        dataHeaders = escape(dataHeaders);
        
        TimeSeriesFileHandler.saveToText(data, dataHeaders, setDescription, file.toFile(), ",");
        
    }

    protected List<List<String>> escape(List<List<String>> setDescription) {
        
        return setDescription.stream()
                .map(l -> l.stream()
                        .map(t -> t != null ? t.replace(",", ";") : "")
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toList());
    }
    
    
}
