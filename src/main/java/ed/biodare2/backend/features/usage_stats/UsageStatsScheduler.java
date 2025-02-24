package ed.biodare2.backend.features.usage_stats;

import ed.biodare2.backend.web.rest.UsageStatsController;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
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

    public UsageStatsScheduler(@Value("${bd2.usagestats.file:usage_stats.json}") String outputPath,
                               UsageStatsController usageStatsController,
                               ObjectMapper objectMapper) {
        this.outputFile = Paths.get(outputPath);
        this.usageStatsController = usageStatsController;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24, initialDelay = 1000 * 20)  // every 24 hours after 1 minute (1000*60*1)
    @Transactional
    public void generateUsageStats() throws IOException {
        log.info("Starting usage stats generation");
        Map<String, Integer> stats = usageStatsController.countStats();
        log.info("Usage stats generated: {}", stats);
        objectMapper.writeValue(outputFile.toFile(), stats);
        log.info("Usage stats saved to " + outputFile.toAbsolutePath());
    }
}