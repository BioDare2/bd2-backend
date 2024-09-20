/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

import ed.biodare2.Fixtures;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
//import ed.biodare2.backend.handlers.UsersHandler.ActivationToken;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.services.mail.EmailChecker;
import ed.biodare2.backend.services.mail.Mailer;
import ed.biodare2.backend.features.rdmsocial.RDMSocialHandler;
import ed.biodare2.backend.features.subscriptions.SubscriptionType;
import ed.biodare2.backend.security.dao.UserTokenRep;
import ed.biodare2.backend.security.dao.db.UserToken;
import ed.biodare2.backend.security.dao.db.UserTokenKind;
import ed.biodare2.backend.web.rest.HandlingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SimpleRepoTestConfig.class)
//@Ignore //[TODO DB TEST]
public class UsersHandlerTest {
    
    @Autowired
    Fixtures fixtures;

    @Autowired
    UsersHandler handler;
    
    @Autowired
    @Qualifier("LocalValidator") 
    Validator validator;
    
    @Autowired
    UserAccountRep users;
    
    @Autowired
    UserTokenRep tokens;
    
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Autowired
    EntityManagerFactory emf;
    
    @MockBean
    Mailer mailer;
    
    @MockBean
    RDMSocialHandler rdmSocialHandler;
    
    public UsersHandlerTest() {
    }
    
    @Before
    public void init() {
        //users = mock(UserAccountRep.class);
        //handler = new UsersHandler(users,validator);
        //fixtures = Fixtures.build();
        fixtures.demoUser.setPassword("GoodPasword1");

    }
    
    @Test
    public void registerRegistersTheUserWithLockedAccount() throws UsersHandler.AccountHandlingException {
        
        Map<String,String> details = new HashMap<>();
        details.put("login","an_LoginX");
        details.put("email","some@eMail.edu.cn");
        details.put("password", " don't trim me");
        details.put("firstName"," TomeK");
        details.put("lastName"," ZedW'S");
        details.put("institution","Inst"); 
        details.put("terms","true");
        
        assertFalse(users.findByLogin("an_loginx").isPresent());
        
        BioDare2User res = handler.register(details);
        assertNotNull(res);
        assertEquals(res.getLogin(),"an_loginx");
        assertTrue(users.findByLogin("an_loginx").isPresent());
        assertTrue(res.isLocked());
        
    }
    
    @Test
    public void registerCreatesUserWithFreeSubscriptionAndCurrentTermsVersion() throws UsersHandler.AccountHandlingException {
        
        Map<String,String> details = new HashMap<>();
        details.put("login","an_LoginX2");
        details.put("email","some2@eMail.edu.cn");
        details.put("password", " don't trim me");
        details.put("firstName"," TomeK");
        details.put("lastName"," ZedW'S");
        details.put("institution","Inst"); 
        details.put("terms","true");
        
        assertFalse(users.findByLogin("an_loginx2").isPresent());
        
        BioDare2User res = handler.register(details);
        assertNotNull(res);
        assertNotNull(res.getSubscription());
        assertEquals(SubscriptionType.FREE,res.getSubscription().getKind());
        
        UserAccount u = (UserAccount)res;
        assertNotNull(u.getTermsVersion());
        assertEquals("1.1",u.getTermsVersion());
        
        verify(rdmSocialHandler).createUserAspect(u);
    }   
    
    @Test
    public void registerRegistersTheUserUnderCurentData() throws UsersHandler.AccountHandlingException {
        
        String login = "an_rrtuucd";
        Map<String,String> details = new HashMap<>();
        details.put("login",login);
        details.put("email","an_rrtuucd@email.edu.cn");
        details.put("password", " don't trim me");
        details.put("firstName"," TomeK");
        details.put("lastName"," ZedW'S");
        details.put("institution","Inst"); 
        details.put("terms","true");
        
        assertFalse(users.findByLogin(login).isPresent());
        
        UserAccount res = (UserAccount)handler.register(details);
        assertNotNull(res);
        assertEquals(res.getLogin(),login);
        assertEquals(LocalDate.now(),res.getRegistrationDate());
        assertEquals(LocalDate.now(),res.getCreationDate().toLocalDate());
        assertEquals(LocalDate.now(),res.getModificationDate().toLocalDate());
        assertNull(res.getActivationDate());
        
        assertTrue(users.findByLogin(login).isPresent());
        
    }    
    
