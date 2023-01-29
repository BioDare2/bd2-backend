/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.listeners;

import ed.biodare2.backend.web.tracking.SecurityTracker;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 *
 * @author tzielins
 */
public class OKLogoutSuccessHandler implements  LogoutSuccessHandler {

    final SecurityTracker tracker = new SecurityTracker();
    
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth) throws IOException, ServletException {
        
        String body = "{\"message\":\""+(auth != null ? auth.getName() : "null user")+" is logged out\"}";
        response.setStatus(HttpStatus.OK.value());
        try (PrintWriter w = response.getWriter()) {
            w.print(body);
        };
        
        tracker.handleLogout(auth);
        //tracker.userLogout(auth,request);
    }
    
}
