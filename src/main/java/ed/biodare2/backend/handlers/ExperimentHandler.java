/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

import ed.biodare2.backend.features.rdmsocial.RDMCohort;
import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.PermissionsResolver;
import ed.biodare2.backend.util.concurrent.id.IdGenerator;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.isa_dom.GeneralDesc;
import ed.biodare2.backend.repo.isa_dom.actors.Institution;
import ed.biodare2.backend.repo.isa_dom.measure.MeasurementDesc;
import ed.biodare2.backend.repo.isa_dom.shared.SimpleProvenance;
import ed.biodare2.backend.repo.isa_dom.actors.Person;
import ed.biodare2.backend.repo.isa_dom.biodesc.BiologicalDescription;
import ed.biodare2.backend.repo.isa_dom.biodesc.BiologicalInfoBuilder;
import ed.biodare2.backend.repo.isa_dom.conditions.Environments;
import ed.biodare2.backend.repo.isa_dom.contribution.ContributionDesc;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalDetails;
import ed.biodare2.backend.repo.system_dom.ACLInfo;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentCharacteristic;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.OperationRecord;
import ed.biodare2.backend.repo.system_dom.OperationType;
import ed.biodare2.backend.repo.system_dom.Provenance;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.repo.system_dom.VersionRecord;
import ed.biodare2.backend.repo.system_dom.VersionsInfo;
import ed.biodare2.backend.repo.ui_dom.exp.ExperimentalAssayView;
import ed.biodare2.backend.repo.ui_dom.security.SecuritySummary;
import ed.biodare2.backend.features.rdmsocial.RDMSocialHandler;
import ed.biodare2.backend.features.search.ExperimentSearcher;
import ed.biodare2.backend.features.search.SortOption;
import ed.biodare2.backend.features.subscriptions.ServiceLevelResolver;
import ed.biodare2.backend.repo.isa_dom.openaccess.OpenAccessInfo;
import ed.biodare2.backend.repo.isa_dom.openaccess.OpenAccessLicence;
import ed.biodare2.backend.repo.ui_dom.exp.ExperimentGeneralDescView;
import ed.biodare2.backend.repo.ui_dom.shared.Page;
import ed.biodare2.backend.web.rest.HandlingException;
import ed.biodare2.backend.web.rest.ListWrapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@Service
public class ExperimentHandler extends BaseExperimentHandler {
 
    
    final IdGenerator expIdGenerator;
    final ExperimentPackHub experiments;
    final PermissionsResolver securityResolver;
    final ServiceLevelResolver serviceLevelResolver;
    
    final ExperimentSearcher searcher;
    final RDMSocialHandler rdmSocialHandler;
    
    final PermissionsResolver permissionsResolver;    
    
    //final Comparator<ExperimentalAssay> modificationDateCmp = Comparator.comparing((ExperimentalAssay a) -> a.provenance.modified).reversed();
    
    @Autowired
    public ExperimentHandler(ExperimentPackHub experiments,
            @Qualifier("ExpIdProvider") IdGenerator expIdGenerator,
            ExperimentSearcher searcher,
            PermissionsResolver securityResolver,
            ServiceLevelResolver serviceLevelResolver,
            RDMSocialHandler rdmSocialHandler,
            PermissionsResolver permissionsResolver) {
        this.expIdGenerator = expIdGenerator;
        this.experiments = experiments;
        this.searcher =searcher;
        this.securityResolver = securityResolver;
        this.serviceLevelResolver = serviceLevelResolver;
        this.rdmSocialHandler = rdmSocialHandler;
        this.permissionsResolver = permissionsResolver;
    }

    public ExperimentalAssayView newDraft(BioDare2User user) {
        
        ExperimentalAssayView experiment = new ExperimentalAssayView();
        
        setDefaultExpDetails(experiment,user);
        
        return experiment;
    }


    
    @Transactional
    public ExperimentalAssayView insert(ExperimentalAssayView req,BioDare2User user)  {
        
        ExperimentalAssay exp = new ExperimentalAssay(expIdGenerator.next());
        mergeRequest(req,exp);
        
        EntityACL acl = createNewACL(user);
        //System.out.println(acl);
        SystemInfo systemInfo = createNewSystemInfo(exp,acl,user);
        
        copySystemFeatures(systemInfo,exp);
        
        AssayPack boundle = experiments.newPack(exp, systemInfo, acl);
        boundle = experiments.save(boundle);
        rdmSocialHandler.registerNewAssay(boundle, user);
        
        //exp = experiments.save(exp);
        //systemInfos.save(systemInfo);
        //return exp;
        return assayToView(boundle,user);
    }
    