    @Test
    public void registerThrowsExceptionOnMissingTerms() throws UsersHandler.AccountHandlingException {
        
        Map<String,String> details = new HashMap<>();
        details.put("login","an_LoginX1");
        details.put("email","some@eMailX.edu.cn");
        details.put("password", " don't trim me");
        details.put("firstName"," TomeK");
        details.put("lastName"," ZedW'S");
        details.put("institution","Inst"); 
        
        assertFalse(users.findByLogin("an_loginx1").isPresent());
        
        try {
            BioDare2User res = handler.register(details);
            fail("Exception expected");
        } catch (UsersHandler.AccountHandlingException e) {};
    }
    
    @Test
    public void registerThrowsExceptionOnBioDareLikeLogins() throws UsersHandler.AccountHandlingException
    {
        Map<String,String> details = new HashMap<>();
        details.put("login","biodare");
        details.put("email","some@eMailX.edu.cn");
        details.put("password", " don't trim me");
        details.put("firstName"," TomeK");
        details.put("lastName"," ZedW'S");
        details.put("institution","Inst"); 
        details.put("terms","true");
        
        try {
            handler.register(details);
            fail("Exception expected");
        } catch (UsersHandler.AccountHandlingException e) {}; 
        
        details.put("login","biodare2");
        try {
            handler.register(details);
            fail("Exception expected");
        } catch (UsersHandler.AccountHandlingException e) {}; 

        details.put("login","biodare_x");
        try {
            handler.register(details);
            fail("Exception expected");
        } catch (UsersHandler.AccountHandlingException e) {}; 

        details.put("login","shouldPass");
        handler.register(details);
        
    }
    
    @Test
    public void updateUpdatesTheUserIgnoringPassword() throws UsersHandler.AccountHandlingException {
        
        BioDare2User user = fixtures.demoUser1;
        String currentPassword = "demo";
        assertTrue(passwordEncoder.matches(currentPassword,user.getPassword()));
        
        Map<String,String> details = new HashMap<>();
        details.put("login",user.getLogin());
        details.put("email","updated@non.academic.pl");
        details.put("password", "changed12");
        details.put("firstName","UP"+user.getFirstName());
        details.put("lastName","UP"+user.getLastName());
        details.put("institution","UP"+user.getInstitution()); 
        details.put("currentPassword",currentPassword);
        
        assertTrue(users.findByLogin(user.getLogin()).isPresent());
        
        BioDare2User res = handler.update(details,user);
        assertNotNull(res);
        assertEquals(res.getLogin(),user.getLogin());
        
        BioDare2User up = users.findByLogin(user.getLogin()).get();
        assertEquals(details.get("email"),up.getEmail());
        assertEquals(details.get("firstName"),up.getFirstName());
        assertEquals(details.get("lastName"),up.getLastName());
        assertEquals(details.get("institution"),up.getInstitution());
        assertTrue(passwordEncoder.matches(currentPassword,up.getPassword()));
        
    } 

    @Test
    public void updateUpdatesTheUser() throws UsersHandler.AccountHandlingException {
        
        BioDare2User user = fixtures.demoUser1;
        String currentPassword = "demo";
        assertTrue(passwordEncoder.matches(currentPassword,user.getPassword()));
        
        Map<String,String> details = new HashMap<>();
        details.put("login",user.getLogin());
        details.put("email","updated@non.academic.pl");
        details.put("firstName","UP"+user.getFirstName());
        details.put("lastName","UP"+user.getLastName());
        details.put("institution","UP"+user.getInstitution()); 
        details.put("currentPassword",currentPassword);
        
        assertTrue(users.findByLogin(user.getLogin()).isPresent());
        
        BioDare2User res = handler.update(details,user);
        assertNotNull(res);
        assertEquals(res.getLogin(),user.getLogin());
        
        BioDare2User up = users.findByLogin(user.getLogin()).get();
        assertEquals(details.get("email"),up.getEmail());
        assertEquals(details.get("firstName"),up.getFirstName());
        assertEquals(details.get("lastName"),up.getLastName());
        assertEquals(details.get("institution"),up.getInstitution());
        assertTrue(passwordEncoder.matches(currentPassword,up.getPassword()));
        
    } 
    
