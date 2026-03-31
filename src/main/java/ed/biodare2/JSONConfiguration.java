package ed.biodare2;

import tools.jackson.core.util.DefaultIndenter;
import tools.jackson.core.util.DefaultPrettyPrinter;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JSONConfiguration {

    // Removed date formatting as timestamps (should be iso instead) for simplicity
    // It's no longer under SerializationFeature
    // Could probably be found somewhere else if needed

    @Bean(name = "DomMapper")
    ObjectMapper domMapper() {
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter()
                .withArrayIndenter(DefaultPrettyPrinter.NopIndenter.instance())
                .withObjectIndenter(new DefaultIndenter(" ", "\n"));

        return JsonMapper.builder()
                // .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .defaultPrettyPrinter(pp)
                .build();
    }

    @Bean(name = "PlainMapper")
    ObjectMapper plainMapper() {
        return JsonMapper.builder()
                // .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.INDENT_OUTPUT)
                .build();
    }

    @Bean
    @Primary
    ObjectMapper defaultMapper() {
        return JsonMapper.builder()
                // .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.INDENT_OUTPUT)
                .build();
    }
}
