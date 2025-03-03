package ed.biodare2.backend.features.usage_stats;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.dto.AnalyticsDataDTO;
import ed.biodare2.backend.dto.UsageStatsDTO;
import ed.biodare2.backend.web.rest.UsageStatsController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(SimpleRepoTestConfig.class)
public class UsageStatsSchedulerTest {

    @Autowired
    private UsageStatsScheduler usageStatsScheduler;

    @MockitoBean
    private UsageStatsController usageStatsController;

    @Value("${bd2.usagestats.file:usage_stats.json}")
    String outputPath;
    private Path outputFile;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        outputFile = Paths.get(outputPath);
        Files.deleteIfExists(outputFile);
    }

    @Test
    public void testGenerateUsageStats() throws IOException {
        List<AnalyticsDataDTO> analyticsData = new ArrayList<>();
        analyticsData.add(new AnalyticsDataDTO("Poland", 100));

        List<UsageStatsDTO> usageStats = new ArrayList<>();
        usageStats.add(new UsageStatsDTO(2023, 100, 200, 50, 100, 1000));

        when(usageStatsController.getAnalyticsData()).thenReturn(analyticsData);
        when(usageStatsController.get_stats_by_year()).thenReturn(usageStats);

        usageStatsScheduler.generateUsageStats();

        verify(usageStatsController, times(1)).getAnalyticsData();
        verify(usageStatsController, times(1)).get_stats_by_year();
        
        assertTrue(Files.exists(outputFile));

        Map<String, Object> result = new ObjectMapper().readValue(outputFile.toFile(), new TypeReference<Map<String, Object>>() {});
        assertNotNull(result);
        assertTrue(result.containsKey("analytics"));
        assertTrue(result.containsKey("year_stats"));

        List<AnalyticsDataDTO> resultAnalytics = new ObjectMapper().convertValue(result.get("analytics"), new TypeReference<List<AnalyticsDataDTO>>() {});
        List<UsageStatsDTO> resultUsageStats = new ObjectMapper().convertValue(result.get("year_stats"), new TypeReference<List<UsageStatsDTO>>() {});

        assertEquals(analyticsData, resultAnalytics);
        assertEquals(usageStats, resultUsageStats);
    }
}