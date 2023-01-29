/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.listeners;

import java.io.IOException;
import jakarta.servlet.ServletException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 *
 * @author tzielins
 */
public class OKLogoutSuccessHandlerTest {
    
    public OKLogoutSuccessHandlerTest() {
    }

    @Test
    public void logoutSendOKAndMessage() throws IOException, ServletException {
        
        OKLogoutSuccessHandler instance = new OKLogoutSuccessHandler();
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse resp = new MockHttpServletResponse();        
        
        Authentication auth = new UsernamePasswordAuthenticationToken("tom", "password");
        
        instance.onLogoutSuccess(request, resp, auth);
        
        assertEquals(HttpStatus.OK.value(), resp.getStatus());
        assertEquals("{\"message\":\"tom is logged out\"}",resp.getContentAsString());
        
    }
    
}
