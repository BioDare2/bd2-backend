package ed.biodare2.backend.features.usage_stats;

import ed.biodare2.SimpleRepoTestConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import ed.biodare2.backend.web.rest.UsageStatsController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(SimpleRepoTestConfig.class)
public class UsageStatsSchedulerTest {

    @Autowired
    private UsageStatsScheduler usageStatsScheduler;

    @MockitoBean
    private UsageStatsController usageStatsController;

    private Path outputFile;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        outputFile = Paths.get("usage_stats.json");
        Files.deleteIfExists(outputFile);
    }

    @Test
    public void testGenerateUsageStats() throws IOException {        
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalSets", 10);
        stats.put("totalPublicSets", 5);
        stats.put("totalSeries", 20);
        stats.put("totalPublicSeries", 10);
        stats.put("totalUsers", 100);

        when(usageStatsController.countStats()).thenReturn(stats);

        usageStatsScheduler.generateUsageStats();

        verify(usageStatsController, times(1)).countStats();
    }
}