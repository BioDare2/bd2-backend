package ed.biodare2;

import tools.jackson.core.util.DefaultIndenter;
import tools.jackson.core.util.DefaultPrettyPrinter;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.JacksonModule;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JSONConfiguration {

    @Bean
    JsonMapperBuilderCustomizer dateAsTimestamps(JacksonModule bd2eJTKDomModule) {
	return builder -> builder
	    .addModule(bd2eJTKDomModule)
	    .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Bean(name = "DomMapper")
    ObjectMapper domMapper(JacksonModule bd2eJTKDomModule) {
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter()
                .withArrayIndenter(DefaultPrettyPrinter.NopIndenter.instance())
                .withObjectIndenter(new DefaultIndenter(" ", "\n"));

        return JsonMapper.builder()
	    .addModule(bd2eJTKDomModule)
	    .enable(SerializationFeature.INDENT_OUTPUT)
	    .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
	    .defaultPrettyPrinter(pp)
	    .build();
    }
}
