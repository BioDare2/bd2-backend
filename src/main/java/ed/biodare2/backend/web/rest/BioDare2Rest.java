/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

/**
 *
 * @author tzielins
 */
public abstract class BioDare2Rest {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    
    
    /*
    protected UserAccount principalToAccount(Principal principal) {
        
        try {
            Authentication auth = (Authentication)principal;
            return ((UserAccount)auth.getPrincipal());//.getAccount();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Unsupported principal type: "+principal.getClass().getName()+" & "+e.getMessage(),e);
        } catch (NullPointerException e) {
            if (principal == null)
                throw new IllegalArgumentException("Null principal",e);
            else 
                throw new IllegalArgumentException("Null inner principal in: "+principal.getClass().getName(),e);
        }        
    }
    */
    
   
    
    protected void sendFile(Path file, String fileName, String contentType, boolean inline, HttpServletResponse response) throws ServerSideException {

        String contentDisposition = inline ? "inline" : "attachment";
        contentDisposition += "; filename=\""+fileName+"\"";
        
        try {
            response.setContentLength((int)Files.size(file));
            response.setContentType(contentType);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
            try {
                Files.copy(file, response.getOutputStream());
            } finally {
                response.getOutputStream().close();
            }
        } catch (IOException e) {
            throw new ServerSideException("Cannot send file: "+e.getMessage(),e);
        }
       
    }
}
