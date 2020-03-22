/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare.jobcentre2.dom.JobResults;
import ed.biodare.jobcentre2.dom.PPAJobResults;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare.jobcentre2.dom.TSResult;
import ed.biodare.rhythm.ejtk.BD2eJTKRes;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.features.ppa.PPAUtils;
import ed.biodare2.backend.features.rhythmicity.RhythmicityServiceParameters;
import ed.biodare2.backend.features.rhythmicity.dao.RhythmicityArtifactsRep;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.repo.dao.PPAArtifactsRep;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeBD2EJTKResults;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeBD2PPAResults;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeRhythmicityJobSummary;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.ppa.PPARequest;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.tsprocessing.PPAResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
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
//@AutoConfigureMockMvc(secure = false)
@AutoConfigureMockMvc
@Import(SimpleRepoTestConfig.class)
public class ServicesControllerTest extends ExperimentBaseIntTest {
    
    public ServicesControllerTest() {
    }
    
    @Autowired
    MockMvc mockMvc;    
    
    @Autowired
    RhythmicityArtifactsRep rhythmicityRep;
    
    @Autowired
    TSDataHandler tsHandler;    
    
    @Autowired
    RhythmicityServiceParameters rhythmicityParameters;
    
    @Autowired
    PPAArtifactsRep ppaRep;
    
    
    @Before
    @Override //so it will not configure security
    public void setUp() throws Exception  {
    }    

    /**
     * Test of handleRhythmicityResults method, of class ServicesController.
     */
    @WithMockUser(value = "ppaserver",roles = {"SERVICE"})
    @Test
    public void testHandleRhythmicityResults() throws Exception {
        
        UUID jobId = UUID.randomUUID();
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay(); 
        long expId = exp.getId();
        insertData(pack);

        RhythmicityJobSummary job = makeRhythmicityJobSummary(jobId, expId);
        job.jobStatus.state = State.SUBMITTED;
        job.jobStatus.completed = null;

        rhythmicityRep.saveJobDetails(job);
        
        DetrendingType detrending = DetrendingType.valueOf(job.parameters.get(job.DATA_SET_TYPE));        
        
        JobResults<TSResult<BD2eJTKRes>> results = makeBD2EJTKResults(jobId, expId); 
        BD2eJTKRes example = results.results.get(0).result;
        
        results.results.clear();
        
        List<DataTrace> orgData = tsHandler.getDataSet(pack, detrending).get();
        for (DataTrace trace : orgData) {
            results.results.add(new TSResult(trace.dataId, example));
        }
        
        String orgJSON = mapper.writeValueAsString(results);
        
        /*TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                rhythmicityParameters.ppaUsername,
                rhythmicityParameters.ppaPassword, "SERVICE");
        authentication.setAuthenticated(true);*/
        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/api/services/rhythmicity/results/"+expId)
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)                
                ;//.with(SecurityMockMvcRequestPostProcessors.authentication(authentication));

        
        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                //.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertNotNull(resp);        
        
        
        
        job = rhythmicityRep.findJob(jobId, expId).get();
        assertEquals(State.SUCCESS, job.jobStatus.state);
        assertNotNull(job.jobStatus.completed);

        Optional<JobResults<TSResult<BD2eJTKRes>>> savedO = rhythmicityRep.findJobResults(jobId, expId);
        assertTrue(savedO.isPresent());
        
        JobResults<TSResult<BD2eJTKRes>> saved = savedO.get();
        
        assertEquals(results, saved);
        
        /*
        assertEquals(results.externalId, saved.externalId);
        assertEquals(results.jobId, saved.jobId);
        assertEquals(results.message, saved.message);
        assertEquals(results.state, saved.state);
        //assertEquals(results.results, saved.results);
        
        for (int i = 0; i< results.results.size();i++) {
            TSResult<BD2eJTKRes> r1 = results.results.get(i);
            TSResult<BD2eJTKRes> r2 = saved.results.get(i);
            
            System.out.println(r1.result.getClass());
            System.out.println(r2.result.getClass());
            
            assertEquals("D"+i, r1, r2);
        }*/
        
    }
    
    static class TypedPPAJobResults extends JobResults<TSResult<PPAResult>>{

    } 
    
    @WithMockUser(value = "ppaserver",roles = {"SERVICE"})
    @Test
    public void testHandlePPA2Results() throws Exception {
        
        PPAUtils ppaUtils = new PPAUtils();
        UUID jobId = UUID.randomUUID();
        
        AssayPack pack = insertExperiment();
        ExperimentalAssay exp = pack.getAssay(); 
        long expId = exp.getId();
        insertData(pack);

        PPARequest req = DomRepoTestBuilder.makePPARequest();        
        PPAJobSummary job = ppaUtils.prepareNewPPAJobSummary(exp.getId(), req, jobId);        

        ppaRep.saveJobSummary(job, pack);
        
        DetrendingType detrending = DetrendingType.valueOf(job.dataSetType);        
        
        PPAJobResults results = makeBD2PPAResults(jobId, expId); 
        PPAResult example = results.results.get(0).result;
        
        results.results.clear();
        
        List<DataTrace> orgData = tsHandler.getDataSet(pack, detrending).get();
        for (DataTrace trace : orgData) {
            results.results.add(new TSResult(trace.dataId, example));
        }
        
        String orgJSON = mapper.writeValueAsString(results);
        
        /*TestingAuthenticationToken authentication = new TestingAuthenticationToken(
                rhythmicityParameters.ppaUsername,
                rhythmicityParameters.ppaPassword, "SERVICE");
        authentication.setAuthenticated(true);*/
        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/api/services/ppa2/results/"+expId)
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)                
                ;//.with(SecurityMockMvcRequestPostProcessors.authentication(authentication));

        
        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                //.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        assertNotNull(resp);        
        
        
        
        job = ppaRep.getJobSummary(pack, job.uuid).get();
        
        assertEquals(State.FINISHED.name(), job.state.name());
        assertNotNull(job.completed);

        
        List<PPAResult> saved = ppaRep.getJobIndResults(pack, job.uuid).stream().map( r -> r.getResult())
                .collect(Collectors.toList());
        
        assertFalse(saved.isEmpty());
        
        List<PPAResult> send = results.results.stream().map( r -> r.result).collect(Collectors.toList());
        
        assertEquals(send, saved);
        
        
    }
    

    
}
