/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.local;

import static ed.biodare2.IdsConfiguration.ASSETSID_PROVIDER;
import static ed.biodare2.IdsConfiguration.EXPID_PROVIDER;
import ed.biodare2.backend.features.ppa.PPAJC2ResultsHandler;
import ed.biodare2.backend.features.ppa.dao.PPAArtifactsRepJC2;
import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.UserGroupRep;
import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.security.dao.db.UserGroup;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.handlers.UsersHandler;
import ed.biodare2.backend.util.concurrent.id.IdGenerators;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.dao.ExperimentalAssayRep;
import ed.biodare2.backend.repo.dao.FileAssetRep;
import ed.biodare2.backend.repo.dao.SystemInfoRep;
import static ed.biodare2.backend.repo.isa_dom.biodesc.DataCategory.*;
import ed.biodare2.backend.repo.system_dom.ACLInfo;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.repo.system_dom.FeaturesAvailability;
import ed.biodare2.backend.repo.system_dom.ServiceLevel;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.features.rdmsocial.RDMSocialHandler;
import ed.biodare2.backend.features.search.ExperimentIndexer;
import ed.biodare2.backend.features.subscriptions.AccountSubscription;
import ed.biodare2.backend.features.subscriptions.ServiceLevelResolver;
import ed.biodare2.backend.features.subscriptions.SubscriptionType;
import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import ed.biodare2.backend.repo.db.dao.db.SearchInfo;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@Service
public class DBFixer {
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    
    @Autowired
    UserAccountRep accounts;
    
    @Autowired
    UserGroupRep groups;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Autowired
    ExperimentsStorage expStorage;
    
    @Autowired
    ExperimentalAssayRep experimentalAssays;
    
    @Autowired
    ExperimentPackHub expPacks;
    
    @Autowired
    FileAssetRep fileAssets;
    
    @Autowired
    SystemInfoRep systemInfos;
    
    @Autowired
    DBSystemInfoRep dbSystemInfos;
    
    @Autowired
    RDMSocialHandler rdmSocialHandler;
    
    @Autowired
    IdGenerators generators;
    
    
    @Autowired
    ExperimentIndexer experimentIndex;
    

    @Autowired
    PPAArtifactsRepJC2 ppa3RepJC2;
    
    @Autowired
    PPAJC2ResultsHandler jc2ResultsHandler;
   
    @Autowired
    ServiceLevelResolver serviceLevel;
    //@Autowired
    //TSDataHandler dataHandler;
    
    //@Autowired
    //Environment env;
    
    public AccountSubscription makeSubsription(SubscriptionType kind) {
     AccountSubscription sub = new AccountSubscription();
     sub.setKind(kind);
     sub.setStartDate(LocalDate.now());
     sub.setRenewDate(LocalDate.now().plusYears(1));
     return sub;
    }    
    
