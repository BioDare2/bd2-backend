/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import ed.biodare2.SimpleRepoTestConfig;
import static ed.biodare2.backend.features.ppa.PPAUtilsJC2.periodToInt;
import ed.biodare2.backend.features.ppa.dao.PPAArtifactsRepJC2;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.ppa.PPARequest;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAFullResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAResultsGroupSummary;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleResultEntry;

import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.ui_dom.ppa.PPAFitPack;
import ed.biodare2.backend.repo.ui_dom.ppa.PPAResultsGroup;
import ed.biodare2.backend.repo.ui_dom.ppa.PPASelectGroup;
import ed.biodare2.backend.testutil.PPATestSeederJC2;
import ed.robust.dom.tsprocessing.PhaseType;






import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SimpleRepoTestConfig.class)
@DirtiesContext
public class ExperimentPPAJC2ContorllerIntTest extends ExperimentBaseIntTest {
 

    final String serviceRoot = "/api/experiment";
    
    @Autowired
    PPAArtifactsRepJC2 ppaRepJC2;
    
    @Autowired    
    PPATestSeederJC2 ppaTestSeeder;
    
    
    PPARequest preparePPARequest() {
        return DomRepoTestBuilder.makePPARequest();
    }
    
    
    @Test
    @Ignore("JobCentre not available during tests")
    public void newPPAInvokesPPAProcessing() throws Exception {
    
        //ExperimentalAssay desc = DomRepoTestBuilder.makeExperiment(15);
        //exp = experiments.save(desc);
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        insertData(pack);
        
        PPARequest ppaReq = preparePPARequest();
                
        String orgJSON = mapper.writeValueAsString(ppaReq);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(serviceRoot+'/'+exp.getId()+"/ppa2")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("newPPA JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        assertNotNull(info);
        assertNotNull(info.get("analysis"));
        //assertEquals(123,info.get("analysis"));
        
        
    }
    
    
    
    @Test
    public void getPPAJobsReturnsListOfJobsOrderedByIdDesc() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        List<PPAJobSummary> seeds = ppaTestSeeder.getJobs();
        seeds.forEach( job -> {ppaTestSeeder.seedJustJob(job, exp);});
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa2/jobs")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("JOBS JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        
        ListWrapper<PPAJobSummary> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<PPAJobSummary>>() { });
        assertNotNull(wrapper);
        //List<JobSummary> jobs = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<List<JobSummary>>() { });
        List<PPAJobSummary> jobs = wrapper.data;
        assertNotNull(jobs);
        assertFalse(jobs.isEmpty());
        assertEquals(seeds.size(),jobs.size());
        
        PPAJobSummary job = jobs.get(0);
        PPAJobSummary expJ = seeds.stream().filter( j -> j.jobId.equals(job.jobId))
                .findFirst().get();
        
        assertReflectionEquals(expJ,job);
        
