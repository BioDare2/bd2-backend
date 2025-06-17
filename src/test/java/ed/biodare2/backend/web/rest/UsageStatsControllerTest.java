package ed.biodare2.backend.web.rest;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.dto.AnalyticsDataDTO;
import ed.biodare2.backend.dto.SpeciesStatsDTO;
import ed.biodare2.backend.dto.UsageStatsDTO;
import ed.biodare2.backend.services.analytics.AnalyticsService;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.dao.ExperimentalAssayRep;
import ed.biodare2.backend.security.dao.UserAccountRep;
import ed.biodare2.backend.handlers.ExperimentDataHandler;
import ed.robust.dom.util.Pair;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SimpleRepoTestConfig.class})
public class UsageStatsControllerTest extends ExperimentBaseIntTest {

    final String serviceRoot = "/api/usage";
    
    @Autowired
    UsageStatsController usageStatsController;
    
    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ExperimentalAssayRep expRep;

    @MockitoBean
    private ExperimentPackHub expPacks;

    @MockitoBean
    private ExperimentDataHandler dataHandler;

    @MockitoBean
    private UserAccountRep accounts;

    @MockitoBean
    private AnalyticsService analyticsService;

    @Value("${bd2.usagestats.file:usage_stats.json}")
    String outputPath;
    private Path outputFile;

    @Autowired
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        outputFile = Paths.get(outputPath);
        if (!Files.exists(outputFile)) {
            Files.createFile(outputFile);
        }
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void getUsageStatsReturnsCorrectData() throws Exception {
        List<UsageStatsDTO> usageStats = new ArrayList<>();
        usageStats.add(new UsageStatsDTO(2023, 100, 200, 50, 100, 1000));

        List<SpeciesStatsDTO> speciesStats = new ArrayList<>();
        speciesStats.add(new SpeciesStatsDTO("Arabidopsis thaliana", 100, 50, 200, 100));

        List<AnalyticsDataDTO> analyticsData = new ArrayList<>();
        analyticsData.add(new AnalyticsDataDTO("Poland", 100));

        UsageStatsController usageStatsControllerMock = mock(UsageStatsController.class);

        doReturn(usageStats).when(usageStatsControllerMock).get_stats_by_year();
        doReturn(speciesStats).when(usageStatsControllerMock).get_stats_by_species();
        doReturn(analyticsData).when(usageStatsControllerMock).getAnalyticsData();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot + "/get_usage_stats")
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);

        Map<String, Object> result = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<Map<String, Object>>() {});
        
        assertNotNull(result);
        assertTrue(result.get("timestamp") instanceof String);
        assertTrue(result.get("analytics") instanceof List);
        assertTrue(result.get("year_stats") instanceof List);
        assertTrue(result.get("species_stats") instanceof List);
    }

    @Test
    public void getStatsByYearReturnsCorrectData() throws Exception {

        Stream<List<String>> mockSpeciesEntries = Arrays.asList(
            Arrays.asList("Owner1", "2025", "10", "false"),
            Arrays.asList("Owner1", "2025", "20", "false"),
            Arrays.asList("Owner1", "2025", "30", "false")
        ).stream();

        UsageStatsController usageStatsControllerSpy = spy(usageStatsController);
        doReturn(mockSpeciesEntries).when(usageStatsControllerSpy).getDataEntries(ArgumentMatchers.<Stream<Long>>any());
    
        List<UsageStatsDTO> result = usageStatsControllerSpy.get_stats_by_year();

        System.out.println(result);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2025, result.get(0).getYear());
    }

    @Test
    public void getStatsBySpeciesReturnsCorrectData() throws Exception {

        Stream<List<String>> mockSpeciesEntries = Arrays.asList(
            Arrays.asList("Arabidopsis thaliana", "1", "10", "false"),
            Arrays.asList("Arabidopsis thaliana", "2", "20", "false"),
            Arrays.asList("Arabidopsis thaliana", "3", "30", "false")
        ).stream();

        UsageStatsController usageStatsControllerSpy = spy(usageStatsController);
        doReturn(mockSpeciesEntries).when(usageStatsControllerSpy).getSpeciesEntries(ArgumentMatchers.<Stream<Long>>any());
    
        List<SpeciesStatsDTO> result = usageStatsControllerSpy.get_stats_by_species();

        System.out.println(result);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("Arabidopsis thaliana", result.get(0).getSpecies());
    }

    @Test
    public void groupGroupsByUserYear() {
        
        List<List<String>> entries = new ArrayList<>();
        
        entries.add(Arrays.asList("u1","2017","0"));
        entries.add(Arrays.asList("u2","2017","3"));
        entries.add(Arrays.asList("u1","2018","1"));
        entries.add(Arrays.asList("u2","2017","2"));
        entries.add(Arrays.asList("u2","2017","1"));
        
        Map<Pair<String,String>, List<List<String>>> exp = new HashMap<>();
        exp.put(new Pair<>("u1","2017"), Arrays.asList(Arrays.asList("u1","2017","0")));
        exp.put(new Pair<>("u1","2018"), Arrays.asList(Arrays.asList("u1","2018","1")));
        exp.put(new Pair<>("u2","2017"), Arrays.asList(
                Arrays.asList("u2","2017","1"),
                Arrays.asList("u2","2017","3"),
                Arrays.asList("u2","2017","2")
        ));
        
        Map<Pair<String,String>, List<List<String>>> res = usageStatsController.group(entries.parallelStream());
        assertEquals(exp.keySet(), res.keySet());
        
        exp.keySet().forEach( key -> {
            assertEquals(new HashSet<>(exp.get(key)), new HashSet<>(res.get(key)));
        });
    }
    
    @Test
    public void countSetsCountsEntries() {
        
        Map<Pair<String,String>, List<List<String>>> groups = new HashMap<>();
        groups.put(new Pair<>("u1","2017"), Arrays.asList(Arrays.asList("u1","2017","0")));
        groups.put(new Pair<>("u1","2018"), Arrays.asList(Arrays.asList("u1","2018","1")));
        groups.put(new Pair<>("u2","2017"), Arrays.asList(
                Arrays.asList("u2","2017","1"),
                Arrays.asList("u2","2017","3"),
                Arrays.asList("u2","2017","2")
        ));
        
        Map<Pair<String,String>, Integer> exp = new HashMap<>();
        exp.put(new Pair<>("u1","2017"),1);
        exp.put(new Pair<>("u1","2018"),1);
        exp.put(new Pair<>("u2","2017"),3);
        
        Map<Pair<String,String>, Integer> res = usageStatsController.countSets(groups);
        assertEquals(exp, res);
    }

    @Test
    public void countSeriesSumSeries() {
        
        Map<Pair<String,String>, List<List<String>>> groups = new HashMap<>();
        groups.put(new Pair<>("u1","2017"), Arrays.asList(Arrays.asList("u1","2017","0")));
        groups.put(new Pair<>("u1","2018"), Arrays.asList(Arrays.asList("u1","2018","1")));
        groups.put(new Pair<>("u2","2017"), Arrays.asList(
                Arrays.asList("u2","2017","1"),
                Arrays.asList("u2","2017","3"),
                Arrays.asList("u2","2017","2")
        ));
        
        Map<Pair<String,String>, Integer> exp = new HashMap<>();
        exp.put(new Pair<>("u1","2018"),1);
        exp.put(new Pair<>("u2","2017"),6);
        
        Map<Pair<String,String>, Integer> res = usageStatsController.countSeries(groups);
        assertEquals(exp, res);
    }

}