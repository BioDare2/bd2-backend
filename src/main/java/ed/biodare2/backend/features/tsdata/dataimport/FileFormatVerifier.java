/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare.data.topcount.TopCountReader;
import ed.biodare2.backend.features.tsdata.tableview.ExcelDataTableReader;
import ed.biodare2.backend.features.tsdata.tableview.TextDataTableReader;
import ed.biodare2.backend.repo.isa_dom.dataimport.ImportFormat;
import ed.biodare2.backend.web.rest.FormatException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tzielins
 */
public class FileFormatVerifier {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    final TopCountReader topcount = new TopCountReader(false);

    public boolean verify(Path file,ImportFormat format) throws IOException, FormatException {
        
        Optional<String> error = checkFormatError(file, format);
        if (error.isPresent()) {
            throw new FormatException("Is not a valid "+format+" file: "+error.get());
        }
        return true;
    }
    
    protected Optional<String> checkFormatError(Path file,ImportFormat format) throws IOException {
        switch(format) {
            case EXCEL_TABLE: return verifyExcel(file);
            case TOPCOUNT: return verifyTopcount(file);
            case TAB_SEP: return verifyTextTable(file,"\t");
            case COMA_SEP: return verifyTextTable(file,",");
            default: throw new IllegalArgumentException("Unsuported format: "+format);
        }        
    }
    
    protected Optional<String> verifyExcel(Path file) throws IOException {
        
        return ExcelDataTableReader.checkFormatError(file);
    }
    
    protected Optional<String> verifyTopcount(Path file) throws IOException {
        
        return topcount.isSuitableFormat(file) ? Optional.empty() : Optional.of("wrong content");

    }
    
    protected Optional<String> verifyTextTable(Path file, String sep) throws IOException {
        
        return TextDataTableReader.isSuitableFormat(file, sep) ? Optional.empty() : Optional.of("wrong content");
    }


    
    

}