    @Test
    public void updatePasswordChangesOnlyPassword() throws UsersHandler.AccountHandlingException {
        
        BioDare2User user = fixtures.demoUser2;
        String currentPassword = "demo";
        assertTrue(passwordEncoder.matches(currentPassword,user.getPassword()));
        assertTrue(users.findByLogin(user.getLogin()).isPresent());
        assertTrue(passwordEncoder.matches(currentPassword,users.findByLogin(user.getLogin()).get().getPassword()));
        
        String orgEmail = user.getEmail();
        String changedEmail = "updated@non.academic.pl";
        assertNotEquals(orgEmail, changedEmail);
        
        Map<String,String> details = new HashMap<>();
        details.put("login",user.getLogin());
        details.put("email",changedEmail);
        details.put("password", "changed12");
        details.put("firstName","UP"+user.getFirstName());
        details.put("lastName","UP"+user.getLastName());
        details.put("institution","UP"+user.getInstitution()); 
        details.put("currentPassword",currentPassword);
        
        
        
        BioDare2User res = handler.updatePassword(details,user);
        assertNotNull(res);
        assertEquals(res.getLogin(),user.getLogin());
        
        BioDare2User up = users.findByLogin(user.getLogin()).get();
        assertEquals(user.getEmail(),up.getEmail());
        assertEquals(orgEmail,up.getEmail());
        assertEquals(user.getFirstName(),up.getFirstName());
        assertEquals(user.getLastName(),up.getLastName());
        assertEquals(user.getInstitution(),up.getInstitution());
        assertTrue(passwordEncoder.matches("changed12",up.getPassword()));
        
    } 
    
    
    @Test
    public void updateThrowsExceptionOnWrongPassword() throws UsersHandler.AccountHandlingException {
        
        BioDare2User user = fixtures.user1;
        String currentPassword = "aha";
        assertFalse(passwordEncoder.matches(currentPassword,user.getPassword()));
        
        Map<String,String> details = new HashMap<>();
        details.put("login",user.getLogin());
        details.put("email","updated@non.academic.pl");
        details.put("password", "changed12");
        details.put("firstName","UP"+user.getFirstName());
        details.put("lastName","UP"+user.getLastName());
        details.put("institution","UP"+user.getInstitution()); 
        details.put("currentPassword",currentPassword);
        
        assertTrue(users.findByLogin(user.getLogin()).isPresent());
        
        try {
            BioDare2User res = handler.update(details,user);
            fail("Exceptin expected cause of wrong password");
        } catch (UsersHandler.AccountHandlingException e) {};
    } 
    
    @Test
    public void updateThrowsExceptionOnUserMismatch() throws UsersHandler.AccountHandlingException {
        
        BioDare2User user = fixtures.user1;
        String currentPassword = "user1";
        assertTrue(passwordEncoder.matches(currentPassword,user.getPassword()));
        
        Map<String,String> details = new HashMap<>();
        details.put("login",user.getLogin());
        details.put("email","updated@non.academic.pl");
        details.put("password", "changed12");
        details.put("firstName","UP"+user.getFirstName());
        details.put("lastName","UP"+user.getLastName());
        details.put("institution","UP"+user.getInstitution()); 
        details.put("currentPassword",currentPassword);
        
        assertTrue(users.findByLogin(user.getLogin()).isPresent());
        
        try {
            BioDare2User res = handler.update(details,fixtures.demoUser);
            fail("Exceptin expected cause of user mismatch");
        } catch (UsersHandler.AccountHandlingException e) {};
        
    } 
    
    
    @Test
    public void updateThrowsExceptionOnWrongEmail() throws UsersHandler.AccountHandlingException {
        
        BioDare2User user = fixtures.user1;
        String currentPassword = "user1";
        assertTrue(passwordEncoder.matches(currentPassword,user.getPassword()));
        
        Map<String,String> details = new HashMap<>();
        details.put("login",user.getLogin());
        details.put("email","updated");
        details.put("password", "changed12");
        details.put("firstName","UP"+user.getFirstName());
        details.put("lastName","UP"+user.getLastName());
        details.put("institution","UP"+user.getInstitution()); 
        details.put("currentPassword",currentPassword);
        
        assertTrue(users.findByLogin(user.getLogin()).isPresent());
        
        try {
            BioDare2User res = handler.update(details,user);
            fail("Exceptin expected cause of wrong password");
        } catch (UsersHandler.AccountHandlingException e) {};
    }     
    
    @Test
    public void updateDetailsLeavesOldPasswordOnMissing() {
        UserAccount user = Fixtures.build().demoUser;
        String old = user.getPassword();
        
        UserAccount details = new UserAccount();
        details.setPassword("");
        
        handler.updateDetails(details, user);
        
        assertSame(old,user.getPassword());
    }
    
