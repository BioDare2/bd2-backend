/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.openaccess.OpenAccessInfo;
import ed.biodare2.backend.repo.isa_dom.openaccess.OpenAccessLicence;
import ed.biodare2.backend.repo.isa_dom.shared.SimpleProvenance;
import ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tzielins
 */
public class BaseExperimentHandlerTest {
    
    public BaseExperimentHandlerTest() {
    }
    BaseExperimentHandler handler;
    
    @Before
    public void setup() {
        handler = new BaseExperimentHandler();
    }

    @Test
    public void copySystemFeaturesSetsFeaturesBasedOnSystemInfo() {
        
        ExperimentalAssay exp = new ExperimentalAssay(12);
        exp.characteristic.hasTSData = false;
        exp.characteristic.hasPPAJobs = true;
        exp.provenance = new SimpleProvenance();
        exp.versionId = "xxx";
        
        SystemInfo info = SystemDomTestBuilder.makeSystemInfo();
        info.experimentCharacteristic.hasTSData = true;
        info.experimentCharacteristic.hasPPAJobs = true;
        info.experimentCharacteristic.hasAttachments = true;
        info.experimentCharacteristic.hasDataFiles = true;
        info.experimentCharacteristic.attachmentsSize = 2;
        info.currentBioVersion = 1;
        info.currentDataVersion = 2;
        info.currentDescVersion = 3;
        
        handler.copySystemFeatures(info, exp);
        assertEquals(true,exp.characteristic.hasTSData);
        assertEquals(true,exp.characteristic.hasPPAJobs);
        assertEquals(true,exp.characteristic.hasTSData);
        assertEquals(true,exp.characteristic.hasAttachments);
        assertEquals(2,exp.characteristic.attachmentsSize);
        
        assertEquals(info.provenance.creation.actorName,exp.provenance.createdBy);
        assertEquals(info.provenance.creation.dateTime,exp.provenance.created);
        assertEquals(info.provenance.lastChange.actorName,exp.provenance.modifiedBy);
        assertEquals(info.provenance.lastChange.dateTime,exp.provenance.modified);
        
        assertEquals(info.getVersionId(),exp.versionId);
        assertFalse(exp.characteristic.isOpenAccess);
        assertNull(exp.characteristic.licence);
        
        info.openAccessInfo = new OpenAccessInfo();
        info.openAccessInfo.licence = OpenAccessLicence.CC_BY;
        handler.copySystemFeatures(info, exp);
        
        assertTrue(exp.characteristic.isOpenAccess);
        assertNotNull(exp.characteristic.licence);
    }
}
