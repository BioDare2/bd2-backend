/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package ed.biodare2.backend.features.auto_publish;

import ed.biodare2.Fixtures;
import ed.biodare2.backend.features.subscriptions.SubscriptionType;
import static ed.biodare2.backend.features.subscriptions.SubscriptionType.*;
import ed.biodare2.backend.handlers.ExperimentHandler;
import ed.biodare2.backend.repo.dao.ExperimentalAssayRep;
import ed.biodare2.backend.repo.dao.MockReps;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.openaccess.OpenAccessLicence;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder;
import static ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder.emptySystemInfo;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.security.dao.db.UserAccount;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 *
 * @author tzielins
 */
//@Ignore
public class ExpPublishingHandlerTest {
    
    ExpPublishingHandler handler;
    
    ExperimentalAssay testExp;
    MockReps.ExperimentPackTestImp testBoundle;
    Fixtures fixtures;
    BioDare2User user;
    ExperimentHandler experimentHandler;
    UserAccountRep users;
    
    
    public ExpPublishingHandlerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
        
        fixtures = Fixtures.build();
        
        user = fixtures.user1;        
        testExp = DomRepoTestBuilder.makeExperimentalAssay();
        
        SystemInfo info = SystemDomTestBuilder.makeSystemInfo();
        info.parentId = testExp.getId();
        info.entityType = EntityType.EXP_ASSAY;

        DBSystemInfo dbSystemInfo = emptySystemInfo(testExp.getId());
        dbSystemInfo.setEntityType(EntityType.EXP_ASSAY);
        dbSystemInfo.setAcl(new EntityACL());
        dbSystemInfo.setReleaseDate(info.featuresAvailability.releaseDate);
        
        {
            EntityACL acl1 = dbSystemInfo.getAcl();
            acl1.setCreator(user);
            acl1.setOwner(user);
            acl1.setSuperOwner(user.getSupervisor());            
        }
        
        testBoundle = new MockReps.ExperimentPackTestImp();
        testBoundle.expId = testExp.getId();
        testBoundle.assay = testExp;
        testBoundle.systemInfo = info;
        testBoundle.dbSystemInfo = dbSystemInfo;        
        
        experimentHandler = mock(ExperimentHandler.class);
        
        users = mock(UserAccountRep.class);
        UserAccount system = fixtures.systemUser;
        when(users.findByLogin(eq("system"))).thenReturn(Optional.of(system));
        
        
        //handler = new ExperimentHandler(experiments,experiments,systemInfos,dbSystemInfos,idGenerator,routes,importHandler,dataHandler,fileAssets,securityResolver);
        handler = new ExpPublishingHandler(experimentHandler, users);
    }
    
    @After
    public void tearDown() {
    }

    

    
    


    
    @Test
    public void notSuitableForPublishingWithYoungerEmbargo() {
        
        LocalDate cutOff = LocalDate.now().minusDays(2);
        AssayPack exp = testBoundle;
        exp.getDbSystemInfo().setReleaseDate(LocalDate.now());
        assertFalse(handler.isSuitableForPublishing(exp, cutOff));

        
    }    
    
    @Test
    public void suitableForPublishingWithOlderEmbargos() {
        
        LocalDate cutOff = LocalDate.now().minusDays(2);
        AssayPack exp = testBoundle;
        exp.getDbSystemInfo().setReleaseDate(LocalDate.now().minusDays(6));
        assertTrue(handler.isSuitableForPublishing(exp, cutOff));
       
    }    
    
  
    
    @Test
    public void addsComment() {
        AssayPack exp = testBoundle;
        exp.getAssay().generalDesc.comments = null;

        handler.addPublishingComment(exp);      
        assertEquals("Automatically published by BioDare2 system on "+LocalDate.now(),exp.getAssay().generalDesc.comments);
        
        exp.getAssay().generalDesc.comments = "Text";
        handler.addPublishingComment(exp);      
        assertEquals("Text\n     Automatically published by BioDare2 system on "+LocalDate.now(),exp.getAssay().generalDesc.comments);
        
    }
    
    @Test
    public void getsSystemUser() {
        
        
        BioDare2User user = handler.getSystemUser();
        assertEquals("system", user.getLogin());
    }
    
    @Test
    public void attemptAutoPublishingUsesHandlerForPublishing() {

        LocalDate cutOff = LocalDateTime.now().minusDays(2).toLocalDate();
        
        AssayPack exp = testBoundle;
        exp.getDbSystemInfo().setReleaseDate(LocalDate.now());     
        assertFalse(handler.attemptAutoPublishing(exp, cutOff));
        verify(experimentHandler, never()).publish(exp, OpenAccessLicence.CC_BY, fixtures.systemUser);

        exp.getDbSystemInfo().setReleaseDate(LocalDate.now().minusDays(5));     
        assertTrue(handler.attemptAutoPublishing(exp, cutOff));
        verify(experimentHandler).publish(exp, OpenAccessLicence.CC_BY, fixtures.systemUser);
        
    }
    
    
}