    @Test
    public void changePasswordChanges() {
        UserAccount user = Fixtures.build().demoUser;
        String old = user.getPassword();
        
        handler.changePassword("xxx123", user);
        
        assertNotSame(old,user.getPassword());
        
        assertTrue(passwordEncoder.matches("xxx123", user.getPassword()));
    }    
    
    @Test
    public void updateDetailsLeavesLoginAndInitialEmailsUntouched() {
        UserAccount user = Fixtures.build().demoUser;
        String oldL = user.getLogin();
        String oldI = user.getInitialEmail();
        
        UserAccount details = new UserAccount();
        details.setLogin(user.getLogin()+"1");
        details.setInitialEmail(user.getInitialEmail()+"2");
        details.setPassword("");
        
        handler.updateDetails(details, user);
        
        assertSame(oldL,user.getLogin());
        assertSame(oldI,user.getInitialEmail());
        
    }
    
    @Test
    public void updateDetailsChangesMailAndNames() {
        UserAccount user = Fixtures.build().demoUser;
        
        UserAccount details = new UserAccount();
        details.setEmail("A@A.pl");
        details.setInstitution("BInst");
        details.setFirstName("FN");
        details.setLastName("LN");
        details.setPassword("");
        
        handler.updateDetails(details, user);
        
        assertSame("A@A.pl",user.getEmail());
        assertSame("BInst",user.getInstitution());
        assertSame("FN",user.getFirstName());
        assertSame("LN",user.getLastName());        
        
        
    }    
    

    @Test
    public void validatesThrowsExceptionOnEmptyUser() {
        
        //assertTrue(validator.supports(UserAccount.class));
        UserAccount user = new UserAccount();
        
        //Errors errors = new BindException(user,"user");
        
        //validator.validate(user, errors);
        //assertTrue(errors.hasErrors());
        
        try {
            handler.validateUser(user);
            fail("Exception expected");
        } catch (UsersHandler.AccountHandlingException e) {
            //System.out.println("Validation: "+e.getMessage());
        }
        
    }
   
    @Test
    public void validatesNewThrowsExceptionOnShortLogin() throws Exception {
        
        //assertTrue(validator.supports(UserAccount.class));
        UserAccount user = fixtures.demoUser;
        //handler.validateUser(user);
        user.setLogin("al");
        
        try {
            handler.validateNewUser(user);
            fail("Exception expected");
        } catch (UsersHandler.AccountHandlingException e) {
            //System.out.println("Validation: "+e.getMessage());
        }
        
    }
    
    @Test
    public void validatesNewThrowsExceptionOnNonAlphanumericLogins() throws Exception {
        
        UserAccount user = fixtures.demoUser;
        user.setEmail("not@repeated.edu.pl");
        user.setPassword("Long password");
        List<String> wrong = Arrays.asList("with space","with()adfd","withCAP","*1234567");
        
        for (String l : wrong) {
            user.setLogin(l);
        
            try {
                handler.validateUser(user);
                fail("Exception expected on: "+l);
            } catch (UsersHandler.AccountHandlingException e) {
            }
        }        
    }
    
    @Test
    public void validatesAcceptsAlphanumericLogins() throws Exception {
        
        UserAccount user = Fixtures.build().demoUser;
        user.setEmail("not@repeated.edu.pl");
        user.setInitialEmail("not@repeated.pl");
        user.setPassword("Long password");
        List<String> good = Arrays.asList("with_space","with.1adfd","with23",".1234567");
        
        for (String l : good) {
            user.setLogin(l);
        
            handler.validateUser(user);
        }        
    }    
    
    
    @Test
    public void validatesThrowsExceptionOnWrongEmail() throws Exception {
        
        //assertTrue(validator.supports(UserAccount.class));
        UserAccount user = fixtures.demoUser;
        //handler.validateUser(user);
        
        user.setEmail("not_valid");
        
        try {
            handler.validateUser(user);
            fail("Exception expected");
        } catch (UsersHandler.AccountHandlingException e) {
            //System.out.println("Validation: "+e.getMessage());
        }
        
    }
    
