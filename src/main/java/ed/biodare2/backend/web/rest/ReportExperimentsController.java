/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.EnvironmentVariables;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.dao.ExperimentalAssayRep;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.util.TableBuilder;
import ed.robust.dom.data.DetrendingType;
import jakarta.validation.constraints.NotNull;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author tzielins
 */
@RestController
@RequestMapping("api/report")
public class ReportExperimentsController extends BioDare2Rest {
 
    final ExperimentalAssayRep expRep;
    
    final ExperimentPackHub expPacks;
    
    final TSDataHandler dataHandler;
    
    final Path reportDir;
    
    protected final String SEP = ",";
    protected final String ESC = "\"";        

    
    @Autowired
    public ReportExperimentsController(ExperimentalAssayRep expRep, ExperimentPackHub expPacks, TSDataHandler dataHandler, EnvironmentVariables environment) {
        this.expRep = expRep;
        this.expPacks = expPacks;
        this.dataHandler = dataHandler;
        this.reportDir = environment.storageDir.resolve("REPORTS");
    }

    @RequestMapping(value="experiment",method = RequestMethod.GET , produces = "text/csv")
    public ResponseEntity experiments(@NotNull @AuthenticationPrincipal BioDare2User currentUser) {
        log.debug("reportexperiments: {}", currentUser);

        if (!currentUser.getLogin().equals("demo") && !currentUser.getLogin().equals("test"))
            throw new InsufficientRightsException("Only demo and test users can call it");

        try {
            if (!Files.exists(reportDir)) Files.createDirectories(reportDir);
            String fileName = "records_"+LocalDate.now().toString()+".csv";
            Path file = reportDir.resolve(fileName);
            
            saveEntries(file, getDataEntries(expRep.getExerimentsIds()));
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + fileName + ".csv")
                .contentLength(Files.size(file))
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new FileSystemResource(file));            
            
        } catch (IOException e) {
            throw new ServerSideException("Could not save report on the server: "+e.getMessage(),e);
        }
        
        
    }
    
    void saveEntries(Path path, Stream<DataEntry> records) throws IOException {
        try (BufferedWriter out = Files.newBufferedWriter(path)) {
            saveEntries(out, records);
        }
    }
    
    void saveEntries(Writer out, Stream<DataEntry> records) {
    
        
        try {
            saveHeader(out);
                      
        } catch (IOException e) {
            throw new ServerSideException("Cannot save report of experiments, "+e.getMessage(),e);
        }            
        records.forEach( record -> {
            try {
                saveEntry(out, record);              
            } catch (IOException e) {
                throw new ServerSideException("Cannot save report of experiments, "+e.getMessage(),e);
            }
         });

                    
    }
    
    
    void saveHeader(Writer out) throws IOException {
            TableBuilder tb = new TableBuilder(SEP, ESC);
            tb.printLabel("id");
            tb.printLabel("version");
            tb.printLabel("created");
            tb.printLabel("modified");
            tb.printLabel("owner");
            tb.printLabel("domain");
            tb.printLabel("isPublic");
            tb.printLabel("series");
            tb.printLabel("avgSeriesDuration");
            tb.endln();
            
            out.write(tb.toString());        
    }
    
    void saveEntry(Writer out, DataEntry entry) throws IOException {
            TableBuilder tb = new TableBuilder(SEP, ESC);
            tb.printVal(entry.id);
            tb.printVal(entry.versionId);
            tb.printVal(entry.creationDate.toString());
            tb.printVal(entry.modificationDate.toString());
            tb.printVal(entry.owner);
            tb.printVal(entry.domain);
            tb.printVal(""+entry.isPublic);
            tb.printVal(entry.series);
            tb.printVal(entry.avgSeriesDuration);
            tb.endln();
            out.write(tb.toString());        
    }    
    
    Stream<DataEntry> getDataEntries(Stream<Long> ids) {
        
        return ids.parallel().map(id -> getDataEntry(id))
                    .filter(Optional::isPresent)
                    .map(Optional::get);
    }
    
    Optional<DataEntry> getDataEntry(Long id) {
        
        Optional<AssayPack> opt = expPacks.findOne(id);
        if (!opt.isPresent()) return Optional.empty();

        AssayPack expPack = opt.get();
        
        DataEntry entry = new DataEntry();
        entry.id = expPack.getId();
        entry.versionId = expPack.getAssay().versionId;
        entry.creationDate = expPack.getAssay().provenance.created.toLocalDate();
        entry.modificationDate = expPack.getAssay().provenance.modified.toLocalDate();
        entry.isPublic = expPack.getACL().isPublic();
        entry.owner = expPack.getACL().getOwner().getLogin();
        entry.domain = expPack.getACL().getOwner().getEmail();
        entry.domain = entry.domain.substring(entry.domain.indexOf("@"));
        
        
        if (expPack.getSystemInfo().experimentCharacteristic.hasTSData) {
            
            List<DataTrace> data = dataHandler.getDataSet(expPack, DetrendingType.LIN_DTR).orElse(List.of());
            entry.series = data.size();
            
            entry.avgSeriesDuration = data.stream()
                    .mapToDouble(ts -> ts.trace.getDuration())
                    .average()
                    .orElse(0);  
            entry.avgSeriesDuration = Math.floor(entry.avgSeriesDuration);
        }
        
        return Optional.of(entry);
        
    }
    
    public static class DataEntry {
        long id;
        LocalDate creationDate;
        LocalDate modificationDate;
        String versionId;
        String modifiedBy;
        String owner;
        String domain;
        boolean isPublic;
        int series;
        double avgSeriesDuration;
        
    }
        
    
}
