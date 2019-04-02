/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 *
 * @author tzielins
 */

@SpringBootApplication
public class BioDare2WSApplication {
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    //public static final String BACKEND_SERVICE_USERS = "BackendServiceUsers";
    public static final boolean DEBUG = true;
    public static final boolean DEBUG_WEB = false;
    
    // where the old BioDare1 ids starts
    static final long BD1LIMIT = 12500000000000L;
    
    public static void main(String[] args) {
        SpringApplication.run(BioDare2WSApplication.class, args);
    }        
    
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } 
    
 
    
    @Bean(name = "LocalValidator")
    @Autowired
    public Validator localValidatorFactoryBean() {
        Validator val = new LocalValidatorFactoryBean();
        return val;
    }
    

}



