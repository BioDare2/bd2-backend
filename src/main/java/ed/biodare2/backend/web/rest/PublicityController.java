/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.services.mail.Mailer;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Zielu
 */
@RestController
@RequestMapping("api/publicity")
public class PublicityController extends BioDare2Rest {
    
    final Mailer mailer;
    

    Path addressesFile = Paths.get("users-emails.txt");    
    Path contentFile = Paths.get("publicity-content.txt");

    
    @Autowired
    PublicityController(Mailer mailer) {
        this.mailer = mailer;
    }
    
    @RequestMapping(value="send",method = RequestMethod.GET)
    //public ListWrapper<String> dataStats(@NotNull @AuthenticationPrincipal BioDare2User currentUser) {
    public Map<String, String> sendPublicity(@NotNull @AuthenticationPrincipal BioDare2User currentUser) throws IOException, InterruptedException {
        log.debug("Publicity sender: {}", currentUser);
        
        if (!currentUser.getLogin().equals("demo") && !currentUser.getLogin().equals("test"))
            throw new InsufficientRightsException("Only demo and test users can call it");
        

        Set<String> addresses = readDestinations(addressesFile);        
        String subject = readSubject(contentFile);
        String body = readBody(contentFile);
        
        
        int sent = sendPublicityEmails(addresses, subject, body);
        
        Map<String, String> stats = new HashMap<>();
        stats.put("sent",""+sent);
        stats.put("body",readBody(contentFile));
        return stats;
    }   


    


    protected int sendPublicityEmails(Set<String> addresses, String subject, String body) throws InterruptedException {
        
        body = body + "\n\n";
        
        
        for (String to:  addresses) {
            
            boolean state = mailer.send(to, subject, body);
            if (state) {
                log.info("PUBLICITY SENT:\t"+to);
            } else {
                log.info("FAILED PUBLICITY:\t"+to);
            }
            Thread.sleep(200);
            //*/
        }
        
        return addresses.size();
        
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
                .skip(1)
                .collect(Collectors.joining("\n"));
        
        return body.trim();
    }

    protected String readSubject(Path file) throws IOException {
        if (!Files.isRegularFile(file))
            throw new RuntimeException("Missing body file: "+file.toAbsolutePath());
        
        String subj = Files.lines(file).findFirst().get();
        return subj;
                
    }
    
}
