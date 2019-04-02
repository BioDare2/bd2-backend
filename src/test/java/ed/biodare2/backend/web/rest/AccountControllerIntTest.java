/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.handlers.UsersHandler;
import ed.biodare2.backend.security.BioDare2User;
import ed.biodare2.backend.services.mail.Mailer;
import ed.biodare2.backend.services.recaptcha.ReCaptchaService;
import static ed.biodare2.backend.web.rest.AbstractIntTestBase.APPLICATION_JSON_UTF8;
import static ed.biodare2.backend.web.rest.AbstractIntTestBase.MAP_TYPE;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
/**
 *
 * @author Zielu
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SimpleRepoTestConfig.class)
public class AccountControllerIntTest  extends AbstractIntTestBase {


    
    final String serviceRoot = "/api/account";
    
    @MockBean
    ReCaptchaService captcha;
    
    @MockBean
    Mailer mailer;
    
    
    @Autowired
    UserAccountRep users;
    
    @Test
    public void currentAccountGivesLoggedInAccount() throws Exception {
        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);
        
        
        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("RESP: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> res = mapper.readValue(resp.getResponse().getContentAsString(), MAP_TYPE);
        assertNotNull(res);
        
        assertEquals(currentUser.getLogin(),res.get("login"));
        assertFalse(res.containsKey("password"));
        
    }
    
    @Test
    public void accountGivesRequestedAccount() throws Exception {
        
        String login = fixtures.demoUser.getLogin();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+"/"+login)
                .accept(APPLICATION_JSON_UTF8);
                
        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("RESP: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> res = mapper.readValue(resp.getResponse().getContentAsString(), MAP_TYPE);
        assertNotNull(res);
        
        assertEquals(login,res.get("login"));
        assertFalse(res.containsKey("password"));
        
    }
  
    @Test
    public void accountGivesNotFoundForWrongLogin() throws Exception {
        
        String login = "jakis";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+"/"+login)
                .accept(APPLICATION_JSON_UTF8);
                
        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();

        assertNotNull(resp);
        
    }
    
    @Test
    public void availableGivesFalseOnUsedLogin() throws Exception {
        
        String login = fixtures.demoUser.getLogin();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/available-login")
                .content(login)
                .accept(APPLICATION_JSON_UTF8);
                
        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("RESP: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        assertEquals("false",resp.getResponse().getContentAsString());
        
        
    }  
    
    @Test
    public void availableGivesTrueOnUnusedLogin() throws Exception {
        
        String login = "jakis";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/available-login")
                .content(login)
                .accept(APPLICATION_JSON_UTF8);
                
        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("RESP: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        assertEquals("true",resp.getResponse().getContentAsString());
        
        
    }        
    
    
    @Test
    public void availableGivesFalseOnReservedLogins() throws Exception {
        
        List<String> reserved = Arrays.asList("available-login","academic-email","suitable-email");
        
        for (String login : reserved) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/available-login")
                .content(login)
                .accept(APPLICATION_JSON_UTF8);
                
        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("RESP: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        assertEquals("false",resp.getResponse().getContentAsString());
        }
        
    }  
    
    
    @Test
    public void isSuitableEmailReturnsCorrectResponse() throws Exception {
        
        String email = "jakis@ed.ac.uk";
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/suitable-email")
                .content(email)
                .accept(APPLICATION_JSON_UTF8);
                
        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        UsersHandler.EmailSuitability res = mapper.readValue(resp.getResponse().getContentAsString(),UsersHandler.EmailSuitability.class);

        assertNotNull(res);
        assertTrue(res.isFree);
        assertTrue(res.isAcademic);
        
        email = fixtures.demoUser.getEmail();
        
        builder = MockMvcRequestBuilders.post(serviceRoot+"/suitable-email")
                .content(email)
                .accept(APPLICATION_JSON_UTF8);
                
        resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        res = mapper.readValue(resp.getResponse().getContentAsString(),UsersHandler.EmailSuitability.class);

        assertNotNull(res);
        assertFalse(res.isFree);
        assertTrue(res.isAcademic);    
    }        
    
    
    @Test
    public void registerRegistersTheAccount() throws Exception {
        
        when(captcha.verify(anyString())).thenReturn(true);
        when(mailer.send(anyString(), anyString(), anyString())).thenReturn(true);
                
        Map<String,String> details = new HashMap<>();
        String email = "synsysad@ed.AC.uk";
        details.put("login","an_Login");
        details.put("email",email);
        details.put("password", " don't trim me");
        details.put("firstName"," TomeK");
        details.put("lastName"," ZedW'S");
        details.put("institution","Inst");        
        details.put("terms","true");
        details.put("g_recaptcha_response","fakecaptcha");
        
        String orgJSON = mapper.writeValueAsString(details);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(serviceRoot)
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                ;//.with(mockAuthentication);        
        
        MvcResult resp = mockMvc.perform(builder)
                //.andExpect(MockMvcResultMatchers.status().isOk())
                //.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("RESP: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> res = mapper.readValue(resp.getResponse().getContentAsString(), MAP_TYPE);
        assertNotNull(res);
        
        assertEquals("an_login",res.get("login"));
        assertEquals(email.toLowerCase().trim(),res.get("email"));        
        assertFalse(res.containsKey("password"));
        
    }    
    
    
    @Test
    public void updateUpdatesTheAccount() throws Exception {
        
        when(captcha.verify(Matchers.anyString())).thenReturn(true);
                
        
        Map<String,String> details = new HashMap<>();
        details.put("login",currentUser.getLogin());
        details.put("email","update@email.notacademic.pl");
        details.put("password", "NewPassword");
        details.put("firstName","UP"+currentUser.getFirstName());
        details.put("lastName","UP"+currentUser.getLastName());
        details.put("institution","UP"+currentUser.getInstitution());        
        details.put("currentPassword","user1");
        
        String orgJSON = mapper.writeValueAsString(details);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot)
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);        
        
        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("RESP: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> res = mapper.readValue(resp.getResponse().getContentAsString(), MAP_TYPE);
        assertNotNull(res);
        
        assertEquals(currentUser.getLogin(),res.get("login"));
        assertEquals("update@email.notacademic.pl",res.get("email"));        
        assertFalse(res.containsKey("password"));
        
        assertEquals("update@email.notacademic.pl",users.findByLogin(currentUser.getLogin()).get().getEmail());
        
    }   
    
    @Test
    public void remindGivesResetLink() throws Exception {
        
        when(captcha.verify(anyString())).thenReturn(true);
        when(mailer.send(anyString(), anyString(), anyString())).thenReturn(true);
            
        BioDare2User user = fixtures.demoUser;
        Map<String,String> details = new HashMap<>();
        details.put("identifier",user.getLogin());
        details.put("g_recaptcha_response","fakecaptcha");
        
        String orgJSON = mapper.writeValueAsString(details);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/remind")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                ;//.with(mockAuthentication);        
        
        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("RESP: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> res = mapper.readValue(resp.getResponse().getContentAsString(), MAP_TYPE);
        assertNotNull(res);
        
        assertEquals(user.getEmail(),res.get("email"));
        
    }    
    
    
}
