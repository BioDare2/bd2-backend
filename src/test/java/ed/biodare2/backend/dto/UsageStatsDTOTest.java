package ed.biodare2.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsageStatsDTOTest {

    @Test
    public void testUsageStatsDTO() {
        int year = 2023;
        long sets = 100;
        long series = 200;
        long publicSets = 50;
        long publicSeries = 100;
        long users = 1000;

        UsageStatsDTO usageStats = new UsageStatsDTO(year, sets, series, publicSets, publicSeries, users);

        assertEquals(year, usageStats.getYear());
        assertEquals(sets, usageStats.getSets());
        assertEquals(series, usageStats.getSeries());
        assertEquals(publicSets, usageStats.getPublic_sets());
        assertEquals(publicSeries, usageStats.getPublic_series());
        assertEquals(users, usageStats.getUsers());
    }

    @Test
    public void testSetters() {
        UsageStatsDTO usageStats = new UsageStatsDTO(2023, 100, 200, 50, 100, 1000);

        usageStats.setYear(2024);
        usageStats.setSets(150);
        usageStats.setSeries(250);
        usageStats.setPublic_sets(75);
        usageStats.setPublic_series(125);
        usageStats.setUsers(1500);

        assertEquals(2024, usageStats.getYear());
        assertEquals(150, usageStats.getSets());
        assertEquals(250, usageStats.getSeries());
        assertEquals(75, usageStats.getPublic_sets());
        assertEquals(125, usageStats.getPublic_series());
        assertEquals(1500, usageStats.getUsers());
    }
}
