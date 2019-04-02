/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 *
 * @author Zielu
 */
@Configuration
public class JSONConfiguration {
    
    
    //to provide JSON prefix on rest response and avoid json hijack tricks
    @Bean
    @Profile("!test")
    @Autowired
    //public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(Jackson2ObjectMapperBuilder builder) {
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(ObjectMapper mapper) {

        //MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(builder.build());
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);
        converter.setJsonPrefix(")]}',\n");
        //converter.setPrefixJson(true);
        return converter;

    }
    
    


    @Bean(name = "DomMapper")
    ObjectMapper domMapper(Jackson2ObjectMapperBuilder builder) {
    
        
        ObjectMapper mapper = builder.build();
        DefaultPrettyPrinter  pp = (new DefaultPrettyPrinter())
                .withoutSpacesInObjectEntries()
                .withArrayIndenter(new DefaultPrettyPrinter.NopIndenter())
                .withObjectIndenter(new DefaultIndenter(" ", "\n"));
        mapper.setDefaultPrettyPrinter(pp);
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;       
    }
    
    @Bean(name = "PlainMapper")
    ObjectMapper plainMapper(Jackson2ObjectMapperBuilder builder) {
    
        
        ObjectMapper mapper = builder.build();
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        return mapper;       
    }
    
    @Bean
    @Primary
    ObjectMapper defaultMapper(Jackson2ObjectMapperBuilder builder) {
    
        
        ObjectMapper mapper = builder.build();
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.INDENT_OUTPUT);
        return mapper;       
    }    
    
    /*
    @Bean(name = "DomMapper")
    ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        //mapper.registerModule(new TimeSeriesModule());
        DefaultPrettyPrinter  pp = (new DefaultPrettyPrinter())
                .withoutSpacesInObjectEntries()
                .withArrayIndenter(new DefaultPrettyPrinter.NopIndenter())
                .withObjectIndenter(new DefaultIndenter(" ", "\n"));
        mapper.setDefaultPrettyPrinter(pp);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper;
    }
    */
    
    /*@Bean
    Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return (Jackson2ObjectMapperBuilder jomb) -> {
            jomb.serializerByType(TimeSeries.class, new TimeSeriesSerializer());
            jomb.deserializerByType(TimeSeries.class, new TimeSeriesDeSerializer());
        };
            
    }*/    
    
}
