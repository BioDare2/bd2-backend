/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.system_dom;

import ed.biodare2.backend.repo.system_dom.Provenance;
import ed.biodare2.backend.repo.system_dom.OperationRecord;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.repo.system_dom.OperationType;
import ed.biodare2.backend.repo.system_dom.VersionsInfo;
import ed.biodare2.backend.repo.system_dom.VersionRecord;
import ed.biodare2.backend.repo.system_dom.ACLInfo;
import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentCharacteristic;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author tzielins
 */
public class SystemDomTestBuilder {

    static AtomicLong ids = new AtomicLong(1);
    
    public static SystemInfo makeSystemInfo() {
        
        SystemInfo info = new SystemInfo();
        info.parentId = ids.incrementAndGet();
        info.entityType = EntityType.EXP_ASSAY;
        info.security = makeACLInfo();
        info.provenance = makeProvenance();
        info.currentDescVersion = 2;
        info.currentDataVersion = 1;
        info.experimentCharacteristic = makeExperimentCharacteristic();
        info.versionsInfo = makeVersionsInfo();
        info.featuresAvailability = makeFeaturesAvailability();
        
        return info;
    }

    public static ACLInfo makeACLInfo() {
        ACLInfo info = new ACLInfo();
        info.creator = "user";
        info.owner = "user";
        info.superOwner = "pi";
        info.allowedToRead.add("public");
        info.allowedToRead.add("pi_group");
        info.allowedToWrite.add("pi_group");
        return info;
    }

    public static Provenance makeProvenance() {
        Provenance pro = new Provenance();
        pro.creation = makeOperationRecord(OperationType.CREATION,"user",LocalDateTime.now().minus(1, ChronoUnit.DAYS));
        pro.lastChange = makeOperationRecord(OperationType.DATA_UPLOAD,"pi",LocalDateTime.now());
        pro.changes.add(makeOperationRecord(OperationType.DATA_UPLOAD,"user",LocalDateTime.now().minus(5, ChronoUnit.HOURS)));
        return pro;
    }

    public static OperationRecord makeOperationRecord(OperationType operation,String login,LocalDateTime dateTime) {
        OperationRecord rec = new OperationRecord();
        rec.actorLogin = login;
        rec.actorName = "User: "+login;
        rec.dateTime = dateTime;
        rec.operation = operation;
        rec.parameters.set("user",login);
        return rec;
    }

    public static VersionsInfo makeVersionsInfo() {
        
        VersionsInfo info = new VersionsInfo();
        info.versions.add(makeVersionRecord(2,1));
        return info;
    }

    public static VersionRecord makeVersionRecord(long descVersion,long dataVersion) {
        VersionRecord rec = new VersionRecord();
        rec.descVersion = descVersion;
        rec.dataVersion = dataVersion;
        rec.parameters.set("modified","user");
        rec.descriptionCoordinates = "file:x"+descVersion;
        rec.dataCoordinates = "file:y"+dataVersion;
        rec.filesCoordinates.add("file:x"+descVersion+"."+dataVersion);
        return rec;
    }

    protected static ExperimentCharacteristic makeExperimentCharacteristic() {
        
        ExperimentCharacteristic feat = new ExperimentCharacteristic();
        feat.hasDataFiles = true;
        feat.hasTSData = true;
        feat.hasPPAJobs = false;
        return feat;
    }

    public static DBSystemInfo makeDBSystemInfo(SystemInfo sys) {
        DBSystemInfo db = new DBSystemInfo();
        db.setParentId(sys.parentId);
        db.setEntityType(sys.entityType);
        db.setAcl(makeDBACL(sys.security));
        return db;
    }

    public static EntityACL makeDBACL(ACLInfo sys) {
        EntityACL acl = new EntityACL();
        UserAccount u = new UserAccount();
        u.setLogin(sys.owner);
        acl.setOwner(u);
        acl.setCreator(u);
        u = new UserAccount();
        u.setLogin(sys.superOwner);        
        acl.setSuperOwner(u);
        return acl;
    }

    public static FeaturesAvailability makeFeaturesAvailability() {
        FeaturesAvailability f = new FeaturesAvailability();
        f.serviceLevel = ServiceLevel.BASIC;
        return f;
    }
    
}
