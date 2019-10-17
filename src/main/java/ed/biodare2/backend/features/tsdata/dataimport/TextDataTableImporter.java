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
   
    public DataBundle importTimeSeries(Path file, DataTableImportParameters parameters) throws ImportException {
        
        try {
            if (parameters.inRows) {
                return importTimeSeriesFromRows(file, parameters);
            } else {
                throw new UnsupportedOperationException("Importing from columns not implemetned");
            }
        } catch (IOException e) {
            throw new ServerSideException("Cannot read file: "+e.getMessage(),e);
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
        
        if (records.isEmpty()) throw new PivotableImportException("Mising time ", firstTimeCell.row, null);
        
        List<Object> times = records.get(0);
        times = times.subList(firstTimeCell.col, times.size());
        
        try {
            return valsToDoubles(times);
        } catch (NumberFormatException e) {
            throw new PivotableImportException("Non numberical value in", firstTimeCell.row, null);
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

    List<DataTrace> importTracesRows(TextDataTableReader reader, List<Double> times, DataTableImportParameters parameters) throws IOException, PivotableImportException {
        
        int firstRow = parameters.dataStart.row;
        int firstCol = parameters.firstTimeCell.col;
        BiFunction<List<Object>, Integer, String> labeller;
        
        if (parameters.importLabels) {
            labeller = (List<Object> row, Integer rowIx) -> row.get(parameters.labelsSelection.col).toString();
        } else {
            labeller = (List<Object> row, Integer rowIx) -> ""+(rowIx+1);
        }
        
        return importTracesRows(reader, times, firstRow, firstCol, labeller);
    }

    List<DataTrace> importTracesRows(TextDataTableReader reader, List<Double> times, 
            int firstRow, int firstCol, BiFunction<List<Object>, Integer, String> labeller) throws IOException, PivotableImportException {
        
        try(TextDataTableReader.OpennedReader sequentialReader = reader.openReader()) {
            
            return importTracesRows(sequentialReader, times, firstRow, firstCol, labeller);
        }
    }
    
    List<DataTrace> importTracesRows(TextDataTableReader.OpennedReader sequentialReader, List<Double> times, 
            int firstRow, int firstCol, BiFunction<List<Object>, Integer, String> labeller) throws IOException, PivotableImportException {
        
        List<DataTrace> traces = new ArrayList<>();

        sequentialReader.skipLines(firstRow);
        int curRow = firstRow;
        Optional<List<Object>> record;

        while ( (record = sequentialReader.readRecord()).isPresent()) {
            DataTrace trace = importTraceRow(record.get(), times, curRow, firstCol, labeller);
            traces.add(trace);
            curRow++;
        };
        return traces;
    }    

    DataTrace importTraceRow(List<Object> record, List<Double> times, int curRow, int firstCol, BiFunction<List<Object>, Integer, String> labeller) throws PivotableImportException {
        
        List<Double> values = valsToDoubles(record.subList(firstCol, record.size()));
        
        TimeSeries timeserie = makeSerie(times, values);
        
        String label = labeller.apply(record, curRow);
        if (label == null || label.trim().isEmpty()) {
            if (!timeserie.isEmpty()) {
                throw new PivotableImportException("Missing label", curRow, null);
            } else {
                label = "empty";
            }
        }
        
        DataTrace trace = makeTrace(timeserie, label, CellRole.DATA, firstCol, curRow);
        
        return trace;
    }
    
    protected DataTrace makeTrace(TimeSeries serie, String label, CellRole role, int col, int row) {

        DataTrace trace = new DataTrace();
        trace.coordinates = new CellCoordinates(col+1,row+1);
        trace.traceRef = trace.coordinates.columnLetter()+trace.coordinates.row;
        trace.traceFullRef = trace.traceRef;
        trace.details = new DataColumnProperties(label);
        trace.role = role;
        trace.trace = serie;
        return trace;
    }    

}
