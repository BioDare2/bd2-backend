/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2;

/**
 *
 * @author tzielins
 */
public class MockEnvironmentVariables {
    
    public String storageDir = ".";
    public String backendURL = "http://localhost";
    //public String jobcentreURL = "http://localhost:8084/JobCenter/PPAJobCenterWS?wsdl";
    
    public String recaptchaSiteKey = "";
    public String recaptchaSecretKey = "";
    
    public String mailHost ="";
    public String mailUser ="";
    public String mailPassword="";
    
    
    public EnvironmentVariables mock() {
        return new EnvironmentVariables(
                    storageDir, 
                    backendURL,
                    //jobcentreURL, 
                    recaptchaSiteKey, 
                    recaptchaSecretKey, 
                    mailHost, 
                    mailUser, 
                    mailPassword);
    }
    

    
}
