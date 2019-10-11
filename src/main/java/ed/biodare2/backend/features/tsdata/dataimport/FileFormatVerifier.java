/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.bd.parser.topcount.TopCountReader;
import ed.biodare2.backend.features.tsdata.TextDataTableView;
import ed.biodare2.backend.repo.isa_dom.dataimport.ImportFormat;
import ed.synthsys.util.excel.ModernExcelView;
import java.io.IOException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tzielins
 */
public class FileFormatVerifier {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    final TopCountReader topcount = new TopCountReader(false);

    public static class FormatException extends Exception {
        
        public FormatException(String msg) {
            super(msg);
        }
    }
    
    public boolean verify(Path file,ImportFormat format) throws IOException {
        
        switch(format) {
            case EXCEL_TABLE: return verifyExcel(file);
            case TOPCOUNT: return verifyTopcount(file);
            case TAB_SEP: return verifyTextTable(file,"\t");
            case COMA_SEP: return verifyTextTable(file,",");
            default: throw new IllegalArgumentException("Unsuported format: "+format);
        }
    }
    
    
    
    protected boolean verifyExcel(Path file) throws IOException {

        if (!ModernExcelView.isExcelFile(file)) {
            return false;
        }

        return true;
    }
    
    protected boolean verifyTopcount(Path file) throws IOException {
        
        return topcount.isSuitableFormat(file);
        /*
        if (!topcount.isSuitableFormat(file)) {
            return false; //throw new FormatException("Is not a valid topcount file");
        }

        return true;    
        */
    }
    
    boolean verifyTextTable(Path file, String sep) throws IOException {
        
        return TextDataTableView.isSuitableFormat(file, sep);
    }


    
    

}
