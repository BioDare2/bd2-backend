package ed.biodare2;

import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.BetaAnalyticsDataSettings;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Configuration
public class BetaAnalyticsDataClientConfig {

    @Value("${google.analytics.service.account.key}")
    private String serviceAccountKeyPath;

    @Bean
    public BetaAnalyticsDataClient betaAnalyticsDataClient(GoogleCredentials googleCredentials) throws IOException {
        BetaAnalyticsDataSettings settings = BetaAnalyticsDataSettings.newBuilder()
                .setCredentialsProvider(() -> googleCredentials)
                .build();
        return BetaAnalyticsDataClient.create(settings);
    }
}