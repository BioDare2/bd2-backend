/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2;

import static ed.biodare2.BioDare2WSApplication.BD1LIMIT;
import ed.biodare2.local.DBFixer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Zielu
 */
@Configuration
public class StartUpConfiguration {
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    
    @Transactional
    @Bean
    @Profile("local")
    @Order(1)
    public CommandLineRunner dbRestore(Environment env,DBFixer fixer) {
        
        
        log.warn("dbRestore called");
        
        if (isOnProduction(env)) {
            throw new IllegalStateException("DBResotore called in production environment");
        }
        
        if (!isOnProduction(env)) {
            log.warn("RUNNING locally");
            
            if (isInMemory(env)) {
                log.warn("Running with in-memory DB, initializing users and restoring experiments");
                
                fixer.configureAccounts();
                fixer.restoreDBSystemInfos();
                fixer.updateLastIds(BD1LIMIT);
                
                // these are no longer needed
                //fixer.addSubscriptions();
                //fixer.addFeaturesAvailability();
                //fixer.addRDMAspects();
                //fixer.fixDataCategory();
                
                //fixer.fixFileInfos();
                
            } else {
                log.warn("Running with persistent DB");
                
                /*
                fixer.configureAccounts( simplePass);
                fixer.restoreDBSystemInfos();
                fixer.updateLastIds();                
                //*/        
                
            }
            
        } 
        return (evt) -> {};
    }       
    
    @Transactional
    @Bean
    @Profile("local")
    @Order(10)
    public CommandLineRunner indexRestore(Environment env,DBFixer fixer) {
        
        
        log.warn("indexRestore called");
        
        if (isOnProduction(env)) {
            throw new IllegalStateException("indexRestore called in production environment");
        }
        
        if (!isOnProduction(env)) {
            log.warn("RUNNING locally");
        }            

        fixer.reindexAll();    
        
        return (evt) -> {
        
        };
    }       
    
    /*
    @Transactional
    @Bean
    @Order(10)
    public CommandLineRunner recalculateDataMetrics(Environment env, DBFixer fixer) {
        
        
        log.warn("recalculating data metrics");
        

        
        fixer.recalculateDataMetrics();
            
        return (evt) -> {};
    }  
    */
    
    /*
    @Transactional
    @Bean
    @Order(1)
    public CommandLineRunner init(Environment env,DBFixer fixer) {
        
        log.warn("Main init called");
        
        if (!isOnProduction(env)) {
            log.warn("RUNNING locally");
            
            boolean simplePass = true;
            
            if (isInMemory(env)) {
                log.warn("Running with in-memory DB, initializing users and restoring experiments");
                
                fixer.configureAccounts( simplePass);
                fixer.restoreDBSystemInfos();
                fixer.updateLastIds(BD1LIMIT);
                
                //fixer.addSubscriptions();
                //fixer.addFeaturesAvailability();
                //fixer.addRDMAspects();
                //fixer.fixDataCategory();
                
                //fixer.fixFileInfos();
                
            } else {
                log.warn("Running with persistent DB");
                
                
                //fixer.configureAccounts( simplePass);
                //fixer.restoreDBSystemInfos();
                //fixer.updateLastIds();                
                        
                
                //fixer.addSubscriptions();
                //fixer.addFeaturesAvailability();
                //fixer.addRDMAspects();
                //fixer.fixDataCategory();
                //fixer.fixFileInfos();

            }
            
        } else {
            log.warn("RUNNING in production");
            if (isInMemory(env)) {
                throw new IllegalStateException("Production mode cannot run with in-memory DB");
            }
        }
        return (evt) -> {};
    }
    //*/
    
    /*
    @Transactional
    @Bean
    @Order(10)
    public CommandLineRunner ppaFixer(Environment env,DBFixer fixer) {
        
        log.warn("circFixer called");
        
        if (!isOnProduction(env)) {
            log.warn("RUNNING locally");
            
            //fixer.upgradeDataTraces();
            //fixer.migrateJobs();
            //
            //fixer.migratePPAArtifacts();
            //fixer.redoJobsIndResults();
            //fixer.redoJobsProcessing();
            
        } else {
            log.warn("RUNNING in production");
         
            //fixer.upgradeDataTraces();
            //fixer.migrateJobs();
            //
            //fixer.migratePPAArtifacts();
            //fixer.redoJobsIndResults();
            //fixer.redoJobsProcessing();        
        
        }
        return (evt) -> {};
    } 
    //*/       
    

    protected boolean isInMemory(Environment env) {
        
        //return true;
        return (env.getProperty("spring.datasource.url") == null);
        
    }    
    
    protected boolean isOnProduction(Environment env) {
        return env.getProperty("bd2.production","false").equals("true");
    }    
}
