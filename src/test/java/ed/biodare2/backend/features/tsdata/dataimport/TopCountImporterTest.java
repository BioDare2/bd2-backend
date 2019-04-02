/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare2.backend.repo.isa_dom.dataimport.CellCoordinates;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRange;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRangeDescription;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRole;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBlock;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.dataimport.ExcelTSImportParameters;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeColumnProperties;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.util.Pair;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class TopCountImporterTest {
    
    double EPS = 1E-6;
    TopCountImporter instance;
    
    public TopCountImporterTest() {
    }
    
    
    public static Path getTopCountTestFile() {
        try {
            return Paths.get(TopCountImporterTest.class.getResource("col1609.zip").toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }    
    
    @Before
    public void setUp() {
        instance = new TopCountImporter();
        
            
    }
    
    @Test
    public void importsTheData() throws Exception {
        
        Path file = getTopCountTestFile();
        
            ExcelTSImportParameters parameters = new ExcelTSImportParameters();
            
            CellRangeDescription timeColumn = new CellRangeDescription();
            //timeColumn.range = new CellRange();
            //timeColumn.range.first = new CellCoordinates(1,2);
            //timeColumn.range.last = timeColumn.range.first;
        
            TimeColumnProperties prop = new TimeColumnProperties();
            //prop.firstRow = 2;
            //prop.timeType = TimeType.TIME_IN_HOURS;
            prop.timeOffset = -2;
            timeColumn.details = prop;
            timeColumn.role = CellRole.TIME;
            
            parameters.timeColumn = timeColumn;
            parameters.dataBlocks.add(timeColumn);
            
            CellRangeDescription dsc = new CellRangeDescription();
            DataColumnProperties details = new DataColumnProperties();
            dsc.details = details;
            details.dataLabel = "WT";
            
            dsc.role = CellRole.DATA;
            dsc.range = new CellRange();
            dsc.range.first = new CellCoordinates(1, 0);
            dsc.range.last = new CellCoordinates(84, 0);
            parameters.dataBlocks.add(dsc);
            
            dsc = new CellRangeDescription();            
            dsc.role = CellRole.BACKGROUND;
            dsc.range = new CellRange();
            dsc.range.first = new CellCoordinates(85, 0);
            dsc.range.last = new CellCoordinates(96, 0);            
            parameters.dataBlocks.add(dsc); 
            
            DataBundle bundle = instance.importTimeSeries(file, parameters);
            
            assertEquals(2,bundle.blocks.size());
            assertEquals(12,bundle.backgrounds.size());
            assertEquals(96-12,bundle.data.size());
            
            bundle.data.stream().map( d -> d.trace.getLast().getTime()).allMatch(d -> d > (9+12-2) && d< (10+12-2));
            List<Integer> ids = bundle.data.stream().map( d -> d.traceNr).collect(Collectors.toList());
            List<Integer> expIds = new ArrayList<>();
            for (int i =1;i<85;i++) expIds.add(i);
            assertEquals(expIds,ids);
            
            List<Long> lIds = new ArrayList<>();
            for (long i = 1;i<85;i++) lIds.add(i);
            
            assertEquals(lIds,
                    bundle.data.stream().map( d -> d.dataId).collect(Collectors.toList()));

            assertEquals(lIds,
                    bundle.data.stream().map( d -> d.rawDataId).collect(Collectors.toList()));
            
            List<String> refs = bundle.data.stream().map( d -> d.traceRef).collect(Collectors.toList());
            List<String> expR = Arrays.asList("A1","A2","A3","A4","A5","A6");
            assertEquals(expR,refs.subList(0, 6));
            
    }

    @Test
    public void processTimesOffsetsTime() {
        
        TimeColumnProperties timeColumnProperties = new TimeColumnProperties();
        timeColumnProperties.timeOffset = 0;
        
        Map<Pair<Integer, Integer>, TimeSeries> input = new HashMap<>();
        input.put(new Pair<>(1,1),new TimeSeries());
        input.put(new Pair<>(2,3),new TimeSeries());
        input.get(new Pair<>(1,1)).add(2, 3);
        input.get(new Pair<>(2,3)).add(3, 4);
        
        Map<Pair<Integer, Integer>, TimeSeries> res = instance.processTimes(input, timeColumnProperties);
        assertSame(input,res);
        
        timeColumnProperties.timeOffset = -2;
        res = instance.processTimes(input, timeColumnProperties);
        assertNotSame(input,res);        
        assertEquals(input.size(),res.size());
        
        assertEquals(0,res.get(new Pair<>(1,1)).getFirst().getTime(),EPS);
        assertEquals(1,res.get(new Pair<>(2,3)).getFirst().getTime(),EPS);
    }
    
    @Test
    public void colNrToCoordinatesWorks() {
        
        int col = 0;
        Pair<Integer,Integer> exp = new Pair<>(1,1);        
        assertEquals(exp,instance.colNrToCoordinates(col));
        
        col = 11;
        exp = new Pair<>(1,12);        
        assertEquals(exp,instance.colNrToCoordinates(col));
        
        col = 12;
        exp = new Pair<>(2,1);        
        assertEquals(exp,instance.colNrToCoordinates(col));
        
        col = 95;
        exp = new Pair<>(8,12);        
        assertEquals(exp,instance.colNrToCoordinates(col));
        
    }
    
    @Test
    public void coordinatesToWellGivesWells() {
        Pair<Integer,Integer> key = new Pair<>(1,1);
        String exp = "A1";
        assertEquals(exp, instance.coordinatesToWell(key));
        
        
        key = new Pair<>(1,12);
        exp = "A12";
        assertEquals(exp, instance.coordinatesToWell(key));
        
        key = new Pair<>(2,1);
        exp = "B1";
        assertEquals(exp, instance.coordinatesToWell(key));
        
        key = new Pair<>(8,12);
        exp = "H12";
        assertEquals(exp, instance.coordinatesToWell(key));
        
    }
    
    @Test
    public void makeTracePopulatesValuesCorrectly() {
        TimeSeries series = new TimeSeries();        
        DataColumnProperties details = new DataColumnProperties("WT");
        CellRole role = CellRole.DATA;
        Pair<Integer, Integer> key = new Pair<>(2,3);
        
        DataTrace trace = instance.makeTrace(series, details, role, key);
        assertEquals(new CellCoordinates(2,3),trace.coordinates);
        assertSame(details,trace.details);
        assertSame(role,trace.role);
        assertSame(series,trace.trace);
        assertEquals("B3",trace.traceFullRef);
        assertEquals("B3",trace.traceRef);
    }
    
    @Test
    public void makeBlockMakesIt() {
        Map<Pair<Integer, Integer>, TimeSeries> data = new HashMap<>();
        CellRangeDescription dsc = new CellRangeDescription();
        dsc.details = new DataColumnProperties("WT");
        dsc.range = new CellRange();
        dsc.range.first = new CellCoordinates(1, 1);
        dsc.range.last = new CellCoordinates(8, 1);
        dsc.role = CellRole.BACKGROUND;
        
        for (int row = 1;row<=1;row++) {
            for (int col = 1;col<=8;col++) {
                data.put(new Pair<>(row,col),new TimeSeries());
            }
        }
        
        DataBlock block = instance.makeBlock(data, dsc);
        assertSame(dsc.details,block.details);
        assertSame(dsc.range,block.range);
        assertSame(dsc.role,block.role);
        assertEquals(8,block.traces.size());
        
    }
    
}
