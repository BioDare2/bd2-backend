/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import static ed.biodare2.backend.features.tsdata.tableview.TextDataTableReader.countPresence;
import static ed.biodare2.backend.features.tsdata.tableview.TextDataTableReader.isSuitableFormat;
import ed.robust.dom.util.Pair;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class TextDataTableReaderTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    public TextDataTableReaderTest() {
    }
    
    Path dataFile;
    TextDataTableReader instance;
    
    @Before
    public void setUp() throws IOException {
        
        dataFile = testFolder.newFile().toPath();
        instance = new TextDataTableReader(dataFile, ",");
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
     
        Pair<Integer, Integer> rowColSizes = instance.rowsColsTableSize();
        
        assertNotNull(rowColSizes);
        Pair<Integer, Integer> exp = new Pair<>(0,0);
        
        assertEquals(exp,rowColSizes);
        
        List<String> rows = List.of("123");
        Files.write(dataFile, rows);
        
        instance = new TextDataTableReader(dataFile, ",");
        rowColSizes = instance.rowsColsTableSize();
        exp = new Pair<>(1,1);
        assertEquals(exp,rowColSizes);
        
        rows = List.of("123,2,3");
        Files.write(dataFile, rows);
        
        instance = new TextDataTableReader(dataFile, ",");
        rowColSizes = instance.rowsColsTableSize();
        exp = new Pair<>(1,3);
        assertEquals(exp,rowColSizes);
        
        rows = List.of(
                "123,2,3",
                "",
                "2345dfdf fd fdsfdfadsf asf dsafdsafdsafdf dfda",
                "1,2,3,4,5,6,7");
        Files.write(dataFile, rows);
        
        instance = new TextDataTableReader(dataFile, ",");
        rowColSizes = instance.rowsColsTableSize();
        exp = new Pair<>(4,7);
        assertEquals(exp,rowColSizes);
    }
    
    @Test
    public void tableSizeCachesValues() throws Exception {
     

        
        List<String> rows = List.of(
                "123,2,3",
                "",
                "2345dfdf fd fdsfdfadsf asf dsafdsafdsafdf dfda",
                "1,2,3,4,5,6,7");
        Files.write(dataFile, rows);
        
        Pair<Integer,Integer> rowColSizes = instance.rowsColsTableSize();
        Pair<Integer,Integer> exp = new Pair<>(4,7);
        assertEquals(exp,rowColSizes);
        
        exp = rowColSizes;
        rowColSizes = instance.rowsColsTableSize();
        assertSame(exp, rowColSizes);
        
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
        
        instance = new TextDataTableReader(dataFile, "\t");
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
        
        instance = new TextDataTableReader(dataFile, "\t");
        records = instance.readRecords(2, 4);        
        exp = List.of(
                List.of("2345dfdf fd fdsfdfadsf","Tomek"),
                List.of("1,2,3,4,5,6,7"),
                List.of("A,B,C",",4,5")                
        );  
        
        assertEquals(exp, records);
        
    }
  
    
    @Test 
    public void opennedReaderCanSkipLinesAndReadContent() throws Exception {
        
        
        List<String> rows = List.of(
                "123,2,3,",
                "",
                "2345dfdf fd fdsfdfadsf\tTomek\t",
                "1,2,3,4,5,6,7",
                "A,B,C\t,4,5"        
        );
        Files.write(dataFile, rows);
        
        
        try (TextDataTableReader.OpennedReader reader = new TextDataTableReader.OpennedReader(dataFile, ",")) {
            
            int skipped = reader.skipLines(2);
            assertEquals(2, skipped);
            
            List<List<Object>> expRecords = List.of(
                    List.of("2345dfdf fd fdsfdfadsf\tTomek\t"),
                    List.of("1","2","3","4","5","6","7"),
                    List.of("A","B","C\t","4","5")                
            );            
            
            for (List<Object> exp : expRecords) {
                Optional<List<Object>> opt = reader.readRecord();
                assertTrue(opt.isPresent());
                assertEquals(exp, opt.get());
            }
            
            assertTrue(reader.readRecord().isEmpty());
            assertTrue(reader.readRecord().isEmpty());
            assertTrue(reader.readRecord().isEmpty());
            
        }
        
        
    }
    
    // @Test
    public void makeLongCSVColumnFile() throws Exception {
        
        int series = 1000;
        int timepoints = 5*24*10;
        int unit = 6; // minutes
        
        Path file = Paths.get("E:/Temp/long_"+series+"x"+timepoints+".csv");
        try (BufferedWriter out = Files.newBufferedWriter(file)) {
            Random r = new Random();
            List<String> row = new ArrayList<>(series+1);
            row.add("Time");
            for (int i = 0; i< series; i++) {
                row.add("label"+r.nextLong());
            }
            
            String line = row.stream().collect(Collectors.joining(","));
            out.write(line);
            out.newLine();
            
            for (int i = 0; i< timepoints; i++) {
                row = new ArrayList<>(series+1);
                row.add(""+i*unit);
                for (int j = 0; j< series; j++) {
                    row.add(""+r.nextDouble());
                }
                
                line = row.stream().collect(Collectors.joining(","));
                out.write(line);
                out.newLine();
            }
            
        }
    }
    
}
