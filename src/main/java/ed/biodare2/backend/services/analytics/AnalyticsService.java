package ed.biodare2.backend.services.analytics;

import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.RunReportRequest;
import com.google.analytics.data.v1beta.RunReportResponse;
import com.google.analytics.data.v1beta.DateRange;
import com.google.analytics.data.v1beta.Dimension;
import com.google.analytics.data.v1beta.Metric;
import com.google.auth.oauth2.GoogleCredentials;
import ed.biodare2.backend.dto.AnalyticsDataDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final GoogleCredentials googleCredentials;
    private final BetaAnalyticsDataClient analyticsDataClient;

    @Value("${google.analytics.property.id}")
    private String propertyId;

    @Value("${google.analytics.service.account.key}")
    private String serviceAccountKeyPath;

    public AnalyticsService(GoogleCredentials googleCredentials, BetaAnalyticsDataClient analyticsDataClient) {
        this.googleCredentials = googleCredentials;
        this.analyticsDataClient = analyticsDataClient;
    }

    public List<AnalyticsDataDTO> getAnalyticsData() throws GeneralSecurityException, IOException {
        RunReportRequest request = RunReportRequest.newBuilder()
                .setProperty("properties/" + propertyId)
                .addDateRanges(DateRange.newBuilder().setStartDate("365daysAgo").setEndDate("today").build())
                .addMetrics(Metric.newBuilder().setName("activeUsers").build())
                .addDimensions(Dimension.newBuilder().setName("country").build())
                .build();

        RunReportResponse response = analyticsDataClient.runReport(request);

        return response.getRowsList().stream()
                .map(row -> new AnalyticsDataDTO(
                        row.getDimensionValues(0).getValue(),
                        Long.parseLong(row.getMetricValues(0).getValue())
                ))
                .collect(Collectors.toList());
    }
}
