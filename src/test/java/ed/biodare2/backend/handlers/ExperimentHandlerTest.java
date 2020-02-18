/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.Fixtures;
import ed.biodare2.backend.features.rdmsocial.RDMCohort;
import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.security.PermissionsResolver;
import ed.biodare2.backend.util.concurrent.id.IdGenerator;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.dao.MockReps;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.actors.Person;
import ed.biodare2.backend.repo.isa_dom.biodesc.BiologicalDescription;
import ed.biodare2.backend.repo.isa_dom.biodesc.DataCategory;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalDetails;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.FeaturesAvailability;
import ed.biodare2.backend.repo.system_dom.OperationRecord;
import ed.biodare2.backend.repo.system_dom.OperationType;
import ed.biodare2.backend.repo.system_dom.Provenance;
import ed.biodare2.backend.repo.system_dom.ServiceLevel;
import ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.repo.ui_dom.exp.ExperimentalAssayView;
import ed.biodare2.backend.features.rdmsocial.RDMSocialHandler;
import ed.biodare2.backend.features.search.ExperimentSearcher;
import ed.biodare2.backend.features.search.SortOption;
import ed.biodare2.backend.features.subscriptions.ServiceLevelResolver;
import ed.biodare2.backend.repo.isa_dom.openaccess.OpenAccessInfo;
import ed.biodare2.backend.repo.isa_dom.openaccess.OpenAccessLicence;
import ed.biodare2.backend.repo.isa_dom.shared.SimpleProvenance;
import ed.biodare2.backend.repo.ui_dom.shared.Page;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.web.rest.ListWrapper;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author tzielins
 */
public class ExperimentHandlerTest {
    
    
    public ExperimentHandlerTest() {
    }
    
    RDMSocialHandler rdmSocialHandler;
    ExperimentHandler handler;
    ExperimentPackHub experiments;
    PermissionsResolver securityResolver;
    ServiceLevelResolver serviceLevelResolver;
    ExperimentSearcher searcher;
    
    IdGenerator idGenerator;
    
    UserAccount user;
    
    ExperimentalAssay testExp;
    MockReps.ExperimentPackTestImp testBoundle;
    Fixtures fixtures;
    
