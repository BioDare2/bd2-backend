/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.backend.features.search.ExperimentIndexer;
import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.repo.db.dao.db.DBSystemInfo;
import ed.biodare2.backend.repo.dao.AssayPackAssembler.AssayPackImpl;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.AdditionalMatchers.and;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Zielu
 */
public class ExperimentPackHubTest {
    
    AssayPackAssembler assembler;
    DBSystemInfoRep dbSysInfos;
    ExperimentIndexer indexer;
    
    ExperimentPackHub hub;
    
    AssayPackImpl testPack;
    
    public ExperimentPackHubTest() {
    }
    
    @Before
    public void setUp() {
        
        testPack = MockReps.testAssayPack();
        assembler = mock(AssayPackAssembler.class);
        when(assembler.findOne(eq(testPack.expId))).thenReturn(Optional.of(testPack));
        when(assembler.findOne(not(eq(testPack.expId)))).thenReturn(Optional.empty());
        when(assembler.save(any())).then(returnsFirstArg());
        
        dbSysInfos = mock(DBSystemInfoRep.class);
        when(dbSysInfos.findById(anyLong())).thenReturn(Optional.of(new DBSystemInfo()));
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();        
        SystemCopier copier = new SystemCopier(dbSysInfos, mapper);
        
        indexer = mock(ExperimentIndexer.class);
        hub = new ExperimentPackHub(assembler,copier, indexer);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void findOneForWritingReturnsPackWriteEnabled() {
        
        Optional<AssayPack> ans = hub.findOneForWriting(testPack.getId());
        assertTrue(ans.isPresent());
        
        AssayPackImpl pack = (AssayPackImpl)ans.get();
        assertFalse(pack.readOnly);
    }
    
    @Test
    public void findOneForWritingReturnsEmptyOnMissing() {
        
        long id = 10000;
        assertNotEquals(id,testPack.getId());
        
        Optional<AssayPack> ans = hub.findOneForWriting(id);
        assertFalse(ans.isPresent());
        
    }
    
    @Test
    public void findOneForWritingReturnsCopyFromAssembly() {
        
        Optional<AssayPack> ans = hub.findOneForWriting(testPack.getId());
        assertTrue(ans.isPresent());
        
        AssayPackImpl pack = (AssayPackImpl)ans.get();
        assertNotSame(pack,testPack);
    }
    
    @Test
    public void enableWritingMakesCopyAndSetsReadonlyToFalse() {
        testPack.readOnly = true;
        
        AssayPackImpl pack = (AssayPackImpl)hub.enableWriting(testPack);
        assertFalse(pack.readOnly);
        assertNotSame(pack,testPack);
        assertNotSame(pack.assay,testPack.assay);
        assertNotSame(pack.systemInfo,testPack.systemInfo);
        assertNotSame(pack.dbSystemInfo,testPack.dbSystemInfo);
        assertEquals(pack.expId,testPack.expId);
        
    }
    
    @Test
    public void findOneFinds() {
        
        Optional<AssayPack> ans = hub.findOne(testPack.getId());
        assertTrue(ans.isPresent());
        
        AssayPackImpl pack = (AssayPackImpl)ans.get();
        assertSame(pack,testPack);
        
    }
    
    @Test
    public void findOneReturnsEmptyOnMissing() {
        
        long id = 10000;
        assertNotEquals(id,testPack.getId());
        
        Optional<AssayPack> ans = hub.findOne(id);
        assertFalse(ans.isPresent());
        
    }
    
    @Test
    public void findByIdsFindsMatching() {
        long missing = 10000;
        AssayPackImpl other = MockReps.testAssayPack();
        assertNotEquals(other.getId(),testPack.getId());
        assertNotEquals(missing,testPack.getId());
        assertNotEquals(missing,other.getId());
        
        when(assembler.findOne(eq(other.getId()))).thenReturn(Optional.of(other));
        when(assembler.findOne(and(not(eq(testPack.getId())),not(eq(other.getId()))))).thenReturn(Optional.empty());
        
        List<Long> ids = Arrays.asList(missing,
                                        testPack.getId(),
                                        missing,missing,
                                        testPack.getId(),
                                        other.getId(),
                                        other.getId());
        
        List<AssayPack> packs = hub.findByIds(ids).collect(Collectors.toList());
        List<AssayPack> exp = Arrays.asList(testPack,testPack,other,other);
        
        assertEquals(exp,packs);
    }
    
    @Test
    public void saveSavesAndIdexes() {
        
        AssayPack pack = hub.save(testPack);
        assertSame(pack,testPack);
        verify(indexer).updateSearchInfo(pack);
        verify(indexer).indexExperiment(pack);
        verify(assembler).save(testPack);
    }
    
    @Test
    public void newPackMakesWritablePack() {
        
        AssayPackImpl pack = (AssayPackImpl) hub.newPack(testPack.getAssay(), testPack.getSystemInfo(), testPack.getDbSystemInfo().getAcl());
        assertNotNull(pack);
        assertFalse(pack.readOnly);
        assertSame(pack.assay,testPack.assay);
        assertSame(pack.systemInfo,testPack.systemInfo);
        assertNotNull(pack.dbSystemInfo);
        assertSame(pack.dbSystemInfo.getAcl(),testPack.getDbSystemInfo().getAcl());
    }
    
    @Test
    public void newPackSetsExpIdOnSystemAndSuch() {
        
        testPack.expId = 1;
        testPack.systemInfo.parentId = 2;
        testPack.dbSystemInfo.setParentId(3);
        testPack.assay.setId(4);
        
        AssayPackImpl pack = (AssayPackImpl) hub.newPack(testPack.getAssay(), testPack.getSystemInfo(), testPack.getDbSystemInfo().getAcl());
        assertNotNull(pack);
        assertEquals(pack.expId,testPack.assay.getId());
        assertEquals(pack.assay.getId(),testPack.assay.getId());
        assertEquals(pack.systemInfo.parentId,testPack.assay.getId());
        assertEquals(pack.dbSystemInfo.getParentId(),testPack.assay.getId());
        
    }  
    
    @Test
    public void newPackAddsSearchInfo() {
        
        AssayPackImpl pack = (AssayPackImpl) hub.newPack(testPack.getAssay(), testPack.getSystemInfo(), testPack.getDbSystemInfo().getAcl());
        assertNotNull(pack);
        assertNotNull(pack.dbSystemInfo);
        assertNotNull(pack.dbSystemInfo.getSearchInfo());
    }     
    
    
}
