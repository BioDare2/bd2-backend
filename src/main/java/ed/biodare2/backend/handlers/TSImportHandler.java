/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;


import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.features.tsdata.dataimport.ExcelTableImporter;
import ed.biodare2.backend.features.tsdata.dataimport.ImportException;
import ed.biodare2.backend.features.tsdata.dataimport.DataTableImporter;
import ed.biodare2.backend.features.tsdata.dataimport.TopCountImporter;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTableImportParameters;
import ed.biodare2.backend.repo.isa_dom.dataimport.ExcelTSImportParameters;
import ed.biodare2.backend.repo.isa_dom.dataimport.FileImportRequest;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class TSImportHandler {

    final FileUploadHandler uploads;
    final ExcelTableImporter excelTableImporter = new ExcelTableImporter();
    final TopCountImporter topcountImporter = new TopCountImporter();
    final DataTableImporter textTableImporter = new DataTableImporter();
    
    @Autowired
    public TSImportHandler(FileUploadHandler uploads) {
        this.uploads = uploads;
    }
    
    public DataBundle importTimeSeries(FileImportRequest importRequest,BioDare2User user) throws ImportException {
        
        Path file = uploads.get(importRequest.fileId, user);
        
        switch(importRequest.importFormat) {
            case EXCEL_TABLE: return excelTableImporter.importTimeSeries(file, (ExcelTSImportParameters)importRequest.importParameters);
            case TOPCOUNT: return topcountImporter.importTimeSeries(file, (ExcelTSImportParameters)importRequest.importParameters);
            case COMA_SEP: return textTableImporter.importTimeSeries(file, (DataTableImportParameters) importRequest.importParameters);
            case TAB_SEP: return textTableImporter.importTimeSeries(file, (DataTableImportParameters) importRequest.importParameters);
            default: throw new IllegalArgumentException("Unknown format: "+importRequest.importFormat);
        }
    }
    
}
