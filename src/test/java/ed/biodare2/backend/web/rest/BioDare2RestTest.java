/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.backend.web.rest.BioDare2Rest;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *
 * @author tzielins
 */
public class BioDare2RestTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    static class BioDare2RestImpl extends BioDare2Rest {
        
    }
    
    BioDare2Rest instance;
    
    public BioDare2RestTest() {
    }
    
    @Before
    public void init() {
        instance = new BioDare2RestImpl();
    }

    @Test
    public void sendFilesSetsCorrectHeaders() throws Exception {
        
        Path file = testFolder.newFile().toPath();
        
        String contentType = "txt";
        String fileName = "a.file.txt";
        
        Files.write(file, Arrays.asList("A file content"));
        
        MockHttpServletResponse resp = new MockHttpServletResponse();
        resp.setOutputStreamAccessAllowed(true);
        
        instance.sendFile(file, fileName, contentType, false, resp);
        
        assertEquals(Files.size(file),resp.getContentLengthLong());
        assertEquals(contentType,resp.getContentType());
        
        String disp = "attachment; filename=\"a.file.txt\"";
        assertEquals(disp,resp.getHeader(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals("A file content",resp.getContentAsString().trim());
    }
    
}
