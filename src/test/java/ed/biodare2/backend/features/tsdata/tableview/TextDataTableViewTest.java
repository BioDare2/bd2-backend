/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import static ed.biodare2.backend.features.tsdata.tableview.TextDataTableView.countPresence;
import static ed.biodare2.backend.features.tsdata.tableview.TextDataTableView.isSuitableFormat;
import ed.robust.dom.util.Pair;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class TextDataTableViewTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    public TextDataTableViewTest() {
    }
    
    Path dataFile;
    TextDataTableView instance;
    
    @Before
    public void setUp() throws IOException {
        
        dataFile = testFolder.newFile().toPath();
        instance = new TextDataTableView(dataFile, ",");
    }

    @Test
    public void testCountPresence() throws Exception {
        String sep = ",";
        String line = "";
        
        assertEquals(0, countPresence(line, sep));
        
        line = "abderfgghij";
        assertEquals(0, countPresence(line, sep));
        
        line = ",abderfgghij";
        assertEquals(1, countPresence(line, sep));
        
        line = "abderfgghij,";
        assertEquals(1, countPresence(line, sep));
        
        line = ",,abderfg,g,h,,ij,";
        assertEquals(7, countPresence(line, sep));
        
        sep="\t";
        line = "\tabderfgghi\tj\t";
        assertEquals(3, countPresence(line, sep));
    }
    
    @Test
    public void testIsSuitableFormat() throws Exception {
        
        Path file = testFolder.newFile().toPath();
        assertFalse(isSuitableFormat(file, ","));
        
        List<String> lines = List.of("alkafaf","ma","kota","kot ma ale");        
        Files.write(file, lines);        
        assertFalse(isSuitableFormat(file, ","));
        
        file = testFolder.newFile().toPath();
        lines = List.of("alk,afa,f","ma","ko,ta","kot ma ale");       
        Files.write(file, lines);        
        assertFalse(isSuitableFormat(file, ","));        
        
        file = testFolder.newFile().toPath();
        lines = List.of("alk,afa,f","ma","ko,t,a","k,ot ,ma ale");       
        Files.write(file, lines);        
        assertTrue(isSuitableFormat(file, ","));        
        
    }
    
    @Test
    public void tableSizeCountsCorrectly() throws Exception {
     
        Pair<Integer, Integer> rowColSizes = instance.tableSize();
        
        assertNotNull(rowColSizes);
        Pair<Integer, Integer> exp = new Pair<>(0,0);
        
        assertEquals(exp,rowColSizes);
        
        List<String> rows = List.of("123");
        Files.write(dataFile, rows);
        
        rowColSizes = instance.tableSize();
        exp = new Pair<>(1,1);
        assertEquals(exp,rowColSizes);
        
        rows = List.of("123,2,3");
        Files.write(dataFile, rows);
        
        rowColSizes = instance.tableSize();
        exp = new Pair<>(1,3);
        assertEquals(exp,rowColSizes);
        
        rows = List.of(
                "123,2,3",
                "",
                "2345dfdf fd fdsfdfadsf asf dsafdsafdsafdf dfda",
                "1,2,3,4,5,6,7");
        Files.write(dataFile, rows);
        
        rowColSizes = instance.tableSize();
        exp = new Pair<>(4,7);
        assertEquals(exp,rowColSizes);
    }
    
    @Test
    public void lineToRecordSplitsBySep() {
        
        String line = "";
        List<Object> record = instance.lineToRecord(line);
        List<Object> exp = List.of(""); 
        assertEquals(exp, record);
        
        line = ",1234.5,ala";
        record = instance.lineToRecord(line);
        exp = List.of("","1234.5","ala");        
        assertEquals(exp, record);
        
        instance = new TextDataTableView(dataFile, "\t");
        record = instance.lineToRecord(line);
        exp = List.of(line);        
        assertEquals(exp, record);
        
        line = "12\t34";
        record = instance.lineToRecord(line);
        exp = List.of("12","34");        
        assertEquals(exp, record);

      
    }
    
    @Test 
    public void readRecordsCorrectlyReadsPartOfFile() throws Exception {
        
        
        List<String> rows = List.of(
                "123,2,3,",
                "",
                "2345dfdf fd fdsfdfadsf\tTomek\t",
                "1,2,3,4,5,6,7",
                "A,B,C\t,4,5"        
        );
        Files.write(dataFile, rows);
        
        List<List<Object>> records = instance.readRecords(0, 10);
        
        List<List<Object>> exp = List.of(
                List.of("123","2","3"),
                List.of(""),
                List.of("2345dfdf fd fdsfdfadsf\tTomek\t"),
                List.of("1","2","3","4","5","6","7"),
                List.of("A","B","C\t","4","5")                
        );
        
        assertEquals(exp, records);
        
        records = instance.readRecords(0, 0);
        assertEquals(List.of(), records);
        
        records = instance.readRecords(10, 2);
        assertEquals(List.of(), records);        
        
        records = instance.readRecords(0, 1);
        exp = List.of(
                List.of("123","2","3")
        );
        assertEquals(exp, records);
        
        
        records = instance.readRecords(2, 3);        
        exp = List.of(
                List.of("2345dfdf fd fdsfdfadsf\tTomek\t"),
                List.of("1","2","3","4","5","6","7"),
                List.of("A","B","C\t","4","5")                
        );        
        assertEquals(exp, records);
        
        instance = new TextDataTableView(dataFile, "\t");
        records = instance.readRecords(2, 4);        
        exp = List.of(
                List.of("2345dfdf fd fdsfdfadsf","Tomek"),
                List.of("1,2,3,4,5,6,7"),
                List.of("A,B,C",",4,5")                
        );  
        
        assertEquals(exp, records);
        
    }
    
}
