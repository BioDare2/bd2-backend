package ed.biodare2;

import ed.biodare2.backend.util.json.BD2eJTKDomModule;
import ed.biodare2.backend.util.json.TimeSeriesModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.JacksonModule;

@Configuration
public class MapperConfiguration {

    @Bean
    JacksonModule bd2eJTKDomModule() {
        return new BD2eJTKDomModule();
    }

    @Bean
    JacksonModule TimeSeriesModule() {
	return new TimeSeriesModule();
    }
}