    @Transactional
    public ExperimentalAssayView update(AssayPack boundle, ExperimentalAssayView req, BioDare2User user) {
        
        boundle = experiments.enableWriting(boundle);
        ExperimentalAssay exp = boundle.getAssay();
        mergeRequest(req, exp);
        
        registerExpUpdate(boundle.getSystemInfo(),user);        
        copySystemFeatures(boundle.getSystemInfo(),exp);
        
        boundle = experiments.save(boundle);
        rdmSocialHandler.registerUpdateAssay(boundle, user);
        
        return assayToView(boundle,user);
        
    }  
    
    @Transactional
    public ExperimentalAssayView publish(AssayPack boundle, OpenAccessLicence licence, BioDare2User user) {
        
        if (boundle.getSystemInfo().openAccessInfo != null)
            throw new HandlingException("Experiment: "+boundle.getId()+" is already open access");
        
        if (!isSuitableLicenceForUser(licence,user))
            throw new HandlingException("Licence: "+licence+" is not available for the user: "+user.getLogin());
        
        boundle = experiments.enableWriting(boundle);
        SystemInfo systemInfo = boundle.getSystemInfo();
        
        OpenAccessInfo accessInfo = makeNewOpenAccessInfo(licence,user);
        systemInfo.openAccessInfo = accessInfo;
        
        serviceLevelResolver.setServiceForOpen(systemInfo.featuresAvailability);
        markAsPublic(boundle);
        
        updateProvenance(systemInfo.provenance, user, OperationType.PUBLISH, systemInfo.getVersionId());
        
        copySystemFeatures(boundle.getSystemInfo(),boundle.getAssay());
        
        boundle = experiments.save(boundle);
        
        return assayToView(boundle,user);
    }    
    
    protected void markAsPublic(AssayPack boundle) {
        securityResolver.makePublic(boundle.getACL());
        boundle.getSystemInfo().security = convertACL(boundle.getACL());
    }
    
    @Transactional
    public ExperimentalAssayView importBD1(ExperimentalAssay req,BioDare2User user)  {
        
        if (getExperiment(req.getId()).isPresent())
            throw new HandlingException("Experiment: "+req.getId()+" already exists");
        
        ExperimentalAssay exp = req;      
        
        EntityACL acl = createNewACL(user);
        SystemInfo systemInfo = importSystemInfo(exp,acl,user);
        
        copySystemFeatures(systemInfo,exp);
        
        AssayPack boundle = experiments.newPack(exp, systemInfo, acl);
        boundle = experiments.save(boundle);
        rdmSocialHandler.registerNewAssay(boundle, RDMCohort.CONTROL);
        
        return assayToView(boundle,user);
    }
    
    
    @Transactional
    public void updateHasPPAJobs(AssayPack boundle, boolean hasPPAJobs) {
        if (boundle.getSystemInfo().experimentCharacteristic.hasPPAJobs == hasPPAJobs) return;
        
        boundle = experiments.enableWriting(boundle);
        boundle.getSystemInfo().experimentCharacteristic.hasPPAJobs = hasPPAJobs;
        copySystemFeatures(boundle.getSystemInfo(), boundle.getAssay());
        boundle = experiments.save(boundle);
    }
    
    @Transactional
    public void updateHasRhythmicityJobs(AssayPack boundle, boolean hasRhythmicityJobs) {
        if (boundle.getSystemInfo().experimentCharacteristic.hasRhythmicityJobs == hasRhythmicityJobs) return;
        
        boundle = experiments.enableWriting(boundle);
        boundle.getSystemInfo().experimentCharacteristic.hasRhythmicityJobs = hasRhythmicityJobs;
        copySystemFeatures(boundle.getSystemInfo(), boundle.getAssay());
        boundle = experiments.save(boundle);
    }    
    
