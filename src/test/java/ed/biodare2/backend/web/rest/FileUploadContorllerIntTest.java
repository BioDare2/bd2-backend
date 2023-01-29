/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.security.dao.db.UserAccount;
import ed.biodare2.backend.handlers.FileUploadHandler;
import ed.biodare2.backend.handlers.UploadFileInfo;
import static ed.biodare2.backend.web.rest.AbstractIntTestBase.APPLICATION_JSON_UTF8;
import ed.biodare2.backend.security.BioDare2User;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SimpleRepoTestConfig.class)
public class FileUploadContorllerIntTest extends AbstractIntTestBase {
 
    
    final String serviceRoot = "/api/upload";
    
    @Autowired
    FileUploadHandler handler;
    
    @Test
    public void uploadsFile() throws Exception {

        MockMultipartFile upload = new MockMultipartFile("file", "original", "text", new byte[10]);
        
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(serviceRoot+"/one")
                .file(upload)
                .accept(APPLICATION_JSON_UTF8)
                .with(mockAuthentication);

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        
        //System.out.println("Upload JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        
        UploadFileInfo info = mapper.readValue(resp.getResponse().getContentAsString(), UploadFileInfo.class);
        assertNotNull(info);
        
        assertNotNull(info.id);
        assertEquals(info.contentType,"text");
        
        UploadFileInfo stored = handler.getInfo(info.id);
        assertEquals(info,stored);
        
        Path file = handler.get(info.id, currentUser);
        assertEquals(10,Files.size(file));
        
    }
    
    @Test
    public void uploadsFileFailsForAnonymouse() throws Exception {

        MockMultipartFile upload = new MockMultipartFile("file", "original", "text", new byte[10]);
        
        UserAccount user = fixtures.anonymous;
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(serviceRoot+"/one")
                .file(upload)
                .accept(APPLICATION_JSON_UTF8)
                .with(authenticate(user));

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                //.andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();
        assertNotNull(resp);
    
    }
    
    @Test
    public void getInfoGivesUplaodDetails() throws Exception {

        BioDare2User user = fixtures.user1;
        MockMultipartFile upload = new MockMultipartFile("file", "original", "text", new byte[10]);

        UploadFileInfo uploaded = handler.save(upload, user);
        
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(serviceRoot+"/"+uploaded.id)
                .accept(APPLICATION_JSON_UTF8)
                .with(authentication(makeAuthentication(user)));

        MvcResult resp = mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith(APPLICATION_JSON_UTF8))
                .andReturn();

        assertNotNull(resp);
        //System.out.println("Upload JSON: "+resp.getResponse().getStatus()+"; "+ resp.getResponse().getErrorMessage()+"; "+resp.getResponse().getContentAsString());
        
        
        UploadFileInfo info = mapper.readValue(resp.getResponse().getContentAsString(), UploadFileInfo.class);
        assertNotNull(info);
        
        assertNotNull(info.id);
        assertEquals(info.contentType,"text");
        
        assertEquals(uploaded,info);
        
    }
    
    
}
