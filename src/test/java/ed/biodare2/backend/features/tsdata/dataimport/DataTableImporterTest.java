/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare2.backend.features.tsdata.tableview.TextDataTableReader;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellCoordinates;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRole;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTableImportParameters;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.dataimport.ImportFormat;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeType;
import ed.robust.dom.data.TimeSeries;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class DataTableImporterTest {
    
    double EPS = 1E-6;
    
    public DataTableImporterTest() {
    }
    
    DataTableImporter instance;
    TextDataTableReader reader;
    
    @Before
    public void setUp() {
        
        instance = new DataTableImporter();
        reader = mock(TextDataTableReader.class);
        
    }

    
    @Test
    public void importTimesRowGivesTransformedRow() throws Exception {
        
        DataTableImportParameters parameters = new DataTableImportParameters();
        parameters.firstTimeCell = new CellCoordinates(2, 1);
        parameters.timeOffset = 1;
        parameters.timeType = TimeType.TIME_IN_MINUTES;
         
         List<List<Object>> recs = List.of(
                 List.of("A","tom","60","120","600","")
         );
         
         when(reader.readRecords(1, 1)).thenReturn(recs);
         
         List<Double> exp = new ArrayList<>(List.of(1.0+1, 2.0+1, 10.0+1));
         
         List<Double> res = instance.importTimesRow(reader, parameters);
         
         assertEquals(exp, res);
    }
    
    @Test
    public void readTimesRowReadsDoublesFromGivenRowAndColumn() throws Exception {
        
         CellCoordinates firstTime = new CellCoordinates(2, 1);
         
         List<List<Object>> recs = List.of(
                 List.of("A","tom","1","2","10","")
         );
         
         when(reader.readRecords(1, 1)).thenReturn(recs);
         
         List<Double> exp = new ArrayList<>(List.of(1.0, 2.0, 10.0));
         exp.add(null);
         
         List<Double> res = instance.readTimesRow(reader, firstTime);
         
         assertEquals(exp, res);
    }
    
    @Test
    public void valsToDoubleConverts() throws Exception  {
        List<Object> vals = List.of("","","123","1");
        List<Double> exp = new ArrayList<>();
        exp.add(null); exp.add(null); exp.add(123.0); exp.add(1.0);
        
        assertEquals(exp, instance.valsToDoubles(vals));
    }
    
    @Test
    public void testToDoubleConverstion() throws Exception {
        
        Object in = null;
        Double exp = null;
        Double res;
        
        res = instance.valToDouble(in);
        assertNull(res);
        
        in = "";
        res = instance.valToDouble(in);
        assertNull(res);
        
        in = "    \t ";
        res = instance.valToDouble(in);
        assertNull(res);
        
        in = " 123";
        exp = 123.0;
        res = instance.valToDouble(in);
        assertEquals(exp, res, EPS);
        
        in = "123.5";
        exp = 123.5;
        res = instance.valToDouble(in);
        assertEquals(exp, res, EPS);
        
        in = 123L;
        exp = 123.0;
        res = instance.valToDouble(in);
        assertEquals(exp, res, EPS);
        
        in = "ala";
        try {
            instance.valToDouble(in);
            fail("Exception expected");
        } catch (NumberFormatException e) {}
        
    }
    
    @Test
    public void testImportTraceRow() throws Exception {
        
        List<Object> record = List.of("A","toc1","10","20","","40");
        List<Double> times = List.of(1.0,2.0,3.0,4.0);
        
        int curRow = 3;
        int firstCol = 2;
        
        BiFunction<List<Object>, Integer, String> labeller = (List<Object> row, Integer rowIx) -> row.get(1).toString();
        
        Optional<DataTrace> dataO = instance.importTraceRow(record,times,curRow,firstCol,labeller);
        DataTrace data = dataO.get();
        
        TimeSeries expT = new TimeSeries();
        expT.add(1, 10);
        expT.add(2, 20);
        expT.add(4, 40);
        
        assertEquals(expT, data.trace);
        assertEquals("toc1", data.details.dataLabel);
        assertEquals(CellRole.DATA, data.role);
        assertEquals("C4", data.traceRef);
        
    }
    
    
    @Test
    public void testImportTracesRows() throws Exception {
        
        TextDataTableReader.OpennedReader sequentialReader = mock(TextDataTableReader.OpennedReader.class);
        
        List<Object> record1 = List.of("A","toc1","10","20","","40");
        List<Object> record2 = List.of("B","toc2","10","20","","40");
        
        when(sequentialReader.readRecord()).thenReturn(Optional.of(record1), Optional.of(record2), Optional.empty());
        when(sequentialReader.skipLines(3)).thenReturn(3);
        
        List<Double> times = List.of(1.0,2.0,3.0,4.0);
        
        int firstRow = 3;
        int firstCol = 2;
        
        BiFunction<List<Object>, Integer, String> labeller = (List<Object> row, Integer rowIx) -> row.get(1).toString();
        
        List<DataTrace> resp = instance.importTracesRows(sequentialReader, times, firstRow, firstCol, labeller);
        
        assertEquals(2, resp.size());
        
        DataTrace data = resp.get(0);
        
        TimeSeries expT = new TimeSeries();
        expT.add(1, 10);
        expT.add(2, 20);
        expT.add(4, 40);
        
        assertEquals(expT, data.trace);
        assertEquals("toc1", data.details.dataLabel);
        assertEquals(CellRole.DATA, data.role);
        assertEquals("C4", data.traceRef);
        
        data = resp.get(1);
        assertEquals("toc2", data.details.dataLabel);
        

        
    }
    
    @Test
    public void testImportTracesRowsFromReader() throws Exception {
        
        TextDataTableReader.OpennedReader sequentialReader = mock(TextDataTableReader.OpennedReader.class);
        
        when(reader.openReader()).thenReturn(sequentialReader);
        
        List<Object> record1 = List.of("A","toc1","10","20","","40");
        List<Object> record2 = List.of("B","toc2","10","20","","40");
        
        when(sequentialReader.readRecord()).thenReturn(Optional.of(record1), Optional.of(record2), Optional.empty());
        when(sequentialReader.skipLines(3)).thenReturn(3);
        
        List<Double> times = List.of(1.0,2.0,3.0,4.0);
        
        int firstRow = 3;
        int firstCol = 2;
        
        BiFunction<List<Object>, Integer, String> labeller = (List<Object> row, Integer rowIx) -> row.get(1).toString();
        
        List<DataTrace> resp = instance.importTracesRows(reader, times, firstRow, firstCol, labeller);
        
        assertEquals(2, resp.size());
        
        verify(sequentialReader).close();
        
    } 
    
    public static Path getTestDataFile(String name) throws URISyntaxException {
        Path file = Paths.get(DataTableImporterTest.class.getResource(name).toURI());
        return file;
    }
    
    public static DataTableImportParameters getCSVTableInRowsParameters(String fileName) {
       
        DataTableImportParameters parameters = new DataTableImportParameters();
        parameters.fileName = fileName;
        parameters.fileId = parameters.fileName;
        parameters.importFormat = ImportFormat.COMA_SEP;
        parameters.inRows = true;

        parameters.firstTimeCell = new CellCoordinates(1, 0);
        parameters.timeType = TimeType.TIME_IN_HOURS;
        parameters.timeOffset = 1;
        parameters.imgInterval = 0;
    
        parameters.dataStart = new CellCoordinates(-1,1);

        parameters.importLabels = true;
        parameters.labelsSelection = new CellCoordinates(0, -1);
        return parameters;
    }
    
    @Test
    public void importCSVRowDataFromFile() throws Exception {
        
        Path file = getTestDataFile("data_in_rows.csv");
        
        DataTableImportParameters parameters = getCSVTableInRowsParameters("data_in_rows.csv");
        
        DataBundle boundle = instance.importTimeSeries(file, parameters);
        
        assertNotNull(boundle);
        
        List<DataTrace> data = boundle.data;
        assertEquals(64,data.size());
        assertEquals("WT LHY",data.get(0).details.dataLabel);
        assertEquals("WT TOC1",data.get(63).details.dataLabel);
        
        TimeSeries trace = data.get(63).trace;
        assertEquals(1+1, trace.getFirst().getTime(), EPS);
        assertEquals(0.201330533, trace.getFirst().getValue(), EPS);
        assertEquals(1+159, trace.getLast().getTime(), EPS);
        assertEquals(0.553965719, trace.getLast().getValue(), EPS);
                
        assertEquals(1, data.get(0).traceNr);
        assertEquals(64, data.get(63).traceNr);
        
        DataTrace dtrace = data.get(0);
        assertEquals("B2", dtrace.traceFullRef);
        assertEquals("B2", dtrace.traceRef);
        
        dtrace = data.get(63);
        assertEquals("B65", dtrace.traceFullRef);
        assertEquals("B65", dtrace.traceRef);        
        
    }
    
    @Test
    public void importLabelledCSVRowDataFromFile() throws Exception {
        
        Path file = getTestDataFile("data_in_rows.csv");
        
        DataTableImportParameters parameters = getCSVTableInRowsParameters("data_in_rows.csv");
        
        parameters.importLabels = false;
        parameters.userLabels = Arrays.asList(null, null, "L1","L2", null, "L3", null, null);
        DataBundle boundle = instance.importTimeSeries(file, parameters);
        
        assertNotNull(boundle);
        
        List<DataTrace> data = boundle.data;
        assertEquals(3,data.size());
        assertEquals("L1",data.get(0).details.dataLabel);
        assertEquals("L2",data.get(1).details.dataLabel);
        assertEquals("L3",data.get(2).details.dataLabel);
        
        TimeSeries trace = data.get(2).trace;
        assertEquals(1+1, trace.getFirst().getTime(), EPS);
        assertEquals(1.426291469, trace.getFirst().getValue(), EPS);
        assertEquals(1+159, trace.getLast().getTime(), EPS);
        assertEquals(1.799394662, trace.getLast().getValue(), EPS);
                
        assertEquals(1, data.get(0).traceNr);
        assertEquals(3, data.get(2).traceNr);
        
        DataTrace dtrace = data.get(0);
        assertEquals("B3", dtrace.traceFullRef);
        assertEquals("B3", dtrace.traceRef);
        
        dtrace = data.get(2);
        assertEquals("B6", dtrace.traceFullRef);
        assertEquals("B6", dtrace.traceRef);        
        
    }    
    
    public static DataTableImportParameters getCSVTableInColsParameters(String fileName) {
       
        DataTableImportParameters parameters = new DataTableImportParameters();
        parameters.fileName = fileName;
        parameters.fileId = parameters.fileName;
        parameters.importFormat = ImportFormat.COMA_SEP;
        parameters.inRows = false;

        parameters.firstTimeCell = new CellCoordinates(0, 1);
        parameters.timeType = TimeType.TIME_IN_HOURS;
        parameters.timeOffset = 1;
        parameters.imgInterval = 0;
    
        parameters.dataStart = new CellCoordinates(1,-1);

        parameters.importLabels = true;
        parameters.labelsSelection = new CellCoordinates(-1, 0);
        return parameters;
    }    
    
    @Test
    public void importCSVColDataFromFile() throws Exception {
        
        Path file = getTestDataFile("data_in_cols.csv");
        
        DataTableImportParameters parameters = getCSVTableInColsParameters("data_in_cols.csv");
        
        DataBundle boundle = instance.importTimeSeries(file, parameters);
        
        assertNotNull(boundle);
        
        List<DataTrace> data = boundle.data;
        assertEquals(64,data.size());
        assertEquals("WT LHY",data.get(0).details.dataLabel);
        assertEquals("WT TOC1",data.get(63).details.dataLabel);
        
        TimeSeries trace = data.get(63).trace;
        assertEquals(1+1, trace.getFirst().getTime(), EPS);
        assertEquals(0.201330533, trace.getFirst().getValue(), EPS);
        assertEquals(1+159, trace.getLast().getTime(), EPS);
        assertEquals(0.553965719, trace.getLast().getValue(), EPS);
        
        trace = data.get(0).trace;
        assertEquals(1+1, trace.getFirst().getTime(), EPS);
        assertEquals(1.643133821, trace.getFirst().getValue(), EPS);
        assertEquals(1+159, trace.getLast().getTime(), EPS);
        assertEquals(0.859250886, trace.getLast().getValue(), EPS);        
        
        assertEquals(1, data.get(0).traceNr);
        assertEquals(64, data.get(63).traceNr);
        
        DataTrace dtrace = data.get(0);
        assertEquals("B2", dtrace.traceFullRef);
        assertEquals("B2", dtrace.traceRef);
        
        dtrace = data.get(63);
        assertEquals("BM2", dtrace.traceFullRef);
        assertEquals("BM2", dtrace.traceRef);         
        
    }    
    
}
