/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import ed.robust.dom.util.Pair;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class DataTableSlicer {
    
    
    public DataTableSlice slice(TableRecordsReader tableReader, Slice slice) throws IOException {
        
        Pair<Integer, Integer> rowColSizes = tableReader.tableSize();
        
        DataTableSlice dataSlice = new DataTableSlice();
        dataSlice.totalRows = rowColSizes.getLeft();
        dataSlice.totalColumns = rowColSizes.getRight();
        
        dataSlice.colPage = slice.colPage;
        dataSlice.rowPage = slice.rowPage;
        
        int firstRow = slice.rowPage.first();
        int firstCol = slice.colPage.first();
        
        if (firstRow >= dataSlice.totalRows || firstCol >= dataSlice.totalColumns) {
            return dataSlice;
        }
        
        List<List<Object>> records = tableReader.readRecords(firstRow,slice.rowPage.pageSize);        
        records = sliceColumns(records,firstCol,slice.colPage.pageSize);
        records = padColumns(records);
        
        dataSlice.rowsNumbers = numberRows(firstRow, records.size());
        dataSlice.rowsNames = nameRows(dataSlice.rowsNumbers);
        
        dataSlice.columnsNumbers = numberColumns(firstCol, records.isEmpty() ? 0 : records.get(0).size());
        dataSlice.columnsNames = nameColumns(dataSlice.columnsNumbers);
        
        dataSlice.data = records;
        return dataSlice;
    }

    List<List<Object>> sliceColumns(List<List<Object>> records, int firstCol, int size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    List<Integer> numberRows(int firstRow, int size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    List<String> nameRows(List<Integer> rowsNumbers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    List<List<Object>> padColumns(List<List<Object>> records) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    List<Integer> numberColumns(int firstCol, int size) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    List<String> nameColumns(List<Integer> columnsNumbers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
