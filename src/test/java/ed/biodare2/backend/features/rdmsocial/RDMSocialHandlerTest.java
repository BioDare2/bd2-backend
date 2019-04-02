/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rdmsocial;

import ed.biodare2.Fixtures;
import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.repo.dao.MockReps.ExperimentPackTestImp;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.features.rdmsocial.dao.RDMAssetsAspectRep;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

/**
 *
 * @author tzielins
 */
public class RDMSocialHandlerTest {
    
    public RDMSocialHandlerTest() {
    }
    
    RDMAssetsAspectRep assetsAspects;
    RDMSocialHandler handler;
    RDMAssetsAspect assetAspect;
    Fixtures fixtures;
    
    @Before
    public void setup() {
        fixtures = Fixtures.build();
        assetsAspects = mock(RDMAssetsAspectRep.class);
        
        assetAspect = new RDMAssetsAspect();
        when(assetsAspects.findByParent(anyLong(), any())).thenReturn(Optional.of(assetAspect));
        when(assetsAspects.save(any())).then(returnsFirstArg()); 
        
        handler = new RDMSocialHandler(assetsAspects);
    }
    
    ExperimentPackTestImp makeAssayPack(long id) {
        ExperimentPackTestImp pack = new ExperimentPackTestImp();
        pack.expId = id;
        
        pack.dbSystemInfo = new DBSystemInfo();

        EntityACL acl = new EntityACL();
        acl.setOwner(fixtures.user1);
        
        pack.dbSystemInfo.setAcl(acl);
        
        pack.assay = DomRepoTestBuilder.makeExperimentalAssay();
        pack.assay.setId(id);
        
        return pack;
    }
    
    
    @Test
    public void isOwnerCorrctlyResolvesOwnerShip() {
        
        ExperimentPackTestImp pack = makeAssayPack(2);
        
        assertTrue(handler.isOwner(fixtures.user1, pack));
        assertFalse(handler.isOwner(fixtures.demoUser, pack));
        
    }
    
    @Test
    public void drawAssetCohortGivesAlwasControlOnControlUser() {
        RDMCohort userCohort = RDMCohort.CONTROL;
        
        for (int i =0;i<10;i++) {
            assertEquals(RDMCohort.CONTROL,handler.drawAssetCohort(userCohort));
        }
    }

    @Test
    public void drawAssetCohortGivesRandomlyStrictOnStrictUser() {
        RDMCohort userCohort = RDMCohort.STRICT;
        
        int stricts = 0;
        int controls = 0;
        for (int i =0;i<10;i++) {
            RDMCohort assetCohort = handler.drawAssetCohort(userCohort);
            if (assetCohort.equals(RDMCohort.STRICT)) stricts++;
            if (assetCohort.equals(RDMCohort.CONTROL)) controls++;
            
        }
        
        assertEquals(10,stricts+controls);
        assertTrue(stricts > 0);
        assertTrue(controls > 0);
    }

    @Test
    public void drawAssetCohortGivesRandomlyAdviceOnStrictUser() {
        RDMCohort userCohort = RDMCohort.ADIVSE;
        
        int adviced = 0;
        int controls = 0;
        for (int i =0;i<10;i++) {
            RDMCohort assetCohort = handler.drawAssetCohort(userCohort);
            if (assetCohort.equals(RDMCohort.ADIVSE)) adviced++;
            if (assetCohort.equals(RDMCohort.CONTROL)) controls++;
            
        }
        
        assertEquals(10,adviced+controls);
        assertTrue(adviced > 0);
        assertTrue(controls > 0);
    }  
    
    @Test
    public void drawUserCohortsGivesRandomValuesFromAllCohorts() {
        List<RDMCohort> cohorts = new ArrayList<>();
        for (int i =0;i<50;i++) {
            cohorts.add(handler.drawUserCohort());
        }
        
        for (RDMCohort cohort : RDMCohort.values()) {
            assertTrue(cohorts.stream().anyMatch( c -> c.equals(cohort)));
        }
    }
    
