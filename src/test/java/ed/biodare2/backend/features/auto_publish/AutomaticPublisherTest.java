/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package ed.biodare2.backend.features.auto_publish;

import ed.biodare2.Fixtures;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.*;
import org.springframework.data.domain.Limit;

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
        configFile = testFolder.newFile("cuttoff.txt").toPath();
        dbSystemInfos = mock(DBSystemInfoRep.class);
        experiments = mock(ExperimentPackHub.class);
        pubHandler = mock(ExpPublishingHandler.class);
        handler = new AutomaticPublisher(configFile.toString(), dbSystemInfos, experiments, pubHandler);
    }
    
    @After
    public void tearDown() {
    }



    /**
     * Test of getCutoffDate method, of class AutomaticPublisher.
     */
    @Test
    public void readsCutoffDateFromFile() throws IOException {

        Optional<LocalDate> cutoff = handler.getCutoffDate(configFile);
        assertFalse(cutoff.isPresent());
        
        String confText = "PUBLISH_BEFORE: "+LocalDate.now();
        Files.write(configFile, List.of(confText));
        
        cutoff = handler.getCutoffDate(configFile);
        assertEquals(LocalDate.now(), cutoff.get());
        
        confText = "2024-03-05";
        Files.write(configFile, List.of(confText));
        cutoff = handler.getCutoffDate(configFile);
        assertFalse(cutoff.isPresent());
        
    }
    
    @Test
    public void getsExpIdsFromTheRepository() {
        
        LocalDateTime cutoff = LocalDate.now().minusDays(2).atStartOfDay();
        Stream<Long> ids = Stream.of(3L, 5L);
        when(dbSystemInfos.findParentIdsBeforeCutoffAndOpenStatus(EntityType.EXP_ASSAY,cutoff,false,Limit.of(10))).thenReturn(ids);
        
        List<Long> res = handler.getPublishingCandidates(cutoff.toLocalDate(), 10);
        assertEquals(List.of(3L, 5L), res);
        
    }
    
    @Test
    public void doPublishingFindsExperimentAndCallPublisher() {
        
        Fixtures fixtures = Fixtures.build();        
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
    
}
