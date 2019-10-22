/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import ed.robust.dom.util.Pair;
import ed.synthsys.util.excel.ExcelFormatException;
import ed.synthsys.util.excel.ModernExcelView;
import ed.synthsys.util.excel.ModernExcelView.CellCaster;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class ExcelTableReader extends TableRecordsReader {
    
    final Path file;
    Pair<Integer, Integer> rowsColsSize;    
    
    public ExcelTableReader(Path file) {
        this.file = file;
    }
    
    public static boolean isSuitableFormat(Path file) throws IOException {
        
        return ModernExcelView.isExcelFile(file);
    }    
  

    @Override
    public Pair<Integer, Integer> rowsColsTableSize() throws IOException {
        
        if (rowsColsSize == null) {
            
            try (ModernExcelView excel = new ModernExcelView(file)) {
                
                
                int cols = 0;
                int rows = excel.getLastRow();
                
                // cannot distinguish between empty and with one row which is also 0
                if (rows == 0) {
                    try {
                        cols = excel.getLastColumn(0);
                    } catch (IllegalArgumentException e) {
                        return new Pair<>(0,0);
                    }
                }
                rows++;
                int upTo = Math.min(20, rows);
                for (int row = 0;row< upTo; row++) {
                    cols = Math.max(cols, excel.getLastColumn(row)+1);
                }
                rowsColsSize = new Pair<>(rows,cols);
            } catch (ExcelFormatException e) {
                throw new IOException(e.getMessage(),e);
            }
        };
        return rowsColsSize;
    }


    @Override
    public List<List<Object>> readRecords(int firstRow, int size) throws IOException {
        
        try (ModernExcelView excel = new ModernExcelView(file)) {
        
            int last = firstRow+size;
            last = Math.min(excel.getLastRow()+1,last);
            
            List<List<Object>> rows = new ArrayList<>();
            for (int i = firstRow; i < last; i++) {
                List<Object> row = excel.readRow(i,0, naturalCaster);
                rows.add(row);
            }
            
            return rows;
            
        } catch (ExcelFormatException e) {
                throw new IOException(e.getMessage(),e);
        }
    }
    
    static final NaturalCellCaster naturalCaster = new NaturalCellCaster();
    
    protected static class NaturalCellCaster implements CellCaster<Object> {

        @Override
        public Object cast(Cell cell, FormulaEvaluator formEval) {
            if (cell == null) return null;            
            switch(cell.getCellType()) {
                case Cell.CELL_TYPE_STRING: return cell.getRichStringCellValue().getString().trim();
                case Cell.CELL_TYPE_NUMERIC: return cell.getNumericCellValue();
                case Cell.CELL_TYPE_BOOLEAN: return cell.getBooleanCellValue();
                case Cell.CELL_TYPE_FORMULA: {
                    try {
                        CellValue val = formEval.evaluate(cell);
                        if (val.getCellType() == Cell.CELL_TYPE_NUMERIC) return val.getNumberValue();
                        if (val.getCellType() == Cell.CELL_TYPE_STRING) return val.getStringValue().trim();
                        if (val.getCellType() == Cell.CELL_TYPE_BOOLEAN) return val.getBooleanValue();
                    } catch (FormulaParseException e) {
                        return null;
                    }
                    return null;
                }
                default: return null;
            }            
        }        
    }
    
    
}
