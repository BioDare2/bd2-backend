/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import ed.biodare2.backend.features.tsdata.dataimport.ExcelTableImporterTest;
import static ed.biodare2.backend.features.tsdata.tableview.TextDataTableReader.isSuitableFormat;
import ed.robust.dom.util.Pair;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
public class ExcelTableReaderTest {
    
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    Path dataFile;
    ExcelTableReader instance;
    
    public ExcelTableReaderTest() {
    }
    
    @Before
    public void setUp() throws IOException {
        
        dataFile = testFolder.newFile().toPath();
        instance = new ExcelTableReader(dataFile);
    }
    
    Path excelFileLocation(String fileName) {
        try {
            return Paths.get(ExcelTableReaderTest.class.getResource(fileName).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(),e);
        }    
    }

    /**
     * Test of isSuitableFormat method, of class ExcelTableReader.
     */
    @Test
    public void testIsSuitableFormat() throws Exception {
        Path file = testFolder.newFile().toPath();
        
        assertFalse(ExcelTableReader.isSuitableFormat(file));
        
        List<String> lines = List.of("alkafaf","ma","kota","kot ma ale");        
        Files.write(file, lines);        
        assertFalse(isSuitableFormat(file, ","));

        file = excelFileLocation("data-sheet.xlsx");
        assertTrue(ExcelTableReader.isSuitableFormat(file));
        
    }
    
    @Test
    public void calculatesCorrectSize() throws Exception {
        
        Files.copy(excelFileLocation("empty.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        instance = new ExcelTableReader(dataFile);
        
        Pair<Integer,Integer> res = instance.rowsColsTableSize();
        Pair<Integer,Integer> exp = new Pair<>(0,0);
        assertEquals(exp, res);
        
        Files.copy(excelFileLocation("one.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        instance = new ExcelTableReader(dataFile);
        
        res = instance.rowsColsTableSize();
        exp = new Pair<>(1,1);
        assertEquals(exp, res);
        
        Files.copy(excelFileLocation("small.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        instance = new ExcelTableReader(dataFile);
        
        res = instance.rowsColsTableSize();
        exp = new Pair<>(10,6);
        assertEquals(exp, res);
        
    }
    
    @Test
    public void calculatesCorrectSizeCachesSize() throws Exception {
        
        Files.copy(excelFileLocation("small.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        instance = new ExcelTableReader(dataFile);
        
        Pair<Integer,Integer> res = instance.rowsColsTableSize();
        Pair<Integer,Integer> exp = new Pair<>(10,6);
        assertEquals(exp, res);
        
        exp = res;
        res = instance.rowsColsTableSize();
        assertSame(exp, res);
        
    }    
    
    @Test
    public void readRecordsCorrectlyReadsPartOfFile() throws Exception {
        
        Files.copy(excelFileLocation("small.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        instance = new ExcelTableReader(dataFile);
        
        List<List<Object>> records = instance.readRecords(0, 0);
        List<List<Object>> exp = List.of();
        
        assertEquals(exp, records);
        
        records = instance.readRecords(0, 1);
        exp = List.of(
                List.of( "id",	1.0,	3.0,	5.0,	7.0,	9.0)
        );
        assertEquals(exp, records);
        
        records = instance.readRecords(0, 2);
        exp = List.of(
                List.of( "id",	1.0,	3.0,	5.0,	7.0,	9.0),
                List.of( "WT LHY", 1.643133821,	1.421377053,	1.189679697,	0.978052361,	0.819862942)

        );
        assertEquals(exp, records);
        
        records = instance.readRecords(9, 5);
        exp = List.of(
                List.of( "prr79 LHY",	1.185081716,	0.995300484,	0.800789483,	0.639454807,	0.534876435)
        );
        assertEquals(exp, records);
    }       

     
}
