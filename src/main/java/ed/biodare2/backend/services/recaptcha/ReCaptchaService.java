/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.services.recaptcha;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.EnvironmentVariables;
import ed.biodare2.backend.web.rest.ServerSideException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author Zielu
 */
@Service
public class ReCaptchaService {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());
    final String recaptchaURL;
    final String recaptchaSiteKey;
    final String recaptchaSecretKey;
    //final ObjectMapper mapper;

    final RestTemplate restTemplate;
    
    @Autowired
    public ReCaptchaService(EnvironmentVariables environment){//,RestTemplate restTemplate) {
        this.recaptchaSiteKey = environment.recaptchaSiteKey;
        this.recaptchaSecretKey = environment.recaptchaSecretKey;
        
        this.recaptchaURL= "https://www.google.com/recaptcha/api/siteverify"+"?secret="+this.recaptchaSecretKey+"&response=";
        //this.mapper = mapper;
        this.restTemplate = new RestTemplate(); //restTemplate;
    }
    
    public boolean verify(String challenge) {
        
        String add =  recaptchaURL+challenge;
        //log.debug("URL: "+add);
        Map resp = restTemplate.postForObject(add, "", Map.class);
        
        //log.debug("Captcha response {}",resp);
        List<String> err = (List)resp.getOrDefault("error-codes", null);
        
        /*
        if (err != null) {
            log.error("captcha errors: {}",err);
        }*/
        
        if (err != null && err.stream().anyMatch(e -> !("invalid-input-response".equals(e)))) {
            log.error("Captcha error: {}",err);
            throw new ServerSideException("Problem with captcha: "+err.stream().collect(Collectors.joining(",")));
        }
        
        Boolean ans = (Boolean)resp.getOrDefault("success", Boolean.FALSE);
        
        return ans;
        
    }
}
