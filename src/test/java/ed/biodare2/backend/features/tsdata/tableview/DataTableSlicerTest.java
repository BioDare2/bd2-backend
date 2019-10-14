/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import ed.robust.dom.util.Pair;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class DataTableSlicerTest {
    
    public DataTableSlicerTest() {
    }
    
    DataTableSlicer instance;
    TableRecordsReader tableReader;
    
    @Before
    public void setUp() {
        
        tableReader = mock(TableRecordsReader.class);
        instance = new DataTableSlicer();
    }

    /**
     * Test of slice method, of class DataTableSlicer.
     */
    @Test
    public void testSliceGivesEmptySliceWithCorrectSizesIfPageExceeds() throws Exception {
        
        when(tableReader.tableSize()).thenReturn(new Pair(2, 5));
        
        Slice slice = new Slice();
        slice.rowPage.pageIndex = 1;
        slice.rowPage.pageSize = 10;
        slice.colPage.pageIndex = 0;
        slice.colPage.pageSize = 10;
        
        DataTableSlice result = instance.slice(tableReader, slice);
        assertNotNull(result);
        assertEquals(2, result.totalRows);
        assertEquals(5, result.totalColumns);
        assertTrue(result.columnsNames.isEmpty());
        assertTrue(result.columnsNumbers.isEmpty());
        assertTrue(result.rowsNames.isEmpty());
        assertTrue(result.rowsNumbers.isEmpty());
        assertTrue(result.data.isEmpty());
        
        assertEquals(slice.rowPage, result.rowPage);
        assertEquals(slice.colPage, result.colPage);
        
        when(tableReader.tableSize()).thenReturn(new Pair(20, 5));
        slice.colPage.pageIndex = 1;
        
        result = instance.slice(tableReader, slice);
        assertEquals(20, result.totalRows);
        assertEquals(5, result.totalColumns);
        assertTrue(result.columnsNames.isEmpty());
        assertTrue(result.columnsNumbers.isEmpty());
        assertTrue(result.rowsNames.isEmpty());
        assertTrue(result.rowsNumbers.isEmpty());
        assertTrue(result.data.isEmpty());
        
        assertEquals(slice.rowPage, result.rowPage);
        assertEquals(slice.colPage, result.colPage);    }
    
}
