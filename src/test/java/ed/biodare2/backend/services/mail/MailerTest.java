/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.services.mail;

import ed.biodare2.SimpleRepoTestConfig;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
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
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.NONE)
@Import(SimpleRepoTestConfig.class)
public class MailerTest {
    
    

    
    @Autowired
    Mailer instance;
    
    public MailerTest() {
    }

    @Test
    @Ignore("Not know the password")
    public void sendsAnEmail() {
        
        String to = "biodare@ed.ac.uk";
        String subject = "Testing mailer";
        String body = "Hello from biodare";
        
        boolean res = instance.send(to, subject, body);
        assertTrue(res);
    }
    
  
    
    
    
}
