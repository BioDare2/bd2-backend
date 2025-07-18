/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package ed.biodare2.backend.features.auto_publish;

import ed.biodare2.Fixtures;
import static ed.biodare2.backend.features.auto_publish.AutomaticPublisher.CUTOFF_PREFIX;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.dao.MockReps;
import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder;
import static ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder.emptySystemInfo;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.security.dao.db.UserAccount;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.*;
import org.springframework.data.domain.Limit;
import ed.biodare2.backend.features.subscriptions.SubscriptionType;
import ed.biodare2.backend.security.dao.UserAccountRep;

/**
 *
 * @author tzielins
 */
//@Ignore
public class AutomaticPublisherTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
        
    AutomaticPublisher handler;
    Path configFile;
    DBSystemInfoRep dbSystemInfos;
    ExpPublishingHandler pubHandler;
    ExperimentPackHub experiments;
    UserAccountRep users;
    Fixtures fixtures;
    
    public AutomaticPublisherTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        

    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws IOException {
        fixtures = Fixtures.build();
        
        configFile = testFolder.newFile("cuttoff.txt").toPath();
        dbSystemInfos = mock(DBSystemInfoRep.class);
        experiments = mock(ExperimentPackHub.class);
        pubHandler = mock(ExpPublishingHandler.class);
        users = mock(UserAccountRep.class);
        when(users.findBySubscriptionKind(SubscriptionType.EMBARGO_10)).thenReturn(List.of());
        
        handler = new AutomaticPublisher(configFile.toString(), dbSystemInfos, experiments, pubHandler, users);
        
        
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void getsExpIdsFromTheRepository() {
        
        LocalDate cutoff = LocalDate.now().minusDays(2);
        List<Long> ids = List.of(3L, 5L);
        when(dbSystemInfos.findParentIdsWithReleaseBeforeCutoffAndOpenStatus(EntityType.EXP_ASSAY,cutoff,false,Limit.of(10))).thenReturn(ids);
        
        List<Long> res = handler.getPublishingCandidates(cutoff, 10);
        assertEquals(List.of(3L, 5L), res);
        
    }
    
    @Test
    public void doPublishingFindsExperimentAndCallPublisher() {
        
                
        UserAccount user = fixtures.demoUser;         
        ExperimentalAssay testExp = DomRepoTestBuilder.makeExperimentalAssay();
        
        SystemInfo info = SystemDomTestBuilder.makeSystemInfo();
        info.parentId = testExp.getId();
        info.entityType = EntityType.EXP_ASSAY;

        DBSystemInfo dbSystemInfo = emptySystemInfo(testExp.getId());
        dbSystemInfo.setEntityType(EntityType.EXP_ASSAY);
        dbSystemInfo.setAcl(new EntityACL());
        
        {
            EntityACL acl1 = dbSystemInfo.getAcl();
            acl1.setCreator(user);
            acl1.setOwner(user);
            acl1.setSuperOwner(user.getSupervisor());            
        }
        
        MockReps.ExperimentPackTestImp testBoundle = new MockReps.ExperimentPackTestImp();
        testBoundle.expId = testExp.getId();
        testBoundle.assay = testExp;
        testBoundle.systemInfo = info;
        testBoundle.dbSystemInfo = dbSystemInfo;         
        
        Long expId = testExp.getId();
        LocalDate cutoff = LocalDate.now().plusDays(2);
        
        when(experiments.findOne(testExp.getId())).thenReturn(Optional.of(testBoundle));
        when(pubHandler.attemptAutoPublishing(testBoundle, cutoff)).thenReturn(true);
        
        handler.doPublishing(expId, cutoff);
        
        verify(experiments).findOne(testExp.getId());
        verify(pubHandler).attemptAutoPublishing(testBoundle, cutoff);
    }
    
    @Test
    public void updateBatchSizeIncreasesIfMoreThanHalfAreIgnored() {
    
        handler.batchSize = 6;
        int ignored = 2;
        
        handler.updateBatchSize(ignored);
        assertEquals(6, handler.batchSize);
        
        ignored = 4;
        handler.updateBatchSize(ignored);
        assertEquals(12, handler.batchSize);
                
    }
    
    @Test
    public void updateBatchSizeReducesToStartSizeIfLittleIgnored() {
    
        handler.batchSize = 12;
        int ignored = 3;
        
        handler.updateBatchSize(ignored);
        assertEquals(12, handler.batchSize);
        
        ignored = 1;
        handler.updateBatchSize(ignored);
        assertEquals(12, handler.batchSize);
        
        ignored = 1;
        handler.batchSize = AutomaticPublisher.START_BATCH_SIZE+100;
        handler.updateBatchSize(ignored);        
        assertEquals(AutomaticPublisher.START_BATCH_SIZE, handler.batchSize);
                
    } 
    
    @Test
    public void getEmbargoUsers() 
    {
        when(users.findBySubscriptionKind(SubscriptionType.EMBARGO_10)).thenReturn(List.of(fixtures.demoUser));
        when(users.findBySubscriptionKind(SubscriptionType.EMBARGO_06)).thenReturn(List.of(fixtures.demoBoss));
        
        List<String> res = handler.getEmbargoUsers();
        assertEquals(List.of(fixtures.demoUser.getLogin(),fixtures.demoBoss.getLogin()), res);
    }
}