    @Test
    public void validatesThrowsExceptionOnEmptyInstituion() throws Exception {
        
        //assertTrue(validator.supports(UserAccount.class));
        UserAccount user = fixtures.demoUser;
        //handler.validateUser(user);
        
        user.setInstitution("");
        
        try {
            handler.validateUser(user);
            fail("Exception expected");
        } catch (UsersHandler.AccountHandlingException e) {
            //System.out.println("Validation: "+e.getMessage());
        }
        
    }    
    
  
    @Test
    public void validatesNewThrowsExceptionOnDuplicateLogin() throws Exception {
        
        UserAccount user = fixtures.demoUser;
        //user.setPassword("GoodPasword1");
        user.setEmail("test."+user.getEmail());
        user.setInitialEmail("test."+user.getInitialEmail());        
        try {
            handler.validateNewUser(user);
            fail("Exception expected");
        } catch (UsersHandler.AccountHandlingException e) {
            //System.out.println("Validation: "+e.getMessage());
        }
        
        //fail("FOr testing");        
    }
    
    @Test
    public void validateNewThrowsExceptionOnNonAcademicInitalEmail() throws Exception {
        
        UserAccount user = fixtures.demoUser;
        //user.setPassword("GoodPasword1");        
        user.setLogin(user.getLogin()+".test");
        user.setInitialEmail(user.getInitialEmail()+".org");
        
        try {
            handler.validateNewUser(user);
            fail("Exception expected");
        } catch (UsersHandler.AccountHandlingException e) {
            //System.out.println("Validation: "+e.getMessage());
        }
        
        //fail("FOr testing");
        
        
    }    
    
    
    @Test
    public void validatesNewThrowsExceptionOnDuplicateEmail() throws Exception {
        
        UserAccount user = fixtures.demoUser;
        //user.setPassword("GoodPasword1");        
        user.setLogin(user.getLogin()+".test");
        user.setInitialEmail(user.getInitialEmail()+".test");
        
        try {
            handler.validateNewUser(user);
            fail("Exception expected");
        } catch (UsersHandler.AccountHandlingException e) {
            //System.out.println("Validation: "+e.getMessage());
        }
        
        //fail("FOr testing");
        
        
    }    
    
    @Test
    public void validatesNewThrowsExceptionOnDuplicateInitialEmail() throws Exception {
        
        UserAccount user = fixtures.demoUser;
        //user.setPassword("GoodPasword1");        
        user.setLogin(user.getLogin()+".test");
        user.setEmail("test."+user.getEmail());
        
        try {
            handler.validateNewUser(user);
            fail("Exception expected");
        } catch (UsersHandler.AccountHandlingException e) {
            //System.out.println("Validation: "+e.getMessage());
        }
        
        //fail("FOr testing");
        
    }      
    
    @Test
    public void validatesNewThrowsExceptionOnWeakPassword() throws Exception {
        
        UserAccount user = new UserAccount();
        user.setLogin("blablabla");
        user.setEmail("blablabla@bla.bla.ac.uk");
        user.setFirstName("blablabla");
        user.setLastName("blablabla");
        user.setPassword("Difficult.12");
        user.setInstitution("Inst");
        
        handler.validateNewUser(user);
        
        List<String> wrong = Arrays.asList("short","nocapitalletter","NOSMALLLETTERS","12345678910");
        for (String pass :wrong) {
            try {
                user.setPassword(pass);
                handler.validateNewUser(user);
                fail("Exception expected");
            } catch (UsersHandler.AccountHandlingException e) {
                //System.out.println("Validation: "+e.getMessage());
            }
        }
        
        
        
    }    
    
    
    @Test
    public void checkPasswordThrowsExceptionOnWeakPassword() throws Exception {
        
        List<String> wrong = Arrays.asList("short","nocapitalletter","NOSMALLLETTERS","12345678910");
        for (String pass :wrong) {
            try {
                handler.checkPassword(pass);
                fail("Exception expected on: "+pass);
            } catch (UsersHandler.AccountHandlingException e) {
            }
        }
    }    
    
    @Test
    public void checkPasswordAcceptsDecentPassword() throws Exception {
        
        List<String> wrong = Arrays.asList("short1234","noCapitalletter","NOsMALLLETTERS","12.345678910");
        for (String pass :wrong) {
            handler.checkPassword(pass);
        }
    }    
    
