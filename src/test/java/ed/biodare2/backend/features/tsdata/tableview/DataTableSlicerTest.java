/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import ed.robust.dom.util.Pair;
import java.util.List;
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
    DataTableReader tableReader;
    
    @Before
    public void setUp() {
        
        tableReader = mock(DataTableReader.class);
        instance = new DataTableSlicer();
    }

    @Test
    public void testSliceGivesEmptySliceWithCorrectSizesIfPageExceeds() throws Exception {
        
        when(tableReader.rowsColsTableSize()).thenReturn(new Pair(2, 5));
        
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
        
        when(tableReader.rowsColsTableSize()).thenReturn(new Pair(20, 5));
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
        assertEquals(slice.colPage, result.colPage);    
    
    }
    
    @Test
    public void testSliceGivesCorrectlyFullTableOnBigPages() throws Exception {
        
        when(tableReader.rowsColsTableSize()).thenReturn(new Pair(5, 7));
        
        List<List<Object>> fullData = List.of(
                List.of("123","2","3"),
                List.of(""),
                List.of("2345dfdf fd fdsfdfa"),
                List.of("1","2","3","4","5","6","7"),
                List.of("A","B","C","4","5")                
        );

        when(tableReader.readRecords(0, 25)).thenReturn(fullData);
        
        Slice slice = new Slice();
        slice.rowPage.pageIndex = 0;
        slice.rowPage.pageSize = 25;
        slice.colPage.pageIndex = 0;
        slice.colPage.pageSize = 25;
        
        List<List<Object>> exp = List.of(
                List.of("123","2","3","","","",""),
                List.of("","","","","","",""),
                List.of("2345dfdf fd fdsfdfa","","","","","",""),
                List.of("1","2","3","4","5","6","7"),
                List.of("A","B","C","4","5","","")                
        );        
        
        DataTableSlice result = instance.slice(tableReader, slice);
        
        assertNotNull(result);
        
        assertEquals(5, result.totalRows);
        assertEquals(7, result.totalColumns);
        assertEquals(slice.rowPage, result.rowPage);
        assertEquals(slice.colPage, result.colPage);         
        assertEquals(exp, result.data);
        
        assertEquals(List.of(0,1,2,3,4), result.rowsNumbers);
        assertEquals(List.of(0,1,2,3,4,5,6), result.columnsNumbers);
        assertEquals(List.of("1","2","3","4","5"), result.rowsNames);
        assertEquals(List.of("A","B","C","D","E","F","G"), result.columnsNames);
    }
    
    @Test
    public void testSliceGivesCorrectlyPartOfTable() throws Exception {
        
        when(tableReader.rowsColsTableSize()).thenReturn(new Pair(5, 7));
        
        List<List<Object>> fullData = List.of(
                List.of("n123","2","3"),
                List.of("n123","2","3"),
                List.of("123","2","3"),
                List.of(""),
                List.of("2345dfdf fd fdsfdfa"),
                List.of("1","2","3","4","5","6","7"),
                List.of("A","B","C","4","5")                
        );
                
        when(tableReader.readRecords(3, 3)).thenReturn(fullData.subList(3, 6));
        
        Slice slice = new Slice();
        slice.rowPage.pageIndex = 1;
        slice.rowPage.pageSize = 3;
        slice.colPage.pageIndex = 1;
        slice.colPage.pageSize = 2;
        
        List<List<Object>> exp = List.of(
                List.of("",""),
                List.of("",""),
                List.of("3","4")
        );        
        
        DataTableSlice result = instance.slice(tableReader, slice);
        
        assertNotNull(result);
        
        assertEquals(5, result.totalRows);
        assertEquals(7, result.totalColumns);
        assertEquals(slice.rowPage, result.rowPage);
        assertEquals(slice.colPage, result.colPage);         
        assertEquals(exp, result.data);
        
    }
    
    @Test
    public void testSliceColumnsGivesSubset() {
        
        List<List<Object>> records = List.of(
                List.of("123","2","3"),
                List.of(""),
                List.of("2345dfdf fd"),
                List.of("1","2","3","4","5","6","7"),
                List.of("A","B","C\t","4","5")                
        );        
        
        List<List<Object>> exp = List.of(
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        );
        
        List<List<Object>> resp = instance.sliceColumns(records, 0, 0);
        assertEquals(exp, resp);
        
        resp = instance.sliceColumns(records, 10, 1);
        assertEquals(exp, resp);
        
        
        resp = instance.sliceColumns(records, 0, 1);        
        exp = List.of(
                List.of("123"),
                List.of(""),
                List.of("2345dfdf fd"),
                List.of("1"),
                List.of("A")                
        );        
        assertEquals(exp, resp);
        
        resp = instance.sliceColumns(records, 0, 10);        
        exp = List.of(
                List.of("123","2","3"),
                List.of(""),
                List.of("2345dfdf fd"),
                List.of("1","2","3","4","5","6","7"),
                List.of("A","B","C\t","4","5")              
        );        
        assertEquals(exp, resp);        
        
        resp = instance.sliceColumns(records, 2, 2);        
        exp = List.of(
                List.of("3"),
                List.of(),
                List.of(),
                List.of("3","4"),
                List.of("C\t","4")              
        );        
        assertEquals(exp, resp);         
    }
    
    @Test
    public void padColumnsAppendsEmptyStringsToShort() {
        
        List<List<Object>> records = List.of(
                List.of("123","2","3"),
                List.of(""),
                List.of("2345dfdf fd"),
                List.of("1","2","3","4","5","6"),
                List.of("A","B","C\t","4","5")                
        );        
        
        List<List<Object>> exp = List.of(
                List.of("123","2","3","","",""),
                List.of("","","","","",""),
                List.of("2345dfdf fd","","","","",""),
                List.of("1","2","3","4","5","6"),
                List.of("A","B","C\t","4","5","") 
        );
        
        List<List<Object>> resp = instance.padColumns(records);
        assertEquals(exp, resp);
        
    }    
    
    @Test
    public void numberRowsGIves0BasedRows() {
        
        List<Integer> rows = instance.numberRows(0, 0);
        List<Integer> exp = List.of();
        assertEquals(exp, rows);
        
        rows = instance.numberRows(0, 1);
        exp = List.of(0);
        assertEquals(exp, rows);

        rows = instance.numberRows(0, 3);
        exp = List.of(0,1,2);
        assertEquals(exp, rows);
        
        rows = instance.numberRows(2, 3);
        exp = List.of(2,3,4);
        assertEquals(exp, rows);
    }
    
    @Test
    public void nameRowsGives1BasedStrings() {
        
        List<Integer> numbers = List.of(0,2,3);
        
        List<String> names = instance.nameRows(numbers);
        List<String> exp = List.of("1","3","4");
        
        assertEquals(exp, names);
        
    }
    
    @Test
    public void nameColumnsGivesExcelLettersFor0BasedColumns() {
        
        List<Integer> numbers = List.of(0,27,782);
        
        List<String> names = instance.nameColumns(numbers);
        List<String> exp = List.of("A","AB","ADC");
        
        assertEquals(exp, names);
        
    }    
    
}
