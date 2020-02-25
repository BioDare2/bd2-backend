/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import ed.biodare2.Fixtures;
import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.security.dao.db.EntityACL;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.repo.db.dao.db.SearchInfo;
//import ed.biodare2.backend.handlers.ExperimentHandlerTest;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import java.util.Optional;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;

/**
 *
 * @author Zielu
 */
public class MockReps {
    
    public static ExperimentPackHub mockHub() {
        ExperimentPackHub experiments = org.mockito.Mockito.mock(ExperimentPackHub.class);
        when(experiments.findOne(anyLong())).thenReturn(Optional.empty());
        when(experiments.save(any())).then(returnsFirstArg()); 
        when(experiments.enableWriting(any())).then(returnsFirstArg());   
        
        when(experiments.newPack(any(), any(), any())).thenAnswer((InvocationOnMock invocation) -> {
            Object[] args = invocation.getArguments();
            ExperimentalAssay exp = (ExperimentalAssay)args[0];
            SystemInfo info1 = (SystemInfo)args[1];
            EntityACL acl = (EntityACL)args[2];
            DBSystemInfo db = new DBSystemInfo();
            db.setParentId(exp.getId());
            db.setEntityType(EntityType.EXP_ASSAY);
            db.setAcl(acl);
            
            ExperimentPackTestImp p = new ExperimentPackTestImp();
            p.dbSystemInfo = db;
            p.expId = exp.getId();
            p.assay = exp;
            p.systemInfo = info1;
            return p;
        });
        
        return experiments;
    }
    
    public static ExperimentalAssayRep mockExperimentAssayRep(ExperimentalAssay assay) {
        ExperimentalAssayRep experiments = mock(ExperimentalAssayRep.class);
        return configureMock(experiments, assay);

    }
    
    public static ExperimentalAssayRep configureMock(ExperimentalAssayRep experiments,ExperimentalAssay assay) {
        when(experiments.findOne(eq(assay.getId()))).thenReturn(Optional.of(assay));
        when(experiments.findOne(not(eq(assay.getId())))).thenReturn(Optional.empty());        
        when(experiments.save(any())).then(returnsFirstArg()); 
        return experiments;
    }    
    
    public static SystemInfoRep mockSystemInfoRep(SystemInfo sys) {
        
        SystemInfoRep systemInfos = mock( SystemInfoRep.class);
        return configureMock(systemInfos, sys);
    }
    
    public static SystemInfoRep configureMock(SystemInfoRep systemInfos,SystemInfo sys) {
        when(systemInfos.findByParent(eq(sys.parentId), eq(sys.entityType))).thenReturn(Optional.of(sys));
        when(systemInfos.findByParent(not(eq(sys.parentId)), eq(sys.entityType))).thenReturn(Optional.empty());
        when(systemInfos.save(any())).then(returnsFirstArg()); 
        return systemInfos;
    }    
    
    public static DBSystemInfoRep mockDBSystemInfoRep(DBSystemInfo dbSys) {
        
        DBSystemInfoRep dbSystemInfos = mock(DBSystemInfoRep.class);
        return configureMock(dbSystemInfos, dbSys);

    }
    
    public static DBSystemInfoRep configureMock(DBSystemInfoRep dbSystemInfos,DBSystemInfo dbSys) {
        when(dbSystemInfos.findByParentIdAndEntityType(eq(dbSys.getParentId()), eq(dbSys.getEntityType()))).thenReturn(Optional.of(dbSys));
        when(dbSystemInfos.findByParentIdAndEntityType(not(eq(dbSys.getParentId())), eq(dbSys.getEntityType()))).thenReturn(Optional.empty());
        when(dbSystemInfos.save(any(DBSystemInfo.class))).then(returnsFirstArg());
        return dbSystemInfos;
    }
    
    
    public static AssayPackAssembler.AssayPackImpl testAssayPack() {
        
        ExperimentalAssay assay = DomRepoTestBuilder.makeExperimentalAssay();
        SystemInfo sys = SystemDomTestBuilder.makeSystemInfo();
        sys.parentId = assay.getId();
        sys.entityType = EntityType.EXP_ASSAY;
        
        DBSystemInfo dbSys = DBSystemInfo.testInstance(1L);
        dbSys.setParentId(assay.getId());
        dbSys.setEntityType(EntityType.EXP_ASSAY);
        
        SearchInfo search = new SearchInfo();
        dbSys.setSearchInfo(search);
        
        EntityACL acl = EntityACL.testInstance(1L);
        Fixtures f = Fixtures.build();
        acl.setOwner(f.demoUser);
        acl.setCreator(f.demoUser);
        acl.setSuperOwner(f.demoUser);
        
        dbSys.setAcl(acl);
        
        
        AssayPackAssembler.AssayPackImpl testPack = new AssayPackAssembler.AssayPackImpl(assay.getId(), assay, sys, dbSys);        
        return testPack;
    }
    
    public static class ExperimentPackTestImp implements AssayPack {

        public long expId;
        public SystemInfo systemInfo;
        public DBSystemInfo dbSystemInfo;
        public ExperimentalAssay assay;

        @Override
        public long getId() {
            return expId;
        }

        public void setExpId(long expId) {
            this.expId = expId;
        }

        @Override
        public SystemInfo getSystemInfo() {
            return systemInfo;
        }

        public void setSystemInfo(SystemInfo systemInfo) {
            this.systemInfo = systemInfo;
        }

        @Override
        public DBSystemInfo getDbSystemInfo() {
            return dbSystemInfo;
        }

        public void setDbSystemInfo(DBSystemInfo dbSystemInfo) {
            this.dbSystemInfo = dbSystemInfo;
        }

        @Override
        public ExperimentalAssay getAssay() {
            return assay;
        }

        @Override
        public void setAssay(ExperimentalAssay experiment) {
            this.assay = experiment;
        }
        
        
    }
    
    
}