    @Test
    public void encodesPassword() throws Exception {
        
        UserAccountRep users = mock(UserAccountRep.class);
        //Validator v = mock(Validator.class);
        PasswordEncoder enc = mock(PasswordEncoder.class);
        
        EmailChecker checker = mock(EmailChecker.class);
        
        //handler = new UsersHandler(users, tokens, validator, enc,new ObjectMapper(),mock(Encryptor.class),mailer,checker,rdmSocialHandler);
        handler = new UsersHandler(users, tokens, validator, enc,mailer,checker,rdmSocialHandler);
        
        Map<String,String> details = new HashMap<>();
        details.put("login","a_login ");
        details.put("email","some@eMail.here.edu.pl ");
        details.put("password", " don't trim me ");
        details.put("firstName"," TomeK");
        details.put("lastName"," ZedW'S ");
        details.put("institution","Inst");
        details.put("terms","true");
        
        when(users.findByEmail(any())).thenReturn(Collections.emptyList());
        when(users.findByLogin(any())).thenReturn(Optional.empty());
        when(users.save(any(UserAccount.class))).then(returnsFirstArg());
        when(enc.encode(any())).thenReturn("PASS");
        when(checker.isAcademic(anyString())).thenReturn(true);
        
        BioDare2User user = handler.register(details);
        assertNotNull(user);
        assertEquals("PASS",user.getPassword());
        
    }    
    
    
    
    @Test
    public void buildsUserWithEmptyInput() throws Exception {
        
        //assertTrue(validator.supports(UserAccount.class));
        Map<String,String> details = new HashMap<>();
        UserAccount user = handler.makeUser(details);
        assertNotNull(user);
        
        
    }
    
    @Test
    public void buildsCorrectUser() throws Exception {
        
        //assertTrue(validator.supports(UserAccount.class));
        Map<String,String> details = new HashMap<>();
        details.put("login","A login ");
        details.put("email","some@eMail.here ");
        details.put("password", " don't trim me ");
        details.put("firstName"," TomeK");
        details.put("lastName"," ZedW'S ");
        details.put("institution","Inst");
        
        UserAccount user = handler.makeUser(details);
        assertNotNull(user);
        assertEquals("a login",user.getLogin());
        assertEquals("some@email.here",user.getEmail());
        assertEquals("some@email.here",user.getInitialEmail());        
        assertEquals(" don't trim me ",user.getPassword());
        assertEquals("TomeK",user.getFirstName());
        assertEquals("ZedW'S", user.getLastName());
        assertEquals("Inst",user.getInstitution());
        assertEquals(user,user.getSupervisor());
        
        
    }
    
    @Test
    public void makesActivationTokenThatUnlocksTheAccount() throws UsersHandler.AccountHandlingException {
        
        EntityManager em = emf.createEntityManager();
        UserAccount user = em.find(UserAccount.class, fixtures.demoUser1.getId());
        UserToken token = handler.makeActivationToken(user);
        
        em.getTransaction().begin();
        em.persist(token);
        user.setLocked(true);
        em.getTransaction().commit();
        
        
        BioDare2User resp = handler.activateAccount(token.getToken());
        assertNotNull(resp);
        assertEquals(user.getLogin(),resp.getLogin());
        
        em = emf.createEntityManager();
        //em.getTransaction().begin();        
        user = em.find(UserAccount.class, fixtures.demoUser1.getId());
        //em.refresh(user);
        assertFalse(user.isLocked());
        assertEquals(LocalDate.now(),user.getActivationDate());
        //em.getTransaction().rollback();
        
    }
    
    @Test
    public void activationRemovesToken() throws UsersHandler.AccountHandlingException {
        

        UserAccount user = users.findById(fixtures.demoUser1.getId()).get();
        user.setLocked(true);
        users.saveAndFlush(user);
        UserToken token = handler.makeActivationToken(user);
        tokens.saveAndFlush(token);
        assertTrue(tokens.findByToken(token.getToken()).isPresent());
        
        BioDare2User resp = handler.activateAccount(token.getToken());
        assertNotNull(resp);
        assertEquals(user.getLogin(),resp.getLogin());
        
        assertFalse(tokens.findByToken(token.getToken()).isPresent());
        
    }
    
    @Test
    public void removeExpiredTokensRemovesOldTokens() {
        UserAccount user = users.findById(fixtures.demoUser1.getId()).get();
        UserToken token = handler.makeActivationToken(user);
        token.setExpiring(LocalDateTime.now().minusMinutes(1));
        tokens.save(token);
        
        token = handler.makeActivationToken(user);
        token.setExpiring(LocalDateTime.now().minusDays(1));
        tokens.saveAndFlush(token);
        
        List<UserToken> expired = tokens.findByExpiringBefore(LocalDateTime.now());
        int count = expired.size();
        assertTrue(count >= 2);
        
        handler.removeExpiredTokens();
        expired = tokens.findByExpiringBefore(LocalDateTime.now());
        assertTrue(expired.isEmpty());
    }
    
