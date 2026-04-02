/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author tzielins
 */
public class UploadFileInfoTest {
    
    public UploadFileInfoTest() {
    }
    
    @Test
    public void serializesToJSONAndBack() throws JacksonException {

        UploadFileInfo org = new UploadFileInfo();
        org.id = "test";
        org.tmpFileName ="~tmp";
        org.originalFileName = "cos.xml";
        org.contentType = "text/html";
        org.uploadedBy = "zielu";
        org.uploadedOn = LocalDateTime.now();

	ObjectMapper mapper = JsonMapper
	    .builder()
	    .build();

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
