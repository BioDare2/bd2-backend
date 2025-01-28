/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import ed.biodare2.backend.repo.dao.SystemInfoRep;
import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.backend.handlers.FileUploadHandler;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.repo.system_dom.SystemDomTestBuilder;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.repo.system_dom.SystemInfoTest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.*;

/**
 *
 * @author tzielins
 */
public class SystemInfoRepTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    ExperimentsStorage expStorage;
    SystemInfoRep systems;
    SystemInfo info;
    
    @Before
    public void setUp() throws IOException {
        
        expStorage = mock(ExperimentsStorage.class);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        systems = new SystemInfoRep(expStorage, mapper);
        
        info = SystemDomTestBuilder.makeSystemInfo();
        
    }    

    @Test
    public void saveCreatesNewSystemFileUnderSystemDir() throws Exception {
        
        Path expDir = testFolder.newFolder().toPath();
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);
        
        assertEquals(0L,Files.list(expDir).count());
        
        systems.save(info);
        
        assertEquals(1L,Files.list(expDir).count());
        Path sysDir = expDir.resolve(systems.SYSTEM_DIR);
        assertTrue(Files.isDirectory(sysDir));
        
        assertEquals(1L,Files.list(sysDir).count());
        
        String fName = Files.list(sysDir).findFirst().get().getFileName().toString();
        assertTrue(fName.startsWith(""+info.parentId));
        assertTrue(fName.endsWith(systems.SYSTEM_SUFFIX));
        
    }
    
    @Test
    public void saveMakesBackups() throws Exception {
        
        Path expDir = testFolder.newFolder().toPath();
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);
        
        assertEquals(0L,Files.list(expDir).count());
        
        systems.save(info);
        
        assertEquals(1L,Files.list(expDir).count());
        Path sysDir = expDir.resolve(systems.SYSTEM_DIR);
        assertTrue(Files.isDirectory(sysDir));

        String fName = Files.list(sysDir).findFirst().get().getFileName().toString();
        assertTrue(fName.startsWith(""+info.parentId));
        assertTrue(fName.endsWith(systems.SYSTEM_SUFFIX));

        systems.save(info);
        
        assertEquals(2L,Files.list(sysDir).count());
        
    } 
    
    
    
    @Test
    public void findByParentGivesSavedExpSystemInfo() throws Exception {
        
        Path expDir = testFolder.newFolder().toPath();
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);
        
        assertEquals(0L,Files.list(expDir).count());
        
        systems.save(info);
        
        SystemInfo res = systems.findByParent(info.parentId, EntityType.EXP_ASSAY).get();
        
        SystemInfoTest.checkSame(info, res);
        
    }
    
    
}
