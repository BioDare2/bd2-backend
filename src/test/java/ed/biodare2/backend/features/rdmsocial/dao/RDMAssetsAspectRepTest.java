/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rdmsocial.dao;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static ed.biodare2.BioDare2TestUtils.assertFieldsEquals;
import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import ed.biodare2.backend.repo.system_dom.EntityType;
import ed.biodare2.backend.features.rdmsocial.RDMCohort;
import ed.biodare2.backend.features.rdmsocial.RDMAssetsAspect;
import static ed.biodare2.backend.features.rdmsocial.dao.RDMAssetsAspectRep.RDM_GUI_DIR;
import static ed.biodare2.backend.features.rdmsocial.dao.RDMAssetsAspectRep.RDM_GUI_FILE;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Zielu
 */
public class RDMAssetsAspectRepTest {
    
    public RDMAssetsAspectRepTest() {
    }

    @TempDir
    Path testFolder;
    
    RDMAssetsAspectRep rep;
    
    ExperimentsStorage expStorage;
    
    Path expDir;
    
    ObjectMapper mapper;
    
    
    @BeforeEach
    public void setUp() throws IOException {
        
        expDir = testFolder.resolve("test");
        
        expStorage = mock(ExperimentsStorage.class);
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);

	ObjectMapper mapper = JsonMapper.builder().build();
       
        rep = new RDMAssetsAspectRep(expStorage, mapper);
    }

    @Test
    public void findByParentGivesEmptyOnUnknown() {
        
        long parentId = 1;
        EntityType type = EntityType.EXP_ASSAY;
        
        Optional<RDMAssetsAspect> o = rep.findByParent(parentId,type);
        assertFalse(o.isPresent());
        
        //RDMAssetsAspect aspect = o.get();
        //assertEquals(RDMCohort.CONTROL,aspect.cohort);
    }
    
    @Test
    public void saveSavesInCorrectFile() {
        RDMAssetsAspect aspect = new RDMAssetsAspect();
        aspect.cohort = RDMCohort.ADIVSE;
        aspect.entityType = EntityType.EXP_ASSAY;
        aspect.parentId = 2;
        
        Path file = expDir.resolve(RDM_GUI_DIR).resolve(RDM_GUI_FILE);
        assertFalse(Files.exists(file));
        
        rep.save(aspect);
        assertTrue(Files.exists(file));
    }
    
    @Test
    public void findsWhatWasSaved() {
        RDMAssetsAspect aspect = new RDMAssetsAspect();
        aspect.cohort = RDMCohort.ADIVSE;
        aspect.entityType = EntityType.EXP_ASSAY;
        aspect.parentId = 2;
        aspect.measurementWarnings = 1;
        
        rep.save(aspect);
        
        RDMAssetsAspect res = rep.findByParent(aspect.parentId, aspect.entityType).get();
        
        assertFieldsEquals(aspect,res);
    }
}
