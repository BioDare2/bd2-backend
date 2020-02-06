/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2;

import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.security.dao.UserGroupRep;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@TestConfiguration
public class SimpleRepoTestConfig {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
        @Bean
        @Transactional        
        public Fixtures fixtures(UserAccountRep accountsR,UserGroupRep groups,PasswordEncoder passwordEncoder) {
            return Fixtures.build(accountsR,groups,passwordEncoder);
        }
        
        //@MockBean
        //DBFixer fixer;    

        /*
    @Bean
    @Autowired
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(Jackson2ObjectMapperBuilder builder) {

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(builder.build());
        //converter.setJsonPrefix(")]}',\n");
        converter.setPrefixJson(false);
        return converter;

    }    
    
    @Bean
    Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return (Jackson2ObjectMapperBuilder jomb) -> {
            jomb.serializerByType(TimeSeries.class, new TimeSeriesSerializer());
            jomb.deserializerByType(TimeSeries.class, new TimeSeriesDeSerializer());
        };
            
    }    */
        

}