    /*@Transactional
    public ExperimentalAssay save(ExperimentalAssay exp,BioDare2User user)  {
        
        //user = users.findOne(user.getId());
        
        //Optional<SystemInfo> systemInfo = systemInfos.findByParent(exp.getId(), EntityType.EXP_ASSAY);
        Optional<AssayPack> boundle = experiments.findOne(exp.getId());
        
        return update(exp,boundle.get(),user);
    }*/
    
    /*@Transactional(propagation = Propagation.REQUIRED)
    protected ExperimentalAssay update(ExperimentalAssay exp,AssayPack boundle,BioDare2User user) {
        
        registerExpUpdate(boundle.getSystemInfo(),user);
        
        copySystemFeatures(boundle.getSystemInfo(),exp);
        
        boundle.setAssay(exp);
        //exp = experiments.save(exp);
        //systemInfos.save(systemInfo);
        //return exp;
        boundle = experiments.save(boundle);
        return boundle.getAssay();
    }*/
    
    
    /*
    public long countExperiments(BioDare2User user, boolean onlyOwned) {
        
        try (LongStream ids = searchVisible(user, onlyOwned)) {
            return ids.count();
        }
    } */

    /*
    public Stream<ExperimentalAssay> listExperiments(BioDare2User user, boolean onlyOwned, Page page) {
        
        LongStream ids = searchVisible(user, onlyOwned);
        
        return experiments.findByIds(ids)
            .map(AssayPack::getAssay)
            .sorted(Comparator.comparing((ExperimentalAssay a) -> a.provenance.modified).reversed())
            .skip(page.first())
            .limit(page.pageSize)
        ;        
    } */   

    public ListWrapper<ExperimentalAssay> listExperiments(BioDare2User user, boolean showPublic, 
            SortOption sorting, boolean ascending, Page page) {
        
        ListWrapper<Long> ids = searchVisible(user, showPublic, sorting, ascending, page.pageIndex, page.pageSize);
        
        return idsToVisibleAssays(ids, user);

    } 
    
    protected ListWrapper<ExperimentalAssay> idsToVisibleAssays(ListWrapper<Long> ids, BioDare2User user) {
        
        try ( Stream<AssayPack> packs = experiments.findByIds(ids.data)) {
            
            // filter based on visibility in case a query could be fabricated to
            // disabled lucene filters or if the indexing async and it does not refelct
            // current settings            
            List<ExperimentalAssay> exps = packs
                .filter( exp -> permissionsResolver.canRead(exp.getACL(), user))
                .map(AssayPack::getAssay)
                .collect(Collectors.toList());
        
            Page currentPage = ids.currentPage;
            return new ListWrapper<>(exps, currentPage);            
        }        
    }
        
    public ListWrapper<ExperimentalAssay> searchExperiments(String query,
            BioDare2User user, boolean showPublic, 
            SortOption sorting, boolean ascending, Page page) {
        
        ListWrapper<Long> ids = searchVisible(query, user, showPublic, sorting, ascending, page.pageIndex, page.pageSize);
        
        return idsToVisibleAssays(ids, user);        

    }      
        

    /*
    public Stream<ExperimentalAssay> listExperiments(BioDare2User user,boolean onlyOwned) {
        

        ListWrapper<Long> ids = searchVisible(user, onlyOwned);
        
        return experiments.findByIds(ids)
                .map(AssayPack::getAssay)
                .sorted(Comparator.comparing((ExperimentalAssay a) -> a.provenance.modified).reversed())
                ;
                
    }*/

    /*
    protected LongStream searchVisible(BioDare2User user,boolean onlyOwned) {
        
        LongStream ids = searcher.findByOwner(user);
        if (!onlyOwned) ids = LongStream.concat(ids, searcher.findPublic()).distinct();
        return ids;
    }*/
    
    protected ListWrapper<Long> searchVisible(BioDare2User user, boolean showPublic,
            SortOption sorting, boolean ascending,
            int pageIndex, int pageSize) {
        
        return searcher.findAllVisible(user, showPublic, sorting, ascending, pageIndex, pageSize);
    }
    
    protected ListWrapper<Long> searchVisible(String query,
            BioDare2User user, boolean showPublic,
            SortOption sorting, boolean ascending,
            int pageIndex, int pageSize) {
        
        return searcher.findVisible(query, user, showPublic, sorting, ascending, pageIndex, pageSize);
    }    
    
