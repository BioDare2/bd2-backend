package ed.biodare2.backend.web.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FeaturedDatasetControllerTest {

    @TempDir
    Path tmp;

    @Test
    void returnsDeterministicIdFromFile() throws IOException {
        Path file = tmp.resolve("curated.txt");
        Files.writeString(file, """
                # Comment line
                12345

                67890
                54321
                """);

        FeaturedDatasetController ctrl = new FeaturedDatasetController(file.toString());

        // Call endpoint
        ResponseEntity<Long> resp = ctrl.getFeaturedDataset();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());

        // Reproduce expected using same logic
        List<String> ids = Files.readAllLines(file).stream()
                .map(String::trim)
                .filter(l -> !l.isEmpty() && !l.startsWith("#"))
                .toList();
        String expectedPick = ctrl.computeFeaturedIdForWeek(ids);
        assertEquals(Long.valueOf(expectedPick), resp.getBody());

        // Deterministic within same week
        ResponseEntity<Long> resp2 = ctrl.getFeaturedDataset();
        assertEquals(resp.getBody(), resp2.getBody());
    }

    @Test
    void returnsSingleIdIfOnlyOnePresent() throws IOException {
        Path file = tmp.resolve("one.txt");
        Files.writeString(file, "99999\n");
        FeaturedDatasetController ctrl = new FeaturedDatasetController(file.toString());

        ResponseEntity<Long> resp = ctrl.getFeaturedDataset();
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(99999L, resp.getBody());
    }

    @Test
    void returnsNoContentIfNoUsableIds() throws IOException {
        Path file = tmp.resolve("empty.txt");
        Files.writeString(file, """
                # only comments
                                
                # another
                """);
        FeaturedDatasetController ctrl = new FeaturedDatasetController(file.toString());

        ResponseEntity<Long> resp = ctrl.getFeaturedDataset();
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        assertNull(resp.getBody());
    }

    @Test
    void computeFeaturedIdForWeekHandlesNullOrEmpty() {
        FeaturedDatasetController ctrl = new FeaturedDatasetController("dummy");
        assertNull(ctrl.computeFeaturedIdForWeek(null));
        assertNull(ctrl.computeFeaturedIdForWeek(List.of()));
    }

    @Test
    void computeFeaturedIdForWeekReturnsElement() {
        FeaturedDatasetController ctrl = new FeaturedDatasetController("dummy");
        String pick = ctrl.computeFeaturedIdForWeek(List.of("111", "222", "333"));
        assertTrue(List.of("111", "222", "333").contains(pick));
    }
}
