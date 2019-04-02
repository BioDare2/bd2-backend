/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.assets;

import ed.biodare2.backend.repo.isa_dom.assets.FileAssets;
import ed.biodare2.backend.repo.isa_dom.assets.FileAsset;
import ed.biodare2.backend.repo.isa_dom.assets.AssetType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.util.TreeMap;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tzielins
 */
public class FileAssetsTest {
    
    public FileAssetsTest() {
    }
    
    ObjectMapper mapper;
    FileAssets assets;
    FileAsset f1;
    FileAsset f2;
    
    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        
        
        f1 = new FileAsset(1,"f1","f1", AssetType.DATA);
        f1.add("local","cos1", "txt");
        f2 = new FileAsset(2,"f2","f2", AssetType.TS_DATA);
        f2.add("local","cos2", "txt");
        f2.add("local","cos2","txt");
        f2.versions.get(0).description = "Some description";
        
        assets = new FileAssets();
        assets.set( f1);
        assets.set(f2);
        
    }    

    @Test
    public void serializesToJSONAndBack() throws JsonProcessingException, IOException {

        FileAssets org = assets;
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        System.out.println(json);
        
        FileAssets cpy = mapper.readValue(json, FileAssets.class);        
        assertEquals(org,cpy);
        
        assertTrue(cpy.assets instanceof TreeMap);
    }
    
}