    public Optional<AssayPack> getExperiment(long expId) {
        
        return experiments.findOne(expId);
    }

    protected void setDefaultExpDetails(ExperimentalAssayView assay, BioDare2User user) {
        
   
                
        ContributionDesc contr = new ContributionDesc();
        contr.authors.add(account2Person(user)); 
        contr.institutions.add(instName2Institution(user.getInstitution()));
        assay.contributionDesc = contr;
        
        ExperimentalDetails expDetails = new ExperimentalDetails();
        
        MeasurementDesc measurement = new MeasurementDesc();
        expDetails.measurementDesc = measurement;
        
        Environments experimental = new Environments();
        expDetails.experimentalEnvironments = experimental;
        
        Environments growth = new Environments();
        expDetails.growthEnvironments = growth;
        
        expDetails.executionDate = LocalDate.now();
        
        assay.experimentalDetails = expDetails;
        
        ExperimentGeneralDescView general = new ExperimentGeneralDescView();
        general.executionDate = expDetails.executionDate;
        assay.generalDesc = general;
        
        
        assay.features = new ExperimentCharacteristic();
        
        //assay.bioDescription = makeDefaultBioDescription();
        //assay.bioSummary = new BiologySummary(assay.bioDescription);
        assay.provenance = new SimpleProvenance();
        
        SecuritySummary sec = new SecuritySummary();
        sec.canRead = true;
        sec.canWrite = true;
        sec.isOwner = true;
        sec.isSuperOwner = false;
        
        assay.security = sec;
                
                
    }
    
    protected Person account2Person(BioDare2User user) {
        Person person = new Person();
        person.firstName = user.getFirstName();
        person.lastName = user.getLastName();
        person.id = user.getId();
        person.login = user.getLogin();
        person.ORCID = user.getORCID();
        //person.externalService = routes.getServiceName();
        //person.externalPath = routes.getEntityPath(user);
        return person;
    }



    protected EntityACL createNewACL(BioDare2User user) {
        

        return securityResolver.createNewACL(user);
    }

    protected SystemInfo createNewSystemInfo(ExperimentalAssay exp, EntityACL acl, BioDare2User user) {
        
        SystemInfo info = new SystemInfo();
        info.parentId = exp.getId();
        info.entityType = EntityType.EXP_ASSAY;
        info.currentDataVersion = 0;
        info.currentDescVersion = 1;
        info.experimentCharacteristic = new ExperimentCharacteristic();
        info.featuresAvailability = serviceLevelResolver.buildForExperiment(user);
        info.provenance = createNewProvenance(user,info.getVersionId());
        info.security = convertACL(acl);
        info.versionsInfo = new VersionsInfo();
        
        VersionRecord ver = new VersionRecord();
        ver.dataVersion = info.currentDataVersion;
        ver.descVersion = info.currentDescVersion;
        info.versionsInfo.versions.add(ver);

        return info;
    }
    
    protected SystemInfo importSystemInfo(ExperimentalAssay exp, EntityACL acl, BioDare2User user) {
        
        SystemInfo info = new SystemInfo();
        info.parentId = exp.getId();
        info.entityType = EntityType.EXP_ASSAY;
        int[] versions = extractVersions(exp.versionId);
        info.currentDataVersion = versions[0];
        info.currentDescVersion = versions[2];
        
        info.experimentCharacteristic = new ExperimentCharacteristic();
        info.experimentCharacteristic.biodare1Id = exp.getId();
        info.featuresAvailability = serviceLevelResolver.buildForExperiment(user);
        info.provenance = importProvenance(exp.provenance,user,info.getVersionId());
        info.security = convertACL(acl);
        info.versionsInfo = new VersionsInfo();
        
        VersionRecord ver = new VersionRecord();
        ver.dataVersion = info.currentDataVersion;
        ver.descVersion = info.currentDescVersion;
        info.versionsInfo.versions.add(ver);

        return info;
    } 
    
    protected int[] extractVersions(String versionId) {
        String[] parts = versionId.split("\\.");
        if (parts.length != 3) throw new HandlingException("Wrong number of parts in version id: "+versionId);
        try {
            int[] ids = new int[3];
            for (int i = 0;i< ids.length;i++) {
                ids[i] = Integer.parseInt(parts[i]);
            }
            return ids;
        } catch (NumberFormatException e) {
            throw new HandlingException("Cannot parse versionId token: "+versionId+"; "+e.getMessage());
        }
    }
    
