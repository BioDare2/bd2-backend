/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.features.search.SortOption;
import static ed.biodare2.backend.features.search.SortOption.*;
import ed.biodare2.backend.handlers.ExperimentHandlerTest;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.ui_dom.exp.ExperimentSummary;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.openaccess.OpenAccessLicence;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.ui_dom.exp.ExperimentalAssayView;
import ed.biodare2.backend.repo.ui_dom.shared.Page;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SimpleRepoTestConfig.class)
public class ExperimentContorllerIntTest extends ExperimentBaseIntTest {
 

    final String serviceRoot = "/api/experiment";
    
    @Autowired
    ExperimentPackHub expBoundles;    

    
    @Test
    public void getExperimentsGetsCorrectJsonRepresentation() throws Exception {
    
        AssayPack pack = insertExperiment();
        ExperimentalAssay org = pack.getAssay();
        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/experiments")
                //.param("onlyOwned", "false")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        //System.out.println("Exps JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        //List<ExperimentSummary> exps = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<List<ExperimentSummary>>() { });
        ListWrapper<ExperimentSummary> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<ExperimentSummary>>() { });
        assertNotNull(wrapper);
        List<ExperimentSummary> exps = wrapper.data;
        assertNotNull(exps);
        assertFalse(exps.isEmpty());
        
