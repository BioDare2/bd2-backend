/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare2.backend.features.tsdata.tableview.TextDataTableReader;
import ed.biodare2.backend.features.tsdata.tableview.TextDataTableReader.OpennedReader;
import ed.robust.dom.util.Pair;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.*;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class TextTableTransposerTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    public TextTableTransposerTest() {
    }
    
    TextTableTransposer instance;
    
    @Before
    public void setUp() {
        instance = new TextTableTransposer();
    }

    @Test
    public void sublistGetsCutRegardlessOfIndexesAndFillsWithNullsIfNeeded() {
        
        List<Integer> list = List.of(0,1,2,3,4);
        
        List<Integer> exp = List.of();
        List<Integer> resp = instance.subList(list, 0, 0);
        assertEquals(exp, resp);
        
        resp = instance.subList(list, 10, 10);
        assertEquals(exp, resp);        
        
        resp = instance.subList(list, 10, 12);
        exp = new ArrayList<>();
        exp.add(null);
        exp.add(null);
        assertEquals(exp, resp);        
        
        resp = instance.subList(list, 0, 1);
        exp = List.of(0);
        assertEquals(exp, resp);        
        
        resp = instance.subList(list, 1, 3);
        exp = List.of(1,2);
        assertEquals(exp, resp);        
        
        resp = instance.subList(list, 2, 6);
        exp = List.of(2,3,4);
        exp = new ArrayList<>(exp);
        exp.add(null);
        assertEquals(exp, resp);        
    }
    
    @Test
    public void readTransposedChunkReturnsTransposedColumnsAsRows() throws Exception {
        
        OpennedReader sequentialReader = mock(OpennedReader.class);
        
        List<Object> row1 = List.of("Id","L1","L2","L3");
        List<Object> row2 = List.of("1","v11","v12");
        List<Object> row3 = List.of("2","v21","v22","v23");
        
        when(sequentialReader.readRecord())
                .thenReturn(Optional.of(row1),Optional.of(row2),Optional.of(row3),Optional.empty());
        
        List<List<Object>> trans = instance.readTransposedChunk(sequentialReader, 0, 4);
        
        assertEquals(4, trans.size());
        
        List<List<Object>> exp = List.of(
                List.of("Id", "1", "2"),
                List.of("L1", "v11", "v21"),
                List.of("L2", "v12", "v22"),
                Arrays.asList("L3", null, "v23")
        );
        
        assertEquals(exp, trans);
    }
    
    @Test
    public void saveToTextSavesTablesWithEmptyCellsForNulls() throws Exception {
        
        List<List<Object>> rows = List.of(
                List.of("Id", "1", "2"),
                List.of("L1", "v11", "v21"),
                List.of("L2", "v12", "v22"),
                Arrays.asList("L3", null, "v23")
        );
        
        Path file = testFolder.newFile().toPath();
        
        
        instance.saveToTextTable(rows, file, ",");
        
        List<String> saved = Files.readAllLines(file);
        List<String> exp = List.of(
                "Id,1,2",
                "L1,v11,v21",
                "L2,v12,v22",
                "L3,,v23"
        );
        
        assertEquals(exp, saved);
    }
    
    @Test
    public void transposeTransposesFile() throws Exception {
        
        List<String> inRows = List.of(
                "Id,L1,L2,L3",
                "1,v11,v12",
                "2,v21,v22,v23"
        );
        
        Path inFile = testFolder.newFile().toPath();
        Files.write(inFile, inRows);
        
        Path outFile = testFolder.newFile().toPath();
        
        instance.transpose(inFile, ",", outFile);
        
        assertTrue(Files.exists(outFile));
        
        List<String> outRows = Files.readAllLines(outFile);
        
        List<String> exp = List.of(
                "Id,1,2",
                "L1,v11,v21",
                "L2,v12,v22",
                "L3,,v23"
        );
        
        assertEquals(exp, outRows);
        
    }
    
    
    @Test
    public void transposeInChunksTransposesFile() throws Exception {
        
        List<String> inRows = List.of(
                "Id,L1,L2,L3,L4",
                "1,v11,v12",
                "2,v21,v22,v23,v24"
        );
        
        Path inFile = testFolder.newFile().toPath();
        Files.write(inFile, inRows);
        
        Path outFile = testFolder.newFile().toPath();
        
        TextDataTableReader reader = new TextDataTableReader(inFile, ",");
        
        instance.transposeInChunks(reader, reader.rowsColsTableSize().getRight(), outFile, 2, ",");
        
        assertTrue(Files.exists(outFile));
        
        List<String> outRows = Files.readAllLines(outFile);
        
        List<String> exp = List.of(
                "Id,1,2",
                "L1,v11,v21",
                "L2,v12,v22",
                "L3,,v23",
                "L4,,v24"
        );
        
        assertEquals(exp, outRows);
        
    }
    

    
    @Test
    public void divideRangeGivesChunksOfRequestedSize() {
        
        int size = 0;
        int chunk = 3;
        
        List<Pair<Integer,Integer>> chunks = instance.divideRange(size, chunk);
        
        assertEquals(List.of(),chunks);
        
        size = 1;
        chunks = instance.divideRange(size, chunk);
        assertEquals(List.of(new Pair<>(0,1)),chunks);

        size = 2;
        chunks = instance.divideRange(size, chunk);
        assertEquals(List.of(new Pair<>(0,2)),chunks);
        
        size = 4;
        chunks = instance.divideRange(size, chunk);
        assertEquals(List.of(new Pair<>(0,3), new Pair<>(3,4)),chunks);
        
        size = 6;
        chunks = instance.divideRange(size, chunk);
        assertEquals(List.of(new Pair<>(0,3), new Pair<>(3,6)),chunks);

        size = 7;
        chunks = instance.divideRange(size, chunk);
        assertEquals(List.of(new Pair<>(0,3), new Pair<>(3,6), new Pair<>(6,7)),chunks);
        
    }
    
    @Test
    public void joinFilesAppendsFiles() throws Exception {
        Path f1 = testFolder.newFile().toPath();
        Path f2 = testFolder.newFile().toPath();
        Path f3 = testFolder.newFile().toPath();
        
        Files.writeString(f1, "A");
        Files.write(f2, List.of("B","C"));
        Files.write(f3, List.of("D","E"));
        
        Path out = testFolder.newFile().toPath();
        
        instance.joinFiles(List.of(f1,f2,f3), out);
        
        List<String> lines = Files.readAllLines(out);
        List<String> exp = List.of("AB","C","D","E");
        
        assertEquals(exp, lines);
    }
    
    // @Test
    public void transposeLongFile() throws Exception {
        
        Path inFile = Paths.get("E:\\Temp\\long_1000x1200.csv");
        Path outFile = Paths.get("E:\\Temp\\long_1000x1200.transp.csv");
        
        instance.transpose(inFile, ",", outFile);
        
        assertTrue(Files.exists(outFile));
 
        
    }
    
    @Test
    @Ignore("takes too long to generate and transpose big file")
    public void transposeLongFile2() throws Exception {
        
        Path file = testFolder.newFile().toPath();
        int series = 3000;
        int timepoints = 5*24*10;
        
        makeLongCSVColumnFile(file,series, timepoints);
        
        Path out = testFolder.newFile().toPath();
        
        long sT = System.currentTimeMillis();
        instance.transpose(file, ",", out);
        long dur = (System.currentTimeMillis()-sT)/1000;
        
        System.out.println("Transpose Took: "+dur+"s");
        
        assertTrue(Files.exists(out));
        //assertEquals(Files.size(file), Files.size(out));
        
        TextDataTableReader reader = new TextDataTableReader(file, ",");
        Pair<Integer,Integer> dim = reader.rowsColsTableSize();
        
        reader = new TextDataTableReader(out, ",");
        Pair<Integer,Integer> dim2 = reader.rowsColsTableSize();
        
        assertEquals(dim.getLeft(), dim2.getRight());
        assertEquals(dim.getRight(), dim2.getLeft());

    }    

    public void makeLongCSVColumnFile(Path file, int series, int timepoints) throws Exception {
        
        int unit = 6; // minutes
        
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