/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.datahandling;

import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.data.TimeSeries;
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
import static org.mockito.Mockito.*;

/**
 *
 * @author tzielins
 */
public class TSDataHandlerTest {
    
    public TSDataHandlerTest() {
    }
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    TSDataHandler instance;
    
    ExperimentsStorage expStorage;
    
    @Before
    public void init() {
        expStorage = mock(ExperimentsStorage.class);
        instance = new TSDataHandler(expStorage);
        
    }

    @Test
    public void standarizeRemovesBackground() {
        
        List<DataTrace> data = new ArrayList<>();
        List<DataTrace> bckg = new ArrayList<>();
        
        DataTrace trace;
        TimeSeries serie;
        
        trace = new DataTrace();
        trace.traceRef = "A";        
        serie = new TimeSeries();
        serie.add(1,1);
        serie.add(2,2);
        trace.trace = serie;
        data.add(trace);
        
        trace = new DataTrace();
        trace.traceRef = "B";        
        serie = new TimeSeries();
        serie.add(1,2);
        serie.add(2,4);
        serie.add(3,5);
        trace.trace = serie;
        data.add(trace);
        
        trace = new DataTrace();
        trace.traceRef = "C";        
        serie = new TimeSeries();
        serie.add(0,0);
        serie.add(1,0);
        serie.add(3,2);
        serie.add(4,2);
        trace.trace = serie;
        bckg.add(trace);
        
        trace = new DataTrace();
        trace.traceRef = "D";        
        serie = new TimeSeries();
        serie.add(0,0);
        serie.add(1,2);
        serie.add(3,4);
        serie.add(4,6);
        trace.trace = serie;
        bckg.add(trace);
        
        DataBundle rawData = new DataBundle();
        rawData.backgrounds = bckg;
        rawData.data = data;
        
        List<DataTrace> res = instance.standarize(rawData);
        
        assertEquals(2,res.size());
        assertEquals(Arrays.asList("A","B"),res.stream().map(t -> t.traceRef).collect(Collectors.toList()));
        
        TimeSeries exp = new TimeSeries();
        exp.add(1,0);
        exp.add(2,0);
        assertTrue(exp.almostEquals(res.get(0).trace, 1E-3));
        
        exp = new TimeSeries();
        exp.add(1,1);
        exp.add(2,2);
        exp.add(3,2);
        assertTrue(exp.almostEquals(res.get(1).trace, 1E-3));
        
    }
    
    @Test
    public void processDataDoesDetrending() {
        
        List<DataTrace> data = new ArrayList<>();
        
        DataTrace trace;
        TimeSeries serie;
        
        trace = new DataTrace();
        trace.traceRef = "A";        
        serie = new TimeSeries();
        serie.add(1,1);
        serie.add(2,2);
        serie.add(3,3);
        trace.trace = serie;
        data.add(trace);
        
        trace = new DataTrace();
        trace.traceRef = "B";        
        serie = new TimeSeries();
        serie.add(1,2);
        serie.add(2,3);
        serie.add(3,4);
        trace.trace = serie;
        data.add(trace);
        
        trace = new DataTrace();
        trace.traceRef = "C";        
        serie = new TimeSeries();
        serie.add(0,1);
        serie.add(1,3);
        serie.add(3,7);
        serie.add(4,9);
        trace.trace = serie;
        data.add(trace);
        
        
        Map<DetrendingType,List<DataTrace>> proc = instance.processData(data);
        
        for(DetrendingType detrending:DetrendingType.values()) {
            List<DataTrace> res = proc.get(detrending);
            assertEquals(data.size(),res.size());
        
            

            for (int i =0;i<data.size();i++) {
                assertEquals(data.get(i).trace.size(), res.get(i).trace.size());
            
                if (detrending.equals(DetrendingType.NO_DTR)) {
                    assertEquals(data.get(i).trace, res.get(i).trace);
                } else {
                    double[] exp = new double[data.get(i).trace.size()];
                    Arrays.fill(exp,data.get(i).trace.getMeanValue());

                    //System.out.println(Arrays.toString(data.get(i).trace.getValues()));
                    //System.out.println(Arrays.toString(res.get(i).trace.getValues()));
                    assertArrayEquals(exp, res.get(i).trace.getValues(),1E-3);
                }
            }
            
        }
        
    }
    
