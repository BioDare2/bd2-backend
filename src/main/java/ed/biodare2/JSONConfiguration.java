package ed.biodare2;

import tools.jackson.core.util.DefaultIndenter;
import tools.jackson.core.util.DefaultPrettyPrinter;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JSONConfiguration {

    @Bean(name = "DomMapper")
    ObjectMapper domMapper() {
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter()
                .withArrayIndenter(DefaultPrettyPrinter.NopIndenter.instance())
                .withObjectIndenter(new DefaultIndenter(" ", "\n"));

        return JsonMapper.builder()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .defaultPrettyPrinter(pp)
                .build();
    }
}
