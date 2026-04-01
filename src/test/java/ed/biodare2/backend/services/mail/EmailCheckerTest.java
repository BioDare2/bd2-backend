/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.services.mail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class EmailCheckerTest {

    @TempDir
    Path testFolder;
    
    EmailChecker handler;
    
    public EmailCheckerTest() {
    }
    
    @BeforeEach
    public void setUp() {
        handler = new EmailChecker();
    }

    @Test
    public void isAcademicGivesTrueOnAcademicEmails() {
        
        List<String> mails = Arrays.asList(
                "tz@havrard.edu",
                "cos.ktos@bla.tra-st.edu",
                "ja.tez@ed.ac.uk",
                "ja.tez@ed.edu.uk",
                "ktos@waw.ac.pl",
                "elalmo@ngha.med.sa",
                "zajawka@o2.pl" //the test email
        );
        
        for (String email : mails) {
            assertTrue(handler.isAcademic(email), "Expected academic for "+email);
        }
        
       // fail("Exepcted");
        
    }
    
    @Test
    public void isAcademicGivesTrueOnKnownAcademicInstitutions() {
        
        List<String> mails = Arrays.asList(
                "test@cid.csic.es",
                "cos.ktos@ethz.ch",
                "ja.tez@mpimp-golm.mpg.de",
                "ja.tez@upct.es",
                "ktos@waw.ac.pl",
                "tomcio@cragenomica.es"
        
        );
        
        for (String email : mails) {
            assertTrue(handler.isAcademic(email), "Expected academic for "+email);
        }
        
       // fail("Exepcted");
        
    }    
    
    @Test
    public void isAcademicGivesFalseOnNonAcademicEmails() {
        
        List<String> mails = Arrays.asList(
                "tz@havrard.ed",
                "cos.ktos@bla.tra-st.edu.org",
                "cos.ktos@bla.tra-st.edu.com",
                "ja.tez@ed.edu.org",
                "ktos@waw.ac.com",
                "ja.tez@ed.edu.biz",
                "elalmo@ngha.med.com",
                "elalmo@nghamed.sa",
                "elalmo@ngha.med.sa.com",
                "zielu@o2.pl"
        );
        
        for (String email : mails) {
            assertFalse(handler.isAcademic(email), "Expected non academic for "+email);
        }
        
        //fail("Exepcted");
    }
    
    @Test
    public void readKnowDomainsReadsTrimmedFilesContent() throws IOException {
        
        Path file = testFolder.resolve("test");
        List<String> domains = Arrays.asList("first.pl","last.ed"," ","");
        Files.write(file, domains);
        
        List<String> res = handler.readKnownDomains(file);
        List<String> exp = Arrays.asList("first.pl","last.ed");
        
        assertEquals(exp,res);
    }
    
    @Test
    public void afterUpdateKnownDomainsRecognizesNewEmail() throws IOException {
        String email = "bla@not.known";
        assertFalse(handler.isAcademic(email));
        
        Path file = testFolder.resolve("test");
        List<String> domains = Arrays.asList("first.pl","not.known","last.ed");
        Files.write(file, domains);
        
        handler.updateKnownDomains(file);
        
        assertTrue(handler.isAcademic(email));        
    
    }
    
    @Test
    public void updateKnownDomainsDoesNotCrashesOnMissingConfigFile() {
        String email = "bla@not.known";
        assertFalse(handler.isAcademic(email));
        
        Path file = null;
        
	file = testFolder.resolve("test").resolve("missing");
	assertFalse(Files.exists(file));
        
        handler.updateKnownDomains(file);
        
        assertFalse(handler.isAcademic(email));        
    }

    /*
    @Test
    public void constructorMakesInitialConfiguarionUpdate() throws IOException {
        String email = "bla@not.known";
        assertFalse(handler.isAcademic(email));
        
        Path file = testFolder.resolve("test");
        List<String> domains = Arrays.asList("first.pl","not.known","last.ed");
        Files.write(file, domains);
        
        handler.updateKnownDomains(file);
        
        assertTrue(handler.isAcademic(email));        
    
    }*/
    
    
}
