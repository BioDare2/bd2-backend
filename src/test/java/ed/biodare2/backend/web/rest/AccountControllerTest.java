/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.Fixtures;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.handlers.UsersHandler;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.services.recaptcha.ReCaptchaService;
import ed.biodare2.backend.web.tracking.AccountTracker;
import ed.biodare2.backend.web.tracking.SecurityTracker;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Zielu
 */
public class AccountControllerTest {
    
    
    UserAccountRep accounts;
    UsersHandler usersHandler;
    AccountTracker tracker;
    SecurityTracker secTracker;
    UserAccount account;
    ReCaptchaService captcha;
    
    AccountController controller;
    Fixtures fixtures;
    
    public AccountControllerTest() {

    }
    
    
    @Before
    public void setUp() {
        fixtures = Fixtures.build();
        
        tracker = new AccountTracker();
        secTracker = new SecurityTracker();
                
        
        accounts = mock(UserAccountRep.class);
        usersHandler = mock(UsersHandler.class);
        captcha = mock(ReCaptchaService.class);
        when(captcha.verify(anyString())).thenReturn(true);
        controller = new AccountController(accounts,usersHandler,captcha,tracker,secTracker);
        
        account = fixtures.demoUser;
        /*account = new UserAccount();
        account.setLogin("test1");
        account.setFirstName("Test");
        account.setLastName("User1");
        account.setEmail("test1@ed.ac.uk");
        account.setPassword("xyz");*/
        
    }
    
    @After
    public void tearDown() {
    }
    
    /*@Test
    public void currentAccountGivesConvertedPrincipal() {
        BioDare2User user = account;//new BioDare2User(account, Collections.emptyList());
        
        Map<String,Object> map = controller.currentAccount(user);
        assertEquals(account.getLogin(),map.get("login"));
        assertEquals(account.getFirstName(),map.get("firstName"));
        assertFalse(map.containsKey("password"));
        
    }*/
    
    @Test
    public void currentAccountGivesVersionFromTheDbNotSession() {
        
        UserAccount user1 = fixtures.demoUser;
        
        UserAccount user2 = UserAccount.testInstance(123);
        user2.setLogin(user1.getLogin());
        user2.setSupervisor(user2);
        assertNotEquals(user1,user2);
        assertNotEquals(user1.getEmail(),user2.getEmail());
        
        when(accounts.findByLogin(eq(user2.getLogin()))).thenReturn(Optional.of(user1));
        
        Map<String,Object> resp = controller.currentAccount(user2);
        assertEquals(user1.getEmail(),resp.get("email"));
    }
    
    @Test
    public void registerGivesErrorOnFailedCaptcha() throws UsersHandler.AccountHandlingException {
        
        Map<String,String> details = new HashMap<>();
        details.put("email", "bla@bla");
        
        BioDare2User user = fixtures.demoUser;
        when(usersHandler.register(eq(details))).thenReturn(user);   
        
        when(captcha.verify(anyString())).thenReturn(false);
        
        
        try {
            Map<String,Object> acc = controller.register(details, account);
            fail("Exception expected");
        } catch (HandlingException e) {}
    } 
    
    @Test
    public void registerGivesErrorOnFullAccount() {
        
        Map<String,String> details = new HashMap<>();
        details.put("email", "bla@bla");
        
        try {
            Map<String,Object> acc = controller.register(details, account);
            fail("Exception expected");
        } catch (HandlingException e) {}
    }  
    
    @Test
    public void registerReturnsRegisteredUserFromTheHandler() throws Exception {
        
        Map<String,String> details = new HashMap<>();
        details.put("email", "bla@bla");
        details.put("g_recaptcha_response","xxx");
        
        BioDare2User user = fixtures.demoUser;
        when(usersHandler.register(eq(details))).thenReturn(user);        
        Map<String,Object> acc = controller.register(details, fixtures.anonymous);
        verify(usersHandler).register(eq(details));
        
        assertEquals(user.getLogin(),acc.get("login"));
        assertEquals(user.getEmail(),acc.get("email"));
    }    
    
    @Test
    public void updateMarksTheCurrentUserAsDirty() throws Exception {
        
        BioDare2User user = fixtures.demoUser;
        Map<String,String> details = new HashMap<>();
        details.put("email", "bla@bla");
        details.put("login",user.getLogin());

        
        BioDare2User userRes = fixtures.demoUser1;
        when(usersHandler.update(eq(details),eq(user))).thenReturn(userRes);        
        
        Map<String,Object> acc = controller.update(details, user);
        verify(usersHandler).update(eq(details),eq(user));
        
        assertTrue(user.hasDirtySession());
        assertFalse(userRes.hasDirtySession());
        
    }    


    @Test
    public void account2userMapDoesNotAddPassword() {
        
        Map<String,Object> userMap = AccountController.account2UserMap(account);
        assertFalse(userMap.containsKey("password"));
        assertFalse(userMap.containsValue("xyz"));
    }
    

    
    @Test
    public void account2userMapsCorrectValues() {
        
        Map<String,Object> userMap = AccountController.account2UserMap(account);
        assertEquals(account.getLogin(),userMap.get("login"));
        assertEquals(account.getFirstName(),userMap.get("firstName"));
        assertEquals(account.getLastName(),userMap.get("lastName"));
        assertEquals(account.getEmail(),userMap.get("email"));
        assertEquals(account.isAnonymous(),userMap.get("anonymous"));
        assertEquals(account.getInstitution(),userMap.get("institution"));
    }
    
}
