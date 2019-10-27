/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import ed.biodare2.backend.repo.isa_dom.dataimport.ImportFormat;
import ed.biodare2.backend.web.rest.HandlingException;
import ed.synthsys.util.excel.Excel2TextConverter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class DataTableReaderProvider implements AutoCloseable {

    final static int WORKBOOK_SIZE_THRESHOLD = 20*1024*1024; // 20Mb
    final static Excel2TextConverter excel2TextConverter = new Excel2TextConverter();
    
    Path orgFile;
    Path proxyFile;
    DataTableReader reader;
    ImportFormat format;
    
    public DataTableReaderProvider(Path file, ImportFormat format) {
        this.orgFile = file;
        this.format = format;
    }
    
    public DataTableReader reader() {
        if (reader == null) {
            reader = prepareReader();
        }
        return reader;
    }
    
    DataTableReader prepareReader() {
        switch(format) {
            case COMA_SEP: return new TextDataTableReader(orgFile, ",");
            case TAB_SEP: return new TextDataTableReader(orgFile, "\t");
            case EXCEL_TABLE: return prepareExcelReader(); 
            default: throw new HandlingException("Unsuported format: "+format);
        }        
    }
    
    DataTableReader prepareExcelReader() {
        
        try {
            if (Files.size(orgFile) < WORKBOOK_SIZE_THRESHOLD) {
                return new ExcelDataTableReader(orgFile);
            }
            
            proxyFile = Files.createTempFile(null, null);
            excel2TextConverter.convert(orgFile, proxyFile);
            return new TextDataTableReader(proxyFile, ",");
            
        } catch(IOException| InvalidFormatException e) {
            throw new HandlingException("Cannot process excel file: "+e.getMessage(),e);
        }
    }
    
    @Override
    public void close() throws IOException {
        
        if (proxyFile != null && Files.exists(proxyFile)) {
            Files.delete(proxyFile);
        }
    }
    
}
