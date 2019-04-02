/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class ExcelTableSimplifierTest {
    
    public ExcelTableSimplifierTest() {
    }

    ExcelTableSimplifier makeInstance() {
        return new ExcelTableSimplifier();
    }
    
    
    @Test
    public void simplifyCorrectlyReducesTheInputData() throws Exception {
        
        Path file = Paths.get(this.getClass().getResource("data-sheet.xlsx").toURI());
        ExcelTableSimplifier instance = makeInstance();
        
        int rows = 2;
        List<List<String>> table = instance.simplify(file, rows,8,8);
        table = table.stream().map( l -> l.subList(0,3)).collect(Collectors.toList());
        
        List<List<String>> exp = new ArrayList<>();
        exp.add(Arrays.asList("ROI '5..","ROI '5..","ROI '5.."));
        exp.add(Arrays.asList("92.00","94.94","77.09"));
        
        assertEquals(rows,table.size());
    }
    
    @Test
    public void readTableReadsRequestedNumbersOfRows() throws Exception {
        
        Path file = Paths.get(this.getClass().getResource("data-sheet.xlsx").toURI());
        ExcelTableSimplifier instance = makeInstance();
        
        int rows = 5;
        List<List<String>> table = instance.readTable(file, rows);
        
        assertEquals(rows,table.size());
    }
    
    @Test
    public void readTableReadsUpToExistingNumberOfRows() throws Exception {
        
        Path file = Paths.get(this.getClass().getResource("data-sheet.xlsx").toURI());
        ExcelTableSimplifier instance = makeInstance();
        
        int rows = 500;
        List<List<String>> table = instance.readTable(file, rows);
        
        assertTrue(rows > table.size());
    }
    
    @Test
    public void readTableReadsCorrectlyRows() throws Exception {
        
        Path file = Paths.get(this.getClass().getResource("data-sheet.xlsx").toURI());
        ExcelTableSimplifier instance = makeInstance();
        
        int rows = 5;
        List<List<String>> table = instance.readTable(file, rows);
        
        List<String> row = table.get(0);
        assertEquals("Image Plane",row.get(0));
        assertEquals("ROI '592' Ellipse (Integrated)",row.get(row.size()-1));
        
        row = table.get(4);
        assertEquals("1.5",row.get(0));
        assertEquals("0",row.get(row.size()-1));
    }    
    
}
