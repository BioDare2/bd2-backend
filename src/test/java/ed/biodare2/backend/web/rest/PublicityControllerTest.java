/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.services.mail.Mailer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;

/**
 *
 * @author tzielins
 */
public class PublicityControllerTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); 
    
    
    PublicityController instance;
    Mailer mailer;
    Path addressFile;
    Path contentFile;
    

    public PublicityControllerTest() {
    }
    
    
    @Before
    public void setUp() throws IOException {
        mailer = mock(Mailer.class);
        instance = new PublicityController(mailer);
        
        //testFolder.newFolder();
        addressFile = testFolder.newFile().toPath();        
        Files.write(addressFile, List.of("biodare@ed.ac.uk","biodare2@ed.ac.uk"));
        
        contentFile = testFolder.newFile().toPath();
        Files.write(contentFile, List.of("Subject","Body"));
        
        instance.addressesFile = addressFile;
        instance.contentFile = contentFile;        
    }
    

    


    @Test
    //@Ignore("Test ig cause normaly publicity sending is disabled")
    public void testSendPublicity() throws Exception {

        BioDare2User currentUser = mock(BioDare2User.class);
        when(currentUser.getLogin()).thenReturn("test");
        
        Map<String, String> expResult = Map.of("sent","2","body","Body");
        Map<String, String> result = instance.sendPublicity(currentUser);
        assertEquals(expResult, result);
        
    }

    @Test
    public void testSendPublicityEmails() throws Exception {

        Set<String> addresses = Set.of("biodare@ed.ac.uk","biodare2@ed.ac.uk");
        String subject = "Test Subject";
        String testBody = "Test Body";
        
        int expResult = 2;
        int result = instance.sendPublicityEmails(addresses, subject, testBody);
        assertEquals(expResult, result);
        verify(mailer, times(2)).send(any(), any(), any());
        
    }

    @Test
    public void testReadDestinations() throws Exception {

        Set<String> expResult = Set.of("biodare@ed.ac.uk","biodare2@ed.ac.uk");
        Set<String> result = instance.readDestinations(addressFile);
        assertEquals(expResult, result);
    }

    @Test
    public void testReadBody() throws Exception {

        String expResult = "Body";
        String result = instance.readBody(contentFile);
        assertEquals(expResult, result);
        
    }

    @Test
    public void testReadSubject() throws Exception {

        String expResult = "Subject";
        String result = instance.readSubject(contentFile);
        assertEquals(expResult, result);
    }
    
}
