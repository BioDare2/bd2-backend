/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.assets;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.util.TreeMap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

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
    
    @BeforeEach
    public void setUp() {
	mapper = JsonMapper
	    .builder()
	    .enable(SerializationFeature.INDENT_OUTPUT)
	    .build();
        
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
    public void serializesToJSONAndBack() throws JacksonException {

        FileAssets org = assets;
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        System.out.println(json);
        
        FileAssets cpy = mapper.readValue(json, FileAssets.class);        
        assertEquals(org,cpy);
        
        assertTrue(cpy.assets instanceof TreeMap);
    }
}
