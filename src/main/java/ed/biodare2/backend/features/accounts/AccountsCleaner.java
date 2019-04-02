/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.accounts;

import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@Service
public class AccountsCleaner {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());    
    
    final static int WEEK_OLD = 7;
    
    final UserAccountRep users;
    
    final DBSystemInfoRep systemInfos;

    @Autowired
    public AccountsCleaner(UserAccountRep users, DBSystemInfoRep systemInfos) {
        this.users = users;
        this.systemInfos = systemInfos;
    }
    
    @Scheduled(fixedRate = 1000*60*60*24, initialDelay = 1000*60*30)
    @Transactional        
    public void removeNonActivated() {
        
        log.info("Cleaning not activated accounts");
        
        List<UserAccount> nonActivated = users.findByActivationDateIsNull();
        
        nonActivated = filterFreshAndUsed(nonActivated);

        
        if (nonActivated.isEmpty()) return;
        
        log.warn("Will remove {} not activated accounts: {}",
                nonActivated.size(),
                nonActivated.stream()
                        .map( acc -> acc.getLogin())
                        .collect(Collectors.joining(","))
                );
        
        users.deleteAll(nonActivated);
        
    }

    protected boolean isNonInner(UserAccount acc) {
        return (!acc.isSystem() && !acc.isBackendOnly());
    }
    
    protected boolean hasNoEntries(UserAccount acc) {
        try (Stream<DBSystemInfo> infos = systemInfos.findByAclOwner(acc)) {
            return infos.count() == 0;
        }
    }
    

    protected List<UserAccount> filterFreshAndUsed(List<UserAccount> accs) {
        
        final LocalDate old = LocalDate.now().minusDays(WEEK_OLD);
        
        return accs.stream()
                .filter(this::isNonInner)
                .filter( acc -> acc.getRegistrationDate() != null)
                .filter( acc -> acc.getRegistrationDate().isBefore(old))
                .filter(this::hasNoEntries)
                .collect(Collectors.toList());
    }
    
}