        final long id = org.getId();
        assertTrue(exps.stream().anyMatch( s -> s.id == id));
    }
    
    @Test
    public void getExperimentsCanListsVisiblePublicExperiments() throws Exception {
    
        AssayPack pack = insertPublicExperiment();
        ExperimentalAssay org = pack.getAssay();
        
        UserAccount user = fixtures.demoUser;
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/experiments")
                //.param("onlyOwned", "false")
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(user));

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        //System.out.println("Exps JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        //List<ExperimentSummary> exps = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<List<ExperimentSummary>>() { });
        ListWrapper<ExperimentSummary> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<ExperimentSummary>>() { });
        assertNotNull(wrapper);
        List<ExperimentSummary> exps = wrapper.data;
        assertNotNull(exps);
        assertTrue(exps.isEmpty());
        
        builder = MockMvcRequestBuilders.get("/api/experiments")
                .param("showPublic", "true")
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(user));

        resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        //System.out.println("Exps JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        //List<ExperimentSummary> exps = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<List<ExperimentSummary>>() { });
        wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<ExperimentSummary>>() { });
        assertNotNull(wrapper);
        exps = wrapper.data;
        assertNotNull(exps);
        assertFalse(exps.isEmpty());
        final long id = org.getId();
        assertTrue(exps.stream().anyMatch( s -> s.id == id));    
    }    
    
    
    @Test
    public void getExperimentsAppliesPagination() throws Exception {
    
        AssayPack pack = insertPublicExperiment();
        AssayPack pack2 = insertPublicExperiment();
        ExperimentalAssay org = pack.getAssay();
        
        UserAccount user = fixtures.demoUser;

        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/experiments")
                .param("showPublic", "true")
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(user));

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        //System.out.println("Exps JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        //List<ExperimentSummary> exps = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<List<ExperimentSummary>>() { });
        ListWrapper<ExperimentSummary> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<ExperimentSummary>>() { });
        assertNotNull(wrapper);
        List<ExperimentSummary> exps = wrapper.data;
        assertNotNull(exps);
        assertFalse(exps.isEmpty());
        assertEquals(25, wrapper.currentPage.pageSize);
        final long id = org.getId();
        assertTrue(exps.stream().anyMatch( s -> s.id == id));    
        assertTrue(exps.stream().anyMatch( s -> s.id == pack2.getId())); 
                
        int total = wrapper.currentPage.length;
        
        // System.out.println( id +" "+pack2.getId());
        // System.out.println(exps.stream().map(e -> ""+e.id).collect(Collectors.joining(",")));
        
        builder = MockMvcRequestBuilders.get("/api/experiments")
                .param("showPublic", "true")
                .param("pageIndex","1")
                .param("pageSize", ""+(total-1))
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(user));

        resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn(); 
        
        wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<ExperimentSummary>>() { });
        assertNotNull(wrapper);
        exps = wrapper.data;
        assertNotNull(exps);
        assertEquals(1, exps.size());
        assertEquals(1, wrapper.currentPage.pageIndex);
        
        // System.out.println(exps.stream().map(e -> ""+e.id).collect(Collectors.joining(",")));

        // assertTrue(exps.stream().anyMatch( s -> s.id == id || s.id == pack2.getId()));    
        
    }    
    
    
    @Test
    public void getExperimentsAppliesSorting() throws Exception {
    
        AssayPack pack = insertPublicExperiment();
        AssayPack pack2 = insertPublicExperiment();
        AssayPack pack3 = insertPublicExperiment();
        
        pack.getAssay().generalDesc.name = "Bsecond";
        pack2.getAssay().generalDesc.name = "Afirst";
        pack3.getAssay().generalDesc.name = "Cthird";
        
        expBoundles.save(expBoundles.enableWriting(pack));
        expBoundles.save(expBoundles.enableWriting(pack2));
        expBoundles.save(expBoundles.enableWriting(pack3));
        
        
        UserAccount user = fixtures.demoUser;

        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/experiments")
                .param("showPublic", "true")
                .param("sorting", "name")
                .param("direction", "asc")
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(user));

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        //System.out.println("Exps JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        //List<ExperimentSummary> exps = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<List<ExperimentSummary>>() { });
        ListWrapper<ExperimentSummary> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<ExperimentSummary>>() { });
        assertNotNull(wrapper);
        List<ExperimentSummary> exps = wrapper.data;
        assertNotNull(exps);
        assertEquals(3, exps.size());
        
        assertEquals(pack2.getId(), exps.get(0).id);
        assertEquals(pack.getId(), exps.get(1).id);
        assertEquals(pack3.getId(), exps.get(2).id);
        
        
        builder = MockMvcRequestBuilders.get("/api/experiments")
                .param("showPublic", "true")
                .param("sorting", "name")
                .param("direction", "desc")
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(user)); 
        
        resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);     
        
        wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<ExperimentSummary>>() { });
        exps = wrapper.data;
        assertNotNull(exps);
        assertEquals(3, exps.size());
        
        assertEquals(pack3.getId(), exps.get(0).id);
        assertEquals(pack.getId(), exps.get(1).id);
        assertEquals(pack2.getId(), exps.get(2).id);
        
    }    
    
    
    @Test
    public void searchExperimentsSearchesAndSorts() throws Exception {
    
        AssayPack pack = insertPublicExperiment();
        AssayPack pack2 = insertPublicExperiment();
        AssayPack pack3 = insertPublicExperiment();
        
        pack.getAssay().generalDesc.name += "D clock Winterpride unique";
        pack2.getAssay().generalDesc.name = "Afirst clock Winterpride";
        pack3.getAssay().generalDesc.name = "Cthird missing";
        
        expBoundles.save(expBoundles.enableWriting(pack));
        expBoundles.save(expBoundles.enableWriting(pack2));
        expBoundles.save(expBoundles.enableWriting(pack3));
        
        
        UserAccount user = fixtures.demoUser;

        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/experiments/search")
                .param("showPublic", "true")
                .param("sorting", "name")
                .param("direction", "asc")
                .param("query", "winterpride")
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(user));

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        //System.out.println("Exps JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        //List<ExperimentSummary> exps = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<List<ExperimentSummary>>() { });
        ListWrapper<ExperimentSummary> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<ExperimentSummary>>() { });
        assertNotNull(wrapper);
        List<ExperimentSummary> exps = wrapper.data;
        assertNotNull(exps);
        assertEquals(2, exps.size());
        
        assertEquals(pack2.getId(), exps.get(0).id);
        
        
        builder = MockMvcRequestBuilders.get("/api/experiments/search")
                .param("showPublic", "true")
                .param("sorting", "name")
                .param("direction", "desc")
                .param("query", "winterpride")                
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(user)); 
        
        resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);     
        
        wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<ExperimentSummary>>() { });
        exps = wrapper.data;
        assertNotNull(exps);
        assertEquals(2, exps.size());
        
        assertEquals(pack.getId(), exps.get(0).id);
        
        builder = MockMvcRequestBuilders.get("/api/experiments/search")
                .param("showPublic", "true")
                .param("sorting", "name")
                .param("direction", "")
                .param("query", "unique")                
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(user)); 
        
        resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);     
        
        wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<ExperimentSummary>>() { });
        exps = wrapper.data;
        assertNotNull(exps);
        assertEquals(1, exps.size());
        
        assertEquals(pack.getId(), exps.get(0).id);
        
        
    }    
    
    @Test
    public void draftCreatesNewExperimentWithCurrentUserAsAuthor() throws Exception {
    
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+"/draft")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("RESP: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        //System.out.println("DRAFT\n"+resp.getResponse().getContentAsString());
        
        ExperimentalAssayView exp = mapper.readValue(resp.getResponse().getContentAsString(), ExperimentalAssayView.class);
        assertNotNull(exp);
        
        //ssertTrue(exp.getId() > 0);
        
        assertEquals(currentUser.getLogin(),exp.contributionDesc.authors.get(0).login);
        //assertTrue(false);
    }
    
    @Test
    public void draftThrowsInsufficientRightsForAnonymousUser() throws Exception {
    
        
        UserAccount user = fixtures.anonymous;
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+"/draft")
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(user));

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                //.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("RESP: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        //Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        String info = resp.getResponse().getErrorMessage();
        assertNotNull(info);
        //assertEquals("Loggin to perform the operation",info.get("message"));
        assertEquals("Loggin to perform the operation",info);

    }
    
    
    @Test
    public void getExperimentGetsCorrectJsonRepresentation() throws Exception {
    
        //ExperimentalAssay desc = DomRepoTestBuilder.makeExperiment(3);
        //org = experiments.save(desc);
        AssayPack pack = insertExperiment();
        ExperimentalAssay org = pack.getAssay();        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+"/"+org.getId())
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("Exp JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        ExperimentalAssayView exp = mapper.readValue(resp.getResponse().getContentAsString(), ExperimentalAssayView.class);
        assertNotNull(exp);
        
        assertEquals(org.getId(),exp.id);
        assertEquals(org.getName(),exp.generalDesc.name);
        assertEquals(org.experimentalDetails.executionDate,exp.experimentalDetails.executionDate);
        //assertTrue(org.hasSameValues(exp));
        
    }
    
    @Test
    public void insertThrowsInsufficientRightsForAnonymousUser() throws Exception {
    
        
        UserAccount user = fixtures.anonymous;
        
        ExperimentalAssayView org = DomRepoTestBuilder.makeExperimentalAssayView();
        //assertFalse(expBoundles.findOne(org.getId()).isPresent());
        
        
        String orgJSON = mapper.writeValueAsString(org);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(serviceRoot)
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(user));

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                //.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();
        assertNotNull(resp);
        
        //System.out.println("RESP: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        //Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        String info = resp.getResponse().getErrorMessage();
        assertNotNull(info);
        //assertEquals("Loggin to perform the operation",info.get("message"));
        assertEquals("Loggin to perform the operation",info);

    }
    

    @Test
    public void insertExperimentSavesJsonRepresentationAsExperiment() throws Exception {
    
        ExperimentalAssayView org = DomRepoTestBuilder.makeExperimentalAssayView();
        //assertFalse(expBoundles.findOne(org.getId()).isPresent());
        
        String orgJSON = mapper.writeValueAsString(org);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(serviceRoot)
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("Exp Insert JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        
        ExperimentalAssayView exp = mapper.readValue(resp.getResponse().getContentAsString(), ExperimentalAssayView.class);
        assertNotNull(exp);
        
        assertNotNull(exp.id);
        assertTrue(exp.id > 0L);
        
        assertTrue(expBoundles.findOne(exp.id).isPresent());
        
    }
    
    
    String bd1requestJSON() throws IOException {
        Path req = (new ExperimentHandlerTest()).testFile("3967.importdsc.json").toPath();
        try (Stream<String> lines = Files.lines(req)) {
            return lines.collect(Collectors.joining("\n"));
        }
        
    } 
    
    @Test
    @Ignore
    public void importBD1ExperimentSavesJsonRepresentationAsExperiment() throws Exception {
    
        
        
        String orgJSON = bd1requestJSON();
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(serviceRoot+"/bd1-import")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("Exp BD1 Import JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        
        ExperimentalAssayView exp = mapper.readValue(resp.getResponse().getContentAsString(), ExperimentalAssayView.class);
        assertNotNull(exp);
        
        assertNotNull(exp.id);
        assertEquals(3967,exp.id);
        
        assertTrue(expBoundles.findOne(exp.id).isPresent());
        
    }
    
    @Test
    public void insertExperimentSavesAngularRequest() throws Exception {
    
        
        String orgJSON = "{\"generalDesc\":{\"name\":\"New experiment payload\",\"purpose\":\"Checking how wiring works\",\"description\":\"Is it ok, you think so??\",\"comments\":null,\"executionDate\" : [ 2020, 1, 20 ]},\"contributionDesc\":{\"authors\":[{\"firstName\":\"Demo\",\"lastName\":\"User\",\"id\":4,\"login\":\"demo\",\"ORCID\":null}],\"curators\":[],\"institutions\":[{\"name\":\"University of Edinburgh\"}],\"fundings\":[]},\"experimentalDetails\":{\"measurementDesc\":{\"parameters\":[],\"technique\":null,\"equipment\":null,\"description\":null},\"growthEnvironments\":{\"environments\":[]},\"experimentalEnvironments\":{\"environments\":[]},\"executionDate\":[2016,10,5]},\"id\":0,\"features\":{\"hasTSData\":false,\"hasPPAJobs\":false,\"hasDataFiles\":false},\"species\":\"Arabidopsis thaliana\",\"dataCategory\":\"GEN_IMAGING\",\"provenance\":{\"created\":null,\"createdBy\":null,\"modified\":null,\"modifiedBy\":null},\"security\":{\"canRead\":true,\"canWrite\":true,\"isOwner\":true,\"isSuperOwner\":false}}";
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(serviceRoot)
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("Exp Insert JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        
        ExperimentalAssayView exp = mapper.readValue(resp.getResponse().getContentAsString(), ExperimentalAssayView.class);
        assertNotNull(exp);
        
        assertNotNull(exp.id);
        assertTrue(exp.id > 0L);
        
        assertTrue(expBoundles.findOne(exp.id).isPresent());
        
    }    
    
    @Test
    public void updateThrowsInsufficientRightsForAnonymousUser() throws Exception {
    
        
        UserAccount user = fixtures.anonymous;
        AssayPack pack = insertExperiment();
        
        assertTrue(expBoundles.findOne(pack.getId()).isPresent());
        
        ExperimentalAssayView req = DomRepoTestBuilder.makeExperimentalAssayView();
        
        String orgJSON = mapper.writeValueAsString(req);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/"+pack.getId())
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(user));

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                //.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();
        assertNotNull(resp);
        
        //System.out.println("RESP: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        //Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        String info = resp.getResponse().getErrorMessage();
        assertNotNull(info);
        //assertEquals("Loggin to perform the operation",info.get("message"));
        assertEquals("Loggin to perform the operation",info);

    }
    
    @Test
    public void updateExperimentSavesJsonRepresentationAsExperiment() throws Exception {
    
        AssayPack pack = insertExperiment();
        
        assertTrue(expBoundles.findOne(pack.getId()).isPresent());
        
        ExperimentalAssayView req = DomRepoTestBuilder.makeExperimentalAssayView();
        req.generalDesc.name = "Updated name";

        
        String orgJSON = mapper.writeValueAsString(req);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/"+pack.getId())
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("Update JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        
        ExperimentalAssayView exp = mapper.readValue(resp.getResponse().getContentAsString(), ExperimentalAssayView.class);
        assertNotNull(exp);
        
        assertEquals(pack.getId(),exp.id);
        assertEquals(exp.generalDesc.name,req.generalDesc.name);


        
    }
    
    @Test
    public void updateExperimentUsesDateFromGeneralDescription() throws Exception {
    
        AssayPack pack = insertExperiment();
        
        assertTrue(expBoundles.findOne(pack.getId()).isPresent());
        
        ExperimentalAssayView req = DomRepoTestBuilder.makeExperimentalAssayView();
        req.generalDesc.name = "Updated name";
        LocalDate date = LocalDate.now().minus(1,ChronoUnit.YEARS);
        req.generalDesc.executionDate = date;
        req.experimentalDetails.executionDate = LocalDate.now().plus(1, ChronoUnit.DAYS);
        
        String orgJSON = mapper.writeValueAsString(req);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/"+pack.getId())
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("Update JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        
        ExperimentalAssayView exp = mapper.readValue(resp.getResponse().getContentAsString(), ExperimentalAssayView.class);
        assertNotNull(exp);
        
        assertEquals(pack.getId(),exp.id);
        assertEquals(exp.generalDesc.name,req.generalDesc.name);
        assertEquals(date, exp.generalDesc.executionDate);
        assertEquals(date, exp.experimentalDetails.executionDate);
        
    }    
    
    @Test
    public void updateExperimentSavesAngularRequest() throws Exception {
    
        AssayPack pack = insertExperiment();        
        assertTrue(expBoundles.findOne(pack.getId()).isPresent());
        
        String orgJSON = "{\"generalDesc\":{\"name\":\"Testing new experiment creationg\",\"purpose\":\"Checking if all the wiring works\",\"description\":\"asdfdsf dsfsdfd\",\"comments\":null,\"executionDate\" : [ 2020, 1, 20 ]},\"contributionDesc\":{\"authors\":[{\"firstName\":\"Demo\",\"lastName\":\"User\",\"id\":4,\"login\":\"demo\",\"ORCID\":null}],\"curators\":[],\"institutions\":[{\"name\":\"University of Edinburhg\"}],\"fundings\":[]},\"experimentalDetails\":{\"measurementDesc\":{\"parameters\":[],\"technique\":null,\"equipment\":null,\"description\":null},\"growthEnvironments\":{\"environments\":[]},\"experimentalEnvironments\":{\"environments\":[]},\"executionDate\":[2016,9,29]},\"id\":1020,\"features\":{\"hasTSData\":false,\"hasPPAJobs\":false,\"hasDataFiles\":false},\"species\":\"Homo sapiens\",\"dataCategory\":\"GEN_IMAGING\",\"provenance\":{\"created\":[2016,9,29,16,57,47,857000000],\"createdBy\":\"Demo User\",\"modified\":[2016,9,29,16,57,47,857000000],\"modifiedBy\":\"Demo User\"},\"security\":{\"canRead\":true,\"canWrite\":true,\"isOwner\":true,\"isSuperOwner\":false}}";
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/"+pack.getId())
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("Update JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        
        ExperimentalAssayView exp = mapper.readValue(resp.getResponse().getContentAsString(), ExperimentalAssayView.class);
        assertNotNull(exp);
        
        assertEquals(pack.getId(),exp.id);
        assertEquals(exp.generalDesc.name,"Testing new experiment creationg");
        
    }
    
    @Test
    public void publishThrowsInsufficientRightsForAnonymousUser() throws Exception {
    
        
        UserAccount user = fixtures.anonymous;
        AssayPack pack = insertExperiment();
        
        assertTrue(expBoundles.findOne(pack.getId()).isPresent());
        
        String orgJSON = mapper.writeValueAsString(OpenAccessLicence.CC_BY);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(serviceRoot+"/"+pack.getId()+"/publish")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(user));

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                //.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();
        assertNotNull(resp);
        
        //System.out.println("RESP: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        //Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        String info = resp.getResponse().getErrorMessage();
        assertNotNull(info);
        //assertEquals("Loggin to perform the operation",info.get("message"));
        assertEquals("Loggin to perform the operation",info);

    }
    
    @Test
    public void publishThrowsInsufficientRightsForNotOwnerUser() throws Exception {
    
        
        UserAccount user = fixtures.demoUser;
        AssayPack pack = insertExperiment();
        
        assertTrue(expBoundles.findOne(pack.getId()).isPresent());
        
        String orgJSON = mapper.writeValueAsString(OpenAccessLicence.CC_BY);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(serviceRoot+"/"+pack.getId()+"/publish")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(user));

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                //.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();
        assertNotNull(resp);
        
        //System.out.println("RESP: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        //Map<String,String> info = mapper.readValue(resp.getResponse().getContentAsString(), Map.class);
        String info = resp.getResponse().getErrorMessage();
        assertNotNull(info);
        //assertEquals("Loggin to perform the operation",info.get("message"));
        assertEquals("Insufficient rights to perform the operation",info);

    }
    
    @Test
    public void publishPublishesTheExperiment() throws Exception {
    
        
        AssayPack pack = insertExperiment();
        
        assertTrue(expBoundles.findOne(pack.getId()).isPresent());
        
        String orgJSON = mapper.writeValueAsString(OpenAccessLicence.CC_BY);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(serviceRoot+"/"+pack.getId()+"/publish")
                .contentType(APPLICATION_JSON_UTF8)
                .content(orgJSON)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("Publish JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        ExperimentalAssayView exp = mapper.readValue(resp.getResponse().getContentAsString(), ExperimentalAssayView.class);
        assertNotNull(exp);
        
        assertEquals(pack.getId(),exp.id);
        assertTrue(exp.features.isOpenAccess);
        
        pack = expBoundles.findOne(pack.getId()).get();
        assertTrue(pack.getACL().isPublic());

    }
    
    @Test
    //@Ignore //it is ingnored as was getting errors with tests from TestSeeder. Don't understand why but hibernates id generators
    //were not commited and clashes with each other.
    public void checkConcurrentInsertsGeneratesIds() throws Exception {
        
        
        Queue<String> errs = new ConcurrentLinkedQueue<>();
        Queue<Long> ids = new ConcurrentLinkedQueue<>();

        ExecutorService exec = Executors.newFixedThreadPool(6);
        String orgJSON = "{\"generalDesc\":{\"name\":\"New experiment payload\",\"purpose\":\"Checking how wiring works\",\"description\":\"Is it ok, you think so??\",\"comments\":null},\"contributionDesc\":{\"authors\":[{\"firstName\":\"Demo\",\"lastName\":\"User\",\"id\":4,\"login\":\"demo\",\"ORCID\":null}],\"curators\":[],\"institutions\":[{\"name\":\"University of Edinburgh\"}],\"fundings\":[]},\"experimentalDetails\":{\"measurementDesc\":{\"parameters\":[],\"technique\":null,\"equipment\":null,\"description\":null},\"growthEnvironments\":{\"environments\":[]},\"experimentalEnvironments\":{\"environments\":[]},\"executionDate\":[2016,10,5]},\"id\":0,\"features\":{\"hasTSData\":false,\"hasPPAJobs\":false,\"hasDataFiles\":false},\"species\":\"Arabidopsis thaliana\",\"dataCategory\":\"GEN_IMAGING\",\"provenance\":{\"created\":null,\"createdBy\":null,\"modified\":null,\"modifiedBy\":null},\"security\":{\"canRead\":true,\"canWrite\":true,\"isOwner\":true,\"isSuperOwner\":false}}";
        
        int N = 30;
        for (int i =0;i<N;i++) {

            exec.submit( () -> {
                MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put(serviceRoot)
                    .contentType(APPLICATION_JSON_UTF8)
                    .content(orgJSON)
                    .accept(APPLICATION_JSON_UTF8)
                    .with(mockAuthentication);

                try {
                    MvcResult resp = mockMvc.perform(builder)
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                        .andReturn();
                    
                    ExperimentalAssayView exp = mapper.readValue(resp.getResponse().getContentAsString(), ExperimentalAssayView.class);                    
                    if (exp == null) errs.add("Null exp");
                    else ids.add(exp.id);
                    
                } catch (Exception e) {
                    errs.add(e.getClass().getSimpleName()+":"+e.getMessage());
                };
            });
        }
        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.MINUTES);
        
        System.out.println("checkConcurrentInsertsGeneratesIds Errs: ");
        errs.forEach(System.out::println);        
        System.out.println("with IDs: "+ids);
        
        assertTrue(errs.isEmpty());
        assertEquals(N,ids.size());
        Set<Long> unique = new HashSet<>(ids);
        assertEquals(N,unique.size());
        
        
    }
    
    @Test
    public void pageConvertsParamsToSensibles() {
        
        int pageSize = 10;
        int pageIndex = 1;
        
        Page page = ExperimentController.paramsToPage(pageIndex, pageSize);
        assertEquals(new Page(1, 10), page);
        
        pageSize = 1500;
        pageIndex = -1;
        
        page = ExperimentController.paramsToPage(pageIndex, pageSize);
        assertEquals(new Page(0, 1000), page);        
    }
    
    @Test
    public void sortingOptionsDefaultsToRankIfNoDirection() {
        String sorting = "id";
        String direction = null;
        
        SortOption sort = ExperimentController.paramsToSort(sorting, direction);
        assertEquals(SortOption.RANK, sort);
        
        direction = "";
        sort = ExperimentController.paramsToSort(sorting, direction);
        assertEquals(SortOption.RANK, sort);        
        
        direction = "asc";
        sort = ExperimentController.paramsToSort(sorting, direction);
        assertEquals(SortOption.ID, sort);        
        
        sorting = "modified";
        direction = "desc";
        sort = ExperimentController.paramsToSort(sorting, direction);
        assertEquals(SortOption.MODIFICATION_DATE, sort);        
        
    }
    
    @Test
    public void sortingOptionsDecodesSimpleNames() {
        
        String[] terms = {"rank", "id", "name", "author", "executed", "modified"};
        
        SortOption[] exps = {RANK, ID, NAME, FIRST_AUTHOR, EXECUTION_DATE, MODIFICATION_DATE};
        
        for (int i = 0; i< terms.length; i++) {
            String term = terms[i];
            SortOption exp = exps[i];
            
            SortOption sort = ExperimentController.paramsToSort(term, "asc");
            assertEquals(term, exp,sort);
        }
        
    }
}
