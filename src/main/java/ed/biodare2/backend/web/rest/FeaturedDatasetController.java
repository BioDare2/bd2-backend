package ed.biodare2.backend.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
public class FeaturedDatasetController extends BioDare2Rest {

    private static final Logger log = LoggerFactory.getLogger(FeaturedDatasetController.class);

    private final Path curatedDatasetsFile;

    FeaturedDatasetController(@Value("${bd2.curated-datasets.file:curated_datasets.txt}") String curatedDatasetsFile) {
        this.curatedDatasetsFile = Paths.get(curatedDatasetsFile);
    }

    @GetMapping("featured-dataset")
    public ResponseEntity<Long> getFeaturedDataset() {
        try {
            Long featuredId = getFeaturedDatasetId();
            if (featuredId == null) return ResponseEntity.noContent().build();
            return ResponseEntity.ok(featuredId);
        } catch (IOException e) {
            log.error("Error reading curated datasets file: {}", curatedDatasetsFile, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    public Long getFeaturedDatasetId() throws IOException {
        try (var lines = Files.lines(curatedDatasetsFile)) {
            List<String> ids = lines
                    .map(String::trim)
                    .filter(l -> !l.isEmpty() && !l.startsWith("#"))
                    .collect(Collectors.toList());
            String pick = computeFeaturedIdForWeek(ids);
            if (pick == null) return null;
            return Long.valueOf(pick);
        }
    }

    String computeFeaturedIdForWeek(List<String> ids) {
        if (ids == null || ids.isEmpty()) return null;
        var now = java.time.LocalDate.now(java.time.ZoneOffset.UTC);
        var wf = java.time.temporal.WeekFields.ISO;
        int year = now.get(wf.weekBasedYear());
        int week = now.get(wf.weekOfWeekBasedYear());
        int idx = Math.floorMod(Objects.hash(year, week), ids.size());
        return ids.get(idx);
    }
}
