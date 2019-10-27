/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import ed.biodare2.backend.repo.isa_dom.dataimport.ImportFormat;
import ed.robust.dom.util.Pair;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class DataTableReaderProviderTest {
    
    public DataTableReaderProviderTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testProvidesTextReadersForTSVFormats() throws IOException {
        
        Path file = getTestFile("small.tsv");
        ImportFormat format = ImportFormat.TAB_SEP;
        
        try (DataTableReaderProvider provider = new DataTableReaderProvider(file, format)) {
            DataTableReader reader = provider.reader();
        
            assertTrue(reader instanceof TextDataTableReader);
            assertEquals(new Pair<>(10,6),reader.rowsColsTableSize());
            
            DataTableReader reader2 = provider.reader();
            assertSame(reader, reader2);
        }
        assertTrue(Files.exists(file));
        
    }
    
    @Test
    public void testProvidesTextReadersForCSVFormats() throws IOException {
        
        Path file = getTestFile("small.csv");
        ImportFormat format = ImportFormat.COMA_SEP;
        
        try (DataTableReaderProvider provider = new DataTableReaderProvider(file, format)) {
            DataTableReader reader = provider.reader();
        
            assertTrue(reader instanceof TextDataTableReader);
            assertEquals(new Pair<>(10,6),reader.rowsColsTableSize());
            
            DataTableReader reader2 = provider.reader();
            assertSame(reader, reader2);
        }
        assertTrue(Files.exists(file));
        
    }
    
    @Test
    public void testProvidesExcelReadersForSmallExcells() throws IOException {
        
        Path file = getTestFile("small.xlsx");
        ImportFormat format = ImportFormat.EXCEL_TABLE;
        
        try (DataTableReaderProvider provider = new DataTableReaderProvider(file, format)) {
            DataTableReader reader = provider.reader();
        
            assertTrue(reader instanceof ExcelDataTableReader);
            assertEquals(new Pair<>(10,6),reader.rowsColsTableSize());
            
            DataTableReader reader2 = provider.reader();
            assertSame(reader, reader2);
        }
        assertTrue(Files.exists(file));
        
    }
    
    @Test
    @Ignore("Test file is not committed")
    public void testProvidesExcelReadersForLargeExcells() throws IOException {
        
        Path file = Paths.get("E:\\Temp\\long_5000x1200.xlsx");
        ImportFormat format = ImportFormat.EXCEL_TABLE;
        Path tmp = null;
        try (DataTableReaderProvider provider = new DataTableReaderProvider(file, format)) {
            DataTableReader reader = provider.reader();
        
            tmp = provider.proxyFile;
            assertTrue(reader instanceof TextDataTableReader);
            assertEquals(new Pair<>(1201,5001),reader.rowsColsTableSize());
            
            DataTableReader reader2 = provider.reader();
            assertSame(reader, reader2);
            
            assertTrue(Files.isRegularFile(tmp));
        }
        assertTrue(Files.exists(file));
        assertNotNull(tmp);
        assertFalse(Files.exists(tmp));
        
    }    
    
    
    
    Path getTestFile(String name) {
        try {
            return Paths.get(this.getClass().getResource(name).toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
    
}
