/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.ppa.PPARequest;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.ui_dom.ppa.PPAFitPack;
import ed.biodare2.backend.repo.ui_dom.ppa.PPAResultsGroup;
import ed.biodare2.backend.repo.ui_dom.ppa.PPASelectGroup;
import ed.biodare2.backend.features.ppa.PPAUtils;
import static ed.biodare2.backend.features.ppa.PPAUtils.periodToInt;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAResultsGroupSummary;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPASimpleResultEntry;
import ed.robust.dom.jobcenter.JobSummary;
import ed.robust.dom.tsprocessing.PhaseType;
import ed.robust.dom.tsprocessing.ResultsEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.junit.Assert.*;
import org.junit.Ignore;
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
public class ExperimentPPAContorllerIntTest extends ExperimentBaseIntTest {
 

    final String serviceRoot = "/api/experiment";
    
    
    
    PPARequest preparePPARequest() {
        return DomRepoTestBuilder.makePPARequest();
    }
    
    
    @Test
    @Ignore("service not available during tests")
    public void newPPAInvokesPPAProcessing() throws Exception {
    
        //ExperimentalAssay desc = DomRepoTestBuilder.makeExperiment(15);
        //exp = experiments.save(desc);
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();        
        
        insertData(pack);
        
        PPARequest ppaReq = preparePPARequest();
                
        String orgJSON = mapper.writeValueAsString(ppaReq);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(serviceRoot+'/'+exp.getId()+"/ppa")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("newPPA JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        assertNotNull(info);
        assertNotNull(info.get("analysis"));
        //assertEquals(123,info.get("analysis"));
        
        
    }
    
    
    
    @Test
    public void getPPAJobsReturnsListOfJobsOrderedByIdDesc() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        List<JobSummary> seeds = testSeeder.getJobs();
        seeds.forEach( job -> {testSeeder.seedJustJob(job, exp);});
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa/jobs")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("JOBS JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        
        ListWrapper<PPAJobSummary> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<PPAJobSummary>>() { });
        assertNotNull(wrapper);
        //List<JobSummary> jobs = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<List<JobSummary>>() { });
        List<PPAJobSummary> jobs = wrapper.data;
        assertNotNull(jobs);
        assertFalse(jobs.isEmpty());
        assertEquals(seeds.size(),jobs.size());
        
        PPAJobSummary job = jobs.get(0);
        PPAJobSummary expJ = seeds.stream()
                .filter( j -> j.getJobId() == job.jobId)
                .findFirst()
                .map( j -> PPAUtils.simplifyJob(j))
                .map( j -> { j.parentId = exp.getId(); return j; })
                .get();
        
        assertReflectionEquals(expJ,job);
        
