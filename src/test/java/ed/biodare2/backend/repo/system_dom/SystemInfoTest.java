/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.system_dom;

import ed.biodare2.backend.repo.system_dom.Provenance;
import ed.biodare2.backend.repo.system_dom.SystemInfo;
import ed.biodare2.backend.repo.system_dom.VersionsInfo;
import ed.biodare2.backend.repo.system_dom.ACLInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tzielins
 */
public class SystemInfoTest {

    
    public SystemInfoTest() {
    }

    ObjectMapper mapper;
    
    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    @After
    public void tearDown() {
    }    
    
    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

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
