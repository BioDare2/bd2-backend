/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import ed.biodare2.backend.repo.isa_dom.dataimport.CellCoordinates;
import ed.robust.dom.util.Pair;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        
        return records.stream()
                .map( list -> {
                    if (firstCol >= list.size()) return List.of();
                    int last = firstCol+size;
                    last = Math.min(last, list.size());
                    return list.subList(firstCol, last);
                })
                .collect(Collectors.toList());
    }

    List<Integer> numberRows(int firstRow, int size) {
        return IntStream.range(firstRow, firstRow+size)
                .boxed()
                .collect(Collectors.toList());
    }

    List<String> nameRows(List<Integer> rowsNumbers) {
        return rowsNumbers.stream()
                .map( nr -> ""+(nr+1))
                .collect(Collectors.toList());
    }

    List<List<Object>> padColumns(List<List<Object>> records) {
        
        final int columns = records.stream().mapToInt(list -> list.size()).max().orElse(0);
        
        return records.stream()
                .map( list -> {
                    if (list.size() == columns) return list;
                    list = new ArrayList<>(list);
                    while(list.size() < columns) list.add("");
                    return list;
                })
                .collect(Collectors.toList());
    }

    List<Integer> numberColumns(int firstCol, int size) {
        return numberRows(firstCol, size);
    }

    List<String> nameColumns(List<Integer> columnsNumbers) {
        
        return columnsNumbers.stream()
                .map( nr -> CellCoordinates.colNrToExcelLetter(nr+1))
                .collect(Collectors.toList());
    }
}
