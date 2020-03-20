/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare.data.excel.Excel2TextConverter;
import ed.biodare2.backend.features.tsdata.tableview.DataTableReader;
import ed.biodare2.backend.features.tsdata.tableview.ExcelDataTableReader;
import ed.biodare2.backend.features.tsdata.tableview.TextDataTableReader;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTableImportParameters;
import ed.biodare2.backend.repo.isa_dom.dataimport.ImportFormat;
import ed.biodare2.backend.web.rest.ServerSideException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class ExcelDataTableImporter extends DataTableImporter {
    
    final static int WORKBOOK_SIZE_THRESHOLD = 20*1024*1024; // 20Mb
    final Excel2TextConverter csvConverter = new Excel2TextConverter();
    
    @Override
    public DataBundle importTimeSeries(Path file, DataTableImportParameters parameters) throws ImportException {
        
        if (!ImportFormat.EXCEL_TABLE.equals(parameters.importFormat)) {
            throw new IllegalArgumentException("Should only be called with Excel files");
        }
        
        try {
            if (Files.size(file) < WORKBOOK_SIZE_THRESHOLD) {
                DataTableReader reader = new ExcelDataTableReader(file);
                return super.importTimeSeries(reader, parameters);
            }
        } catch (IOException e) {
            throw new ServerSideException("Cannot read file: "+e.getMessage(),e);
        }
        
        // above limit, convert to CSV first
        Path csvFile = null;
        try {
            csvFile = Files.createTempFile(null, null);
            csvConverter.convert(file, csvFile);
            DataTableReader reader = new TextDataTableReader(csvFile, ",");
            return super.importTimeSeries(reader, parameters);            
        } catch (IOException| InvalidFormatException e) {
            throw new ImportException("Could not create intermediate csv file "+e.getMessage(),e);
        } finally {
            if (csvFile != null && Files.exists(csvFile)) {
                try {
                    Files.delete(csvFile);
                } catch (IOException e) {
                    throw new ImportException("Could not delete intermediate csv file "+e.getMessage(),e);
                }
            }
        }
    }
}
