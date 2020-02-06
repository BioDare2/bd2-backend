/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import static ed.biodare2.backend.web.rest.AbstractIntTestBase.APPLICATION_JSON_UTF8;
import ed.robust.dom.util.Pair;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 *
 * @author Zielu
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SimpleRepoTestConfig.class})
public class UsageStatsControllerTest extends ExperimentBaseIntTest {
    

    
    final String serviceRoot = "/api/usage";
    
    @Autowired
    UsageStatsController usageStats;
    
    public UsageStatsControllerTest() {
    }
    
    @Test
    public void dataStatsReturnsRowsOfCVS() throws Exception {
        
        AssayPack pack1 = insertExperiment();
        ExperimentalAssay exp1 = pack1.getAssay();        
        
        int series = insertData(pack1);

        AssayPack pack2 = insertExperiment();
        ExperimentalAssay exp2 = pack2.getAssay();        
        
        series += insertData(pack2);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+"/data")
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(fixtures.demoUser));

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("getTSData JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        /*
        ListWrapper<String> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<String>>() { });
        assertNotNull(wrapper);
        List<String> data = wrapper.data;
        assertNotNull(data);
        //data.forEach(System.out::println);
        assertEquals(4, data.size());        
        assertEquals("user,"+LocalDate.now().getYear()+","+series,data.get(3));*/
        
        Map<String, Map<String, Integer>> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<Map<String, Map<String, Integer>>>() { });
        assertNotNull(wrapper);
        assertEquals(2, wrapper.size());
        
        wrapper.forEach( (k, stats) -> {
            stats.forEach( (y, stat) -> System.out.println(k+","+y+","+stat));
        });

        
        
    }
    //Simple unit tests
    

    @Test
    public void speciesStatsReturnsMapsOfStats() throws Exception {
        
        AssayPack pack1 = insertExperiment();
        ExperimentalAssay exp1 = pack1.getAssay();        
        
        int series = insertData(pack1);

        AssayPack pack2 = insertExperiment();
        ExperimentalAssay exp2 = pack2.getAssay();        
        
        series += insertData(pack2);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+"/species")
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(fixtures.demoUser));

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("species JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        
        Map<String, Map<String, Integer>> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<Map<String, Map<String, Integer>>>() { });
        assertNotNull(wrapper);
        assertEquals(2, wrapper.size());
        assertTrue(wrapper.containsKey("speciesSets"));
        
        Map<String, Integer> sets = wrapper.get("speciesSets");
        assertNotNull(sets);
        assertTrue(2 <= (int)sets.get("Arabidopsis thaliana"));
        
        wrapper.forEach( (k, stats) -> {
            stats.forEach( (y, stat) -> System.out.println(k+","+y+","+stat));
        });

        
        
    }
    
    @Test
    public void getDataEntryExtractsDetails() throws Exception {
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        String owner = pack.getSystemInfo().security.owner;
        int series = insertData(pack);

        List<String> entry = usageStats.getDataEntry(pack.getId());
        assertEquals(Arrays.asList(owner,""+LocalDate.now().getYear(),""+series), entry);

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
        
        Map<Pair<String,String>, List<List<String>>> res = usageStats.group(entries.parallelStream());
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
        
        Map<Pair<String,String>, Integer> res = usageStats.countSets(groups);
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
        
        Map<Pair<String,String>, Integer> res = usageStats.countSeries(groups);
        assertEquals(exp, res);
        
    }

}
