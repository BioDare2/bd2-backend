/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import ed.biodare2.backend.repo.dao.AssayPackAssembler;
import ed.biodare2.backend.repo.dao.SystemInfoRep;
import ed.biodare2.backend.repo.dao.ExperimentalAssayRep;
import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import static ed.biodare2.backend.repo.dao.MockReps.mockDBSystemInfoRep;
import static ed.biodare2.backend.repo.dao.MockReps.mockExperimentAssayRep;
import static ed.biodare2.backend.repo.dao.MockReps.mockSystemInfoRep;
import static ed.biodare2.backend.repo.dao.MockReps.testAssayPack;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import java.util.Optional;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;


/**
 *
 * @author tzielins
 */
public class AssayPackAssemblerTest {
    
    ExperimentalAssayRep experiments;
    SystemInfoRep systemInfos;
    DBSystemInfoRep dbSystemInfos;    
    
    AssayPackAssembler assembler;
    
    AssayPackAssembler.AssayPackImpl testPack;
    
    public AssayPackAssemblerTest() {
    }
    
    @Before
    public void setUp() throws Exception {
        
        testPack = testAssayPack();
        /*testPack.expId = assay.getId();
        testPack.assay = assay;
        testPack.systemInfo = sys;
        testPack.dbSystemInfo = dbSys;*/

        experiments = mockExperimentAssayRep(testPack.getAssay());
        
        systemInfos = mockSystemInfoRep(testPack.getSystemInfo());
        
        dbSystemInfos = mockDBSystemInfoRep(testPack.getDbSystemInfo());
        
        assembler = new AssayPackAssembler(experiments, systemInfos, dbSystemInfos);
    }
    

    @Test
    public void findOneGivesOptionalEmptyOnUnknownId() {
        long expId = 10000;
        assertNotEquals(expId,testPack.expId);
        
        Optional<AssayPack> ans = assembler.findOne(expId);
        assertNotNull(ans);
        assertFalse(ans.isPresent());
    }
    
    @Test
    public void findOneGivesOptionalEmptyOnMissingOneOfThePart() {
        long expId = testPack.expId;
        when(experiments.findOne(eq(expId))).thenReturn(Optional.empty());
        
        Optional<AssayPack> ans = assembler.findOne(expId);
        assertNotNull(ans);
        assertFalse(ans.isPresent());
    }
    
    @Test
    public void findOneAssemblesReadOnlyPack() {
        long expId = testPack.expId;
        Optional<AssayPack> ans = assembler.findOne(expId);
        assertNotNull(ans);
        assertTrue(ans.isPresent());        
        
        AssayPack pack = ans.get();
        assertNotNull(pack.getAssay());
        assertNotNull(pack.getSystemInfo());
        assertNotNull(pack.getDbSystemInfo());
        assertEquals(expId,pack.getId());
        assertTrue(((AssayPackAssembler.AssayPackImpl)pack).readOnly);
        
        verify(experiments).findOne(expId);
        verify(systemInfos).findByParent(expId,EntityType.EXP_ASSAY);
        verify(dbSystemInfos).findByParentIdAndEntityType(expId,EntityType.EXP_ASSAY);
    }
    
    @Test
    public void saveSaves() {
        
        testPack.readOnly = false;
        AssayPack pack = assembler.save(testPack);
        assertNotNull(pack);
        assertSame(pack,testPack);
        assertNotNull(pack.getAssay());
        assertNotNull(pack.getSystemInfo());
        assertNotNull(pack.getDbSystemInfo());
        
        verify(experiments).save(testPack.assay);
        verify(systemInfos).save(testPack.systemInfo);
        verify(dbSystemInfos).save(testPack.dbSystemInfo);
    }  
    
    @Test
    public void saveReturnsReadonlyVersion() {
        
        testPack.readOnly = false;
        AssayPackAssembler.AssayPackImpl pack = (AssayPackAssembler.AssayPackImpl)assembler.save(testPack);
        assertNotNull(pack);
        assertTrue(pack.readOnly);
    }     
    
    @Test
    public void saveThrowsExceptionOnReadOnly() {
        
        testPack.readOnly = true;
        try {
            AssayPack pack = assembler.save(testPack);
            fail("Exception expected on read only pack");
        } catch (IllegalArgumentException e) {};
        
        
        verify(experiments,never()).save(testPack.assay);
        verify(systemInfos,never()).save(testPack.systemInfo);
        verify(dbSystemInfos,never()).save(testPack.dbSystemInfo);
    }    
    
}
