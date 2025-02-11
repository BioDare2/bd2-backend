/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2;

import static ed.biodare2.BioDare2WSApplication.BD1LIMIT;
import ed.biodare2.backend.util.concurrent.id.IdGenerator;
import ed.biodare2.backend.util.concurrent.id.IdGenerators;
import ed.biodare2.backend.util.concurrent.id.LongRecordManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Zielu
 */
@Configuration
public class IdsConfiguration {
    
    public static final String DATA_SOURCE_VERSION = "DataSourceVersion";
    public static final String EXPID_PROVIDER = "ExpIdProvider"; 
    public static final String ASSETSID_PROVIDER = "AssetsIdProvider";
    
    @Bean
    IdGenerators idGenerators(LongRecordManager recordManager) {
        IdGenerators generators = new IdGenerators(recordManager);
        
        generators.initGenerator(DATA_SOURCE_VERSION, 10,10000,Long.MAX_VALUE);
        generators.initGenerator(EXPID_PROVIDER, 10,10000,BD1LIMIT); //there are some exp with ids above it in biodare
        generators.initGenerator(ASSETSID_PROVIDER, 10,10000,Long.MAX_VALUE);
        return generators;
    }
    
    @Bean(name = ASSETSID_PROVIDER)
    IdGenerator assetsIdProvider(IdGenerators generators) {
        return generators.getGenerator(ASSETSID_PROVIDER);
    }  
    
    @Bean(name = EXPID_PROVIDER)
    IdGenerator expIdProvider(IdGenerators generators) {
        return generators.getGenerator(EXPID_PROVIDER);
    }    
    
    @Bean(name = DATA_SOURCE_VERSION)
    IdGenerator dataSourceVersionProvider(IdGenerators generators) {
        return generators.getGenerator(DATA_SOURCE_VERSION);
    }  
    
}
