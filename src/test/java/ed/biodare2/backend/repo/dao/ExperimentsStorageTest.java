/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.dao;

import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import ed.biodare2.EnvironmentVariables;
import ed.biodare2.MockEnvironmentVariables;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author tzielins
 */
public class ExperimentsStorageTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    Path bdStorageDir;
    Path experimentsDir;
    
    ExperimentsStorage expStorage;
    
    
    public ExperimentsStorageTest() {
    }
    
    @Before
    public void setUp() throws IOException {
        bdStorageDir = testFolder.newFolder().toPath();
        experimentsDir = bdStorageDir.resolve("experiments");
        
        MockEnvironmentVariables var = new MockEnvironmentVariables();
        var.storageDir = bdStorageDir.toString();
        
        EnvironmentVariables env = var.mock(); //new EnvironmentVariables(bdStorageDir.toString(),"http://localhost","http://localhost:8084/JobCenter/PPAJobCenterWS?wsdl","","","","","");        
        
        expStorage = new ExperimentsStorage(env);        
    }    
    

    @Test
    public void experimentsDirGivesResolvesSubfolder() {
        
        assertEquals(experimentsDir,expStorage.getExperimentsDir());
    }
    
    @Test
    public void experimentDirGivesSubfolderWithIdAsName() {
        long id = 124;
        Path exp = experimentsDir.resolve(""+id);
        assertEquals(exp,expStorage.getExperimentDir(id));
    }
    
    
    @Test
    public void uponConstructionInitializesFolders() {
        
        Path dir = bdStorageDir.resolve("xxx");
        assertFalse(Files.exists(dir));
        
        MockEnvironmentVariables var = new MockEnvironmentVariables();
        var.storageDir = dir.toString();
        
        EnvironmentVariables env = var.mock(); //new EnvironmentVariables(dir.toString(),"http://localhost","http://localhost:8084/JobCenter/PPAJobCenterWS?wsdl","","","","","");
        expStorage = new ExperimentsStorage(env);  

        assertTrue(Files.exists(dir.resolve(ExperimentsStorage.EXPERIMENTS_STORAGE_DIR)));
        
    }     
    
}
