/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import static ed.biodare2.backend.features.tsdata.dataimport.DataTableImporterTest.getCSVTableInColsParameters;
import static ed.biodare2.backend.features.tsdata.dataimport.DataTableImporterTest.getTestDataFile;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTableImportParameters;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.dataimport.ImportFormat;
import ed.robust.dom.data.TimeSeries;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class ExcelDataTableImporterTest {
    
    double EPS = 1E-6;
    
    public ExcelDataTableImporterTest() {
    }
    
    ExcelDataTableImporter instance;
    
    @Before
    public void setUp() {
        instance = new ExcelDataTableImporter();
    }

    @Test
    @Ignore("The test file is not committed")
    public void importExcelColDataFromLargeFile() throws Exception {
        
        Path file = Paths.get("E:\\Temp\\long_10000x1200.xlsx");
        
        DataTableImportParameters parameters = getCSVTableInColsParameters("long_10000x1200.xlsx");
        parameters.importFormat = ImportFormat.EXCEL_TABLE;
        
        DataBundle boundle = instance.importTimeSeries(file, parameters);
        
        assertNotNull(boundle);
        
        List<DataTrace> data = boundle.data;
        assertEquals(10000,data.size());
        
        DataTrace dtrace = data.get(0);
        TimeSeries trace = dtrace.trace;
        assertEquals(1200, trace.size());
    }      
    
    @Test
    @Ignore("The test file is not committed")
    public void importExcelColDataFromMediumLargeFile() throws Exception {
        
        Path file = Paths.get("E:\\Temp\\long_5000x1200.xlsx");
        
        DataTableImportParameters parameters = getCSVTableInColsParameters("long_5000x1200.xlsx");
        parameters.importFormat = ImportFormat.EXCEL_TABLE;
        
        DataBundle boundle = instance.importTimeSeries(file, parameters);
        
        assertNotNull(boundle);
        
        List<DataTrace> data = boundle.data;
        assertEquals(5000,data.size());
        
        DataTrace dtrace = data.get(0);
        TimeSeries trace = dtrace.trace;
        assertEquals(1200, trace.size());
    } 
    
    @Test
    @Ignore("The test file is not committed")
    public void importExcelColDataFromMediumLargeXLSFile() throws Exception {
        
        Path file = Paths.get("E:\\Temp\\long_255x5000.xls");
        
        DataTableImportParameters parameters = getCSVTableInColsParameters("long_255x5000.xls");
        parameters.importFormat = ImportFormat.EXCEL_TABLE;
        
        DataBundle boundle = instance.importTimeSeries(file, parameters);
        
        assertNotNull(boundle);
        
        List<DataTrace> data = boundle.data;
        assertEquals(255,data.size());
        
        DataTrace dtrace = data.get(0);
        TimeSeries trace = dtrace.trace;
        assertEquals(5000, trace.size());
    }     
    
    @Test
    public void importExcelColDataFromFile() throws Exception {
        
        Path file = getTestDataFile("data_in_cols.xlsx");
        
        DataTableImportParameters parameters = getCSVTableInColsParameters("data_in_cols.csv");
        parameters.importFormat = ImportFormat.EXCEL_TABLE;
        
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
