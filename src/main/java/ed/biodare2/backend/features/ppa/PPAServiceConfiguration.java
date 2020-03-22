/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare.jobcentre2.client.JobCentreEndpointClient;
import ed.biodare.jobcentre2.client.JobCentreEndpointDirections;
import ed.biodare2.EnvironmentVariables;
import java.net.MalformedURLException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@Configuration
public class PPAServiceConfiguration {
    
    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @Bean
    public PPAServiceParameters PPAServiceParameters(EnvironmentVariables env,
            final String ppaUsername,
            final String ppaPassword,
            @Value("${ppa.jobcentre2.server.url}") String serverUrl,
            @Value("${ppa.jobcentre2.server.user}") String user,
            @Value("${ppa.jobcentre2.server.password}") String password,
            @Value("${ppa.jobcentre2.testClient:true}") boolean testClient
            ) throws MalformedURLException {
        
        
        PPAServiceParameters params = new PPAServiceParameters();
        params.backendURL = env.backendURL;
        params.ppaUsername = ppaUsername;
        params.ppaPassword = ppaPassword;
        params.testClient = testClient;
        params.directions = new JobCentreEndpointDirections();
        String url = serverUrl.endsWith("/") ? serverUrl : serverUrl+"/";
        url += "api/period";
        URL endpoint = new URL(url);
        params.directions.endpoint = endpoint.toString();
        params.directions.user = user;
        params.directions.password = password;
        
        return params;
        
    }
    
    @Bean
    public JobCentreEndpointClient ppaClient(RestTemplateBuilder builder, PPAServiceParameters parameters)  {
        
        log.info("PPAService configuration uses jobcentre at {}", parameters.directions);
        //MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        //converter.setPrefixJson(false);
        //builder = builder.messageConverters(converter);
        // to disable the json hijiking prefix which was not parsed by jobcentre
        builder = builder.defaultMessageConverters();
        return new JobCentreEndpointClient(builder, parameters.directions);
    }
    
    

        
    
}
