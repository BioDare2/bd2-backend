package ed.biodare2;

import ed.biodare2.backend.dto.AnalyticsDataDTO;
import ed.biodare2.backend.services.analytics.AnalyticsService;
import ed.biodare2.backend.web.rest.AnalyticsController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AnalyticsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private AnalyticsController analyticsController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(analyticsController).build();
    }

    @Test
    public void testGetAnalyticsData() throws Exception {
        AnalyticsDataDTO dto = new AnalyticsDataDTO("USA", 1000);
        when(analyticsService.getAnalyticsData()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/analytics/data")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"country\":\"USA\",\"activeUsers\":1000}]"));
    }

    @Test
    public void testGetAnalyticsDataException() throws Exception {
        when(analyticsService.getAnalyticsData()).thenThrow(new GeneralSecurityException("Security error"));

        mockMvc.perform(get("/api/analytics/data")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to fetch analytics data: Security error"));
    }

    @Test
    public void testHandleRuntimeException() throws Exception {
        when(analyticsService.getAnalyticsData()).thenThrow(new RuntimeException("Test Exception"));

        mockMvc.perform(get("/api/analytics/data")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Test Exception"));
    }
}
