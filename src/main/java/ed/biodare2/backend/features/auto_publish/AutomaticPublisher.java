/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ed.biodare2.backend.features.auto_publish;

import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.EntityType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Limit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class AutomaticPublisher {

    final static String CUTOFF_PREFIX = "PUBLISH_BEFORE";
    final static int BATCH_SIZE = 100;
    
    final Logger log = LoggerFactory.getLogger(this.getClass());    
    final Path configFile;
    
    final DBSystemInfoRep dbSystemInfos; 
    final ExpPublishingHandler pubHandler;
    final ExperimentPackHub experiments;
    
    @Autowired
    public AutomaticPublisher(@Value("${bd2.autopublish.file:cutoff_date.txt}") String configPath, 
            DBSystemInfoRep dbSystemInfos, 
            ExperimentPackHub experiments, 
            ExpPublishingHandler handler) {
        
        this.configFile = Paths.get(configPath);
        this.dbSystemInfos = dbSystemInfos;
        this.experiments = experiments;
        this.pubHandler = handler;
        
        log.info("AutomaticPublisher created with config "+configFile);
        
    }

    @Scheduled(fixedRate = 1000*60*60, initialDelay = 1000*60)    
    public void trigerAutoPublishing() throws IOException {

        Optional<LocalDate> cutoff = getCutoffDate(this.configFile);
        if (cutoff.isEmpty()) {
            log.info("Auto publisher did not found cutoff date in the config file: "+this.configFile);
            return;
        }
        
        
        List<Long> expIds = getPublishingCandidates(cutoff.get(), BATCH_SIZE);
        log.info("Autopublishing "+expIds.size()+" candidates with cutoff "+cutoff.get());
        
        for (Long expId: expIds) {
            
            doPublishing(expId,cutoff.get());
            
        }
                
    }
    
    void doPublishing(Long expId, LocalDate cutoff) {
        
            Optional<AssayPack> pack = experiments.findOne(expId);
            if (pack.isEmpty()) {
                log.warn("Cannot publish "+expId+", experiment not found");
                return;
            }
            
            if (pubHandler.attemptAutoPublishing(pack.get(), cutoff)) {
                log.warn("Automatically published exp: "+pack.get().getId()+" from: "+pack.get().getAssay().provenance.created.toLocalDate());
            } else {
                log.info("Ignored publishing of exp: "+pack.get().getId()+" from: "+pack.get().getAssay().provenance.created.toLocalDate()+" cutoff: "+cutoff);
            }
    }    

    Optional<LocalDate> getCutoffDate(Path configFile) throws IOException {
        if (!Files.isRegularFile(configFile))
            return Optional.empty();
        
        String configText = Files.readString(configFile);
        if (!configText.startsWith(CUTOFF_PREFIX))
            return Optional.empty();

        if (!configText.contains(":"))
            return Optional.empty();
        
        String[] parts = configText.split(":");
        if (parts.length != 2)
            return Optional.empty();
        
        String date = parts[1].trim();
        return Optional.of(LocalDate.parse(date));
    }

    List<Long> getPublishingCandidates(LocalDate cutoff, int limit) {
        
        
        return dbSystemInfos.findParentIdsBeforeCutoffAndOpenStatus(EntityType.EXP_ASSAY, cutoff.atStartOfDay(), false, Limit.of(limit))
                .toList();
    }


    
}
