/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ed.biodare2.backend.features.auto_publish;

import ed.biodare2.backend.features.subscriptions.SubscriptionType;
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
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@Service
public class AutomaticPublisher {

    final static String CUTOFF_PREFIX = "PUBLISH_BEFORE";
    final static int START_BATCH_SIZE = 4;
    final static List<SubscriptionType> EXCLUDED_SUBSCRIPTIONS = List.of(SubscriptionType.FREE_NO_PUBLISH);
    
    
    final Logger log = LoggerFactory.getLogger(this.getClass());    
    final Path configFile;
    
    final DBSystemInfoRep dbSystemInfos; 
    final ExpPublishingHandler pubHandler;
    final ExperimentPackHub experiments;
    
    int batchSize = START_BATCH_SIZE;
    
    @Autowired
    public AutomaticPublisher(@Value("${bd2.autopublish.file:autopublish_cutoff_date.txt}") String configPath, 
            DBSystemInfoRep dbSystemInfos, 
            ExperimentPackHub experiments, 
            ExpPublishingHandler handler) {
        
        this.configFile = Paths.get(configPath);
        this.dbSystemInfos = dbSystemInfos;
        this.experiments = experiments;
        this.pubHandler = handler;
        
        log.info("AutomaticPublisher created with config "+configFile.toAbsolutePath());
        
    }

    @Scheduled(fixedRate = 1000*60*60*2, initialDelay = 1000*60*2)  
    @Transactional
    public void trigerAutoPublishing() throws IOException {

        Optional<LocalDate> cutoff = getCutoffDate(this.configFile);
        if (cutoff.isEmpty()) {
            log.info("Auto publisher did not found cutoff date in the config file: "+this.configFile);
            return;
        }
        
        
        
        List<Long> expIds = getPublishingCandidates(cutoff.get().plusYears(5), batchSize);
        log.info("Autopublishing "+expIds.size()+" candidates with cutoff "+cutoff.get()+". batchSize:"+batchSize);

        int ignored = 0;
        for (Long expId: expIds) {
            
            if (!doPublishing(expId,cutoff.get())) {
                ignored++;
            }                            
        }
        updateBatchSize(ignored);
                
    }
    
    boolean doPublishing(Long expId, LocalDate cutoff) {
        
            Optional<AssayPack> pack = experiments.findOne(expId);
            if (pack.isEmpty()) {
                log.warn("Cannot publish "+expId+", experiment not found");
                return false;
            }
            
            if (pubHandler.attemptAutoPublishing(pack.get(), cutoff)) {
                log.warn("Automatically published exp: "+pack.get().getId()+" from: "+pack.get().getACL().getOwner().getLogin()+" created: "+pack.get().getAssay().provenance.created.toLocalDate());
                return true;
            } else {
                log.debug("Ignored publishing of exp: "+pack.get().getId()+" from: "+pack.get().getACL().getOwner().getLogin()+" created: "+" cutoff: "+cutoff);
                return false;
            }
    }    

    Optional<LocalDate> getCutoffDate(Path configFile) throws IOException {
        if (!Files.isRegularFile(configFile))
            return Optional.empty();
        
        String configText = Files.readString(configFile);
        if (!configText.startsWith(CUTOFF_PREFIX))
            throw new IllegalArgumentException("Expected "+CUTOFF_PREFIX+":YYYY-MM-DD not: "+ configText);

        if (!configText.contains(":"))
            throw new IllegalArgumentException("Expected "+CUTOFF_PREFIX+":YYYY-MM-DD not: "+ configText);
        
        String[] parts = configText.split(":");
        if (parts.length != 2)
            throw new IllegalArgumentException("Expected "+CUTOFF_PREFIX+":YYYY-MM-DD not: "+ configText);
        
        String date = parts[1].trim();
        return Optional.of(LocalDate.parse(date));
    }

    List<Long> getPublishingCandidates(LocalDate cutoff, int limit) {
        
        
        return dbSystemInfos.findParentIdsBeforeCutoffAndOpenStatusNotWithSubscription(EntityType.EXP_ASSAY, cutoff.atStartOfDay(), false, EXCLUDED_SUBSCRIPTIONS, Limit.of(limit));
    }

    void updateBatchSize(int ignored) {
        
        //reduce the bathSize if there are no ignored
        if (ignored < 2 && batchSize > START_BATCH_SIZE) {
            
            batchSize = START_BATCH_SIZE;
            return;
        }
        
        if (ignored < (batchSize / 2))
            return;
        
        //increase the batchSize
        batchSize = 2*batchSize;
    }


    
}