    /*
    @Test
    @Ignore("Different implementation")
    @Deprecated
    public void generatesActivationTokenThatUnlocksTheAccount() throws UsersHandler.AccountHandlingException {
        
        EntityManager em = emf.createEntityManager();
        UserAccount user = em.find(UserAccount.class, fixtures.demoUser1.getId());
        em.getTransaction().begin();
        user.setLocked(true);
        em.getTransaction().commit();
        
        String token = handler.getActivationToken(user);
        assertNotNull(token);
        
        BioDare2User resp = handler.activateAccount(token);
        assertNotNull(resp);
        assertEquals(user.getLogin(),resp.getLogin());
        
        em.getTransaction().begin();
        em.refresh(user);
        assertFalse(user.isLocked());
        assertEquals(LocalDate.now(),user.getActivationDate());
        em.getTransaction().rollback();
    }
    
    @Test
    public void encodeAndDecodeTokenWorksInPair() {
        ActivationToken token = new ActivationToken();
        token.login = "Ala";
        token.email = "ma@kota";
        
        String msg = handler.encodeToken(token);
        assertNotNull(msg);
        ActivationToken res = handler.decodeToken(msg);
        assertNotNull(res);
        assertEquals(token.login,res.login);
        assertEquals(token.email,res.email);
        assertEquals(token.expiration,res.expiration);
    }
    
    @Test
    @Ignore
    @Deprecated
    public void doesNotActiateOnExpiredTokens2() throws UsersHandler.AccountHandlingException {
        
        UserAccount user = fixtures.demoUser;
        ActivationToken token = new ActivationToken();
        token.email =user.getEmail();
        token.login= user.getLogin();
        
        String msg = handler.encodeToken(token);
        
        assertNotNull(handler.activateAccount(msg));
        
        token.expiration = token.expiration.minusDays(2);
        msg = handler.encodeToken(token);
        try {
            handler.activateAccount(msg);
            fail("Exception expected");
        } catch (UsersHandler.AccountHandlingException e) {};
        
    }    
    
    
    */
    
    @Test
    public void sendsActivationEmail() throws UsersHandler.AccountHandlingException {
        
        BioDare2User user = fixtures.demoUser;
        when(mailer.send(eq(user.getEmail()), anyString(), anyString())).thenReturn(true);
        
        handler.sendActivationEmail(user);
        
        verify(mailer).send(eq(user.getEmail()), anyString(), anyString());
    }
    
    @Test
    public void makeActivationTokenCreatesCorrectToken() {
        BioDare2User user = fixtures.demoUser;
        UserToken token = handler.makeActivationToken(user);
        assertNotEquals("", token.getToken());
        assertEquals(UserTokenKind.ACTIVATION,token.getKind());
        assertSame(user,token.getUser());
        assertEquals(LocalDate.now().plusDays(5),token.getExpiring().toLocalDate());
    }
    
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void sendActivationEmailSavesTheToken() throws Exception {
        BioDare2User user = fixtures.demoUser1;
        when(mailer.send(eq(user.getEmail()), anyString(), anyString())).thenReturn(true);
        
        List<UserToken> userTokens = tokens.findByUser(user);
        assertTrue(userTokens.isEmpty());
        handler.sendActivationEmail(user);
        
        userTokens = tokens.findByUser(user);
        assertEquals(1,userTokens.size());
        
        UserToken token = userTokens.get(0);
        assertEquals(UserTokenKind.ACTIVATION,token.getKind());
        
    }
    
    @Test
    public void doesNotActiateOnExpiredTokens() throws UsersHandler.AccountHandlingException {
        
        UserAccount user = fixtures.demoUser;
        UserToken token = handler.makeActivationToken(user);
        token.setExpiring(LocalDateTime.now().minusDays(1));
        tokens.saveAndFlush(token);
        
        String msg = token.getToken();
        try {
            handler.activateAccount(msg);
            fail("Exception expected");
        } catch (UsersHandler.AccountHandlingException e) {
            assertEquals("Activation token expired",e.getMessage());
        };
        
    }    
    
