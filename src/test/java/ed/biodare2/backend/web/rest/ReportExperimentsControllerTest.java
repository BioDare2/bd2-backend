/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
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
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({SimpleRepoTestConfig.class})
public class ReportExperimentsControllerTest extends ExperimentBaseIntTest {
    
    
    
    final String serviceRoot = "/api/report";
    
    @Autowired
    ReportExperimentsController reporter;
    
    public ReportExperimentsControllerTest() {
    }
 
    @Test
    public void experimentsReturnFile() throws Exception {
        AssayPack pack1 = insertExperiment();
        ExperimentalAssay exp1 = pack1.getAssay();        
        
        int series = insertData(pack1);

        AssayPack pack2 = insertExperiment();
        ExperimentalAssay exp2 = pack2.getAssay();        
        
        series += insertData(pack2);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+"/experiment")
                .contentType(APPLICATION_JSON_UTF8)
                .accept("text/csv")
                .with(authenticate(fixtures.demoUser));

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("text/csv"))
                .andReturn();

        assertNotNull(resp);
        
        String wrapper = resp.getResponse().getContentAsString();
        assertNotNull(wrapper);
        assertTrue(wrapper.length() > 100);
        assertTrue(wrapper.startsWith("id,version,created,modified,owner,domain,isPublic,series,avgSeriesDuration,\n"));
        //System.out.println(wrapper.substring(0,100));
        
        
    }


    @Test
    public void getDataEntryMapsToRecord() throws Exception {
        AssayPack pack1 = insertExperiment();
        ExperimentalAssay exp1 = pack1.getAssay();        
        
        int series = insertData(pack1);

        ReportExperimentsController.DataEntry record = reporter.getDataEntry(exp1.getId()).orElse(null);
        assertNotNull(record);
        
        assertEquals(pack1.getId(), record.id);
        assertEquals(LocalDate.now().minus(5, ChronoUnit.DAYS), record.creationDate);
        assertEquals(LocalDate.now().minus(1, ChronoUnit.DAYS), record.modificationDate);
        assertEquals("user1", record.owner);
        assertEquals("@ed.ac.uk", record.domain);
        assertEquals(series, record.series);
        assertTrue(record.avgSeriesDuration > 0);
        
        
    }
    
    @Test
    public void testEntryGeneratesCSVRow() throws Exception {
        
        ReportExperimentsController.DataEntry record = new ReportExperimentsController.DataEntry();
        record.id = 2;
        record.versionId = "1.1";
        record.creationDate = LocalDate.of(2023, Month.MARCH, 4);
        record.modificationDate = LocalDate.of(2023, Month.MARCH, 5);
        record.owner ="user1";
        record.domain="@ed.ac.uk";
        record.series  = 3;
        record.avgSeriesDuration = 24;
        
        StringWriter out = new StringWriter();
        
        reporter.saveEntry(out, record);
        
        assertEquals("2,1.1,2023-03-04,2023-03-05,user1,@ed.ac.uk,false,3,24.0,\n", out.getBuffer().toString());
    }
    
    @Test
    public void testSaveHeaderGeneratesCSVRow() throws Exception {
        

        
        StringWriter out = new StringWriter();
        
        reporter.saveHeader(out);
        
        assertEquals("id,version,created,modified,owner,domain,isPublic,series,avgSeriesDuration,\n", out.getBuffer().toString());
    }    
    
}
