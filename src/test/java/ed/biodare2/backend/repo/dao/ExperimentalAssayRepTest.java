/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import ed.biodare2.backend.repo.dao.ExperimentalAssayRep;
import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.EnvironmentVariables;
import ed.biodare2.MockEnvironmentVariables;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author tzielins
 */
public class ExperimentalAssayRepTest {
    
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    Path bdStorageDir;
    Path experimentsDir;
    ExperimentalAssayRep experiments;
    ObjectMapper mapper;
    ExperimentsStorage expStorage;    
    
    ExperimentalAssay exp;
    
    public ExperimentalAssayRepTest() {
    }
    
    @Before
    public void setUp() throws IOException {
        bdStorageDir = testFolder.newFolder().toPath();
        
        MockEnvironmentVariables var = new MockEnvironmentVariables();
        var.storageDir = bdStorageDir.toString();
        
        EnvironmentVariables env = var.mock(); //new EnvironmentVariables(bdStorageDir.toString(),"http://localhost","http://localhost:8084/JobCenter/PPAJobCenterWS?wsdl","","","","","");                
        expStorage = new ExperimentsStorage(env);         
        
        experimentsDir = expStorage.getExperimentsDir();
        
        //Files.createDirectories(experimentsDir);
        
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();  
        
        exp = DomRepoTestBuilder.makeExperimentalAssay();
        
        
        experiments = new ExperimentalAssayRep(expStorage,mapper);
        
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void getExperimentsIdsRetrievesIdsFromFoldersNames() throws Exception {
        Set<Long> ids = new HashSet<>(Arrays.asList(1L,12L,15L));
        for (long id : ids)
            Files.createDirectories(experimentsDir.resolve(""+id));
        
        Set<Long> res = experiments.getExerimentsIds().collect(Collectors.toSet());
        assertEquals(ids,res);
    }
    
    @Test
    public void extractIdConvertsFileNameToId() {
        Path p = Paths.get("D:/Temp/1001");
        assertEquals(1001,experiments.extractId(p));
    }
    
    @Test
    public void getExperimentalAssayDirGivesAssaySubfolder() {
        Path p = Paths.get("D:/Temp/cos");
        Path exp = p.resolve("ASSAY");
        assertEquals(exp,experiments.getExperimentalAssayDir(p));
    }
    
    @Test
    public void getExperimentalAssayFileGivesFileFromSubfolderWithIdJson() {
        long id = 12;
        
        Path dir = experimentsDir.resolve(""+id);
        
        Path res = experiments.getExperimentalAssayFile(dir,id);
        Path exp = dir.resolve("ASSAY/"+id+".json");
        assertEquals(exp,res);        
    }    
    
   
    @Test
    public void findOneGivesEmptyOptionalOnWrongId() {
        
        long id = -1;
        
        assertFalse(experiments.findOne(id).isPresent());        
    }
    
    
    @Test
    public void saveCreatesNewFileRecordOfExperiment() throws IOException {
        
        assertFalse(Files.list(experimentsDir).findAny().isPresent());
        
        experiments.save(exp);
        
        Path expDir = Files.list(experimentsDir).findFirst().get();
        
        Path file = experiments.getExperimentalAssayFile(expDir, exp.getId());
        
        assertTrue(Files.isRegularFile(file));
        assertTrue(file.toString().contains("ASSAY"));
    }
    
    @Test
    public void canFindOneAfterSavingIt() {
        assertFalse(experiments.findOne(exp.getId()).isPresent());
        
        experiments.save(exp);
        
        ExperimentalAssay res = experiments.findOne(exp.getId()).get();
        //assertTrue(exp.hasSameValues(res));
        assertEquals(exp.getId(),res.getId());
    }
    
    @Test
    public void saveCanOvewrite() {
        assertFalse(experiments.findOne(exp.getId()).isPresent());
        
        experiments.save(exp);
        
        exp.generalDesc.name = "Cos1111";
        
        experiments.save(exp);
        
        ExperimentalAssay res = experiments.findOne(exp.getId()).get();
        //assertTrue(exp.hasSameValues(res));
        assertEquals(exp.getId(),res.getId());
        assertEquals("Cos1111",res.getName());
    }
    
    @Test
    public void saveBackupsExistingDescription() throws Exception {
        assertFalse(experiments.findOne(exp.getId()).isPresent());

        Path expDir = expStorage.getExperimentDir(exp.getId());
        Path expFile = experiments.getExperimentalAssayFile(expDir, exp.getId());
        assertFalse(Files.isRegularFile(expFile));
        
        
        experiments.save(exp);
        
        assertTrue(Files.isRegularFile(expFile));
        assertEquals(1,Files.list(expFile.getParent()).count());

        long size = Files.size(expFile);
        
        exp.generalDesc.name += exp.generalDesc.name;
        
        experiments.save(exp);
        
        assertTrue(size < Files.size(expFile));
        assertEquals(2,Files.list(expFile.getParent()).count());
                
    }    

    
    @Test
    public void readFromFileReadsJsonContent() throws IOException {
        
        Path file = experimentsDir.resolve("a.json");
        
        mapper.writeValue(file.toFile(), exp);
        
        ExperimentalAssay res = experiments.readFromFile(file);
        assertNotNull(res);
        //assertTrue(exp.hasSameValues(res));
        assertEquals(exp.getId(),res.getId());
    }
    
    @Test
    public void writeToFileWritesJsonContent() throws IOException {
        
        Path file = experimentsDir.resolve("b.json");
        
        assertFalse(Files.exists(file));
        
        experiments.writeToFile(exp, file);
        
        assertTrue(Files.exists(file));
        assertTrue(Files.size(file) > 10);
        
    }    
    
}
