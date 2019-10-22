/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare2.backend.features.tsdata.tableview.TextDataTableReader;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellCoordinates;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRange;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRole;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBlock;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTableImportParameters;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.robust.dom.data.TimeSeries;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class TextDataTableImporter extends TSDataImporter {
   
    final TextTableTransposer transposer = new TextTableTransposer();
    
    public DataBundle importTimeSeries(Path file, DataTableImportParameters parameters) throws ImportException {
        
        try {
            if (parameters.inRows) {
                return importTimeSeriesFromRows(file, parameters);
            } else {
                return importTimeSeriesFromCols(file, parameters);
            }
        } catch (IOException e) {
            throw new ServerSideException("Cannot read file: "+e.getMessage(),e);
        }    
    }
    
    DataBundle importTimeSeriesFromCols(Path file, DataTableImportParameters parameters) throws ImportException, IOException {
        
        Path transpFile = Files.createTempFile(null, null);
        try {
            
            TextDataTableReader orgReader = makeReader(file, parameters);            
            transposer.transpose(orgReader, transpFile, orgReader.sep);
            
            DataTableImportParameters transp = parameters.transpose();
            DataBundle boundle = importTimeSeriesFromRows(transpFile, transp);
            transposeBoundle(boundle);
            return boundle;
        } catch (TransposableImportException e) {
            throw e.transpose();
        } finally {
            if (Files.exists(transpFile)) Files.delete(transpFile);
        }
        
    }    

    DataBundle importTimeSeriesFromRows(Path file, DataTableImportParameters parameters) throws ImportException, IOException {
        
        TextDataTableReader reader = makeReader(file, parameters);
        
        return importTimeSeriesFromRows(reader, parameters);
    }
    
    DataBundle importTimeSeriesFromRows(TextDataTableReader reader, DataTableImportParameters parameters) throws IOException, ImportException {
        
        List<Double> times = importTimesRow(reader, parameters);
        
        List<DataTrace> traces = importTracesRows(reader,times,parameters);
        
        DataBlock block = new DataBlock();
        block.role = CellRole.DATA;
        
        CellRange range = new CellRange();
        range.first = traces.get(0).coordinates;
        range.last = traces.get(traces.size()-1).coordinates;
        block.range = range;
        block.traces = traces;
        
        List<DataBlock> blocks = List.of(block);
        insertNumbers(blocks);
        insertIds(blocks);
        
        return makeBundle(blocks);
    }
    

    TextDataTableReader makeReader(Path file, DataTableImportParameters parameters) {
        
        switch (parameters.importFormat) {
            case COMA_SEP: return new TextDataTableReader(file, ",");
            case TAB_SEP: return new TextDataTableReader(file, "\t");
            default: throw new IllegalArgumentException("Unsuported format "+parameters.importFormat);
        }
    }

    List<Double> importTimesRow(TextDataTableReader reader, DataTableImportParameters parameters) throws IOException, ImportException {
            
        List<Double> times = readTimesRow(reader, parameters.firstTimeCell);    
        
        times = processTimes(times, parameters.timeType, parameters.timeOffset, parameters.imgInterval);
        return times;
    }

    List<Double> readTimesRow(TextDataTableReader reader, CellCoordinates firstTimeCell) throws IOException, ImportException {
        
        List<List<Object>> records = reader.readRecords(firstTimeCell.row, 1);
        
        if (records.isEmpty()) throw new TransposableImportException("Mising time ", firstTimeCell.row, null);
        
        List<Object> times = records.get(0);
        times = times.subList(firstTimeCell.col, times.size());
        
        try {
            return valsToDoubles(times);
        } catch (NumberFormatException e) {
            throw new TransposableImportException("Non numberical value in", firstTimeCell.row, null);
        }
        
    }

    List<Double> valsToDoubles(List<Object> vals) {
        
        return vals.stream().map( v -> valToDouble(v))
                .collect(Collectors.toList());
    }

    Double valToDouble(Object v) {
        if (v == null) return null;
        if (v instanceof Number) {
            return ((Number) v).doubleValue();
        }
        String s = v.toString().trim();
        if (s.equals("")) return null;
        
        return Double.parseDouble(s);
    }

    List<DataTrace> importTracesRows(TextDataTableReader reader, List<Double> times, DataTableImportParameters parameters) throws IOException, TransposableImportException {
        
        int firstRow = parameters.dataStart.row;
        int firstCol = parameters.firstTimeCell.col;
        BiFunction<List<Object>, Integer, String> labeller;
        
        if (parameters.importLabels) {
            labeller = (List<Object> row, Integer rowIx) -> row.get(parameters.labelsSelection.col).toString();
        } else {
            final List<String> labels = parameters.userLabels;
            labeller = (List<Object> row, Integer rowIx) -> {
                if (rowIx >= labels.size()) return null;
                return labels.get(rowIx);
            };
        }
        
        return importTracesRows(reader, times, firstRow, firstCol, labeller);
    }

    List<DataTrace> importTracesRows(TextDataTableReader reader, List<Double> times, 
            int firstRow, int firstCol, BiFunction<List<Object>, Integer, String> labeller) throws IOException, TransposableImportException {
        
        try(TextDataTableReader.OpennedReader sequentialReader = reader.openReader()) {
            
            return importTracesRows(sequentialReader, times, firstRow, firstCol, labeller);
        }
    }
    
    List<DataTrace> importTracesRows(TextDataTableReader.OpennedReader sequentialReader, List<Double> times, 
            int firstRow, int firstCol, BiFunction<List<Object>, Integer, String> labeller) throws IOException, TransposableImportException {
        
        List<DataTrace> traces = new ArrayList<>();

        sequentialReader.skipLines(firstRow);
        int curRow = firstRow;
        Optional<List<Object>> record;

        while ( (record = sequentialReader.readRecord()).isPresent()) {
            Optional<DataTrace> trace = importTraceRow(record.get(), times, curRow, firstCol, labeller);
            if (trace.isPresent())
                traces.add(trace.get());
            curRow++;
        };
        return traces;
    }    

    Optional<DataTrace> importTraceRow(List<Object> record, List<Double> times, int curRow, int firstCol, BiFunction<List<Object>, Integer, String> labeller) throws TransposableImportException {
        
        
        String label = labeller.apply(record, curRow);
        if (label == null || label.trim().isEmpty()) {
            return Optional.empty();
        }

        List<Double> values = valsToDoubles(record.subList(firstCol, record.size()));        
        TimeSeries timeserie = makeSerie(times, values);
        
        if (timeserie.isEmpty()) {
            return Optional.empty();
        }
        
        DataTrace trace = makeTrace(timeserie, label, CellRole.DATA, firstCol, curRow);
        
        return Optional.of(trace);
    }
    
    protected DataTrace makeTrace(TimeSeries serie, String label, CellRole role, int col, int row) {

        DataTrace trace = new DataTrace();
        trace.coordinates = new CellCoordinates(col+1,row+1);
        trace.traceRef = traceRef(trace.coordinates);
        trace.traceFullRef = trace.traceRef;
        trace.details = new DataColumnProperties(label);
        trace.role = role;
        trace.trace = serie;
        return trace;
    }

    protected String traceRef(CellCoordinates coordinates) {
        return coordinates.columnLetter()+coordinates.row;
    }

    void transposeBoundle(DataBundle boundle) {
        
        boundle.backgrounds.forEach( dt -> transposeDataTrace(dt));
        boundle.data.forEach( dt -> transposeDataTrace(dt));
        boundle.blocks.forEach( block -> {
            block.range = block.range.transpose();
        });
        
    }

    void transposeDataTrace(DataTrace trace) {
        
        trace.coordinates = trace.coordinates.transpose();
        trace.traceRef = traceRef(trace.coordinates);
        trace.traceFullRef = trace.traceRef;
    }

}
