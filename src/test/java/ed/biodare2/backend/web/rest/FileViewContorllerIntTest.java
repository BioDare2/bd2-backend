/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.features.tsdata.tableview.DataTableSlice;
import ed.biodare2.backend.features.tsdata.tableview.Slice;
import ed.biodare2.backend.handlers.FileUploadHandler;
import ed.biodare2.backend.handlers.UploadFileInfo;
import ed.biodare2.backend.repo.isa_dom.dataimport.ImportFormat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Before;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;


/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SimpleRepoTestConfig.class)
public class FileViewContorllerIntTest extends AbstractIntTestBase {
 

    
    final String serviceRoot = "/api/file";
    
    @Autowired
    FileUploadHandler handler;

    UploadFileInfo uploaded;
    
    @Before
    @Override
    public void setUp() throws Exception {
    
        super.setUp();
        
        
        /*InputStream in = this.getClass().getResourceAsStream("data-sheet.xlsx");
        MockMultipartFile upload = new MockMultipartFile("file", "original", "text", in);
        uploaded = handler.save(upload, currentUser);
        */
        
        uploaded = upload("data-sheet.xlsx");
    }
    
    protected UploadFileInfo upload(String resName) throws IOException {
        InputStream in = this.getClass().getResourceAsStream(resName);
        MockMultipartFile upload = new MockMultipartFile("file", "original", "text", in);

        return handler.save(upload, currentUser);        
    }
       
    
    protected UploadFileInfo upload(Path file) throws IOException {
        InputStream in = Files.newInputStream(file);
        MockMultipartFile upload = new MockMultipartFile("file", "original", "text", in);

        return handler.save(upload, currentUser);        
    }    
    
    @Test
    public void getSimpleTableViewWorksOnExcel() throws Exception {

        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+"/"+uploaded.id+"/view/simpletable")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        //System.out.println("DataView JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        ListWrapper<List<String>> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<List<String>>>() { });
        assertNotNull(wrapper);
        List<List<String>> table = wrapper.data; //mapper.readValue(resp.getResponse().getContentAsString(), List.class);
        assertNotNull(table);
        