        assertTrue(jobs.get(0).submitted.isAfter(jobs.get(1).submitted));
        
    }
    
    
    @Test
    public void getPPAJobGivesJob() throws Exception {
    
        AssayPack exp = insertExperiment();

        PPAJobSummary org = ppaTestSeeder.getJobSummary();
        ppaTestSeeder.seedJustJob(org, exp);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa2/job/"+org.jobId)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        // System.out.println("Job JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        PPAJobSummary job = mapper.readValue(resp.getResponse().getContentAsString(), PPAJobSummary.class);
        assertNotNull(job);
        assertEquals(org.jobId,job.jobId);
        
    }
    
    @Test
    public void deletePPAJobRemovesJob() throws Exception {
    
        AssayPack exp = insertExperiment();

        PPAJobSummary org = ppaTestSeeder.getJobSummary();
        ppaTestSeeder.seedFullJob(org, exp);
        
        assertTrue(ppaRepJC2.getJobSummary(exp, org.jobId).isPresent());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(serviceRoot+'/'+exp.getId()+"/ppa2/job/"+org.jobId)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("DeleteJob JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        assertFalse(ppaRepJC2.getJobSummary(exp, org.jobId).isPresent());

        PPAJobSummary job = mapper.readValue(resp.getResponse().getContentAsString(), PPAJobSummary.class);
        assertNotNull(job);
        assertEquals(org.jobId,job.jobId);
        
        
    }
    
    
    @Test
    public void exportPPAJobProducesCSVFile() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        PPAJobSummary org = ppaTestSeeder.getJobSummary();
        ppaTestSeeder.seedFullJob(org, exp);
        
        PhaseType phaseType = PhaseType.ByMethod;
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa2/job/"+org.jobId+"/export/"+phaseType.name())
                //.accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("text/csv"))
                .andReturn();

        assertNotNull(resp);
        
        assertTrue(resp.getResponse().getContentLength() > 100);
        //System.out.println("R: "+resp.getResponse().getContentAsString());
        assertTrue(resp.getResponse().getContentAsString().contains(phaseType.friendlyName));
        assertTrue(resp.getResponse().getContentAsString().contains(""+org.jobId));
    }
    
    
    @Test
    public void getPPAJobGivesNotFoundOnMissing() throws Exception {
    
        AssayPack exp = insertExperiment();

        UUID jobId = UUID.randomUUID();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa2/job/"+jobId)
                .accept(APPLICATION_JSON_UTF8)
                //.contentType(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                //.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("Job JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        //PPAJobSummary job = mapper.readValue(resp.getResponse().getContentAsString(), PPAJobSummary.class);
        //assertNotNull(job);
        //assertEquals(org.getJobId(),job.jobId);
        
    }
    

    
    
    @Test
    public void getPPAJobResultsGroupedEmptyContainerOnMissing() throws Exception {
    
        AssayPack exp = insertExperiment();
        insertData(exp);
        
        PPAJobSummary orgJob = ppaTestSeeder.getJobSummary();
        ppaTestSeeder.seedJustJob(orgJob, exp);
                
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa2/job/"+orgJob.jobId+"/results/grouped")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        // System.out.println("getPPAJobResultsGrouped JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        //ListWrapper<PPASelectGroup> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<PPASelectGroup>>() { });
        //assertNotNull(wrapper);
        PPAJobResultsGroups groups = mapper.readValue(resp.getResponse().getContentAsString(),PPAJobResultsGroups.class);
        assertNotNull(groups);        
        assertTrue(groups.groups.isEmpty());
        assertEquals(orgJob.jobId,groups.jobId);
        //assertEquals(""+orgJob.getJobId(),groups.jobId);
        
    }
    
    @Test
    public void getPPAJobResultsGroupedGivesCorrectResults() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        PPAJobSummary orgJob = ppaTestSeeder.getJobSummary();
        ppaTestSeeder.seedFullJob(orgJob, exp);
                
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa2/job/"+orgJob.jobId+"/results/grouped")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("getPPAJobResultsGrouped JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        //ListWrapper<PPASelectGroup> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<PPASelectGroup>>() { });
        //assertNotNull(wrapper);
        PPAJobResultsGroups groups = mapper.readValue(resp.getResponse().getContentAsString(),PPAJobResultsGroups.class);
        assertNotNull(groups);        
        assertFalse(groups.groups.isEmpty());
        assertEquals(orgJob.jobId,groups.jobId);
        //assertEquals(""+orgJob.getJobId(),groups.uuid);
        assertEquals(5,groups.groups.size());
        
    }
    
   
    @Test
    public void getPPAJobSimpleResultsGivesCorrectResults() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        PPAJobSummary orgJob = ppaTestSeeder.getJobSummary();
        ppaTestSeeder.seedFullJob(orgJob, exp);
                
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa2/job/"+orgJob.jobId+"/results/simple")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("getPPAJobSimpleResults JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        //ListWrapper<PPASelectGroup> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<PPASelectGroup>>() { });
        //assertNotNull(wrapper);
        PPAJobSimpleResults res = mapper.readValue(resp.getResponse().getContentAsString(),PPAJobSimpleResults.class);
        assertNotNull(res);        
        assertEquals(orgJob.jobId,res.jobId);
        //assertEquals(""+orgJob.getJobId(),res.uuid);
        assertFalse(res.results.isEmpty());
        assertEquals(10,res.results.size());
        
    }

    @Test
    public void getPPAJobSimpleResultsGivesEmptyContainerOnMissing() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        PPAJobSummary orgJob = ppaTestSeeder.getJobSummary();
        ppaTestSeeder.seedData(ppaTestSeeder.getData(), exp);
        ppaTestSeeder.seedJustJob(orgJob, exp);
                
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa2/job/"+orgJob.jobId+"/results/simple")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("getPPAJobSimpleResults JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        //ListWrapper<PPASelectGroup> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<PPASelectGroup>>() { });
        //assertNotNull(wrapper);
        PPAJobSimpleResults res = mapper.readValue(resp.getResponse().getContentAsString(),PPAJobSimpleResults.class);
        assertNotNull(res);        
        assertEquals(orgJob.jobId,res.jobId);
        //assertEquals(""+orgJob.getJobId(),res.uuid);        
        assertTrue(res.results.isEmpty());
        
    }
    
    @Test
    //@Ignore
    public void getPPAForSelectListsResultsForSelect() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        PPAJobSummary orgJob = ppaTestSeeder.getJobSummary();
        ppaTestSeeder.seedFullJob(orgJob, exp);
        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa2/job/"+orgJob.jobId+"/results/select")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("PPAForSelect JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        ListWrapper<PPASelectGroup> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<PPASelectGroup>>() { });
        assertNotNull(wrapper);
        
        List<PPASelectGroup> groups = wrapper.data;
        assertNotNull(groups);
        assertFalse(groups.isEmpty());
        assertEquals(9,groups.size());
        
        PPASelectGroup group = groups.stream()
                .filter(g -> g.dataId == 9).findFirst().get();
        //assertEquals("3",group.label);
        assertEquals(2,group.periods.size());
        
    }    
    

    @Test
    public void doPPASelectionChangesResultsStatus() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        PPAJobSummary orgJob = ppaTestSeeder.getJobSummary();
        ppaTestSeeder.seedFullJob(orgJob, exp);
        UUID jobId = orgJob.jobId;
        
        List<PPAFullResultEntry> entries = ppaRepJC2.getJobIndResults(exp, orgJob.jobId);
        Map<String,String> req = new HashMap<>();
        
        PPAFullResultEntry forSelect = entries.get(6);
        PPAFullResultEntry forIgnore = entries.get(7);
        PPAFullResultEntry forIgnoreGood = entries.get(1);
        assertTrue(forSelect.result.needsAttention());
        assertTrue(forIgnore.result.needsAttention());
        assertFalse(forIgnoreGood.result.needsAttention());
        
        req.put("s_"+forSelect.dataId,""+periodToInt(forSelect.result.getPeriod()));
        req.put("s_"+forIgnore.dataId,"dismiss");
        req.put("s_"+forIgnoreGood.dataId,"dismiss");
        
        PPAJobResultsGroups groupsOrg = ppaRepJC2.getJobResultsGroups(exp, jobId);

        String body = mapper.writeValueAsString(req);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+'/'+exp.getId()+"/ppa2/job/"+jobId+"/results/select")
                .accept(APPLICATION_JSON_UTF8)
                .contentType(APPLICATION_JSON_UTF8)
                .content(body)                
                .with(mockAuthentication);
        

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("Do selectPPA JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        Map<String,Object> res = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<Map<String,Object>>() { });
        
        assertNotNull(res);
        //assertEquals(orgJob.attentionCount-1,res.get("needsAttention"));
        
        entries = ppaRepJC2.getJobIndResults(exp, orgJob.jobId);
        forSelect = entries.get(6);
        forIgnore = entries.get(7);
        forIgnoreGood = entries.get(1);
        assertFalse(forSelect.result.needsAttention());
        assertTrue(forIgnore.result.needsAttention());
        assertFalse(forIgnore.ignored);
        assertTrue(forIgnore.result.isIgnored());
        assertTrue(forIgnoreGood.result.isIgnored());
        
        PPAJobSimpleResults simpleRes = ppaRepJC2.getJobSimpleResults(exp, jobId);
        assertFalse(simpleRes.results.get(6).attention);
        assertTrue(simpleRes.results.get(7).attention);
        assertTrue(simpleRes.results.get(7).ignored);
        assertTrue(simpleRes.results.get(1).ignored);
        
        //PPASimpleResultEntry groupM = simpleRes.results.get(6); /*simpleRes.results.stream()
        //                                .filter( e -> e.label.equals(simpleRes.results.get(7).label))
        //                                .findFirst().get();*/
        
        //PPAResultsGroupSummary resGroup = groupsOrg.groups.stream().filter( e -> e.memberDataId == groupM.dataId).findFirst().get();
        //assertEquals(0,resGroup.periods.size());
        
        //PPAJobResultsGroups groupsUpd = ppaRepJC2.getJobResultsGroups(exp, jobId);
        //resGroup = groupsUpd.groups.stream().filter( e -> e.memberDataId == groupM.dataId).findFirst().get();
        //assertEquals(1,resGroup.periods.size());
        //assertTrue(resGroup.periods.contains(simpleRes.results.get(6).period));
        
        //for ingored sample 2 that was prevously present (belong to group with 1)
        //resGroup = groupsOrg.groups.stream().filter( e -> e.memberDataId == 1).findFirst().get();
        //int prevSize = resGroup.periods.size();
        //resGroup = groupsUpd.groups.stream().filter( e -> e.memberDataId == 1).findFirst().get();
        //assertEquals(prevSize-1,resGroup.periods.size());
        //assertFalse(resGroup.periods.contains(forIgnoreGood.result.getPeriod()));
    }

    
    
    @Test
    public void getPPAJobSimpleStatsGivesCorrectStats() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        PPAJobSummary orgJob = ppaTestSeeder.getJobSummary();
        ppaTestSeeder.seedFullJob(orgJob, exp);
                
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa2/job/"+orgJob.jobId+"/stats/simple")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("getPPAJobSimpleStats JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        PPAJobSimpleStats stats = mapper.readValue(resp.getResponse().getContentAsString(),PPAJobSimpleStats.class);
        assertNotNull(stats);        
        assertEquals(orgJob.jobId,stats.jobId);
        //assertEquals(""+orgJob.getJobId(),stats.uuid);
        assertEquals(5,stats.stats.size());
        
    }
    
    @Test
    public void getPPAJobSimpleStatsGivesEmptyContainerOnMissing() throws Exception {
    
        AssayPack exp = insertExperiment();
        insertData(exp);
        
        PPAJobSummary orgJob = ppaTestSeeder.getJobSummary();
        ppaTestSeeder.seedJustJob(orgJob, exp);
                
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa2/job/"+orgJob.jobId+"/stats/simple")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        // System.out.println("getPPAJobSimpleStats JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        PPAJobSimpleStats stats = mapper.readValue(resp.getResponse().getContentAsString(),PPAJobSimpleStats.class);
        assertNotNull(stats);        
        assertEquals(orgJob.jobId,stats.jobId);
        //assertEquals(""+orgJob.getJobId(),stats.uuid);
        assertEquals(0,stats.stats.size());
        
    }
    
    @Test
    //@Ignore
    public void getDataFitReturnsDataAndFitTraces() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        PPAJobSummary orgJob = ppaTestSeeder.getJobSummary();
        ppaTestSeeder.seedFullJob(orgJob, exp);
                   
        long dataId = 7;
        boolean selectable = true;
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa2/job/"+orgJob.jobId+"/fits/"+dataId+"/"+selectable)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("getDataFit JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');


        PPAFitPack fitPack = mapper.readValue(resp.getResponse().getContentAsString(), PPAFitPack.class);
        assertNotNull(fitPack);
        assertEquals(4,fitPack.traces.traces.size());
        assertEquals(3,fitPack.options.size());
        assertTrue(fitPack.options.stream().anyMatch(o -> o.id.equals("dismiss")));
        

    }

    
    
    /*@Test
    public void getJoinedPPAResultsGivesCorrectResults() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        List<PPAJobSummary> orgJobs = ppaTestSeeder.getJobs();
        orgJobs.forEach( job -> ppaTestSeeder.seedFullJob(job, exp));
                
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa2/results")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("getJoinedPPAResults JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        ListWrapper<PPAResultsGroup> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<PPAResultsGroup>>() { });
        assertNotNull(wrapper);
        
        List<PPAResultsGroup> groups = wrapper.data;
        assertEquals(10,groups.size());
        assertEquals(orgJobs.size(),groups.get(0).results.size());
        
        
        
    }*/
    
    
    
    @Test
    public void exportPPAProducesZippedContent() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        List<PPAJobSummary> orgJobs = ppaTestSeeder.getJobs();
        orgJobs.forEach( job -> ppaTestSeeder.seedFullJob(job, exp));
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa2/export")
                //.accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/zip"))
                .andReturn();

        assertNotNull(resp);
        
        assertTrue(resp.getResponse().getContentLength() > 0);
    }

    
}
