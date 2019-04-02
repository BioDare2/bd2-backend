/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.bd.parser.topcount.TopCountReader;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.util.Pair;
import java.nio.file.Path;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class TopCountTableSimplifierTest {
    
    public TopCountTableSimplifierTest() {
    }
    
    double EPS = 1E-6;
    TopCountTableSimplifier instance;
    TopCountReader reader;
    
    @Before
    public void setUp() {
        instance = new TopCountTableSimplifier();
        reader = new TopCountReader(false);
    }

    @Test
    public void linearizeOrdersDataByRows() throws Exception {
        
        Path file = TopCountImporterTest.getTopCountTestFile();
        Map<Pair<Integer, Integer>, TimeSeries> data = reader.read(file);
        
        List<TimeSeries> res = instance.linearize(data);
        assertEquals(96,res.size());
        
        assertEquals(2067,res.get(0).getFirst().getValue(),EPS);
        assertEquals(1948,res.get(1).getFirst().getValue(),EPS);
        assertEquals(1475,res.get(11).getFirst().getValue(),EPS);
        assertEquals(2116,res.get(12).getFirst().getValue(),EPS);
        assertEquals(30,res.get(95).getFirst().getValue(),EPS);
    }
    
    @Test
    public void linearizeAddsEmptySeriesOnMissing() {
        Map<Pair<Integer, Integer>, TimeSeries> data = new HashMap<>();
        TimeSeries ser = new TimeSeries();
        ser.add(1,2);
        data.put(new Pair<>(1,10),ser);
        data.put(new Pair<>(2,2),ser);
        
        List<TimeSeries> res = instance.linearize(data);
        assertEquals(96,res.size());

        assertSame(ser,res.get(9));
        assertSame(ser,res.get(13));
        assertTrue(res.get(95).isEmpty());
    }
    
    @Test
    public void seriesToColumnsMakesValuesColumns() {
        
        List<TimeSeries> series = new ArrayList<>();
        TimeSeries ser = new TimeSeries();
        ser.add(1,2);
        ser.add(2,3);
        ser.add(3,3.01);
        series.add(ser);
        
        ser = new TimeSeries();
        series.add(ser);
        
        ser = new TimeSeries();
        ser.add(2,10);
        series.add(ser);
        
        List<List<String>> res = instance.seriesToColumns(series);
        assertEquals(3,res.size());
        assertEquals(Arrays.asList("2.0","3.0","3.01"),res.get(0));
        assertEquals(Collections.emptyList(),res.get(1));
        assertEquals(Arrays.asList("10.0"),res.get(2));
    }
    
    @Test
    public void equalizeColumnsTrimsAndAddEmptyToEqualize() {
        
        List<List<String>> columns = new ArrayList<>();
        columns.add(new ArrayList(Arrays.asList("1","1")));
        columns.add(new ArrayList(new ArrayList<>()));        
        columns.add(new ArrayList(Arrays.asList("1","1","2","3")));
        
        columns = instance.equalizeColumns(columns,3);
        
        assertEquals(Arrays.asList("1","1",""),columns.get(0));
        assertEquals(Arrays.asList("","",""),columns.get(1));
        assertEquals(Arrays.asList("1","1","2"),columns.get(2));
        
    }
    
    @Test
    public void pivotChangesColumnsTableToRowsOne() {

        List<List<String>> columns = new ArrayList<>();
        columns.add(Arrays.asList("1","1","1","2"));
        columns.add(Arrays.asList("2","2","2","3"));
        columns.add(Arrays.asList("3","3","3","4"));
        
        List<List<String>> rows = instance.pivot(columns, 4);
        assertEquals(4,rows.size());
        
        assertEquals(Arrays.asList("1","2","3"),rows.get(0));
        assertEquals(Arrays.asList("1","2","3"),rows.get(1));
        assertEquals(Arrays.asList("1","2","3"),rows.get(2));
        assertEquals(Arrays.asList("2","3","4"),rows.get(3));
        
    }
    
    
    @Test
    public void readTableReadCorrectRowsWithData() throws Exception {
        Path file = TopCountImporterTest.getTopCountTestFile();
        
        List<List<String>> table = instance.readTable(file, 3);
        
        assertEquals(3,table.size());
        table.forEach( row -> assertEquals(96,row.size()));
    }
    
    @Test
    public void simplifies() throws Exception {
        Path file = TopCountImporterTest.getTopCountTestFile();
        
        List<List<String>> table = instance.simplify(file, 5);
        
        assertEquals(5,table.size());
        table.forEach( row -> assertEquals(96,row.size()));
    }
}
