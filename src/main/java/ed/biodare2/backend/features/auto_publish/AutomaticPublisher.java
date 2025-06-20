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
import ed.biodare2.backend.security.dao.UserAccountRep;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Limit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.comparator.Comparators;

/**
 *
 * @author tzielins
 */
@Service
public class AutomaticPublisher {

    final static String CUTOFF_PREFIX = "PUBLISH_BEFORE";
    final static int START_BATCH_SIZE = 100;
    
    
    final Logger log = LoggerFactory.getLogger(this.getClass());    
    final Path configFile;
    
    final DBSystemInfoRep dbSystemInfos; 
    final ExpPublishingHandler pubHandler;
    final ExperimentPackHub experiments;
    final UserAccountRep users;
    
    int batchSize = START_BATCH_SIZE;
    
    @Autowired
    public AutomaticPublisher(@Value("${bd2.autopublish.file:autopublish_cutoff_date.txt}") String configPath, 
            DBSystemInfoRep dbSystemInfos, 
            ExperimentPackHub experiments, 
            ExpPublishingHandler handler,
            UserAccountRep users) {
        
        this.configFile = Paths.get(configPath);
        this.dbSystemInfos = dbSystemInfos;
        this.experiments = experiments;
        this.pubHandler = handler;
        this.users = users;
        
        log.info("AutomaticPublisher created with config "+configFile.toAbsolutePath());
        
    }
    

    @Scheduled(fixedRate = 1000*60*60*2, initialDelay = 1000*60*2)   //  every 2 hours, starting after 2 minutes
    @Transactional
    public void trigerAutoPublishing() throws IOException {

        Optional<LocalDate> cutoff = Optional.of(java.time.LocalDate.now());
        
        logEmbargoUsers();
                
        List<Long> expIds = getPublishingCandidates(cutoff.get(), batchSize);
        log.info("Autopublishing "+expIds.size()+" candidates with cutoff "+cutoff.get()+". batchSize:"+batchSize);

        int ignored = 0;
        for (Long expId: expIds) {
            
            if (!doPublishing(expId,cutoff.get())) {
                ignored++;
            }                            
        }
        updateBatchSize(ignored);
                
    }
    
    void logEmbargoUsers() {
        List<String> noPublishUsers = getEmbargoUsers();
        log.info("The following users have longer embargo: "+noPublishUsers.stream().collect(Collectors.joining(",")));        
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

    List<Long> getPublishingCandidates(LocalDate cutoff, int limit) {
        
        
        return dbSystemInfos.findParentIdsWithReleaseBeforeCutoffAndOpenStatus(EntityType.EXP_ASSAY, cutoff, false, Limit.of(limit));
    }

    /*
    The batch size is dinamic to prevent situation where all the hits within batchsize from DB query are being ignored
    and hence they will end up again in the results at the next call.
    It should not happen with the current code, but, it may in future development if the candidates search criteria and 
    candidates  suitability criteria diverge.
    In the local instance, the in memorry db sets created to the current date while the provenance data is the original one, which means
    no experiment are returned unless the cutoff is in the future.
    */
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

    List<String> getEmbargoUsers() {
        
        List<String> embargoed = users.findBySubscriptionKind(SubscriptionType.EMBARGO_06)
             .stream()
             .map( u -> u.getLogin())
             .collect(Collectors.toList());
        
        embargoed.addAll(
             users.findBySubscriptionKind(SubscriptionType.EMBARGO_10)
             .stream()
             .map( u -> u.getLogin())
             .collect(Collectors.toList())                
        );
        
        embargoed.sort( (s1, s2) -> s1.compareTo(s2));
        return embargoed;
    }


    
}