    @Test
    public void processDataPreservesInputDataRawIdAndTraceRef() {
        
        List<DataTrace> data = new ArrayList<>();
        
        DataTrace trace;
        TimeSeries serie;
        
        trace = new DataTrace();
        trace.traceRef = "A";   
        trace.rawDataId = 3;
        serie = new TimeSeries();
        serie.add(1,1);
        serie.add(2,2);
        serie.add(3,3);
        trace.trace = serie;
        data.add(trace);
        
        trace = new DataTrace();
        trace.traceRef = "B";
        trace.rawDataId = 4;
        serie = new TimeSeries();
        serie.add(1,2);
        serie.add(2,3);
        serie.add(3,4);
        trace.trace = serie;
        data.add(trace);
        
        
        
        Map<DetrendingType,List<DataTrace>> proc = instance.processData(data);
        
        for(DetrendingType detrending:DetrendingType.values()) {
            List<DataTrace> res = proc.get(detrending);
            assertEquals(data.size(),res.size());
        
            assertEquals(
                    data.stream().map(t -> t.traceRef).collect(Collectors.toList()),
                    res.stream().map(t -> t.traceRef).collect(Collectors.toList())
            );
            assertEquals(
                    data.stream().map(t -> t.rawDataId).collect(Collectors.toList()),
                    res.stream().map(t -> t.rawDataId).collect(Collectors.toList())
            );            

            
        }
        
    }    
    
    @Test
    public void storeDataSavesToFiles() throws Exception {
        
        Map<DetrendingType, List<DataTrace>> bundles = new HashMap<>();
        
        List<DataTrace> data = new ArrayList<>();
        
        DataTrace trace;
        TimeSeries serie;
        
        trace = new DataTrace();
        trace.traceRef = "A";        
        serie = new TimeSeries();
        serie.add(1,1);
        serie.add(2,2);
        serie.add(3,3);
        trace.trace = serie;
        data.add(trace);
        
        trace = new DataTrace();
        trace.traceRef = "B";        
        serie = new TimeSeries();
        serie.add(1,2);
        serie.add(2,3);
        serie.add(3,4);
        trace.trace = serie;
        data.add(trace);
        
        bundles.put(DetrendingType.NO_DTR,data);
        
        data = new ArrayList<>();        
        trace = new DataTrace();
        trace.traceRef = "C";        
        serie = new TimeSeries();
        serie.add(0,1);
        serie.add(1,3);
        serie.add(3,7);
        serie.add(4,9);
        trace.trace = serie;
        data.add(trace);
        
        bundles.put(DetrendingType.BAMP_DTR,data);
        
        Path dir = testFolder.newFolder().toPath();
        instance.storeData(bundles, dir);
        
        assertEquals(2,Files.list(dir).count());
    }
    
    @Test
    public void storedDataCanBeRetrived() throws Exception {
        
        Map<DetrendingType, List<DataTrace>> bundles = new HashMap<>();
        
        List<DataTrace> data = new ArrayList<>();
        
        DataTrace trace;
        TimeSeries serie;
        
        trace = new DataTrace();
        trace.traceRef = "A";        
        serie = new TimeSeries();
        serie.add(1,1);
        serie.add(2,2);
        serie.add(3,3);
        trace.trace = serie;
        data.add(trace);
        
        trace = new DataTrace();
        trace.traceRef = "B";        
        serie = new TimeSeries();
        serie.add(1,2);
        serie.add(2,3);
        serie.add(3,4);
        trace.trace = serie;
        data.add(trace);
        
        bundles.put(DetrendingType.NO_DTR,data);
        
        data = new ArrayList<>();        
        trace = new DataTrace();
        trace.traceRef = "C";        
        serie = new TimeSeries();
        serie.add(0,1);
        serie.add(1,3);
        serie.add(3,7);
        serie.add(4,9);
        trace.trace = serie;
        data.add(trace);
        
        bundles.put(DetrendingType.BAMP_DTR,data);
        
        Path dir = testFolder.newFolder().toPath();
        instance.storeData(bundles, dir);
        
        List<DataTrace> res = instance.getDataSet(DetrendingType.BAMP_DTR, dir).get();
        
        assertEquals(data.size(),res.size());
        assertEquals(data.get(0).traceRef,res.get(0).traceRef);
        assertEquals(data.get(0).trace,res.get(0).trace);
    }    
}
