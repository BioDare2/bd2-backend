/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;


import ed.biodare2.EnvironmentVariables;
import ed.biodare2.MockEnvironmentVariables;
import ed.robust.jobcenter.dom.job.RemoteRESTCallBack;
import ed.robust.jobcenter.error.ConnectionException;
import java.io.IOException;
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
public class PPAAnalysisServiceTest {
    
    public PPAAnalysisServiceTest() {
    }

    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    Path storageDir;
    
    PPAAnalysisService instance;
    String ppaUsername = "ktos";
    String ppaPassword = "cos";
    
    
    @Before
    public void init() throws IOException {
        
        storageDir = testFolder.newFolder().toPath();
        //expDir = Paths.get("D:/Temp/ppaResTest");
        //Files.createDirectories(expDir);
        MockEnvironmentVariables var = new MockEnvironmentVariables();
        var.storageDir = storageDir.toString();
        
        EnvironmentVariables environment = var.mock(); //new EnvironmentVariables(storageDir.toString(),"http://localhost","http://localhost:8084/JobCenter/PPAJobCenterWS?wsdl","","","","","");
        instance = new PPAAnalysisService(environment, ppaUsername, ppaPassword);
        //to keep imports for a bit
        Path cos = mock(Path.class);
    }    
    
    @Test
    public void getStatusConnectsToJobCenter() throws ConnectionException {
        
        String status = instance.serviceStatus();
        assertNotNull(status);
        System.out.println(status);
    }
    
    @Test
    public void getBioDareCallbackUsesInjectedDetails() {
        
        RemoteRESTCallBack res = instance.getBioDare2CallBack();
        assertEquals(ppaUsername, res.user);
        assertEquals(ppaPassword, res.password);
        
    }
    
}
