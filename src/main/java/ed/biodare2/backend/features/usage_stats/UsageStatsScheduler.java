package ed.biodare2.backend.features.usage_stats;

import ed.biodare2.backend.dto.AnalyticsDataDTO;
import ed.biodare2.backend.dto.UsageStatsDTO;
import ed.biodare2.backend.dto.SpeciesStatsDTO;
import ed.biodare2.backend.web.rest.UsageStatsController;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UsageStatsScheduler {

    final Logger log = LoggerFactory.getLogger(this.getClass());
    final Path outputFile;
    final UsageStatsController usageStatsController;
    final ObjectMapper objectMapper;
    @Value("${bd2.usagestats.file:usage_stats.json}") String outputPath;

    public UsageStatsScheduler(UsageStatsController usageStatsController,
                               ObjectMapper objectMapper,
                               @Value("${bd2.usagestats.file:usage_stats.json}") String outputPath) {
        log.debug("jsonFile value: {}", outputPath);
        this.outputFile = Paths.get(outputPath);
        this.usageStatsController = usageStatsController;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24, initialDelay = 1000 * 10)  // every 24 hours after 10 seconds
    @Transactional
    public void generateUsageStats() throws IOException {
        Map<String, Object> usage = new HashMap<>();
        List<AnalyticsDataDTO> analytics_data = usageStatsController.getAnalyticsData();
        List<UsageStatsDTO> statsByYear = usageStatsController.get_stats_by_year();
        List<SpeciesStatsDTO> speciesStats = usageStatsController.get_stats_by_species();
        usage.put("analytics", analytics_data);
        usage.put("year_stats", statsByYear);
        usage.put("species_stats", speciesStats);
        objectMapper.writeValue(outputFile.toFile(), usage);
        log.info("Usage stats saved to " + outputFile.toAbsolutePath());
    }

}