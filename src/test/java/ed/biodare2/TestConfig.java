package ed.biodare2;

import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.auth.oauth2.GoogleCredentials;
import ed.biodare2.backend.services.analytics.AnalyticsService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;

@Configuration
public class TestConfig {

    @Bean
    public GoogleCredentials googleCredentials() throws IOException {
        return Mockito.mock(GoogleCredentials.class);
    }

    @Bean
    @Primary
    public BetaAnalyticsDataClient betaAnalyticsDataClient() {
        return Mockito.mock(BetaAnalyticsDataClient.class);
    }

    @Bean
    @Primary
    public AnalyticsService analyticsService(GoogleCredentials googleCredentials, BetaAnalyticsDataClient betaAnalyticsDataClient) {
        return new AnalyticsService(googleCredentials, betaAnalyticsDataClient);
    }
}
