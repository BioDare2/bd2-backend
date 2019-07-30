/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.shared.SimpleProvenance;
import ed.biodare2.backend.repo.system_dom.OperationRecord;
import ed.biodare2.backend.repo.system_dom.OperationType;
import ed.biodare2.backend.repo.system_dom.Provenance;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import java.time.LocalDateTime;

/**
 *
 * @author tzielins
 */
class BaseExperimentHandler {
    
    protected void updateProvenance(Provenance prov,BioDare2User user,OperationType operation,String versionId) {
        
        OperationRecord mod = new OperationRecord();
        mod.actorLogin = user.getLogin();
        mod.actorName = user.getName();
        mod.dateTime = LocalDateTime.now();
        mod.operation = operation;
        mod.versionId = versionId;
        
        prov.changes.add(0, prov.lastChange);
        prov.lastChange = mod;        
    }
    
    protected void copySystemFeatures(SystemInfo systemInfo, ExperimentalAssay exp) {
        
        exp.characteristic.hasPPAJobs = systemInfo.experimentCharacteristic.hasPPAJobs;
        exp.characteristic.hasTSData = systemInfo.experimentCharacteristic.hasTSData;
        exp.characteristic.hasDataFiles = systemInfo.experimentCharacteristic.hasDataFiles;
        exp.characteristic.hasAttachments = systemInfo.experimentCharacteristic.hasAttachments;
        exp.characteristic.attachmentsSize = systemInfo.experimentCharacteristic.attachmentsSize;
        exp.characteristic.hasRhythmicityJobs = systemInfo.experimentCharacteristic.hasRhythmicityJobs;
        
        exp.provenance = convertProvenance(systemInfo.provenance);
        exp.versionId = systemInfo.getVersionId();
        
        if (systemInfo.openAccessInfo != null && systemInfo.openAccessInfo.licence != null) {
            exp.characteristic.isOpenAccess = true;
            exp.characteristic.licence = systemInfo.openAccessInfo.licence;
        }
    }
    
    protected SimpleProvenance convertProvenance(Provenance provenance) {
        SimpleProvenance simp = new SimpleProvenance();
        simp.created = provenance.creation.dateTime;
        simp.createdBy = provenance.creation.actorName;
        simp.modified = provenance.lastChange.dateTime;
        simp.modifiedBy = provenance.lastChange.actorName;
        return simp;
    }    
    
}
