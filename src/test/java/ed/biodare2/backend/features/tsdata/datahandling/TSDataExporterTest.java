/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.datahandling;

import ed.biodare2.backend.repo.dao.MockReps;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.data.TimeSeries;
import ed.robust.util.timeseries.TSGenerator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author tzielins
 */
public class TSDataExporterTest {
    
    public TSDataExporterTest() {
    }

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    TSDataExporter instance;
    
    AssayPack exp;
    
    
    @Before
    public void init() {
        
        exp = MockReps.testAssayPack();
        instance = new TSDataExporter();
        
        
    }
    
    @Test
    public void renderSetsSettingsPreparesSetsDetails() {
        
        DetrendingType detrending = DetrendingType.POLY_DTR;
        Map<String, String> displayProperties = new HashMap<>();
        
        List<List<String>> headers = instance.renderSetDescription(exp, detrending, displayProperties);
        
        assertNotNull(headers);
        
        List<String> toInclude = Arrays.asList(
                ""+exp.getId(),
                exp.getAssay().getName(),
                "https://biodare2.ed.ac.uk/experiment/"+exp.getId(),
                detrending.longName,
                "Firsttest Lasttest"
                );
        
        List<String> terms = headers.stream().flatMap(List::stream).collect(Collectors.toList());
        //System.out.println("Headers: "+terms);
        
        toInclude.forEach( term -> {
            assertTrue("Missing term: "+term,terms.stream().anyMatch(term::equals));
        });
    }
    
    @Test
    public void renderDataHeadersPreparesDataDetails() {
        
        List<DataTrace> traces = new ArrayList<>();
        
        DataTrace tr = new DataTrace();
        tr.traceNr = 2;
        tr.traceRef = "C2";
        tr.details = new DataColumnProperties("first");
        traces.add(tr);
        
        tr = new DataTrace();
        tr.traceNr = 3;
        tr.traceRef = "D2";
        tr.details = new DataColumnProperties("second");
        traces.add(tr);        
        
        List<List<String>> headers = instance.renderDataHeaders(traces);
        
        assertNotNull(headers);
        
        List<String> exp = Arrays.asList(
                "Data Nr:","2","3",
                "Data Ref:","C2","D2",
                "Label:","first","second"
        );
        
        List<String> terms = headers.stream().flatMap(List::stream).collect(Collectors.toList());
        
        //System.out.println("RenderHeaders: "+terms);
        
        assertEquals(exp,terms);
    }    

    @Test
    public void saveSavesToCSVFile() throws Exception {
        List<List<String>> setDescription = new ArrayList<>();
        setDescription.add(Arrays.asList("DataSet"));
        
        List<List<String>> dataHeaders = new ArrayList<>();
        dataHeaders.add(Arrays.asList("Data","1","2"));
                
        List<TimeSeries> data = new ArrayList<>();
        data.add(TSGenerator.makeCos(50, 1, 24, 1));
        data.add(TSGenerator.makeCos(50, 1, 25, 2));
        
        
        Path file = testFolder.newFile().toPath();        
        assertEquals(0,Files.size(file));
        
        instance.save(setDescription, dataHeaders, data, file);
        
        assertTrue(Files.exists(file));
        assertTrue(Files.size(file) > 10);
        
    }
    
    @Test
    public void exportCreatesFileWithData() throws Exception {
        
        DetrendingType detrending = DetrendingType.POLY_DTR;
        Map<String, String> displayProperties = new HashMap<>();
        
        List<DataTrace> traces = new ArrayList<>();
        
        DataTrace tr = new DataTrace();
        tr.traceNr = 2;
        tr.traceRef = "C2";
        tr.details = new DataColumnProperties("first");
        tr.trace = TSGenerator.makeCos(50, 1, 24, 1);
        traces.add(tr);
        
        tr = new DataTrace();
        tr.traceNr = 3;
        tr.traceRef = "D2";
        tr.details = new DataColumnProperties("second");
        tr.trace = TSGenerator.makeCos(50, 1, 25, 2);
        traces.add(tr); 
        
        //Path file = Paths.get("D:/Temp/t.csv");        
        //assertFalse(Files.exists(file));
        Path file = testFolder.newFile().toPath();        
        assertEquals(0,Files.size(file));        
        
        instance.export(traces, exp, detrending, displayProperties, file);
        
        assertTrue(Files.exists(file));
        assertTrue(Files.size(file) > 10);
    }
    
    
}
