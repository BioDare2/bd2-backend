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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *
 * @author tzielins
 */
public class BioDare2RestTest {

    @TempDir
    Path testFolder;
    
    static class BioDare2RestImpl extends BioDare2Rest {
        
    }
    
    BioDare2Rest instance;
    
    public BioDare2RestTest() {
    }
    
    @BeforeEach
    public void init() {
        instance = new BioDare2RestImpl();
    }

    @Test
    public void sendFilesSetsCorrectHeaders() throws Exception {
        
        Path file = testFolder.resolve("test");
        
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