        assertEquals(25,table.size());
        
        
    }
    
    @Test
    public void getSimpleTableViewWorksOnTopCount() throws Exception {

        //ImportFormat format = ImportFormat.TOPCOUNT;
        uploaded = upload("col1609.zip");        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+"/"+uploaded.id+"/view/simpletable")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        //System.out.println("DataView JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        ListWrapper<List<String>> wrapper = mapper.readValue(resp.getResponse().getContentAsString(), new TypeReference<ListWrapper<List<String>>>() { });
        assertNotNull(wrapper);
        List<List<String>> table = wrapper.data; //mapper.readValue(resp.getResponse().getContentAsString(), List.class);
        assertNotNull(table);
        
        assertEquals(25,table.size());
        assertEquals(96,table.get(0).size());
        
        
    }
    
    @Test
    public void getSimpleTableViewGivesHandlingExceptionOnNonRecognizedFile() throws Exception {

        
        //InputStream in = this.getClass().getResourceAsStream("signs.csv");
        //MockMultipartFile upload = new MockMultipartFile("file", "original", "text", in);

        uploaded = upload("signs.csv");
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+"/"+uploaded.id+"/view/simpletable")
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                //.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        //System.out.println("DataView ERROR JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        
        
    }    
    
    @Test
    public void verifyGivesTrueOnValidExcelFile() throws Exception {

        ImportFormat format = ImportFormat.EXCEL_TABLE;
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+"/"+uploaded.id+"/verify/format/"+format.name())
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        //System.out.println("Verify JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        assertEquals("true",resp.getResponse().getContentAsString());
                
    }   
    
    @Test
    public void verifyGivesTrueOnValidTopcountFile() throws Exception {

        ImportFormat format = ImportFormat.TOPCOUNT;
        uploaded = upload("col1609.zip");
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+"/"+uploaded.id+"/verify/format/"+format.name())
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        //System.out.println("Verify JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        assertEquals("true",resp.getResponse().getContentAsString());
                
    }   
    
    
    @Test
    public void verifyGivesBadRequestOnInvalidExcelFile() throws Exception {

        ImportFormat format = ImportFormat.EXCEL_TABLE;
        
        //InputStream in = this.getClass().getResourceAsStream("signs.csv");
        //MockMultipartFile upload = new MockMultipartFile("file", "original", "text", in);
        //uploaded = handler.save(upload, currentUser);
        uploaded = upload("signs.csv");
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+"/"+uploaded.id+"/verify/format/"+format.name())
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                //.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        System.out.println("Verify JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        //assertEquals("true",resp.getResponse().getContentAsString());
        
        
    }     
    
    @Test
    public void getTableSliceWorksOnCSV() throws Exception {

        uploaded = upload("wt_prr_simpl.csv");
        ImportFormat format = ImportFormat.COMA_SEP;
        
        Slice slice = new Slice();
        slice.rowPage.pageSize = 10;
        slice.colPage.pageSize = 5;
        
        String orgJSON = mapper.writeValueAsString(slice);
        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/"+uploaded.id+"/view/tableslice"+"/"+format.name())
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
        //System.out.println("DataView JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        DataTableSlice dataSlice = mapper.readValue(resp.getResponse().getContentAsString(), DataTableSlice.class);
        assertNotNull(dataSlice);
        
        assertEquals(10, dataSlice.data.size());
        assertEquals(5, dataSlice.data.get(0).size());
        
        assertEquals("id", dataSlice.data.get(0).get(0));
        assertEquals("1.113459299", dataSlice.data.get(1).get(2));
        
    }    
    
    @Test
    public void getTableSliceWorksWithTSV() throws Exception {

        uploaded = upload("wt_prr_simpl.tsv");
        ImportFormat format = ImportFormat.TAB_SEP;
        
        Slice slice = new Slice();
        slice.rowPage.pageIndex = 1;
        slice.rowPage.pageSize = 10;
        slice.colPage.pageIndex = 1;
        slice.colPage.pageSize = 5;
        
        String orgJSON = mapper.writeValueAsString(slice);
        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/"+uploaded.id+"/view/tableslice"+"/"+format.name())
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
        //System.out.println("DataView JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        DataTableSlice dataSlice = mapper.readValue(resp.getResponse().getContentAsString(), DataTableSlice.class);
        assertNotNull(dataSlice);
        
        assertEquals(10, dataSlice.data.size());
        assertEquals(5, dataSlice.data.get(0).size());
        
        assertEquals(10, (int)dataSlice.rowsNumbers.get(0));
        assertEquals("0.677429628", dataSlice.data.get(0).get(0));
        assertEquals("0.735742597", dataSlice.data.get(1).get(0));
        
    }     
    
    @Test
    public void getTableSliceWorksOnExcel() throws Exception {

        uploaded = upload("wt_prr_simpl.xlsx");
        ImportFormat format = ImportFormat.EXCEL_TABLE;
        
        Slice slice = new Slice();
        slice.rowPage.pageSize = 10;
        slice.colPage.pageSize = 5;
        
        String orgJSON = mapper.writeValueAsString(slice);
        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/"+uploaded.id+"/view/tableslice"+"/"+format.name())
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
        //System.out.println("DataView JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        DataTableSlice dataSlice = mapper.readValue(resp.getResponse().getContentAsString(), DataTableSlice.class);
        assertNotNull(dataSlice);
        
        assertEquals(10, dataSlice.data.size());
        assertEquals(5, dataSlice.data.get(0).size());
        
        assertEquals("id", dataSlice.data.get(0).get(0));
        assertEquals(1.113459299, dataSlice.data.get(1).get(2));
        
    }    
    
    @Test
    @Ignore("Test file not commited")
    public void getTableSliceWorksOnLargeExcel() throws Exception {

        uploaded = upload(Paths.get("E:\\Temp\\long_255x5000.xls"));
        ImportFormat format = ImportFormat.EXCEL_TABLE;
        
        Slice slice = new Slice();
        slice.rowPage.pageSize = 10;
        slice.colPage.pageSize = 5;
        
        String orgJSON = mapper.writeValueAsString(slice);
        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(serviceRoot+"/"+uploaded.id+"/view/tableslice"+"/"+format.name())
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
        //System.out.println("DataView JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        DataTableSlice dataSlice = mapper.readValue(resp.getResponse().getContentAsString(), DataTableSlice.class);
        assertNotNull(dataSlice);
        
        assertEquals(10, dataSlice.data.size());
        assertEquals(5, dataSlice.data.get(0).size());
        
        assertEquals("Time", dataSlice.data.get(0).get(0));
        
    }    
    
}
