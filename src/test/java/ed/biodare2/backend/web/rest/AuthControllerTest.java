/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.web.rest.AuthController;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.security.dao.UserAccountRep;
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
public class AuthControllerTest {
    

    //AccountRep accounts;
    UserAccount account;
    UserAccountRep accounts;
    
    AuthController controller;
    
    
    
    public AuthControllerTest() {
    }
    
    
    @Before
    public void setUp() {
        account = UserAccount.testInstance(1234);
        account.setLogin("test1");
        account.setFirstName("Test");
        account.setLastName("User1");
        account.setEmail("test1@ed.ac.uk");
        account.setPassword("xyz");
                
        accounts = mock(UserAccountRep.class);
        when(accounts.findById(eq(account.getId()))).thenReturn(Optional.of(account));
        //accounts = mock(AccountRep.class);
        //controller = new AuthController(accounts);
        //controller = new AuthController(accounts);
        controller = new AuthController();
        
        
        //*/
    }
    
    @After
    public void tearDown() {
    }

    

    
    /**
     * Test of user method, of class AuthController.
     */
    @Test
    public void givesSessionVersionOfUser() {
        BioDare2User user = account;
        
        //when(accounts.findByLogin(account.getLogin())).thenReturn(Optional.of(account));
        
        Map<String,Object> userMap = controller.user(user);        
        assertNotNull(userMap);
        assertEquals(account.getLogin(),userMap.get("login"));
        assertEquals(account.getFirstName(),userMap.get("firstName"));
        assertEquals(account.getLastName(),userMap.get("lastName"));
        assertEquals(account.getEmail(),userMap.get("email"));
        assertEquals(account.isAnonymous(),userMap.get("anonymous"));
        //verify(accounts).findOne(eq(account.getId()));
    }
    
    @Test
    public void givesAnonymousUserDirectly() {
        UserAccount user = account;
        user.setAnonymous(true);
        Map<String,Object> userMap = controller.user(user);        
        assertNotNull(userMap);
        assertEquals(account.getLogin(),userMap.get("login"));
        verify(accounts,never()).findById(eq(account.getId()));        
    }

    @Test
    public void neverSendsPasswordIfUserCalled() {
        BioDare2User user = account;
        
        //when(accounts.findByLogin(account.getLogin())).thenReturn(Optional.of(account));
        
        Map<String,Object> userMap = controller.user(user);
        assertFalse(userMap.containsKey("password"));
        assertFalse(userMap.containsValue("xyz"));
    }
    
}