    @Before
    public void setUp() throws Exception {
        
        fixtures = Fixtures.build();
        
        user = fixtures.demoUser;
        
        testExp = DomRepoTestBuilder.makeExperimentalAssay();
        
        SystemInfo info = SystemDomTestBuilder.makeSystemInfo();
        info.parentId = testExp.getId();
        info.entityType = EntityType.EXP_ASSAY;

        DBSystemInfo dbSystemInfo = new DBSystemInfo();
        dbSystemInfo.setParentId(testExp.getId());
        dbSystemInfo.setEntityType(EntityType.EXP_ASSAY);
        dbSystemInfo.setAcl(new EntityACL());
        
        {
            EntityACL acl1 = dbSystemInfo.getAcl();
            acl1.setCreator(fixtures.demoUser1);
            acl1.setOwner(fixtures.demoUser1);
            acl1.setSuperOwner(fixtures.demoUser1.getSupervisor());            
        }
        
        testBoundle = new MockReps.ExperimentPackTestImp();
        testBoundle.expId = testExp.getId();
        testBoundle.assay = testExp;
        testBoundle.systemInfo = info;
        testBoundle.dbSystemInfo = dbSystemInfo;        
        
        experiments = MockReps.mockHub();
        
        securityResolver = mock(PermissionsResolver.class);
        EntityACL acl = new EntityACL();
        acl.setCreator(user);
        acl.setOwner(user);
        acl.setSuperOwner(user.getSupervisor());
        when(securityResolver.createNewACL(any())).thenReturn(acl);        
        doCallRealMethod().when(securityResolver).makePublic(any());
        when(securityResolver.canRead(any(), any())).thenReturn(true);
        
        serviceLevelResolver = mock(ServiceLevelResolver.class);
        FeaturesAvailability avability = new FeaturesAvailability();
        when(serviceLevelResolver.buildForExperiment(any())).thenReturn(avability);
        //doCallRealMethod().when(serviceLevelResolver).setServiceForOpen(any());
        
        idGenerator = mock(IdGenerator.class);
        when(idGenerator.next()).thenReturn(5L);
        
        searcher = mock(ExperimentSearcher.class);
        when(searcher.findAllVisible(any(), anyBoolean(), anyInt(), anyInt())).thenReturn(new ListWrapper<>());
        
        // when(searcher.findByOwner(any())).thenReturn(LongStream.empty());
        // when(searcher.findPublic()).thenReturn(LongStream.empty());
        
        
        rdmSocialHandler = mock(RDMSocialHandler.class);
        
        //handler = new ExperimentHandler(experiments,experiments,systemInfos,dbSystemInfos,idGenerator,routes,importHandler,dataHandler,fileAssets,securityResolver);
        handler = new ExperimentHandler(experiments,
                idGenerator,
                searcher,
                securityResolver,
                serviceLevelResolver,
                rdmSocialHandler,
                securityResolver
        );
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void newDraftGivesExpWithUserAsTheAuthor() {
        
        
        ExperimentalAssayView exp = handler.newDraft(user);
        
        assertTrue(exp.contributionDesc.authors.stream()
                        .map(a -> a.login)
                        .anyMatch( log -> log.equals(user.getLogin())));
    }
    
    @Test
    public void newDraftGivesExpWithUserInstitution() {
        
        
        ExperimentalAssayView exp = handler.newDraft(user);
        
        assertTrue(exp.contributionDesc.institutions.stream()
                        .map(i -> i.name)
                        .anyMatch( inst -> inst.equals(user.getInstitution())));
    }
    
    
    
    @Test
    public void newDraftCreatesSubFieldsInExperiment() {
        
        
        ExperimentalAssayView assay = handler.newDraft(user);

        //assertTrue(exp.getId() > 0);
        assertNotNull(assay.generalDesc);
        assertNotNull(assay.contributionDesc);
        assertNotNull(assay.features);
        //assertNotNull(assay.bioDescription);
        //assertNotNull(assay.bioSummary);
        assertNull(assay.species);
        assertNull(assay.dataCategory);
        assertNotNull(assay.security);
        assertNotNull(assay.provenance);
        
        
        ExperimentalDetails exp = assay.experimentalDetails;
        assertNotNull(exp.measurementDesc);
        assertNotNull(exp.experimentalEnvironments);
        assertNotNull(exp.growthEnvironments);
        assertNotNull(exp.measurementDesc);
        assertNotNull(exp.executionDate);
        
        // copies execution date to the view
        assertEquals(exp.executionDate, assay.generalDesc.executionDate);
        
    }
    

    /*
    @Test
    public void saveDoesUpdateOnExistingSystemInfo() throws Exception {
        
        ExperimentalAssay sec = testExp;
        assertFalse(user.getName().equals(sec.provenance.modifiedBy));
        
        ExperimentPackTestImp boundle = testBoundle;
        when(experiments.findOne(eq(sec.getId()))).thenReturn(Optional.of(boundle));
        
        //when(systemInfos.findByParent(anyLong(), any())).thenReturn(Optional.of(info));
        //when(dbSystemInfos.findByParentIdAndEntityType(anyLong(), any())).thenReturn(Optional.of(dbSystemInfo));
        
        ExperimentalAssay res = handler.save(testExp, user);
        
        //verify(experiments).save(eq(testExp));
        //verify(systemInfos).save(any());
        //verify(dbSystemInfos,never()).save(any(DBSystemInfo.class));
        //verify(dbSystemInfos).save(eq(dbSystemInfo));
        
        verify(experiments).findOne(eq(sec.getId()));
        verify(experiments).save(eq(boundle));
        
        assertSame(sec, res);
        assertEquals(res.provenance.modifiedBy,user.getName());
        
    }*/
    
    @Test
    public void listExperimentsUsesTheIdsProvidedBySearcherAndFetchesFromRepo() {
        
        AssayPack p1 = MockReps.testAssayPack();
        AssayPack p2 = MockReps.testAssayPack();
        AssayPack p3 = MockReps.testAssayPack();
        
        // p1.getAssay().provenance.modified = LocalDateTime.now();
        // p2.getAssay().provenance.modified = LocalDateTime.now().minus(2, ChronoUnit.DAYS);
        // p3.getAssay().provenance.modified = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
        
        // when(searcher.findByOwner(any())).thenReturn(Arrays.asList(p1.getId(),p2.getId(),p3.getId()).stream().mapToLong( l -> l.longValue()));
        when(searcher.findAllVisible(user, true,SortOption.MODIFICATION_DATE, false , 0, 10)).thenReturn(new ListWrapper<>(Arrays.asList(p1.getId(),p2.getId(),p3.getId())));        
        when(experiments.findByIds((List<Long>)any())).thenReturn(Arrays.asList(p1,p2,p3).stream());
        
        List<ExperimentalAssay> exp = Arrays.asList(p1.getAssay(),p2.getAssay(),p3.getAssay());
        
        Page page = new Page(0, 10);
        SortOption sorting = SortOption.MODIFICATION_DATE;
        boolean ascending = false;
        List<ExperimentalAssay> res = handler.listExperiments(user,true,sorting, ascending, page).data; 
        
        assertEquals(exp,res);
        verify(searcher).findAllVisible(user, true,SortOption.MODIFICATION_DATE, false , 0, 10);
        verify(experiments).findByIds((List<Long>)any());
        
    }    
    
    @Test
    public void searchExperimentsUsesTheIdsProvidedBySearcherAndFetchesFromRepo() {
        
        AssayPack p1 = MockReps.testAssayPack();
        AssayPack p2 = MockReps.testAssayPack();
        AssayPack p3 = MockReps.testAssayPack();
        
        when(searcher.findVisible("clock", user, true,SortOption.MODIFICATION_DATE, false , 0, 10)).thenReturn(new ListWrapper<>(Arrays.asList(p1.getId(),p2.getId(),p3.getId())));        
        when(experiments.findByIds((List<Long>)any())).thenReturn(Arrays.asList(p1,p2,p3).stream());
        
        List<ExperimentalAssay> exp = Arrays.asList(p1.getAssay(),p2.getAssay(),p3.getAssay());
        
        Page page = new Page(0, 10);
        SortOption sorting = SortOption.MODIFICATION_DATE;
        boolean ascending = false;
        List<ExperimentalAssay> res = handler.searchExperiments("clock", user,true,sorting, ascending, page).data; 
        
        assertEquals(exp,res);
        verify(searcher).findVisible("clock", user, true,SortOption.MODIFICATION_DATE, false , 0, 10);
        verify(experiments).findByIds((List<Long>)any());
        
    }      
    /*
    @Test
    public void listExperimentsSortsThemByModificationDateDesc() {
        
        AssayPack p1 = MockReps.testAssayPack();
        AssayPack p2 = MockReps.testAssayPack();
        AssayPack p3 = MockReps.testAssayPack();
        
        p1.getAssay().provenance.modified = LocalDateTime.now();
        p2.getAssay().provenance.modified = LocalDateTime.now().minus(2, ChronoUnit.DAYS);
        p3.getAssay().provenance.modified = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
        
        // when(searcher.findByOwner(any())).thenReturn(Arrays.asList(p1.getId(),p2.getId(),p3.getId()).stream().mapToLong( l -> l.longValue()));
        when(searcher.findAllVisible(any(), anyBoolean(), anyInt(), anyInt())).thenReturn(new ListWrapper<>(Arrays.asList(p1.getId(),p2.getId(),p3.getId())));        
        when(experiments.findByIds((LongStream)any())).thenReturn(Arrays.asList(p1,p2,p3).stream());
        
        List<ExperimentalAssay> exp = Arrays.asList(p1.getAssay(),p3.getAssay(),p2.getAssay());
        
        Page page = new Page(0, 10);
        List<ExperimentalAssay> res = handler.listExperiments(user,true,page).data; 
        
        assertEquals(exp,res);
        
    }
    */
    
    /*
    @Test
    public void listExperimentsSortsAndAppliesPagination() {
        
        AssayPack p1 = MockReps.testAssayPack();
        AssayPack p2 = MockReps.testAssayPack();
        AssayPack p3 = MockReps.testAssayPack();
        
        p1.getAssay().provenance.modified = LocalDateTime.now();
        p2.getAssay().provenance.modified = LocalDateTime.now().minus(2, ChronoUnit.DAYS);
        p3.getAssay().provenance.modified = LocalDateTime.now().minus(1, ChronoUnit.DAYS);
        
        // when(searcher.findByOwner(any())).thenReturn(Arrays.asList(p1.getId(),p2.getId(),p3.getId()).stream().mapToLong( l -> l.longValue()));
        when(searcher.findAllVisible(any(), anyBoolean(), anyInt(), anyInt())).thenReturn(new ListWrapper<>(Arrays.asList(p1.getId(),p2.getId(),p3.getId())));        
        when(experiments.findByIds((LongStream)any())).thenReturn(Arrays.asList(p1,p2,p3).stream());
        
        Page page = new Page(0,2);
        List<ExperimentalAssay> exp = Arrays.asList(p1.getAssay(),p3.getAssay());
        List<ExperimentalAssay> res = handler.listExperiments(user,true,page).data; 
        assertEquals(exp,res);

        // when(searcher.findByOwner(any())).thenReturn(Arrays.asList(p1.getId(),p2.getId(),p3.getId()).stream().mapToLong( l -> l.longValue()));
        when(experiments.findByIds((LongStream)any())).thenReturn(Arrays.asList(p1,p2,p3).stream());
        
        page = new Page(1,1);
        exp = Arrays.asList(p3.getAssay());
        res = handler.listExperiments(user,true,page).data; //.collect(Collectors.toList());
        assertEquals(exp,res);
        
        // when(searcher.findByOwner(any())).thenReturn(Arrays.asList(p1.getId(),p2.getId(),p3.getId()).stream().mapToLong( l -> l.longValue()));
        when(experiments.findByIds((LongStream)any())).thenReturn(Arrays.asList(p1,p2,p3).stream());
        
        page = new Page(0,10);
        exp = Arrays.asList(p1.getAssay(),p3.getAssay(),p2.getAssay());
        res = handler.listExperiments(user,true,page).data; //.collect(Collectors.toList());
        assertEquals(exp,res);
    } 
    */
    
    
    /*@Test
    public void searchVisibleMergesPublicWithOwned() {
        
        
        List<Long> owned = Arrays.asList(1L, 3L, 5L);
        List<Long> open = Arrays.asList(2L, 3L, 6L);
        
        
        when(searcher.findByOwner(any())).thenReturn(owned.stream().mapToLong(l -> l));
        when(searcher.findPublic()).thenReturn(open.stream().mapToLong(l -> l));

        long[] res = handler.searchVisible(user, true).toArray();
        long[] exp = {1L, 3L, 5L};
        
        assertArrayEquals(exp, res);
        
        when(searcher.findByOwner(any())).thenReturn(owned.stream().mapToLong(l -> l));
        when(searcher.findPublic()).thenReturn(open.stream().mapToLong(l -> l));
        res = handler.searchVisible(user, false).toArray();
        exp = new long[]{1L, 3L, 5L, 2L, 6L};
        
        assertArrayEquals(exp, res);
    }*/    
    
    /*@Test
    public void countExperimentsCountsIdFromSearchStream() {
        
        
        List<Long> owned = Arrays.asList(1L, 3L, 5L);
        List<Long> open = Arrays.asList(2L, 3L);
        
        
        when(searcher.findByOwner(any())).thenReturn(owned.stream().mapToLong(l -> l));
        when(searcher.findPublic()).thenReturn(open.stream().mapToLong(l -> l));

        long res = handler.countExperiments(user, true);
        assertEquals(3, res);
        
        when(searcher.findByOwner(any())).thenReturn(owned.stream().mapToLong(l -> l));
        when(searcher.findPublic()).thenReturn(open.stream().mapToLong(l -> l));
        res = handler.countExperiments(user, false);
        assertEquals(4, res);
    }*/     
    
    @Test
    public void getExperimentGetsExperimentFromRep() {
        
        
        //when(experiments.findOne(eq(1L))).thenReturn(Optional.empty());
        //when(experiments.findOne(eq(2L))).thenReturn(Optional.of(testExp));
        
        when(experiments.findOne(eq(1L))).thenReturn(Optional.empty());
        AssayPack boundle = testBoundle;
        when(experiments.findOne(eq(2L))).thenReturn(Optional.of(boundle));
        
        Optional<AssayPack> res = handler.getExperiment(1L);
        //verify(experiments).findOne(eq(1L));
        verify(experiments).findOne(eq(1L));
        assertFalse(res.isPresent());
        
        res = handler.getExperiment(2L);
        //verify(experiments).findOne(eq(2L));
        verify(experiments).findOne(eq(1L));
        assertSame(boundle,res.get());
    }
    
    
    @Test
    public void account2PersonMakesCorrectPerson() {
        
        Person person = handler.account2Person(user);
        assertNotNull(person);
        assertEquals(user.getId(),person.id);
        assertEquals(user.getLogin(),person.login);
        assertEquals(user.getORCID(),person.ORCID);
        assertEquals(user.getFirstName(),person.firstName);
        assertEquals(user.getLastName(),person.lastName);
        //assertEquals("BioDare2",person.externalService);
        //assertTrue(person.externalPath.endsWith(user.getLogin()));
    }
    
    @Test
    public void insertCreatesDBAndSystemInfoAndSavesAllThree() throws Exception {
        
        ExperimentalAssayView req = DomRepoTestBuilder.makeExperimentalAssayView();
        
        ExperimentalAssayView ans = handler.insert(req, user);
        assertNotNull(ans);
        assertNotNull(ans.id);
        assertTrue(ans.id > 0);
        assertEquals(ans.generalDesc.name,req.generalDesc.name);
        assertSame(ans.experimentalDetails,req.experimentalDetails);
        assertEquals(ans.provenance.createdBy,user.getName());
        assertEquals(ans.provenance.modifiedBy,user.getName());        
        assertSame(ans.species,req.species);
        assertSame(ans.dataCategory,req.dataCategory);
        
        verify(experiments).newPack(any(), any(), any());
        verify(experiments).save(any());
        
        verify(serviceLevelResolver).buildForExperiment(user);
        
        
        verify(rdmSocialHandler).registerNewAssay(any(), any(BioDare2User.class));
        
        //verify(dbSystemInfos).save(any(DBSystemInfo.class));
        //verify(systemInfos).save(any(SystemInfo.class));
        //verify(experiments).save(any(ExperimentalAssay.class));
        
        //assertSame(sec, ans);
        
    }    
    
    @Test
    public void updateUpdatesDetailsUsingRequestsAndSavesThem() throws Exception {
        
        ExperimentalAssay sec = testExp;
        SystemInfo info = SystemDomTestBuilder.makeSystemInfo();
        info.parentId = testExp.getId();
        info.entityType = EntityType.EXP_ASSAY;
        DBSystemInfo dbInfo = SystemDomTestBuilder.makeDBSystemInfo(info);
        
        MockReps.ExperimentPackTestImp boundle = new MockReps.ExperimentPackTestImp();
        boundle.expId = sec.getId();
        boundle.assay = sec;
        boundle.systemInfo = info;
        boundle.dbSystemInfo = dbInfo;
        
        ExperimentalAssayView req = DomRepoTestBuilder.makeExperimentalAssayView();
        req.contributionDesc.authors.get(0).login="updated";
        req.generalDesc.name = "updated name";
        req.experimentalDetails.experimentalEnvironments.environments.get(0).name="updated cond name";
        req.species = "surpries";
        req.dataCategory = DataCategory.METABOLITE;
        assertNotEquals(sec.species,req.species);
        assertNotEquals(sec.dataCategory,req.dataCategory);    
        
        assertNotEquals(sec.contributionDesc.authors.get(0).login, req.contributionDesc.authors.get(0).login);
        assertNotEquals(sec.generalDesc.name,req.generalDesc.name);
        assertNotEquals(sec.experimentalDetails.experimentalEnvironments.environments.get(0).name,req.experimentalDetails.experimentalEnvironments.environments.get(0).name);
        
        ExperimentalAssayView ans = handler.update(boundle,req, user);
        assertNotNull(ans);
        assertEquals(ans.provenance.modifiedBy,user.getName());
        assertEquals(ans.generalDesc.name,req.generalDesc.name);
        
        assertEquals(sec.contributionDesc.authors.get(0).login, req.contributionDesc.authors.get(0).login);
        assertEquals(sec.generalDesc.name,req.generalDesc.name);
        assertEquals(sec.experimentalDetails.experimentalEnvironments.environments.get(0).name,req.experimentalDetails.experimentalEnvironments.environments.get(0).name);
        
        assertSame(ans.species,req.species);
        assertSame(ans.dataCategory,req.dataCategory);        
        //verify(dbSystemInfos).save(any(DBSystemInfo.class));
        //verify(systemInfos).save(any(SystemInfo.class));
        //verify(experiments).save(any(ExperimentalAssay.class));

        verify(experiments).save(eq(boundle));
        verify(rdmSocialHandler).registerUpdateAssay(boundle, user);
        
    }    
    
    /*
    @Test
    public void updateUpdatesSystemInfoAndSavesInfoPlusExp() throws Exception {
        
        ExperimentalAssay sec = testExp;
        SystemInfo info = SystemDomTestBuilder.makeSystemInfo();
        info.parentId = testExp.getId();
        info.entityType = EntityType.EXP_ASSAY;
        
        MockReps.ExperimentPackTestImp boundle = new MockReps.ExperimentPackTestImp();
        boundle.expId = sec.getId();
        boundle.assay = sec;
        boundle.systemInfo = info;
        
        ExperimentalAssay ans = handler.update(sec,boundle, user);
        assertNotNull(ans);
        assertEquals(ans.provenance.modifiedBy,user.getName());
        
        //verify(dbSystemInfos).save(any(DBSystemInfo.class));
        //verify(systemInfos).save(any(SystemInfo.class));
        //verify(experiments).save(any(ExperimentalAssay.class));

        verify(experiments).save(eq(boundle));  
        verify(rdmSocialHandler).registerUpdateAssay(boundle, user);
        assertSame(sec, ans);
        
    }  */  
    

    @Test
    public void createNewACLCallesSecurityResolver() {
        ExperimentalAssay exp = new ExperimentalAssay(testExp.getId()+20);
        EntityACL acl = new EntityACL();
        when(securityResolver.createNewACL(any())).thenReturn(acl);
        
        EntityACL res = handler.createNewACL(user);
        assertSame(acl,res);
    }


    
    @Test
    public void createNewProvenanceCreatesCorrectRecord() {
        
        Provenance prov = handler.createNewProvenance(user, "1.2");
        
        assertNotNull(prov.creation);
        assertEquals(prov.creation.actorLogin,user.getLogin());
        assertEquals(prov.creation.actorName,user.getName());
        assertEquals(prov.creation.operation,OperationType.CREATION);
        assertNotNull(prov.creation.dateTime);
        assertSame(prov.creation,prov.lastChange);
        assertTrue(prov.changes.isEmpty());
    }
    
    @Test
    public void createNewSystemInfoDoesTheJob() {
        ExperimentalAssay exp = new ExperimentalAssay(testExp.getId()+30);
        EntityACL acl = new EntityACL();
        acl.setCreator(user);
        acl.setOwner(user);
        acl.setSuperOwner(user.getSupervisor());
        //DBSystemInfo dbi = new DBSystemInfo();
        //dbi.acl = acl;
        
        SystemInfo info = handler.createNewSystemInfo(exp, acl, user);
        assertNotNull(info);
        assertEquals(exp.getId(),info.parentId);
        assertEquals(EntityType.EXP_ASSAY,info.entityType);
        
        assertEquals(0,info.currentDataVersion);
        assertEquals(1,info.currentDescVersion);
        assertNotNull(info.experimentCharacteristic);
        
        assertNotNull(info.featuresAvailability);
        
        assertNotNull(info.provenance);
        assertEquals(user.getLogin(),info.provenance.creation.actorLogin);
        assertNotNull(info.security);
        assertEquals(user.getLogin(),info.security.creator);
        assertEquals(user.getLogin(),info.security.owner);        
        assertNotNull(info.versionsInfo);
    }
    
    @Test
    public void createNewSystemInfoSetsAvailableFeaturesDependingOnUserSubscription() {
      
        ExperimentalAssay exp = new ExperimentalAssay(testExp.getId()+31);
        EntityACL acl = new EntityACL();
        acl.setCreator(user);
        acl.setOwner(user);
        acl.setSuperOwner(user.getSupervisor());
        
        SystemInfo info = handler.createNewSystemInfo(exp, acl, user);
        assertNotNull(info);
        assertNotNull(info.featuresAvailability);
        
        
        verify(serviceLevelResolver).buildForExperiment(eq(user));
        
        ServiceLevel lev = serviceLevelResolver.buildForExperiment(user).serviceLevel;
        assertEquals(lev,info.featuresAvailability.serviceLevel);
        
    }
    

    @Test
    public void registerExpUpdateUpdatesProvenanceAndVersion() {
        
        SystemInfo info = SystemDomTestBuilder.makeSystemInfo();
        
        OperationRecord last = info.provenance.lastChange;
        long descVer = info.currentDescVersion;
        long dataVer = info.currentDataVersion;
        long bioVer = info.currentBioVersion;
        
        handler.registerExpUpdate(info, user);
        assertEquals(dataVer,info.currentDataVersion);
        assertEquals(bioVer,info.currentBioVersion);
        assertEquals(descVer+1,info.currentDescVersion);
        assertSame(last,info.provenance.changes.get(0));
        
        assertEquals(OperationType.DESC_EDITION,info.provenance.lastChange.operation);
        assertEquals(user.getLogin(), info.provenance.lastChange.actorLogin);
        
    }
    
  
     @Test
    public void mergeRequestsIgnoresNullValues() throws Exception {
        
        ExperimentalAssayView req = new ExperimentalAssayView();
        req.contributionDesc = null;
        req.generalDesc = null;
        req.experimentalDetails = null;
        
        handler.mergeRequest(req, testExp);
        
        assertNotNull(testExp.contributionDesc);
        assertNotNull(testExp.generalDesc);
        assertNotNull(testExp.experimentalDetails);
        
    }    
    
    @Test
    public void mergeRequestsIgnoresSystemGeneratedEntries() throws Exception {
        
        ExperimentalAssayView req = DomRepoTestBuilder.makeExperimentalAssayView();
        
        assertNotSame(req.features, testExp.characteristic);
        assertNotSame(req.provenance, testExp.provenance);
        
        handler.mergeRequest(req, testExp);
        
        assertNotSame(req.features, testExp.characteristic);
        assertNotSame(req.provenance, testExp.provenance);
        
    }    
    
    @Test
    public void mergeRequestMergesParts() throws Exception {
        
        ExperimentalAssayView req = DomRepoTestBuilder.makeExperimentalAssayView();
        
        assertNotSame(req.generalDesc, testExp.generalDesc);
        assertNotSame(req.contributionDesc, testExp.contributionDesc);
        assertNotSame(req.experimentalDetails, testExp.experimentalDetails);
        
        handler.mergeRequest(req, testExp);
        
        assertSame(req.generalDesc, testExp.generalDesc);
        assertSame(req.contributionDesc, testExp.contributionDesc);
        assertSame(req.experimentalDetails, testExp.experimentalDetails);
        
    } 
    
    @Test
    public void mergeRequestCopiesDateFromGeneralDesc() throws Exception {
        
        ExperimentalAssayView req = DomRepoTestBuilder.makeExperimentalAssayView();
        
        assertNotSame(req.generalDesc, testExp.generalDesc);
        assertNotSame(req.contributionDesc, testExp.contributionDesc);
        assertNotSame(req.experimentalDetails, testExp.experimentalDetails);
        
        LocalDate date = LocalDate.now().minus(1, ChronoUnit.DAYS);
        req.generalDesc.executionDate = date;
        testExp.experimentalDetails.executionDate = null;
        
        handler.mergeRequest(req, testExp);
        
        assertSame(req.generalDesc, testExp.generalDesc);
        assertSame(req.contributionDesc, testExp.contributionDesc);
        assertSame(req.experimentalDetails, testExp.experimentalDetails);
        assertSame(date, testExp.experimentalDetails.executionDate);
        
    }    
    
    @Test
    public void makeDefaultBioCreatesBioWithOneEmptyEntry() {
        BiologicalDescription desc = handler.makeDefaultBioDescription();
        assertNotNull(desc);
        assertEquals(1,desc.bios.size());
        desc.bios.forEach( bio -> assertEquals("",bio.genotype));
        
    }
    
    /* BD imports */
    ExperimentalAssay importedAssay() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        
        try {
            return mapper.readValue(testFile("3967.importdsc.json"), ExperimentalAssay.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public File testFile(String name) {
        try { 
            return new File(this.getClass().getResource(name).toURI());
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    } 
    
    @Test
    public void extractVersionWorks() {
        String versionId = "1.0.5";
        int[] res = handler.extractVersions(versionId);
        int[] exp  = {1,0,5};
        assertArrayEquals(exp,res);
    }
    
    @Test
    public void importProvenanceUsersNamesAndDatesFromTheRecord() {
        SimpleProvenance external = importedAssay().provenance;
        
        
        Provenance res = handler.importProvenance(external, user, "1.0.5");
        assertEquals(user.getLogin(),res.creation.actorLogin);
        assertEquals(external.createdBy,res.creation.actorName);
        assertEquals(external.created,res.creation.dateTime);
        assertEquals("0.0.1",res.creation.versionId);
        assertEquals(OperationType.CREATION,res.creation.operation);
        
        assertEquals(user.getLogin(),res.lastChange.actorLogin);
        assertEquals(external.modifiedBy,res.lastChange.actorName);
        assertEquals(external.modified,res.lastChange.dateTime);
        assertEquals("1.0.5",res.lastChange.versionId);
        assertEquals(OperationType.DESC_EDITION,res.lastChange.operation);
        
    }
    
    @Test
    public void importSystemInfoCreatesOneBasedOnTheRecored() {
        
        EntityACL acl = handler.createNewACL(user);
        ExperimentalAssay exp = importedAssay();
        SystemInfo res = handler.importSystemInfo(exp, acl, user);
        
        assertNotNull(res);
        
        assertEquals(exp.getId(),res.parentId);
        assertEquals(EntityType.EXP_ASSAY,res.entityType);
        
        assertEquals(1,res.currentDataVersion);
        assertEquals(5,res.currentDescVersion);
        assertNotNull(res.experimentCharacteristic);
        assertEquals(exp.getId(),res.experimentCharacteristic.biodare1Id);        
        assertNotNull(res.featuresAvailability);
        
        assertEquals(user.getLogin(),res.provenance.creation.actorLogin);
        assertNotNull(res.security);
        assertEquals(user.getLogin(),res.security.creator);
        assertEquals(user.getLogin(),res.security.owner);        
        assertNotNull(res.versionsInfo);        

    }
    
    @Test
    public void importBD1SavesExpBoundle() throws Exception {
        
        ExperimentalAssay exp = importedAssay();
        
        ExperimentalAssayView ans = handler.importBD1(exp, user);
        assertNotNull(ans);
        assertEquals(exp.getId(),ans.id);
        
        assertEquals(ans.generalDesc.name,exp.generalDesc.name);
        assertSame(ans.experimentalDetails,exp.experimentalDetails);
        //assertEquals(ans.provenance.createdBy,user.getName());
        //assertEquals(ans.provenance.modifiedBy,user.getName());        
        assertSame(ans.species,exp.species);
        assertSame(ans.dataCategory,exp.dataCategory);
        
        verify(experiments).newPack(any(), any(), any());
        verify(experiments).save(any());
        
        verify(serviceLevelResolver).buildForExperiment(user);
        
        
        verify(rdmSocialHandler).registerNewAssay(any(), eq(RDMCohort.CONTROL));
        
        
    }    
    
    
    @Test
    public void makesNewOpenAccessInfo() {
        
        OpenAccessInfo info = handler.makeNewOpenAccessInfo(OpenAccessLicence.CC_BY, user);
        
        assertNotNull(info);
        assertSame(OpenAccessLicence.CC_BY,info.licence);
        assertEquals(LocalDate.now(), info.grantedOn.toLocalDate());
        assertEquals(user.getLogin(),info.grantedByLogin);
        
    } 
    
    @Test
    public void markAsPublicUpdatesBothACLs() {
        assertFalse(testBoundle.getACL().isPublic());
        assertFalse(testBoundle.systemInfo.security.isPublic);
        
        handler.markAsPublic(testBoundle);

        assertTrue(testBoundle.getACL().isPublic());
        assertTrue(testBoundle.systemInfo.security.isPublic);
        
    }
    
    @Test
    public void publishMakesExpPublicAndUpdatesOpenAccess() throws Exception {
        
        ExperimentalAssay exp = testExp;
        SystemInfo info = SystemDomTestBuilder.makeSystemInfo();
        info.parentId = testExp.getId();
        info.entityType = EntityType.EXP_ASSAY;
        DBSystemInfo dbInfo = SystemDomTestBuilder.makeDBSystemInfo(info);
        
        MockReps.ExperimentPackTestImp boundle = new MockReps.ExperimentPackTestImp();
        boundle.expId = exp.getId();
        boundle.assay = exp;
        boundle.systemInfo = info;
        boundle.dbSystemInfo = dbInfo;

        assertNull(boundle.systemInfo.openAccessInfo);
        assertFalse(boundle.systemInfo.security.isPublic);
        assertFalse(boundle.getACL().isPublic());
        assertFalse(boundle.getAssay().characteristic.isOpenAccess);
        
        OpenAccessLicence licence = OpenAccessLicence.CC_BY;
        ExperimentalAssayView ans = handler.publish(boundle,OpenAccessLicence.CC_BY, user);
        
        assertNotNull(ans);
        
        assertNotNull(boundle.systemInfo.openAccessInfo);
        assertEquals(licence,boundle.systemInfo.openAccessInfo.licence);
        assertTrue(boundle.systemInfo.security.isPublic);
        assertTrue(boundle.getACL().isPublic());
        
        assertEquals(ans.provenance.modifiedBy,user.getName());
        assertTrue(ans.features.isOpenAccess);

        verify(experiments).save(eq(boundle));
        verify(securityResolver).makePublic(any());
        verify(serviceLevelResolver).setServiceForOpen(any());
        
    }    
    
}
