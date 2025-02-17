package ed.biodare2;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class GoogleCredentialsConfig {

    @Value("${google.analytics.service.account.key}")
    private String serviceAccountKeyPath;

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        return GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyPath));
    }
}