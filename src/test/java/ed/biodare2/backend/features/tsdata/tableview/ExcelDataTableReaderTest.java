/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import ed.biodare.data.excel.ModernExcelView;
import static ed.biodare2.backend.features.tsdata.tableview.ExcelDataTableReader.getRowsNumber;
import static ed.biodare2.backend.features.tsdata.tableview.TextDataTableReader.isSuitableFormat;
import ed.robust.dom.util.Pair;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class ExcelDataTableReaderTest {
    
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    Path dataFile;
    ExcelDataTableReader instance;
    
    public ExcelDataTableReaderTest() {
    }
    
    @Before
    public void setUp() throws IOException {
        
        dataFile = testFolder.newFile().toPath();
        instance = new ExcelDataTableReader(dataFile);
    }
    
    Path excelFileLocation(String fileName) {
        try {
            return Paths.get(ExcelDataTableReaderTest.class.getResource(fileName).toURI());
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
        
        assertFalse(ExcelDataTableReader.isSuitableFormat(file));
        
        List<String> lines = List.of("alkafaf","ma","kota","kot ma ale");        
        Files.write(file, lines);        
        assertFalse(isSuitableFormat(file, ","));

        file = excelFileLocation("data-sheet.xlsx");
        assertTrue(ExcelDataTableReader.isSuitableFormat(file));
        
    }
    
    @Test
    public void checFormatErrosReturnsError() throws Exception {
        Path file = testFolder.newFile().toPath();
        
        assertTrue(ExcelDataTableReader.checkFormatError(file).isPresent());
        
        List<String> lines = List.of("alkafaf","ma","kota","kot ma ale");        
        Files.write(file, lines);        
        assertTrue(ExcelDataTableReader.checkFormatError(file).isPresent());

        file = excelFileLocation("empty-1st-sheet.xlsx");
        assertTrue(ExcelDataTableReader.checkFormatError(file).isPresent());
        //System.out.println(ExcelDataTableReader.checkFormatError(file).get());
        
                
        file = excelFileLocation("data-sheet.xlsx");
        assertFalse(ExcelDataTableReader.checkFormatError(file).isPresent());
                
    }    
    
    @Test
    public void calculatesCorrectSize() throws Exception {
        
        Files.copy(excelFileLocation("empty.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        instance = new ExcelDataTableReader(dataFile);
        
        Pair<Integer,Integer> res = instance.rowsColsTableSize();
        Pair<Integer,Integer> exp = new Pair<>(0,0);
        assertEquals(exp, res);
        
        Files.copy(excelFileLocation("one.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        instance = new ExcelDataTableReader(dataFile);
        
        res = instance.rowsColsTableSize();
        exp = new Pair<>(1,1);
        assertEquals(exp, res);
        
        Files.copy(excelFileLocation("small.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        instance = new ExcelDataTableReader(dataFile);
        
        res = instance.rowsColsTableSize();
        exp = new Pair<>(10,6);
        assertEquals(exp, res);
        
    }
    
    @Test
    @Ignore("Test files not commited")
    public void getsSizesOfLargeFiles() throws Exception {
        
        // System.out.println("Get large");
        dataFile = Paths.get("E:\\Temp\\long_10000x1200.xlsx"); 
        instance = new ExcelDataTableReader(dataFile);
        
        Pair<Integer,Integer> res = instance.rowsColsTableSize();
        Pair<Integer,Integer> exp = new Pair<>(1202,10001);
        assertEquals(exp, res);
        
        dataFile = Paths.get("E:\\Temp\\long_255x10000.xls");
        instance = new ExcelDataTableReader(dataFile);
        
        res = instance.rowsColsTableSize();
        exp = new Pair<>(10001,256);
        assertEquals(exp, res);
        
        
    }    
    
    @Test
    public void getRowsCanHandleEmptyFiles() throws Exception {
        
        Files.copy(excelFileLocation("empty.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        try (ModernExcelView excel = new ModernExcelView(dataFile)) {
            assertEquals(0, getRowsNumber(excel));
        }
        
        Files.copy(excelFileLocation("one.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        try (ModernExcelView excel = new ModernExcelView(dataFile)) {
            assertEquals(1, getRowsNumber(excel));
        }
        
        Files.copy(excelFileLocation("small.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        try (ModernExcelView excel = new ModernExcelView(dataFile)) {
            assertEquals(10, getRowsNumber(excel));
        }
        
    }    
    
    @Test
    public void calculatesCorrectSizeCachesSize() throws Exception {
        
        Files.copy(excelFileLocation("small.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        instance = new ExcelDataTableReader(dataFile);
        
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
        instance = new ExcelDataTableReader(dataFile);
        
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

    @Test
    public void sequentialReaderGivesNumberOfSkippedLines() throws Exception {
        
        Files.copy(excelFileLocation("empty.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        try (ExcelDataTableReader.OpennedReader reader = new ExcelDataTableReader.OpennedReader(dataFile)) {
            
            assertEquals(0, reader.skipLines(0));
            assertEquals(0, reader.skipLines(1));
        }
        
        Files.copy(excelFileLocation("one.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        try (ExcelDataTableReader.OpennedReader reader = new ExcelDataTableReader.OpennedReader(dataFile)) {
            
            assertEquals(0, reader.skipLines(0));
            assertEquals(1, reader.skipLines(1));
            assertEquals(0, reader.skipLines(0));
        }
        
        Files.copy(excelFileLocation("small.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        try (ExcelDataTableReader.OpennedReader reader = new ExcelDataTableReader.OpennedReader(dataFile)) {
            
            assertEquals(0, reader.skipLines(0));
            assertEquals(2, reader.skipLines(2));
            assertEquals(3, reader.skipLines(3));
            assertEquals(5, reader.skipLines(10));
            assertEquals(0, reader.skipLines(3));
        }
    }
    
    @Test
    public void sequentialReaderReadsRecordsInSequence() throws Exception {
        
        Files.copy(excelFileLocation("empty.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        try (ExcelDataTableReader.OpennedReader reader = new ExcelDataTableReader.OpennedReader(dataFile)) {
            
            Optional<List<Object>> row = reader.readRecord();
            assertTrue(row.isEmpty());
            
            row = reader.readRecord();
            assertTrue(row.isEmpty());            
        }
        
        Files.copy(excelFileLocation("one.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        try (ExcelDataTableReader.OpennedReader reader = new ExcelDataTableReader.OpennedReader(dataFile)) {
            
            Optional<List<Object>> row = reader.readRecord();
            assertFalse(row.isEmpty());
            assertEquals(List.of(1.0), row.get());
            
            row = reader.readRecord();
            assertTrue(row.isEmpty());            
            
            row = reader.readRecord();
            assertTrue(row.isEmpty());             
        }
        
        Files.copy(excelFileLocation("small.xlsx"), dataFile, StandardCopyOption.REPLACE_EXISTING);
        try (ExcelDataTableReader.OpennedReader reader = new ExcelDataTableReader.OpennedReader(dataFile)) {
            
            assertEquals(7, reader.skipLines(7));
            Optional<List<Object>> row = reader.readRecord();
            assertFalse(row.isEmpty());
            assertEquals(List.of("WT LHY",1.155862767,	0.931743855,	0.710180436,	0.524829526,	0.389353438), row.get());
            
            row = reader.readRecord();
            assertFalse(row.isEmpty());
            assertEquals("WT LHY",row.get().get(0));
            
            row = reader.readRecord();
            assertFalse(row.isEmpty());
            assertEquals("prr79 LHY",row.get().get(0));
            
            row = reader.readRecord();
            assertTrue(row.isEmpty());        
        }
    }
    

     
}
