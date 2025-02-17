package ed.biodare2.backend.services.analytics;

import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.DimensionValue;
import com.google.analytics.data.v1beta.MetricValue;
import com.google.analytics.data.v1beta.Row;
import com.google.analytics.data.v1beta.RunReportRequest;
import com.google.analytics.data.v1beta.RunReportResponse;
import com.google.auth.oauth2.GoogleCredentials;
import ed.biodare2.backend.dto.AnalyticsDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AnalyticsServiceTest {

    @Mock
    private BetaAnalyticsDataClient analyticsDataClient;

    @Mock
    private GoogleCredentials googleCredentials;

    @InjectMocks
    private AnalyticsService analyticsService;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        googleCredentials = GoogleCredentials.create(null).createScoped(List.of("https://www.googleapis.com/auth/analytics.readonly"));
        analyticsService = new AnalyticsService(googleCredentials, analyticsDataClient);
    }

    @Test
    public void testGetAnalyticsData() throws GeneralSecurityException, IOException {
        // Mock the response from the analyticsDataClient
        RunReportResponse response = RunReportResponse.newBuilder()
                .addRows(Row.newBuilder()
                        .addDimensionValues(DimensionValue.newBuilder().setValue("USA").build())
                        .addMetricValues(MetricValue.newBuilder().setValue("1000").build())
                        .build())
                .build();

        when(analyticsDataClient.runReport(any(RunReportRequest.class))).thenReturn(response);

        // Call the method under test
        List<AnalyticsDataDTO> result = analyticsService.getAnalyticsData();

        // Verify the result
        assertEquals(1, result.size());
        assertEquals("USA", result.get(0).getCountry());
        assertEquals(1000, result.get(0).getSessions());
    }
}