    @Test
    public void drawUserCohortsGivesControlCohortWithSmallerRatioThenRest() {
        List<RDMCohort> cohorts = new ArrayList<>();
        for (int i =0;i<200;i++) {
            cohorts.add(handler.drawUserCohort());
        }
        
        long controls = cohorts.stream().filter( c -> c.equals(RDMCohort.CONTROL)).count();
        assertTrue(controls > 20);
        
        for (RDMCohort cohort : RDMCohort.values()) {
            if (cohort.equals(RDMCohort.CONTROL)) continue;
            
            long other = cohorts.stream().filter( c-> c.equals(cohort)).count();
            assertTrue(controls < (other-20));
        }
    }
    
    
    @Test
    public void includesMeasuremntCorrectlyAssesTheFact() {
        ExperimentalAssay assay = DomRepoTestBuilder.makeExperimentalAssay();
        assay.experimentalDetails.measurementDesc.technique = "LUC";
        
        assertTrue(handler.includesMeasurment(assay));
        
        assay.experimentalDetails.measurementDesc.technique = "";
        assertFalse(handler.includesMeasurment(assay));
        
        assay.experimentalDetails.measurementDesc.technique = null;
        assertFalse(handler.includesMeasurment(assay));
        
        assay.experimentalDetails.measurementDesc = null;
        assertFalse(handler.includesMeasurment(assay));
        
    }
    
    @Test
    public void canIgnoreMeasurementsIfAlreadyPresent() {
        ExperimentPackTestImp pack = makeAssayPack(3);
        UserAccount user = fixtures.user1;
        
        pack.getAssay().experimentalDetails.measurementDesc.technique = "Luc";
        assertTrue(handler.canIgnoreMeasurement(pack, user));        
    }
    
    @Test
    public void canIgnoreMeasurementsIfUserIsNotOwner() {
        ExperimentPackTestImp pack = makeAssayPack(3);
        UserAccount user = fixtures.demoUser;
        assertFalse(handler.isOwner(user, pack));
        
        pack.getAssay().experimentalDetails.measurementDesc.technique = null;
        assertTrue(handler.canIgnoreMeasurement(pack, user));                        
    }
    
    @Test
    public void canIgnoreMeasurementsIfAspectIsControl() {
        ExperimentPackTestImp pack = makeAssayPack(3);
        UserAccount user = fixtures.user1;
        assertTrue(handler.isOwner(user, pack));
        
        assetAspect.cohort = RDMCohort.CONTROL;
        
        pack.getAssay().experimentalDetails.measurementDesc.technique = null;
        assertTrue(handler.canIgnoreMeasurement(pack, user));                        
    }    
    
    @Test
    public void canIgnoreMeasurementsIfItIsAdviceAndWarningsAreExceded() {
        ExperimentPackTestImp pack = makeAssayPack(3);
        UserAccount user = fixtures.user1;
        assertTrue(handler.isOwner(user, pack));
        
        assetAspect.cohort = RDMCohort.ADIVSE;
        assetAspect.measurementWarnings = handler.MEASUREMENT_WARNINGS_LIMIT;
        
        pack.getAssay().experimentalDetails.measurementDesc.technique = null;
        assertTrue(handler.canIgnoreMeasurement(pack, user));                        
    }    
    
    @Test
    public void canIgnoreMeasurementsIfWarningsAreNotExceded() {
        ExperimentPackTestImp pack = makeAssayPack(3);
        UserAccount user = fixtures.user1;
        assertTrue(handler.isOwner(user, pack));
        
        assetAspect.cohort = RDMCohort.ADIVSE;
        assetAspect.measurementWarnings = handler.MEASUREMENT_WARNINGS_LIMIT-1;
        
        pack.getAssay().experimentalDetails.measurementDesc.technique = null;
        assertTrue(handler.canIgnoreMeasurement(pack, user));                        
        
        assetAspect.cohort = RDMCohort.STRICT;
        assetAspect.measurementWarnings = handler.MEASUREMENT_WARNINGS_LIMIT-1;
        
        pack.getAssay().experimentalDetails.measurementDesc.technique = null;
        assertTrue(handler.canIgnoreMeasurement(pack, user));                        
        
    }  
    
    @Test
    public void cannotIgnoreMeasurementsIfItIsStringAndWarningsAreExceded() {
        ExperimentPackTestImp pack = makeAssayPack(3);
        UserAccount user = fixtures.user1;
        assertTrue(handler.isOwner(user, pack));
        
        assetAspect.cohort = RDMCohort.STRICT;
        assetAspect.measurementWarnings = handler.MEASUREMENT_WARNINGS_LIMIT;
        
        pack.getAssay().experimentalDetails.measurementDesc.technique = null;
        assertFalse(handler.canIgnoreMeasurement(pack, user));                        
    }    
    