    @Transactional
    public void configureAccounts() {
        log.info("Configuring groups and accounts");
        UserGroup group;

        group = new UserGroup();
        group.setName("demo");
        group.setLongName("Demo Group");        
        UserGroup demoG = groups.findByName(group.getName()).orElseGet( () -> groups.save(group));
        
        
        UserAccount sys;        
        sys = new UserAccount();
        sys.setLogin("system");
        sys.setFirstName("System");
        sys.setLastName("User");
        sys.setEmail("biodare@ed.ac.uk");
        sys.setPassword("CannotLoginWithThat");
        sys.setSupervisor(sys);
        sys.setSystem(true);
        sys.setBackendOnly(true);
        sys.setInstitution("BioDare");
        sys.setSubscription(makeSubsription(SubscriptionType.SYSTEM));
        sys.setTermsVersion(UsersHandler.currentTermsVersion);
        rdmSocialHandler.createUserAspect(sys);
        
        { // must be a block for lambdas
            final UserAccount tmp = sys;
            sys = accounts.findByLogin(sys.getLogin()).orElseGet( () -> accounts.save(tmp));
        }
        
        
        UserAccount adm = new UserAccount();
        adm.setLogin("bdadmin");
        adm.setFirstName("BioDare");
        adm.setLastName("Admin");
        adm.setEmail("biodare@ed.ac.uk");
        adm.setPassword("CannotLoginWithThat");
        adm.setSupervisor(adm);
        adm.setAdmin(true);
        adm.setLocked(true); //casue on the beggining it is not being used
        adm.setInstitution("University of Edinburgh");
        adm.setSubscription(makeSubsription(SubscriptionType.FREE));
        adm.setTermsVersion(UsersHandler.currentTermsVersion);
        rdmSocialHandler.createUserAspect(adm);
        
        accounts.findByLogin(adm.getLogin()).orElseGet( () -> accounts.save(adm));
               
        
        UserAccount acc,demoPI;

        acc = new UserAccount();
        acc.setLogin("test");
        acc.setFirstName("Test");
        acc.setLastName("User");
        acc.setEmail("biodare@ed.ac.uk");
        acc.setPassword(passwordEncoder.encode("test"));
        acc.setSupervisor(acc);
        acc.setInstitution("University of Edinburgh");
        acc.setSubscription(makeSubsription(SubscriptionType.FREE));
        acc.setTermsVersion(UsersHandler.currentTermsVersion);
        rdmSocialHandler.createUserAspect(acc);
        {
            final UserAccount tmp = acc;
            acc = accounts.findByLogin(acc.getLogin()).orElseGet( () -> accounts.save(tmp));
        }
        

        
        acc = new UserAccount();
        acc.setLogin("demoboss");
        acc.setFirstName("Demo");
        acc.setLastName("PI");
        acc.setEmail("biodare@ed.ac.uk");
        acc.setPassword(passwordEncoder.encode("bd.demo"));
        acc.setSupervisor(acc);
        acc.addGroup(demoG);
        acc.setInstitution("University of Edinburgh");
        acc.setSubscription(makeSubsription(SubscriptionType.FREE));
        acc.setTermsVersion(UsersHandler.currentTermsVersion);
        rdmSocialHandler.createUserAspect(acc);
        {
            final UserAccount tmp = acc;
            demoPI = accounts.findByLogin(acc.getLogin()).orElseGet( () -> accounts.save(tmp));
        }     

        acc = new UserAccount();
        acc.setLogin("demo");
        acc.setFirstName("Demo");
        acc.setLastName("User");
        acc.setEmail("biodare@ed.ac.uk");
        acc.setPassword(passwordEncoder.encode("demo"));
        acc.setSupervisor(demoPI);
        acc.addGroup(demoG);
        acc.setInstitution("University of Edinburgh");
        acc.setSubscription(makeSubsription(SubscriptionType.EMBARGO_10));
        acc.setTermsVersion(UsersHandler.currentTermsVersion);
        rdmSocialHandler.createUserAspect(acc);
       {
            final UserAccount tmp = acc;
            acc = accounts.findByLogin(acc.getLogin()).orElseGet( () -> accounts.save(tmp));
        }
        
        acc = new UserAccount();
        acc.setLogin("demo1");
        acc.setFirstName("Demo");
        acc.setLastName("User1");
        acc.setEmail("biodare@ed.ac.uk");
        acc.setPassword(passwordEncoder.encode("demo"));
        acc.setSupervisor(demoPI);
        acc.addGroup(demoG);
        acc.setInstitution("University of Edinburgh");
        acc.setSubscription(makeSubsription(SubscriptionType.FREE));
        acc.setTermsVersion(UsersHandler.currentTermsVersion);
        rdmSocialHandler.createUserAspect(acc);
        {
            final UserAccount tmp = acc;
            acc = accounts.findByLogin(acc.getLogin()).orElseGet( () -> accounts.save(tmp));
        } 

        acc = new UserAccount();
        acc.setLogin("demo2");
        acc.setFirstName("Demo");
        acc.setLastName("User2");
        acc.setEmail("biodare@ed.ac.uk");
        acc.setPassword(passwordEncoder.encode("demo"));
        acc.setSupervisor(demoPI);
        acc.addGroup(demoG);
        acc.setInstitution("University of Edinburgh");
        acc.setSubscription(makeSubsription(SubscriptionType.FREE));
        acc.setTermsVersion(UsersHandler.currentTermsVersion);
        rdmSocialHandler.createUserAspect(acc);
        {
            final UserAccount tmp = acc;
            acc = accounts.findByLogin(acc.getLogin()).orElseGet( () -> accounts.save(tmp));
        } 
        
        acc = new UserAccount();
        acc.setLogin("biodare1");
        acc.setFirstName("BioDare");
        acc.setLastName("One");
        acc.setEmail("biodare@ed.ac.uk");
        acc.setPassword(passwordEncoder.encode("biodare1"));
        acc.setSupervisor(acc);
        //acc.addGroup(demoG);
        acc.setInstitution("University of Edinburgh");
        acc.setSubscription(makeSubsription(SubscriptionType.FREE));
        acc.setTermsVersion(UsersHandler.currentTermsVersion);
        rdmSocialHandler.createUserAspect(acc);
       {
            final UserAccount tmp = acc;
            acc = accounts.findByLogin(acc.getLogin()).orElseGet( () -> accounts.save(tmp));
        }        

    } 
    
    
    