        assertTrue(jobs.get(0).jobId > jobs.get(1).jobId);
        
    }
    
    
    @Test
    public void getPPAJobGivesJob() throws Exception {
    
        AssayPack exp = insertExperiment();

        JobSummary org = testSeeder.getJob();
        testSeeder.seedJustJob(org, exp);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa/job/"+org.getJobId())
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("Job JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        PPAJobSummary job = mapper.readValue(resp.getResponse().getContentAsString(), PPAJobSummary.class);
        assertNotNull(job);
        assertEquals(org.getJobId(),job.jobId);
        
    }
    
    @Test
    public void deletePPAJobRemovesJob() throws Exception {
    
        AssayPack exp = insertExperiment();

        JobSummary org = testSeeder.getJob();
        testSeeder.seedFullJob(org, exp);
        
        assertTrue(ppaRep.getJobSummary(exp, org.getJobId()).isPresent());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(serviceRoot+'/'+exp.getId()+"/ppa/job/"+org.getJobId())
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("DeleteJob JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        assertFalse(ppaRep.getJobSummary(exp, org.getJobId()).isPresent());

        PPAJobSummary job = mapper.readValue(resp.getResponse().getContentAsString(), PPAJobSummary.class);
        assertNotNull(job);
        assertEquals(org.getJobId(),job.jobId);
        
        
    }
    
    
    @Test
    public void exportPPAJobProducesCSVFile() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        JobSummary org = testSeeder.getJob();
        testSeeder.seedFullJob(org, exp);
        
        PhaseType phaseType = PhaseType.ByMethod;
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa/job/"+org.getJobId()+"/export/"+phaseType.name())
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
        assertTrue(resp.getResponse().getContentAsString().contains(""+org.getJobId()));
    }
    
    
    @Test
    public void getPPAJobGivesNotFoundOnMissing() throws Exception {
    
        AssayPack exp = insertExperiment();

        long jobId = 666;
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa/job/"+jobId)
                .accept(APPLICATION_JSON_UTF8)
                //.contentType(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                //.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("Job JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        //PPAJobSummary job = mapper.readValue(resp.getResponse().getContentAsString(), PPAJobSummary.class);
        //assertNotNull(job);
        //assertEquals(org.getJobId(),job.jobId);
        
    }
    

    
    
    @Test
    public void getPPAJobResultsGroupedEmptyContainerOnMissing() throws Exception {
    
        AssayPack exp = insertExperiment();
        insertData(exp);
        
        JobSummary orgJob = testSeeder.getJob();
        testSeeder.seedJustJob(orgJob, exp);
                
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa/job/"+orgJob.getJobId()+"/results/grouped")
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
        assertEquals(orgJob.getJobId(),groups.jobId);
        //assertEquals(""+orgJob.getJobId(),groups.jobId);
        
    }
    
    @Test
    public void getPPAJobResultsGroupedGivesCorrectResults() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        JobSummary orgJob = testSeeder.getJob();
        testSeeder.seedFullJob(orgJob, exp);
                
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa/job/"+orgJob.getJobId()+"/results/grouped")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("getPPAJobResultsGrouped JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        //ListWrapper<PPASelectGroup> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<PPASelectGroup>>() { });
        //assertNotNull(wrapper);
        PPAJobResultsGroups groups = mapper.readValue(resp.getResponse().getContentAsString(),PPAJobResultsGroups.class);
        assertNotNull(groups);        
        assertFalse(groups.groups.isEmpty());
        assertEquals(orgJob.getJobId(),groups.jobId);
        //assertEquals(""+orgJob.getJobId(),groups.uuid);
        assertEquals(4,groups.groups.size());
        
    }
    
   
    @Test
    public void getPPAJobSimpleResultsGivesCorrectResults() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        JobSummary orgJob = testSeeder.getJob();
        testSeeder.seedFullJob(orgJob, exp);
                
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa/job/"+orgJob.getJobId()+"/results/simple")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("getPPAJobSimpleResults JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        //ListWrapper<PPASelectGroup> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<PPASelectGroup>>() { });
        //assertNotNull(wrapper);
        PPAJobSimpleResults res = mapper.readValue(resp.getResponse().getContentAsString(),PPAJobSimpleResults.class);
        assertNotNull(res);        
        assertEquals(orgJob.getJobId(),res.jobId);
        //assertEquals(""+orgJob.getJobId(),res.uuid);
        assertFalse(res.results.isEmpty());
        assertEquals(12,res.results.size());
        
    }

    @Test
    public void getPPAJobSimpleResultsGivesEmptyContainerOnMissing() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        JobSummary orgJob = testSeeder.getJob();
        testSeeder.seedData(testSeeder.getData(), exp);
        testSeeder.seedJustJob(orgJob, exp);
                
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa/job/"+orgJob.getJobId()+"/results/simple")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("getPPAJobSimpleResults JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        //ListWrapper<PPASelectGroup> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<PPASelectGroup>>() { });
        //assertNotNull(wrapper);
        PPAJobSimpleResults res = mapper.readValue(resp.getResponse().getContentAsString(),PPAJobSimpleResults.class);
        assertNotNull(res);        
        assertEquals(orgJob.getJobId(),res.jobId);
        //assertEquals(""+orgJob.getJobId(),res.uuid);        
        assertTrue(res.results.isEmpty());
        
    }
    
    @Test
    //@Ignore
    public void getPPAForSelectListsResultsForSelect() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        JobSummary orgJob = testSeeder.getJob();
        testSeeder.seedFullJob(orgJob, exp);
        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa/job/"+orgJob.getJobId()+"/results/select")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("PPAForSelect JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        ListWrapper<PPASelectGroup> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<PPASelectGroup>>() { });
        assertNotNull(wrapper);
        
        List<PPASelectGroup> groups = wrapper.data;
        assertNotNull(groups);
        assertFalse(groups.isEmpty());
        assertEquals(12,groups.size());
        
        PPASelectGroup group = groups.get(0);
        assertEquals("Complex",group.label);
        assertEquals(3,group.periods.size());
        
    }    
    

    @Test
    public void doPPASelectionChangesResultsStatus() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        JobSummary orgJob = testSeeder.getJob();
        testSeeder.seedFullJob(orgJob, exp);
        long jobId = orgJob.getJobId();
        
        List<ResultsEntry> entries = ppaRep.getJobIndResults(exp, orgJob.getJobId());
        Map<String,String> req = new HashMap<>();
        
        ResultsEntry forSelect = entries.get(6);
        ResultsEntry forIgnore = entries.get(7);
        ResultsEntry forIgnoreGood = entries.get(1);
        assertTrue(forSelect.getResult().needsAttention());
        assertTrue(forIgnore.getResult().needsAttention());
        assertFalse(forIgnoreGood.getResult().needsAttention());
        
        req.put("s_"+forSelect.getDataId(),""+periodToInt(forSelect.getResult().getPeriod()));
        req.put("s_"+forIgnore.getDataId(),"dismiss");
        req.put("s_"+forIgnoreGood.getDataId(),"dismiss");
        
        PPAJobResultsGroups groupsOrg = ppaRep.getJobResultsGroups(exp, jobId);

        String body = mapper.writeValueAsString(req);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+'/'+exp.getId()+"/ppa/job/"+jobId+"/results/select")
                .accept(APPLICATION_JSON_UTF8)
                .contentType(APPLICATION_JSON_UTF8)
                .content(body)                
                .with(mockAuthentication);
        

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("Do selectPPA JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        Map<String,Object> res = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<Map<String,Object>>() { });
        
        assertNotNull(res);
        assertEquals(orgJob.getAttentionCount()-2,res.get("needsAttention"));
        
        entries = ppaRep.getJobIndResults(exp, orgJob.getJobId());
        forSelect = entries.get(6);
        forIgnore = entries.get(7);
        forIgnoreGood = entries.get(1);
        assertFalse(forSelect.getResult().needsAttention());
        assertTrue(forIgnore.getResult().needsAttention());
        assertFalse(forIgnore.ignored);
        assertTrue(forIgnore.getResult().isIgnored());
        assertTrue(forIgnoreGood.getResult().isIgnored());
        
        PPAJobSimpleResults simpleRes = ppaRep.getJobSimpleResults(exp, jobId);
        assertFalse(simpleRes.results.get(6).attention);
        assertTrue(simpleRes.results.get(7).attention);
        assertTrue(simpleRes.results.get(7).ignored);
        assertTrue(simpleRes.results.get(1).ignored);
        
        PPASimpleResultEntry groupM = simpleRes.results.get(6); /*simpleRes.results.stream()
                                        .filter( e -> e.label.equals(simpleRes.results.get(7).label))
                                        .findFirst().get();*/
        
        PPAResultsGroupSummary resGroup = groupsOrg.groups.stream().filter( e -> e.memberDataId == groupM.dataId).findFirst().get();
        assertEquals(0,resGroup.periods.size());
        
        PPAJobResultsGroups groupsUpd = ppaRep.getJobResultsGroups(exp, jobId);
        resGroup = groupsUpd.groups.stream().filter( e -> e.memberDataId == groupM.dataId).findFirst().get();
        assertEquals(1,resGroup.periods.size());
        assertTrue(resGroup.periods.contains(simpleRes.results.get(6).period));
        
        //for ingored sample 2 that was prevously present (belong to group with 1)
        resGroup = groupsOrg.groups.stream().filter( e -> e.memberDataId == 1).findFirst().get();
        int prevSize = resGroup.periods.size();
        resGroup = groupsUpd.groups.stream().filter( e -> e.memberDataId == 1).findFirst().get();
        assertEquals(prevSize-1,resGroup.periods.size());
        assertFalse(resGroup.periods.contains(forIgnoreGood.getResult().getPeriod()));
    }

    
    
    @Test
    public void getPPAJobSimpleStatsGivesCorrectStats() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        JobSummary orgJob = testSeeder.getJob();
        testSeeder.seedFullJob(orgJob, exp);
                
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa/job/"+orgJob.getJobId()+"/stats/simple")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("getPPAJobSimpleStats JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        PPAJobSimpleStats stats = mapper.readValue(resp.getResponse().getContentAsString(),PPAJobSimpleStats.class);
        assertNotNull(stats);        
        assertEquals(orgJob.getJobId(),stats.jobId);
        //assertEquals(""+orgJob.getJobId(),stats.uuid);
        assertEquals(4,stats.stats.size());
        
    }
    
    @Test
    public void getPPAJobSimpleStatsGivesEmptyContainerOnMissing() throws Exception {
    
        AssayPack exp = insertExperiment();
        insertData(exp);
        
        JobSummary orgJob = testSeeder.getJob();
        testSeeder.seedJustJob(orgJob, exp);
                
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa/job/"+orgJob.getJobId()+"/stats/simple")
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
        assertEquals(orgJob.getJobId(),stats.jobId);
        //assertEquals(""+orgJob.getJobId(),stats.uuid);
        assertEquals(0,stats.stats.size());
        
    }
    
    @Test
    //@Ignore
    public void getDataFitReturnsDataAndFitTraces() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        JobSummary orgJob = testSeeder.getJob();
        testSeeder.seedFullJob(orgJob, exp);
                   
        long dataId = 7;
        boolean selectable = true;
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa/job/"+orgJob.getJobId()+"/fits/"+dataId+"/"+selectable)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("getDataFit JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');


        PPAFitPack fitPack = mapper.readValue(resp.getResponse().getContentAsString(), PPAFitPack.class);
        assertNotNull(fitPack);
        assertEquals(5,fitPack.traces.traces.size());
        assertEquals(4,fitPack.options.size());
        assertTrue(fitPack.options.stream().anyMatch(o -> o.id.equals("dismiss")));
        

    }

    
    
    @Test
    public void getJoinedPPAResultsGivesCorrectResults() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        List<JobSummary> orgJobs = testSeeder.getJobs();
        orgJobs.forEach( job -> testSeeder.seedFullJob(job, exp));
                
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa/results")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        System.out.println("getJoinedPPAResults JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+";\n"+resp.getResponse().getContentAsString()+'\n');
        
        ListWrapper<PPAResultsGroup> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<PPAResultsGroup>>() { });
        assertNotNull(wrapper);
        
        List<PPAResultsGroup> groups = wrapper.data;
        assertEquals(12,groups.size());
        assertEquals(orgJobs.size(),groups.get(0).results.size());
        
        
        
    }
    
    
    
    @Test
    public void exportPPAProducesZippedContent() throws Exception {
    
        AssayPack exp = insertExperiment();
        
        List<JobSummary> orgJobs = testSeeder.getJobs();
        orgJobs.forEach( job -> testSeeder.seedFullJob(job, exp));
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/ppa/export")
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
