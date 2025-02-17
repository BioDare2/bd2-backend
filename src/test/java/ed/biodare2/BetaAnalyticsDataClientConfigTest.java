package ed.biodare2;

import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.auth.oauth2.GoogleCredentials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@SpringJUnitConfig
@ContextConfiguration(classes = {BetaAnalyticsDataClientConfigTest.TestConfig.class})
public class BetaAnalyticsDataClientConfigTest {

    @Autowired
    private BetaAnalyticsDataClient betaAnalyticsDataClient;

    @Test
    public void testBetaAnalyticsDataClientBean() {
        assertNotNull(betaAnalyticsDataClient, "BetaAnalyticsDataClient bean should not be null");
    }

    @Configuration
    static class TestConfig {
        @Bean
        public GoogleCredentials googleCredentials() {
            return mock(GoogleCredentials.class);
        }

        @Bean
        public BetaAnalyticsDataClient betaAnalyticsDataClient() {
            return mock(BetaAnalyticsDataClient.class);
        }
    }
}