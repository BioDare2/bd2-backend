/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.system_dom;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

/**
 *
 * @author tzielins
 */
public class SystemInfoTest {

    
    public SystemInfoTest() {
    }

    ObjectMapper mapper;
    
    @BeforeEach
    public void setUp() {
	mapper = JsonMapper
	    .builder()
	    .enable(SerializationFeature.INDENT_OUTPUT)
	    .build();
    }
    
    @Test
    public void serializesToJSONAndBack() throws JacksonException {

        SystemInfo org = SystemDomTestBuilder.makeSystemInfo();
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        System.out.println(json);
        
        SystemInfo cpy = mapper.readValue(json, SystemInfo.class);  
        
        checkSame(org,cpy);
    }

    public static void checkSame(SystemInfo org, SystemInfo cpy) {
        
        assertEquals(org.parentId, cpy.parentId);
        assertEquals(org.entityType, cpy.entityType);
        checkSame(org.security,cpy.security);
        checkSame(org.provenance, cpy.provenance);
        assertEquals(org.currentDescVersion, cpy.currentDescVersion);
        assertEquals(org.currentDataVersion, cpy.currentDataVersion);
        assertEquals(org.experimentCharacteristic, cpy.experimentCharacteristic);
        checkSame(org.versionsInfo, cpy.versionsInfo);
    }
    
    protected static void checkSame(ACLInfo org, ACLInfo cpy) {
        assertEquals(org.owner, cpy.owner);
        assertEquals(org.superOwner, cpy.superOwner);
        assertEquals(org.allowedToRead, cpy.allowedToRead);
        assertEquals(org.allowedToWrite, cpy.allowedToWrite);
    }
    
    protected static void checkSame(Provenance org, Provenance cpy) {
        assertEquals(org.creation,cpy.creation);
        assertEquals(org.lastChange,cpy.lastChange);
        assertEquals(org.changes,cpy.changes);
    }  
    
    protected static void checkSame(VersionsInfo org, VersionsInfo cpy) {
        assertEquals(org.versions,cpy.versions);
    }    
}
