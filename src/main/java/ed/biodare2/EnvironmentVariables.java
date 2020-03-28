/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author tzielins
 */
public class EnvironmentVariables {
    
    final public Path storageDir;
    final public URL backendURL;
    //final public URL jobcentreURL;
    
    final public String recaptchaSiteKey;
    final public String recaptchaSecretKey;
    
    final public String mailHost;
    final public String mailUser;
    final public String mailPassword;
    
    
    public EnvironmentVariables(String storageDirPath,
                                String backendURL,
                                //String jobcentreURL,
                                String recaptchaSiteKey,
                                String recaptchaSecretKey,
                                String mailHost,
                                String mailUser,
                                String mailPassword
    ) {
        
        this.storageDir = Paths.get(storageDirPath);  
        try {
            this.backendURL = new URL(backendURL);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Problem with backendURL: "+e.getMessage(),e);
        }
        /*try {
            this.jobcentreURL = new URL(jobcentreURL);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Problem with jobcentreURL: "+e.getMessage(),e);
        }*/ 
        this.recaptchaSiteKey = recaptchaSiteKey;
        this.recaptchaSecretKey = recaptchaSecretKey;
        
        this.mailHost = mailHost;
        this.mailUser = mailUser;
        this.mailPassword = mailPassword;
        
    }
    
}