    @Test
    public void makeResetTokenCreatesCorrectToken() {
        BioDare2User user = fixtures.demoUser;
        UserToken token = handler.makeResetToken(user);
        assertNotEquals("", token.getToken());
        assertEquals(UserTokenKind.PASSWORD_RESET,token.getKind());
        assertSame(user,token.getUser());
        assertEquals(LocalDate.now().plusDays(1),token.getExpiring().toLocalDate());
    } 

    @Test
    public void restPasswordResetsPasswordAndUnlocks() throws UsersHandler.AccountHandlingException {
        
        UserAccount user = users.findById(fixtures.user1.getId()).get();
        UserToken token = handler.makeResetToken(user);
        tokens.saveAndFlush(token);
        
        user.setPassword("aha");
        user.setLocked(true);
        user.setFailedAttempts(10);
        user.setActivationDate(null);
        users.saveAndFlush(user);
        
        String msg = token.getToken();
        String password = "Difficult12";
        assertFalse(passwordEncoder.matches(password, user.getPassword()));
        
        BioDare2User resp = handler.resetPassword(password, msg);

        user = users.findById(user.getId()).get();
        assertFalse(user.isLocked());
        assertEquals(0,user.getFailedAttempts());
        assertEquals(LocalDate.now(),user.getActivationDate());
        assertTrue(passwordEncoder.matches(password, user.getPassword()));
        
        assertFalse(tokens.findByToken(token.getToken()).isPresent());
        
        assertEquals(resp,user);
    }  
    
    @Test
    public void doesNotResetOnExpiredTokens() throws UsersHandler.AccountHandlingException {
        
        UserAccount user = fixtures.demoUser;
        UserToken token = handler.makeResetToken(user);
        token.setExpiring(LocalDateTime.now().minusDays(1));
        tokens.saveAndFlush(token);
        
        String msg = token.getToken();
        String password = "Difficult12";
        try {
            handler.resetPassword(password, msg);
            fail("Exception expected");
        } catch (UsersHandler.AccountHandlingException e) {
            assertEquals("Token expired",e.getMessage());
        };
        
    }      
    
    @Test
    public void identifyIsCaseInsensitive() {
        
        UserAccount user = fixtures.demoUser;
        if (!users.findByLogin(user.getLogin()).isPresent()) {
            user = users.saveAndFlush(user);            
        } 
        assertTrue(users.findByLogin(user.getLogin()).isPresent());
        String login = user.getLogin().toUpperCase();
        assertNotEquals(login, user.getLogin());
        
        Optional<BioDare2User> resp = handler.identify(login);
        assertTrue(resp.isPresent());
        
    }
    
    @Test
    public void identifyFailsForMulitpleEmails() {
        
        UserAccount user1 = users.findById(fixtures.demoUser.getId()).get();
        user1.setLogin("blabla1");
        user1.setEmail("tz@ed.ac.uk");
        user1 = users.saveAndFlush(user1);
        
        UserAccount user2 = users.findById(fixtures.demoUser1.getId()).get();
        user2.setLogin("blabla2");
        user2.setEmail("tz@ed.ac.uk");
        user2 = users.saveAndFlush(user2);
        
        assertEquals(2,users.findByEmail("tz@ed.ac.uk").size());
        
        try {
        Optional<BioDare2User> resp = handler.identify("tz@ed.ac.uk");
        fail("Exception expected");
        assertTrue(resp.isPresent());
        } catch (HandlingException e) {};
    }    
    
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void sendResetLinkSavesTheToken() throws Exception {
        BioDare2User user = fixtures.demoUser1;
        when(mailer.send(eq(user.getEmail()), anyString(), anyString())).thenReturn(true);
        
        List<UserToken> userTokens = tokens.findByUser(user);
        assertTrue(userTokens.isEmpty());
        handler.sendResetLink(user);
        
        
        userTokens = tokens.findByUser(user);
        assertEquals(1,userTokens.size());
        
        UserToken token = userTokens.get(0);
        assertEquals(UserTokenKind.PASSWORD_RESET,token.getKind());
        
    }   
    
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void sendResetLinkReturnsUserItSentTo() throws Exception {
        UserAccount user = fixtures.demoUser1;
        user.setEmail("ktos@cos.pl");
        when(mailer.send(eq(user.getEmail()), anyString(), anyString())).thenReturn(true);
        
        BioDare2User resp = handler.sendResetLink(user);
        
        assertEquals(user,resp);
        
        
    }  
    
  
    

    
}