    @Transactional    
    public void restoreDBSystemInfos() {
        log.info("Restoring dbsystem infos");
        
        experimentalAssays.getExerimentsIds()
            .map( id -> systemInfos.findByParent(id, EntityType.EXP_ASSAY))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(info -> !dbSystemInfos.findByParentIdAndEntityType(info.parentId, info.entityType).isPresent())
                .map( info -> toDBSystemInfo(info))
                .peek(info -> log.info("Restored DBSystemInfo for {}",info.getParentId()))
                .forEach( info -> dbSystemInfos.save(info));
    }
    
    protected DBSystemInfo toDBSystemInfo(SystemInfo info) {
        try {
        DBSystemInfo db = new DBSystemInfo();
        db.setParentId(info.parentId);
        db.setEntityType(info.entityType);
        db.setAcl(toACL(info.security));
        if (info.featuresAvailability.releaseDate == null) {
            int years = serviceLevel.subscriptionToEmbargo(db.getAcl().getOwner().getSubscription());
            info.featuresAvailability.releaseDate = info.provenance.creation.dateTime.toLocalDate().plusYears(years);
        }
        db.setReleaseDate(info.featuresAvailability.releaseDate);
        
        // db.setSearchInfo(new SearchInfo());
        return db;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage()+"; parent: "+info.parentId,e);
        }
    }

    protected EntityACL toACL(ACLInfo security) {
        EntityACL acl = new EntityACL();
        if (security.creator == null) security.creator = security.owner;
        
        acl.setPublic(security.isPublic);
        acl.setCreator(accounts.findByLogin(security.creator).orElseThrow(()-> new IllegalArgumentException("Uknown: "+security.creator)));
        acl.setOwner( accounts.findByLogin(security.owner).orElseThrow(()-> new IllegalArgumentException("Uknown: "+security.owner)));
        acl.setSuperOwner(accounts.findByLogin(security.superOwner).orElseThrow(()-> new IllegalArgumentException("Uknown: "+security.superOwner)));
        security.allowedToRead.forEach( gname -> {
            acl.addCanRead(groups.findByName(gname).orElseThrow(()-> new IllegalArgumentException("Uknown group: "+gname)));
        });
        security.allowedToWrite.forEach( gname -> {
            acl.addCanWrite(groups.findByName(gname).orElseThrow(()-> new IllegalArgumentException("Uknown group: "+gname)));
        });
        return acl;
    }
    
    
    @Transactional(propagation = Propagation.MANDATORY)
    public void reindexAll() {
        log.info("ReIndexing...");
        
        try (Stream<AssayPack> exps = experimentalAssays.getExerimentsIds()
                                        .map( id -> expPacks.findOne(id))
                                        .filter(Optional::isPresent)
                                        .map(Optional::get)
                                        .map( pack -> expPacks.enableWriting(pack))
                // so no DB problem
                                        .peek( pack -> pack.getDbSystemInfo().getSearchInfo())
                ) {
            
                List<AssayPack> packs = exps.collect(Collectors.toList());
                
                experimentIndex.clear();
                experimentIndex.indexExperiments(packs);
                
                updateSearchInfo(packs);
                log.info("Reindexed {} experiments", packs.size());
        } catch (Exception e) {
            log.error("Could not reindex experiments: {}",e.getMessage(),e);
            throw e;
        }
            /*.forEach( exp -> {
                try {
                    experimentIndex.indexExperiment(exp);
                    log.info("ReIndexed {}", exp.getId());
                } catch (Exception e) {
                    log.error("Could not reindex: {}, {}",exp.getId(),e.getMessage(),e);
                }                
            });*/
        
    }
    
    void updateSearchInfo(List<AssayPack> packs) {
        
        packs.forEach( pack -> {
            
            //pack = expPacks.enableWriting(pack);
            //DBSystemInfo sysInfo = dbSystemInfos.findById(pack.getDbSystemInfo().getInnerId()).orElseThrow(
            //        () -> new IllegalStateException("Missing DBSystemInfo for exp: "+pack.getId()));
            
            DBSystemInfo sysInfo = pack.getDbSystemInfo();
            
            if (sysInfo.getSearchInfo() == null) {
                sysInfo.setSearchInfo(new SearchInfo());
            }
            experimentIndex.updateSearchInfo(pack);
            sysInfo.getSearchInfo().setIndexedDate(LocalDateTime.now());
            dbSystemInfos.save(sysInfo);
            // expPacks.save(pack);
            
        });
    }    

    
    @Transactional
    public void updateLastIds(long BD1LIMIT) {
        
        //we do it that way cause there is a gap in BD1 experiment ids (old one have very high ids)
        //so that way BD2 works in gap between its ids and high from BD1
        //needed after the imports
        Long lastExpId = dbSystemInfos.getLastParentIdBeforeBound(EntityType.EXP_ASSAY,BD1LIMIT);
        if (lastExpId == null) lastExpId = 1L;
        
        if (generators.getGenerator(EXPID_PROVIDER).next() <= lastExpId)
            generators.initGenerator(EXPID_PROVIDER, 10,lastExpId+10,BD1LIMIT);
        
        
        long lastAssetId = fileAssets.lastId();
        if (generators.getGenerator(ASSETSID_PROVIDER).next() <= lastAssetId)
            generators.initGenerator(ASSETSID_PROVIDER, 10,lastAssetId+10,Long.MAX_VALUE);        
    }
    
    @Transactional
    public void addSubscriptions() {
        log.info("Adding subscriptions infos");
        accounts.findAll()
                .forEach(( UserAccount account) -> {
                    
                    if (account.getTermsVersion() == null) {
                        account.setTermsVersion(UsersHandler.currentTermsVersion);
                        log.info("Added T&C to "+account.getLogin());
                    }
                    
                    if (account.getSubscription() == null) {
                        if (account.isSystem()) {
                            account.setSubscription(makeSubsription(SubscriptionType.SYSTEM));
                        } else {
                            account.setSubscription(makeSubsription(SubscriptionType.FREE));
                        }
                        log.info("Added Subscription to "+account.getLogin());
                    }
                    
        });
    }
    
    @Transactional
    public void addRDMAspects() {
        log.info("Adding rdm aspects infos");
        accounts.findAll()
                .forEach(( UserAccount account) -> {
                                        
                    if (account.getRdmAspect() == null) {
                        
                        rdmSocialHandler.createUserAspect(account);
                        
                        log.info("Added RDM Aspect to "+account.getLogin());
                    }
                    
        });
        
        /*
        experimentalAssays.getExerimentsIds()
            .map( id -> experimentalAssays.findOne(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(exp -> rdmSocialHandler.missingAsspect(exp))
                .peek(exp -> log.info("Adding asset asspects to {}",exp.getId()))
                .forEach( exp -> {
                    rdmSocialHandler.registerNewAssay(exp, RDMCohort.CONTROL);
                });
        */
    }
    
    
    @Transactional
    public void addFeaturesAvailability() {
        
        log.info("Adding features availability");
        
        experimentalAssays.getExerimentsIds()
            .map( id -> systemInfos.findByParent(id, EntityType.EXP_ASSAY))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(info -> info.featuresAvailability == null)
                .peek(info -> log.info("Adding features to {}",info.parentId))
                .forEach( info -> {
                    FeaturesAvailability f = new FeaturesAvailability();
                    f.serviceLevel = ServiceLevel.FULL_GRATIS;
                    info.featuresAvailability =f;
                    systemInfos.save(info);
                });
    }
    
    @Transactional
    public void fixDataCategory() {
        
        log.info("Adding data categories");
                
        experimentalAssays.getExerimentsIds()
            .map( id -> experimentalAssays.findOne(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(info -> info.dataCategory == null)
                .peek(info -> log.info("Fixing null data category, adding other {}",info.getId()))
                .forEach( info -> {
                    info.dataCategory = OTHER;
                    experimentalAssays.save(info);
                });
        
        experimentalAssays.getExerimentsIds()
            .map( id -> experimentalAssays.findOne(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(info -> info.dataCategory.disabled)
                .peek(info -> log.info("Fixing data category {}, {}",info.dataCategory,info.getId()))
                .forEach( info -> {
                    switch(info.dataCategory) {
                        case UNKNOWN: info.dataCategory = OTHER; break;
                        //case UNKNOW: info.dataCategory = OTHER; break;
                        case PCR: info.dataCategory = TRANSCRIPT; break;
                        case TRANSC_FUSION: info.dataCategory = EXPR_REPORTER; break;
                        case TRANSL_FUSION: info.dataCategory = EXPR_REPORTER; break;
                        //case IMAGING: info.dataCategory = GEN_IMAGING; break;
                        default: throw new IllegalArgumentException("Unsuported: "+info.dataCategory);
                    }
                    experimentalAssays.save(info);
                });        
    }    
    
    @Transactional
    public void fixFileInfos() {
        
        log.info("Fixng filesinfos");
        /*
        experimentalAssays.getExerimentsIds()
                .forEach( exp -> {
                    fileAssets.fixAssetsInfo(exp, ExperimentDataHandler.TSAssetName);
                    log.info("Fixed FileINfo {}",exp);
                });*/
    } 
    
     
    
    @Transactional
    //@Deprecated
    public void migratePPAArtifacts() {
     
        log.info("Fixing ppa artifacts location");
        
        experimentalAssays.getExerimentsIds()
            .map( id -> expPacks.findOne(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter( pack -> pack.getSystemInfo().experimentCharacteristic.hasPPAJobs)
                .forEach( exp -> {
                    try {
                        log.info("Movind ppa artifacts for {}",exp.getId());
                        Path expDir = expStorage.getExperimentDir(exp.getId());
                        if (!Files.isDirectory(expDir))
                            throw new Exception("Missing exp dir for "+exp.getId());
                        Path requestsDir = expDir.resolve("PPA/JOB_REQUESTS");
                        if (!Files.isDirectory(requestsDir)) {
                            log.warn("Missing request dir for "+exp.getId());
                            return;
                        }
                        Path jobsDir = expDir.resolve("PPA/JOBS");
                        if (!Files.isDirectory(jobsDir)) {
                            log.warn("Missing jobs dir for "+exp.getId());
                            Files.createDirectories(jobsDir);
                        }
                        
                        int fixed[] = {0};
                        try (Stream<Path> files = Files.list(requestsDir)) {
                            files.forEach( file -> {
                                try {
                                String idPart = file.getFileName().toString();                                
                                idPart = idPart.substring(idPart.indexOf(".")+1,idPart.lastIndexOf("."));
                                long jobId = Long.parseLong(idPart);
                                
                                Path jobDir = jobsDir.resolve(""+jobId);
                                if (!Files.isDirectory(jobDir))
                                    Files.createDirectories(jobDir);
                                
                                Files.move(file, jobDir.resolve(file.getFileName()));
                                log.info("Moved {}",file);
                                fixed[0]++;
                                } catch (Exception e) {
                                    log.error("Could not move {} {} {} {}",exp.getId(),file,e.getClass(),e.getMessage());
                                }
                            });
                        }
                        
                        //int fixed = ppaResultsHandler.redoJobsStatsAndSummaries(exp);
                        log.info("Moved {} ppa artifacts for {}",fixed[0],exp.getId());
                    } catch (Exception e) {
                        log.error("Could not move ppa artifacts for: {}, {}",exp.getId(),e.getMessage(),e);
                    }
                });
                ;
        
    }  
    
    /*
    @Transactional
    public void recalculateDataMetrics() {
     
        log.info("Recalculating data metrics");
        
        experimentalAssays.getExerimentsIds()
            .map( id -> expPacks.findOne(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter( pack -> pack.getSystemInfo().experimentCharacteristic.hasTSData)
                .forEach( exp -> {
                    try {
                        log.info("Calculating data metrics for {}",exp.getId());
                        dataHandler.recalculateMeterics(exp);
                        log.info("Recalculated data metrics for {}",exp.getId());
                    } catch (Exception e) {
                        log.error("Could not cacluate data meterics for: {}, {}",exp.getId(),e.getMessage(),e);
                    }
                });
                ;
        
    } */ 

    /*
    @Transactional
    public void migratePPA2ToPPA3() {
        log.info("Migrating ppa2 artifacts");
        
        PPA2ToPPA3Migrator migrator = new PPA2ToPPA3Migrator(ppa2Rep, ppa3RepJC2, jc2ResultsHandler);
                
        experimentalAssays.getExerimentsIds()
            .map( id -> expPacks.findOne(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter( pack -> pack.getSystemInfo().experimentCharacteristic.hasPPAJobs)
                .forEach( exp -> {
                    try {
                        log.info("Migrating ppa artifacts for {}",exp.getId());
                        migrator.migrate(exp);
                        
                        //int fixed = ppaResultsHandler.redoJobsStatsAndSummaries(exp);
                        log.info("Migrated ppa2 artifacts for {}",exp.getId());
                    } catch (Exception e) {
                        log.error("Could not move ppa artifacts for: {}, {}",exp.getId(),e.getMessage(),e);
                    }
                });
                ;
    }*/




    
}
