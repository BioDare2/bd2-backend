/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;

/**
 * Not finished not used but maybe one day
 * 
 * @ContextConfiguration(initializers = TestFolderInitializer.class)
 * @author tzielins
 */
public class TestFolderInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
 
    final Logger log = LoggerFactory.getLogger(this.getClass());
 
    @Override
    public void initialize(ConfigurableApplicationContext context) {
        
        log.info("\nInitializer starts");
        
        String orgPath = context.getEnvironment().getProperty("bd2.storage.dir");
        
        log.info("Org storage path {}\n", orgPath);
        
        // context.addApplicationListener(al);
        //TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
        //  configurableApplicationContext, "example.firstProperty=" + PROPERTY_FIRST_VALUE);
 
        //TestPropertySourceUtils.addPropertiesFilesToEnvironment(
        //  configurableApplicationContext, "context-override-application.properties");
    }
    
}
