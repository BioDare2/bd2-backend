/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend;

import org.springframework.boot.test.context.TestConfiguration;

/**
 *
 * @author tzielins
 */
//@Configuration
@TestConfiguration
public class MapperConfiguration {
    
    /*
    @Primary
    @Bean(name = "DomMapper")
    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.registerModule(new TimeSeriesModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }    
    
    @Bean(name = "PlainMapper")
    ObjectMapper plainObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.registerModule(new TimeSeriesModule());
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    } */   
}
