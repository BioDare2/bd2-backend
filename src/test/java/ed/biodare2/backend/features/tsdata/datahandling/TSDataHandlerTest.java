/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.datahandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeSeriesMetrics;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.data.TimeSeries;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
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
        instance = new TSDataHandler(expStorage, new ObjectMapper());
        
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

    @Test
    public void calculatesMetrics() {
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

        TimeSeriesMetrics metrics = instance.calculateMetrics(data);
        
        assertEquals(2, metrics.series);
        assertEquals(3, metrics.avgLastTime, 1E-6);
        assertEquals(2, metrics.avgDuration, 1E-6);
    }
    
    @Test
    public void storesDataMetricsThatCanBeRead() throws Exception {
        
        Path dir = testFolder.newFolder().toPath();
        
        TimeSeries serie = new TimeSeries();
        serie.add(1,1);
        serie.add(2,2);
        serie.add(3,3);        
        
        TimeSeriesMetrics metrics = TimeSeriesMetrics.fromTimeSeries(serie);
        metrics.series = 2;
        
        instance.storeMetrics(metrics, dir);
        assertTrue(Files.isRegularFile(dir.resolve("metrics.json")));
        
        Optional<TimeSeriesMetrics> read = instance.getMetrics(dir);
        assertTrue(read.isPresent());
        assertEquals(metrics, read.get());
    }
    
    @Test
    @Ignore("Allways false as it seems to cause memory leak")
    public void shouldPreCalculateBinnedIsFalseForSparcedData() {
        
        TimeSeries serie = new TimeSeries();
        serie.add(1,1);
        serie.add(3,1);
        serie.add(5,1);
        serie.add(7,1);
        
        DataTrace trace = new DataTrace();
        trace.trace = serie;
        
        List<DataTrace> series = List.of(trace);
        
        assertFalse(instance.shouldPreCalculateHourly(series));
        
        serie = new TimeSeries();
        serie.add(1,1);
        serie.add(2,1);
        serie.add(3,1);
        
        trace = new DataTrace();
        trace.trace = serie;
        
        series = List.of(trace);        
        assertTrue(instance.shouldPreCalculateHourly(series));        
        
    }
    
    @Test
    @Ignore("Allways false as it seems to cause memory leak")    
    public void shouldPreCalculateBinnedIsFalseForGenerallySparcedData() {
        
        TimeSeries serie = new TimeSeries();
        serie.add(0, 1);
        serie.add(0.1, 1);
        serie.add(0.2, 1);
        serie.add(1, 1);
        serie.add(1.1, 1);
        
        DataTrace trace = new DataTrace();
        trace.trace = serie;
        
        
        List<DataTrace> series = List.of(trace);
        
        assertTrue(instance.shouldPreCalculateHourly(series));
        
        serie = new TimeSeries();
        serie.add(1,1);
        serie.add(3,1);
        serie.add(5,1);
        serie.add(7,1);
        DataTrace trace2 = new DataTrace();
        trace2.trace = serie;
        
        series = List.of(trace, trace2, trace2, trace2);
        assertFalse(instance.shouldPreCalculateHourly(series));
    }
    
    @Test
    public void binDataBinsTraces() {
        
        TimeSeries serie = new TimeSeries();
        serie.add(0, 1);
        serie.add(0.1, 2);
        serie.add(0.2, 3);
        serie.add(1, 1);
        serie.add(1.1, 1);
        
        DataTrace trace = new DataTrace();
        trace.trace = serie; 
        trace.dataId = 1;
        
        serie = new TimeSeries();
        serie.add(1,1);
        serie.add(3,2);
        serie.add(4,3);
        DataTrace trace2 = new DataTrace();
        trace2.trace = serie;        
        trace2.dataId = 2;
        
        Map<DetrendingType, List<DataTrace>> detrended = Map.of(DetrendingType.LIN_DTR, List.of(trace, trace2));
        
        Map<DetrendingType, List<DataTrace>> binned = instance.roundTimes(detrended);
        
        assertEquals(detrended.size(), binned.size());
        List<DataTrace> results = binned.get(DetrendingType.LIN_DTR);
        assertNotNull(results);
        
        assertEquals(1, results.get(0).dataId);
        assertEquals(2, results.get(1).dataId);

        serie = new TimeSeries();
        serie.add(0,2);
        serie.add(1,1);
        assertEquals(serie, results.get(0).trace);
        
        serie = new TimeSeries();
        serie.add(1,1);
        serie.add(3,2);
        serie.add(4,3);
        assertEquals(serie, results.get(1).trace);
        

    }
    
    @Test
    public void storeBinnedDataSavesToFiles() throws Exception {
        
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
        instance.storeHourlyData(bundles, dir);
        
        assertEquals(2,Files.list(dir).count());
        
        Path file = dir.resolve(DetrendingType.BAMP_DTR+".hourly.ser");
        assertTrue(Files.isRegularFile(file));
    }
    
    @Test
    public void storedBinnedDataCanBeRetrived() throws Exception {
        
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
        instance.storeHourlyData(bundles, dir);
        
        List<DataTrace> res = instance.getHourlyDataSet(DetrendingType.BAMP_DTR, dir).get();
        
        assertEquals(data.size(),res.size());
        assertEquals(data.get(0).traceRef,res.get(0).traceRef);
        assertEquals(data.get(0).trace,res.get(0).trace);
    }  
    

    @Test
    public void clearPreCalculateRemovesStored() throws Exception {
        
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
        instance.storeHourlyData(bundles, dir);
        
        long stored = Files.list(dir).count();
        assertTrue(stored > 0);
        
        instance.clearPreCalculateHourly(dir);
        stored = Files.list(dir).count();
        assertEquals(0, stored);
        
    }  
    
    @Test
    public void getBinnedDataSetGivesPrerecorded() throws Exception {
        
        Map<DetrendingType, List<DataTrace>> bundles = new HashMap<>();
        
        List<DataTrace> data = new ArrayList<>();
        
        DataTrace trace;
        TimeSeries serie;
        
        trace = new DataTrace();
        trace.traceRef = "A1";        
        serie = new TimeSeries();
        serie.add(1,1);
        serie.add(2,2);
        serie.add(3,3);
        trace.trace = serie;
        data.add(trace);
        
        trace = new DataTrace();
        trace.traceRef = "B1";        
        serie = new TimeSeries();
        serie.add(1,2);
        serie.add(2,3);
        serie.add(3,4);
        trace.trace = serie;
        data.add(trace);
        
        bundles.put(DetrendingType.LIN_DTR,data);
        
        AssayPack exp = mock(AssayPack.class);
        when(exp.getId()).thenReturn(123L);
        
        Path expDir = testFolder.newFolder().toPath();
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);
        
        
        Path dir = instance.getDataStorage(123);
        instance.storeHourlyData(bundles, dir);
        
        List<DataTrace> res = instance.getHourlyDataSet(exp, DetrendingType.LIN_DTR).get();

        
        
        assertEquals(data.size(),res.size());
        assertEquals(data.get(0).traceRef,res.get(0).traceRef);
        assertEquals(data.get(0).trace,res.get(0).trace);
    }  
    
    @Test
    public void getBinnedDataSetCalculatesOnTheFly() throws Exception {
        
        Map<DetrendingType, List<DataTrace>> bundles = new HashMap<>();
        
        List<DataTrace> data = new ArrayList<>();
        
        DataTrace trace;
        TimeSeries serie;
        
        trace = new DataTrace();
        trace.traceRef = "A1";        
        serie = new TimeSeries();
        serie.add(1,1);
        serie.add(1.6,1.5);
        serie.add(3,2);
        serie.add(5,3);
        trace.trace = serie;
        data.add(trace);
        
        TimeSeries expected = new TimeSeries();
        expected.add(1,1);
        expected.add(2,1.5);
        expected.add(3,2);
        expected.add(5,3);
        
        trace = new DataTrace();
        trace.traceRef = "B1";        
        serie = new TimeSeries();
        serie.add(1,2);
        serie.add(2,3);
        serie.add(3,4);
        trace.trace = serie;
        data.add(trace);
        
        bundles.put(DetrendingType.LIN_DTR,data);
        
        AssayPack exp = mock(AssayPack.class);
        when(exp.getId()).thenReturn(123L);
        
        Path expDir = testFolder.newFolder().toPath();
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);
        
        
        Path dir = instance.getDataStorage(123);
        instance.storeData(bundles, dir);
        
        List<DataTrace> res = instance.getHourlyDataSet(exp, DetrendingType.LIN_DTR).get();

        
        
        assertEquals(data.size(),res.size());
        assertEquals(data.get(0).traceRef,res.get(0).traceRef);
        
        assertEquals(expected,res.get(0).trace);
    }    
    
    
}
