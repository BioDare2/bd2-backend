/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BioDare2WSApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(SimpleRepoTestConfig.class)
public class EnvironmentVariablesTest {

    
    @Autowired
    EnvironmentVariables instance;
    
    public EnvironmentVariablesTest() {
    }
    
  

    @Test
    public void hasStoragePath() {
        
        assertNotNull(instance.storageDir);
        //assertEquals(Paths.get("D:/Temp/biodare2/storage"),instance.storageDir);
    }
    
    @Test
    public void hasBackendURL() {
        
        assertNotNull(instance.backendURL);
    }    
    
    /*@Test
    public void hasJobcentreURL() {
        
        assertNotNull(instance.jobcentreURL);
    }*/    
    
    @Test
    public void hasReCaptchaKeys() {
        
        assertNotNull(instance.recaptchaSiteKey);
        assertNotNull(instance.recaptchaSecretKey);
    }        
    
    @Test
    public void hasMailProperties() {
        
        assertNotNull(instance.mailHost);
        assertNotNull(instance.mailUser);
        assertNotNull(instance.mailPassword);
        assertTrue(instance.mailAuth);
    }     
}
