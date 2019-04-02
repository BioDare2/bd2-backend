/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class UploadFileInfoTest {
    
    public UploadFileInfoTest() {
    }

    
    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        UploadFileInfo org = new UploadFileInfo();
        org.id = "test";
        org.tmpFileName ="~tmp";
        org.originalFileName = "cos.xml";
        org.contentType = "text/html";
        org.uploadedBy = "zielu";
        org.uploadedOn = LocalDateTime.now();
        
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        //System.out.println(json);
        
        UploadFileInfo cpy = mapper.readValue(json, UploadFileInfo.class); 
        assertEquals(org.id ,cpy.id);
        assertEquals(org.tmpFileName ,cpy.tmpFileName);
        assertEquals(org.originalFileName ,cpy.originalFileName);
        assertEquals(org.contentType ,cpy.contentType);        
        assertEquals(org.uploadedBy ,cpy.uploadedBy);        
        assertEquals(org.uploadedOn ,cpy.uploadedOn);        
        assertEquals(org,cpy);
        
    }    
    
    
}
