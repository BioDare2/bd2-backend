/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare2.backend.features.tsdata.tableview.TextDataTableReader;
import ed.biodare2.backend.features.tsdata.tableview.TextDataTableReader.OpennedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
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
    
    // @Test
    public void transposeLongFile() throws Exception {
        
        Path inFile = Paths.get("E:\\Temp\\long_1000x1200.csv");
        Path outFile = Paths.get("E:\\Temp\\long_1000x1200.transp.csv");
        
        instance.transpose(inFile, ",", outFile);
        
        assertTrue(Files.exists(outFile));
        
    }
    
}