    Provenance importProvenance(SimpleProvenance external, BioDare2User user, String versionId) {
        
        Provenance prov = new Provenance();
        prov.creation = new OperationRecord();
        prov.creation.actorLogin = user.getLogin();
        prov.creation.actorName = external.createdBy;
        prov.creation.dateTime = external.created;
        prov.creation.operation = OperationType.CREATION;
        prov.creation.versionId = "0.0.1";
        
        prov.changes.add(prov.creation);
        
        prov.lastChange = new OperationRecord();
        prov.lastChange.actorLogin = user.getLogin();
        prov.lastChange.actorName = external.modifiedBy;
        prov.lastChange.dateTime = external.modified;
        prov.lastChange.operation = OperationType.DESC_EDITION;
        prov.lastChange.versionId = versionId;
        
        return prov;
    }    

    protected Provenance createNewProvenance(BioDare2User user,String versionId) {
        
        Provenance prov = new Provenance();
        prov.creation = new OperationRecord();
        prov.creation.actorLogin = user.getLogin();
        prov.creation.actorName = user.getName();
        prov.creation.dateTime = LocalDateTime.now();
        prov.creation.operation = OperationType.CREATION;
        prov.creation.versionId = versionId;
        prov.lastChange = prov.creation;
        return prov;
    }

    /**
     * It is public static so that tests can reuse this code to correctly populate sys info parts
     * @param acl
     * @return 
     */
    public static ACLInfo convertACL(EntityACL acl) {
        
        ACLInfo info = new ACLInfo();
        info.creator = acl.getCreator().getLogin();
        info.owner = acl.getOwner().getLogin();
        info.superOwner = acl.getSuperOwner().getLogin();
        info.isPublic = acl.isPublic();
        
        info.allowedToRead = acl.getAllowedToRead().stream()
                            .map( g -> g.getName())
                            .collect(Collectors.toSet());
        
        info.allowedToWrite = acl.getAllowedToWrite().stream()
                            .map( g -> g.getName())
                            .collect(Collectors.toSet());
        return info;
    }


    protected void registerExpUpdate(SystemInfo systemInfo, BioDare2User user) {
        
        systemInfo.currentDescVersion++;
        updateProvenance(systemInfo.provenance, user, OperationType.DESC_EDITION, systemInfo.getVersionId());
    }
    
 
    protected void mergeRequest(ExperimentalAssayView req, ExperimentalAssay dest) {
        
        if (req.contributionDesc != null) dest.contributionDesc = req.contributionDesc;
        if (req.experimentalDetails != null) dest.experimentalDetails = req.experimentalDetails;
        if (req.generalDesc != null) {
            dest.generalDesc = req.generalDesc;
            if (req.generalDesc.executionDate != null) {
                dest.experimentalDetails.executionDate = req.generalDesc.executionDate;
            }
        }
        
        dest.species = req.species;
        dest.dataCategory = req.dataCategory;
    }
    
   

    public ExperimentalAssayView assayToView(AssayPack bundle, BioDare2User user) {
        
        ExperimentalAssayView view = new ExperimentalAssayView(bundle.getAssay());
        view.security = securityResolver.permissionsSummary(bundle.getACL(),user);
        return view;
    }

    protected BiologicalDescription makeDefaultBioDescription() {
        BiologicalDescription desc = new BiologicalDescription();
        BiologicalInfoBuilder info = new BiologicalInfoBuilder();
        desc.add(info.build());
        return desc;
    }

    protected Institution instName2Institution(String name) {
        
        Institution inst = new Institution();
        inst.name = name;
        inst.longName = name;
        return inst;
    }

    protected boolean isSuitableLicenceForUser(OpenAccessLicence licence, BioDare2User user) {
        
        return (licence != null && user != null);
    }

    protected OpenAccessInfo makeNewOpenAccessInfo(OpenAccessLicence licence, BioDare2User user) {
        
        OpenAccessInfo info = new OpenAccessInfo();
        info.grantedByLogin = user.getLogin();
        info.grantedByName = user.getName();
        info.grantedOn = LocalDateTime.now();
        info.licence = licence;
        return info;
    }











    
}