    @Test
    public void shouldntShowMeasurementWarningIfMeasurementPresent() {
        ExperimentPackTestImp pack = makeAssayPack(3);
        UserAccount user = fixtures.user1;
        assertTrue(handler.isOwner(user, pack));
        
        assetAspect.cohort = RDMCohort.STRICT;
        assetAspect.measurementWarnings = handler.MEASUREMENT_WARNINGS_LIMIT;
        
        pack.getAssay().experimentalDetails.measurementDesc.technique = "LUC";
        assertFalse(handler.shouldShowMeasurementWarning(pack, user));                                
    }
    
    @Test
    public void shouldntShowMeasurementWarningIfUserNotAnOwner() {
        ExperimentPackTestImp pack = makeAssayPack(3);
        UserAccount user = fixtures.demoUser;
        assertFalse(handler.isOwner(user, pack));
        
        assetAspect.cohort = RDMCohort.STRICT;
        assetAspect.measurementWarnings = handler.MEASUREMENT_WARNINGS_LIMIT;
        
        pack.getAssay().experimentalDetails.measurementDesc.technique = null;
        assertFalse(handler.shouldShowMeasurementWarning(pack, user));                                
    }
    
    @Test
    public void shouldntShowMeasurementWarningIfItIsConntrol() {
        ExperimentPackTestImp pack = makeAssayPack(3);
        UserAccount user = fixtures.user1;
        assertTrue(handler.isOwner(user, pack));
        
        assetAspect.cohort = RDMCohort.CONTROL;
        assetAspect.measurementWarnings = handler.MEASUREMENT_WARNINGS_LIMIT;
        
        pack.getAssay().experimentalDetails.measurementDesc.technique = null;
        assertFalse(handler.shouldShowMeasurementWarning(pack, user));                                
    }  
    
    @Test
    public void shouldntShowMeasurementWarningIfLimitsExceeded() {
        ExperimentPackTestImp pack = makeAssayPack(3);
        UserAccount user = fixtures.user1;
        assertTrue(handler.isOwner(user, pack));
        
        assetAspect.cohort = RDMCohort.ADIVSE;
        assetAspect.measurementWarnings = handler.MEASUREMENT_WARNINGS_LIMIT;
        
        pack.getAssay().experimentalDetails.measurementDesc.technique = null;
        assertFalse(handler.shouldShowMeasurementWarning(pack, user));                                
    }
    
    @Test
    public void shouldShowMeasurementWarningIfBelowLimit() {
        ExperimentPackTestImp pack = makeAssayPack(3);
        UserAccount user = fixtures.user1;
        assertTrue(handler.isOwner(user, pack));
        
        assetAspect.cohort = RDMCohort.ADIVSE;
        assetAspect.measurementWarnings = handler.MEASUREMENT_WARNINGS_LIMIT-1;
        
        pack.getAssay().experimentalDetails.measurementDesc.technique = null;
        assertTrue(handler.shouldShowMeasurementWarning(pack, user));                                
    }    
    
    @Test
    public void registerMeasurementWarningsIncreaseTheCount() {
        
        ExperimentPackTestImp pack = makeAssayPack(3);
        UserAccount user = fixtures.user1;
        
        long prev = assetAspect.measurementWarnings;
        handler.registerMeasurementWarning(pack, user);
        
        assertEquals(prev+1,assetAspect.measurementWarnings);
        verify(assetsAspects).save(assetAspect);
        
        
    }
    
    @Test
    public void registerUpdateIncreasesUpdaesCount() {
        ExperimentPackTestImp pack = makeAssayPack(3);
        UserAccount user = fixtures.user1;
        assetAspect.cohort = RDMCohort.CONTROL;
        
        long prev = assetAspect.updates;
        handler.registerUpdateAssay(pack, user);

        assertEquals(prev+1,assetAspect.updates);
        verify(assetsAspects).save(assetAspect);
        
    }
    
    @Test
    public void registerUpdateDoesNotUpdateMeasurementsPartsOnPresent() {
        ExperimentPackTestImp pack = makeAssayPack(3);
        pack.getAssay().experimentalDetails.measurementDesc.technique="LUC";
        
        UserAccount user = fixtures.user1;
        assetAspect.cohort = RDMCohort.STRICT;
        assetAspect.measurementAdded = true;
        assetAspect.measurementAddedAtUpdate = 2;
        assetAspect.measurementAddedAtWarning = 1;
        assetAspect.measurementWarnings = 2;
        assetAspect.updates = 3;
        
        long prevU = assetAspect.measurementAddedAtUpdate;
        long prevW = assetAspect.measurementAddedAtWarning;
        handler.registerUpdateAssay(pack, user);

        assertEquals(4,assetAspect.updates);
        assertEquals(prevU,assetAspect.measurementAddedAtUpdate);
        assertEquals(prevW,assetAspect.measurementAddedAtWarning);
        verify(assetsAspects).save(assetAspect);
        
    }   
    
