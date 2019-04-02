/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare2.backend.web.rest.HandlingException;
import ed.synthsys.util.excel.ExcelFormatException;
import ed.synthsys.util.excel.ModernExcelView;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tzielins
 */
public class ExcelTableSimplifier extends TableSimplifier {
    
    @Override
    protected List<List<String>> readTable(Path file,int rows) throws IOException, HandlingException {
        
        try (ModernExcelView excel = new ModernExcelView(file)) {
            
            List<List<String>> table = new ArrayList<>(rows);
            
            rows = Math.min(rows,1+excel.getLastRow());
            
            for (int rowNr = 0;rowNr<rows;rowNr++) {
                
                table.add(excel.readStringRow(rowNr, 0));
            }
            return table;
        } catch (ExcelFormatException e) {
            throw new HandlingException("Cannot read excel: "+e.getMessage(),e);
        }
    }

}
