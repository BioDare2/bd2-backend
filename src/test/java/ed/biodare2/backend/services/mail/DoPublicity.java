/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.services.mail;

import ed.biodare2.SimpleRepoTestConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.MOCK)
@Import(SimpleRepoTestConfig.class)
public class DoPublicity {
    
       
    
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    
    @Autowired
    Mailer instance;
    
    Path destinationsList = Paths.get("D:\\Temp\\biodare-users.txt");
    
    Path bodyFile = Paths.get("D:\\Temp\\biodare-email-body.txt");

    String subject = "BioDare2 - new service for circadian research";
    
    public DoPublicity() {
    }

    @Test
    @Ignore
    public void sendPublicityEmails() throws IOException, InterruptedException {
        
        Set<String> destinations = readDestinations(destinationsList);
        
        String body = readBody(bodyFile);
        
        assertFalse(body.isEmpty());
        body = body + "\n\n";
        
        //test run
        String self = "biodare@ed.ac.uk";        
        boolean res = instance.send(self, subject, body);        
        assertTrue(res);
        
        fail("Commented it out before the real work");
        
        for (String to: destinations) {
            
            boolean state = instance.send(to, subject, body);
            if (state) {
                log.info("PUBLICITY SENT:\t"+to);
            } else {
                System.out.println("FAILED PUBLICITY:\t"+to);
            }
            Thread.sleep(400);
            //*/
        }
        
    }

    protected Set<String> readDestinations(Path file) throws IOException {
        if (!Files.isRegularFile(file))
            throw new RuntimeException("Missing address file: "+file.toAbsolutePath());
        
        return Files.lines(file)
                .map( add -> add.toLowerCase().trim())
                .filter( add -> !add.isEmpty())
                .filter( add -> add.contains("@"))
                .collect(Collectors.toSet());
    }

    protected String readBody(Path file) throws IOException {
        if (!Files.isRegularFile(file))
            throw new RuntimeException("Missing body file: "+file.toAbsolutePath());
        
        String body = Files.lines(file)
                .collect(Collectors.joining("\n"));
        
        return body.trim();
    }
    
  
    
    
    
}
