/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.handlers.FileUploadHandler;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.features.tsdata.dataimport.ExcelTableSimplifier;
import ed.biodare2.backend.features.tsdata.dataimport.FileFormatVerifier;
import ed.biodare2.backend.features.tsdata.dataimport.FileFormatVerifier.FormatException;
import ed.biodare2.backend.features.tsdata.dataimport.TableSimplifier;
import ed.biodare2.backend.features.tsdata.dataimport.TopCountTableSimplifier;
import ed.biodare2.backend.repo.isa_dom.dataimport.ImportFormat;
import ed.biodare2.backend.web.tracking.FileTracker;
import ed.synthsys.util.excel.ExcelFormatException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tzielins
 */
@RestController
@RequestMapping("api/file")
public class FileViewController extends BioDare2Rest {
   
    final static int DEF_ROWS_NR = 25;
    final Logger log = LoggerFactory.getLogger(this.getClass());    
    final FileUploadHandler handler;
    final ExcelTableSimplifier excelSimplifier;
    final TopCountTableSimplifier topcountSimplifier;
    
    final FileFormatVerifier formatVerifier;
    final FileTracker tracker;
    
    @Autowired
    public FileViewController(FileUploadHandler handler,FileTracker tracker) {
        this.handler = handler;
        this.excelSimplifier = new ExcelTableSimplifier();
        this.topcountSimplifier = new TopCountTableSimplifier();
        this.formatVerifier = new FileFormatVerifier();
        this.tracker = tracker;
    }

    @RequestMapping(value = "{fileId}/view/simpletable",method = RequestMethod.GET)
    public ListWrapper<List<String>> getSimpleTableView(@PathVariable String fileId,@NotNull @AuthenticationPrincipal BioDare2User user) {
        log.debug("view file as table; file:{}; {}",fileId,user);
        
        Path file = handler.get(fileId, user);
        if (!Files.isRegularFile(file)) {
            log.error("The uploaded file seems not to exists: "+file.toString());
            throw new ServerSideException("Cannot access the data file on the server");
        }
        
        try {
            TableSimplifier simplifier = getSimplifier(getFormat(file));
            
            List<List<String>> table = simplifier.simplify(file, DEF_ROWS_NR);
            ListWrapper<List<String>> resp = new ListWrapper<>(table);
            tracker.fileFormatedView(fileId,"TABLE", user);
            return resp;
        } catch(WebMappedException e) {
            log.error("Cannot read data table {} {}",fileId,e.getMessage(),e);
            throw e;
        } catch (Exception e) {
            log.error("Cannot read data table: {} {}",fileId,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }
    
   
    
    protected ImportFormat getFormat(Path file) throws IOException {
        
        if (formatVerifier.verify(file, ImportFormat.EXCEL_TABLE))
            return ImportFormat.EXCEL_TABLE;
        
        if (formatVerifier.verify(file, ImportFormat.TOPCOUNT))
            return ImportFormat.TOPCOUNT;
        throw new HandlingException("Cannot guess file format");
        
    }
    
    protected TableSimplifier getSimplifier(ImportFormat format) {
        switch(format) {
            case EXCEL_TABLE: return excelSimplifier;
            case TOPCOUNT: return topcountSimplifier;
            default: throw new HandlingException("Unsuported format: "+format);
        }
    }
    
    @RequestMapping(value = "{fileId}/verify/format/{format}",method = RequestMethod.GET)
    public boolean verifyFormat(@PathVariable String fileId, @PathVariable @NotNull ImportFormat format,
            @NotNull @AuthenticationPrincipal BioDare2User user) {
        
        log.debug("verify format: {} file:{}; {}",format,fileId,user);
        
        
        
        Path file = handler.get(fileId, user);
        if (!Files.isRegularFile(file)) {
            log.error("The uploaded file seems not to exists: "+file.toString());
            throw new ServerSideException("Cannot access the data file on the server");
        }
        
        try {
            boolean resp = formatVerifier.verify(file, format);
            tracker.fileFormatCheck(fileId,format,user);
            if (!resp)
                throw new HandlingException("Is not a valid "+format+" file");
            return resp;
        } catch (WebMappedException e) {
            log.error("Cannot verify format: {} {} {}",fileId,format,e.getMessage(),e);
            throw e;
       } catch (Exception e) {
            log.error("Cannot verify format: {} {} {}",fileId,format,e.getMessage(),e);
            throw new ServerSideException(e.getMessage());
        } 
        
    }    
}
