/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.services.recaptcha;

import ed.biodare2.EnvironmentVariables;
import ed.biodare2.MockEnvironmentVariables;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.web.rest.ServerSideException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 * @author Zielu
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.MOCK)
@Import(SimpleRepoTestConfig.class)
public class ReCaptchaServiceTest {
    
    /*@Configuration
    @Import(EnvironmentConfiguration.class)
    @ComponentScan
    public static class Config {
        
    } */    
    
    public ReCaptchaServiceTest() {
    }
    
    @Autowired
    ReCaptchaService service;
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    @Ignore("Tests no longer have valid captcha configuration")
    public void testGivesFalseOnWrongChallenge() {
        
        String challenge = "just testing response";
        
        boolean resp = service.verify(challenge);
        assertFalse(resp);
        
    }
    
    @Test
    public void throwsExceptionOnCaptchaMisconfiguration() {
        MockEnvironmentVariables var = new MockEnvironmentVariables();
        var.recaptchaSiteKey = "wrongSiteKey";
        var.recaptchaSecretKey ="wrongSecretKey";
        EnvironmentVariables env = var.mock(); //new EnvironmentVariables("temp", "http://localhost", "http://localhost", "wrongSiteKey", "wrongSecretKey","","","");
        service = new ReCaptchaService(env);
        
        String challenge = "just testing response";
        
        try {
            service.verify(challenge);
            fail("Exception expected");
        } catch(ServerSideException e) {};

    }
    
}
