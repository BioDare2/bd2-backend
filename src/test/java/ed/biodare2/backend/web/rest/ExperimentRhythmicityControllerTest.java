/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import ed.biodare.jobcentre2.dom.JobResults;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare.jobcentre2.dom.TSResult;
import ed.biodare.rhythm.ejtk.BD2eJTKRes;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.features.rhythmicity.RhythmicityService;
import ed.biodare2.backend.features.rhythmicity.dao.RhythmicityArtifactsRep;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeBD2EJTKResults;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeRhythmicityJobSummary;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeRhythmicityRequest;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityRequest;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SimpleRepoTestConfig.class)
public class ExperimentRhythmicityControllerTest extends ExperimentBaseIntTest {
    
    final String serviceRoot = "/api/experiment";
    
    
    @MockBean
    RhythmicityService rhythmicityService;
    

    @Autowired
    RhythmicityArtifactsRep rhythmicityRep;
    
    /**
     * Test of newRhythmicity method, of class ExperimentRhythmicityController.
     */
    @Test
    public void testNewRhythmicity() throws Exception {
        
        UUID jobId = UUID.randomUUID();
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();                
        insertData(pack, 96);
        
        RhythmicityRequest rhythmicityRequest = makeRhythmicityRequest();
        
        String orgJSON = mapper.writeValueAsString(rhythmicityRequest);
        
        when(rhythmicityService.submitJob(any())).thenReturn(jobId);
        
        assertFalse(exp.characteristic.hasRhythmicityJobs);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(serviceRoot+'/'+exp.getId()+"/rhythmicity")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("newPPA JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        assertNotNull(info);
        assertNotNull(info.get("analysis"));  
        assertEquals(jobId.toString(), info.get("analysis"));

        pack = expBoundles.findOne(pack.getId()).get();
        exp = pack.getAssay();
        assertTrue(exp.characteristic.hasRhythmicityJobs);
        
        assertTrue(rhythmicityRep.findJob(jobId, exp.getId()).isPresent());
        
    }
    
    @Test
    public void testNewJTKClassicRhythmicity() throws Exception {
        
        UUID jobId = UUID.randomUUID();
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();                
        insertData(pack, 96);
        
        RhythmicityRequest rhythmicityRequest = makeRhythmicityRequest();
        rhythmicityRequest.method = "BD2JTK";
        rhythmicityRequest.preset = "COS_2H";        
        
        String orgJSON = mapper.writeValueAsString(rhythmicityRequest);
        
        when(rhythmicityService.submitJob(any())).thenReturn(jobId);
        
        assertFalse(exp.characteristic.hasRhythmicityJobs);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(serviceRoot+'/'+exp.getId()+"/rhythmicity")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("newPPA JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        assertNotNull(info);
        assertNotNull(info.get("analysis"));  
        assertEquals(jobId.toString(), info.get("analysis"));

        pack = expBoundles.findOne(pack.getId()).get();
        exp = pack.getAssay();
        assertTrue(exp.characteristic.hasRhythmicityJobs);
        
        assertTrue(rhythmicityRep.findJob(jobId, exp.getId()).isPresent());
        String met = rhythmicityRep.findJob(jobId, exp.getId())
                      .map( j -> j.parameters.get("METHOD"))
                      .get();
        assertEquals("BD2JTK",met);
    }
    
    
    @Test
    public void testGetJobs() throws Exception {
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();                
        insertData(pack);
        
        RhythmicityJobSummary job1 = makeRhythmicityJobSummary(UUID.randomUUID(), exp.getId());
        rhythmicityRep.saveJobDetails(job1);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/rhythmicity/jobs")
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertNotNull(resp);
        
        ListWrapper<RhythmicityJobSummary> list = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<RhythmicityJobSummary>>() { });
        assertNotNull(list);
        
        List<RhythmicityJobSummary> jobs = list.data;
        assertFalse(jobs.isEmpty());
        
        assertEquals(rhythmicityRep.getJobs(pack), jobs);
        
        
    }
    
    @Test
    public void testGetJobReturnsFinishedJobWithoutJC() throws Exception {
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();                
        insertData(pack);
        
        RhythmicityJobSummary job1 = makeRhythmicityJobSummary(UUID.randomUUID(), exp.getId());
        job1.jobStatus.state = State.SUCCESS;
        
        rhythmicityRep.saveJobDetails(job1);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/rhythmicity/job/"+job1.jobId)
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertNotNull(resp);
        
        RhythmicityJobSummary res = mapper.readValue(resp.getResponse().getContentAsString(), RhythmicityJobSummary.class);
        assertNotNull(res);
        
        assertEquals(job1, res);
        
        verify(rhythmicityService, never()).getJobStatus(job1.jobId);
        
        
    }
    
    @Test
    public void testGetJobReturnsUpdatedJobsFromJCFurRunning() throws Exception {
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();                
        insertData(pack);
        
        RhythmicityJobSummary job1 = makeRhythmicityJobSummary(UUID.randomUUID(), exp.getId());
        job1.jobStatus.state = State.SUBMITTED;
        
        rhythmicityRep.saveJobDetails(job1);

        RhythmicityJobSummary job2 = makeRhythmicityJobSummary(job1.jobId, exp.getId());
        job2.jobStatus.state = State.ERROR;
        
        when(rhythmicityService.getJobStatus(job1.jobId)).thenReturn(job2.jobStatus);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/rhythmicity/job/"+job1.jobId)
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertNotNull(resp);
        
        RhythmicityJobSummary res = mapper.readValue(resp.getResponse().getContentAsString(), RhythmicityJobSummary.class);
        assertNotNull(res);
        
        assertEquals(job2.jobStatus.state, res.jobStatus.state);
        
        verify(rhythmicityService).getJobStatus(job1.jobId);
        
        assertEquals(job2.jobStatus.state, rhythmicityRep.findJob(job1.jobId, exp.getId()).get().jobStatus.state);
        
    }  
    
    @Test
    public void testGetJobResultsGetsTheSavedOne() throws Exception {
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();
        long expId = exp.getId();
        int size = insertData(pack);
        
        RhythmicityJobSummary job1 = makeRhythmicityJobSummary(UUID.randomUUID(), exp.getId());
        job1.jobStatus.state = State.SUCCESS;
        
        rhythmicityRep.saveJobDetails(job1);
        
        JobResults<TSResult<BD2eJTKRes>> results = makeBD2EJTKResults(job1.jobId, expId, 1, size); 
        
        
        rhythmicityRep.saveJobResults(results);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/rhythmicity/job/"+job1.jobId+"/results")
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertNotNull(resp);
        
        JobResults<TSResult<BD2eJTKRes>> res = mapper.readValue(resp.getResponse().getContentAsString(), 
                new TypeReference<JobResults<TSResult<BD2eJTKRes>>>() { });
        assertNotNull(res);
        
        assertEquals(results, res);
        
        verify(rhythmicityService, never()).getJobStatus(job1.jobId);
        
        
    }
    
    @Test
    public void testDeleteJob() throws Exception {
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay();                
        insertData(pack);
        
        RhythmicityJobSummary job1 = makeRhythmicityJobSummary(UUID.randomUUID(), exp.getId());
        rhythmicityRep.saveJobDetails(job1);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(serviceRoot+'/'+exp.getId()+"/rhythmicity/job/"+job1.jobId)
                .contentType(APPLICATION_JSON_UTF8)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertNotNull(resp);
        
        RhythmicityJobSummary job = mapper.readValue(resp.getResponse().getContentAsString(), RhythmicityJobSummary.class);
        assertNotNull(job);

        assertEquals(job1, job);
        
        assertTrue(rhythmicityRep.findJob(job1.jobId, exp.getId()).isEmpty());
        rhythmicityRep.getJobs(pack).forEach( j -> {
            assertNotEquals(job1.jobId, j.jobId);
        });
        
        
    }
    
    
    @Test
    public void exportRhythmicityJobProducesCSVFile() throws Exception {
    
        AssayPack pack = insertExperiment();

        ExperimentalAssay exp = pack.getAssay();
        long expId = exp.getId();
        int size = insertData(pack);
        
        RhythmicityJobSummary job1 = makeRhythmicityJobSummary(UUID.randomUUID(), exp.getId());
        job1.jobStatus.state = State.SUCCESS;
        
        rhythmicityRep.saveJobDetails(job1);
        
        JobResults<TSResult<BD2eJTKRes>> results = makeBD2EJTKResults(job1.jobId, expId, 1, size); 
        rhythmicityRep.saveJobResults(results);

        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+'/'+exp.getId()+"/rhythmicity/job/"+job1.jobId+"/export/")
                //.accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("text/csv"))
                .andReturn();

        assertNotNull(resp);
        
        assertTrue(resp.getResponse().getContentLength() > 100);
        //System.out.println("R: "+resp.getResponse().getContentAsString());
        assertTrue(resp.getResponse().getContentAsString().contains(""+job1.jobId));
    }
    
    
}
