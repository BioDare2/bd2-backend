package ed.biodare2.backend.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AnalyticsDataDTOTest {

    @Test
    public void testAnalyticsDataDTO() {
        String country = "USA";
        int activeUsers = 1000;

        AnalyticsDataDTO analyticsData = new AnalyticsDataDTO(country, activeUsers);

        assertEquals(country, analyticsData.getCountry());
        assertEquals(activeUsers, analyticsData.getActiveUsers());
    }

    @Test
    public void testSetters() {
        AnalyticsDataDTO analyticsData = new AnalyticsDataDTO("USA", 1000);

        analyticsData.setCountry("Canada");
        analyticsData.setActiveUsers(2000);

        assertEquals("Canada", analyticsData.getCountry());
        assertEquals(2000, analyticsData.getActiveUsers());
    }
}