    @Test
    public void registerUpdateDoesNotUpdateMeasurementsPartsWhenMissingInUpdate() {
        ExperimentPackTestImp pack = makeAssayPack(3);
        pack.getAssay().experimentalDetails.measurementDesc.technique=null;
        
        UserAccount user = fixtures.user1;
        assetAspect.cohort = RDMCohort.STRICT;
        assetAspect.measurementAdded = false;
        assetAspect.measurementAddedAtUpdate = 2;
        assetAspect.measurementAddedAtWarning = 1;
        assetAspect.measurementWarnings = 2;
        assetAspect.updates = 3;
        
        long prevU = assetAspect.measurementAddedAtUpdate;
        long prevW = assetAspect.measurementAddedAtWarning;
        handler.registerUpdateAssay(pack, user);

        assertEquals(4,assetAspect.updates);
        assertFalse(assetAspect.measurementAdded);
        
        assertEquals(prevU,assetAspect.measurementAddedAtUpdate);
        assertEquals(prevW,assetAspect.measurementAddedAtWarning);
        verify(assetsAspects).save(assetAspect);
        
    }  
    
    @Test
    public void registerUpdateUpdateMeasurementsPartsWhenAddedInUpdate() {
        ExperimentPackTestImp pack = makeAssayPack(3);
        pack.getAssay().experimentalDetails.measurementDesc.technique="Luc";
        
        UserAccount user = fixtures.user1;
        assetAspect.cohort = RDMCohort.STRICT;
        assetAspect.measurementAdded = false;
        assetAspect.measurementAddedAtUpdate = 2;
        assetAspect.measurementAddedAtWarning = 1;
        assetAspect.measurementWarnings = 2;
        assetAspect.updates = 3;
        
        handler.registerUpdateAssay(pack, user);

        assertEquals(4,assetAspect.updates);
        assertTrue(assetAspect.measurementAdded);
        assertEquals(4,assetAspect.measurementAddedAtUpdate);
        assertEquals(2,assetAspect.measurementAddedAtWarning);
        verify(assetsAspects).save(assetAspect);
        
    }     
    
    
    @Test
    public void registerNewCreatesCorrectAspect() {
        ExperimentPackTestImp pack = makeAssayPack(5);
        pack.getAssay().experimentalDetails.measurementDesc.technique="Luc";
        
        UserAccount user = fixtures.user1;
        assetAspect = handler.registerNewAssay(pack, user);

        assertEquals(5,assetAspect.parentId);
        assertEquals(EntityType.EXP_ASSAY,assetAspect.entityType);
        assertTrue(assetAspect.measurementAdded);
        assertNotNull(assetAspect.cohort);
        
        verify(assetsAspects).save(assetAspect);
        
    }  
    
    @Test
    public void createUserAspectAddsAspectToUserAccount() {
        UserAccount acc = new UserAccount();
        
        assertNull(acc.getRdmAspect());
        
        handler.createUserAspect(acc);
        assertNotNull(acc.getRdmAspect());
        assertNotNull(acc.getRdmAspect().getAccount());
        assertNotNull(acc.getRdmAspect().getCohort());
    }
    
    @Test
    public void getAssayGuiAspectsAssemblesAspects() {
        ExperimentPackTestImp pack = makeAssayPack(3);
        UserAccount user = fixtures.user1;
        assertTrue(handler.isOwner(user, pack));
        
        assetAspect.cohort = RDMCohort.ADIVSE;
        assetAspect.measurementWarnings = handler.MEASUREMENT_WARNINGS_LIMIT-1;
        
        pack.getAssay().experimentalDetails.measurementDesc.technique = null;
        
        RDMAssayGUIAspects aspects = handler.getAssayGuiAspects(pack, user);
        assertNotNull(aspects);
        assertTrue(aspects.showMeasurementWarning);
        assertTrue(aspects.canProceedByMeasurement);
      
        assetAspect.cohort = RDMCohort.STRICT;
        assetAspect.measurementWarnings = handler.MEASUREMENT_WARNINGS_LIMIT;        
        pack.getAssay().experimentalDetails.measurementDesc.technique = null;
        
        aspects = handler.getAssayGuiAspects(pack, user);
        assertNotNull(aspects);
        assertFalse(aspects.showMeasurementWarning);
        assertFalse(aspects.canProceedByMeasurement);
        
    }     
}
