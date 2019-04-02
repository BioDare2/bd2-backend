/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.assets;

import ed.biodare2.backend.repo.isa_dom.assets.FileAsset;
import ed.biodare2.backend.repo.isa_dom.assets.AssetVersion;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tzielins
 */
public class FileAssetTest {
    
    public FileAssetTest() {
    }
    
    FileAsset asset;
    
    @Before() 
    public void setup() {
        asset = new FileAsset(1,"bla");
    }

    @Test
    public void simpleAddCopiesDescriptionFromExistingVersion() {

        asset.add("local","cos.xml","text.xml");
        assertNull(asset.getDescription());
        asset.versions.get(0).description = "HaHa";
        
        asset.add("local","cos2.xml","text.xml");
        assertEquals("HaHa",asset.getDescription());
    }
    
    @Test
    public void addVersionAdsToTheTop() {

        asset.add("local","cos.xml","text.xml");
        assertNull(asset.getDescription());
        
        AssetVersion ver = new AssetVersion();
        asset.add(ver);
        assertSame(ver, asset.versions.get(0));
        assertNotSame(ver,asset.versions.get(1));
        
        asset.add("local2","cos3","text.xml");
        assertEquals("local2",asset.versions.get(0).localFile);
    }    
    
    @Test
    public void addVersionSetsIdIfZero() {
        
        asset.add("local","cos.xml","text.xml");
        assertEquals(1,asset.last().versionId);
        
        AssetVersion ver = new AssetVersion();
        ver.versionId = 5;
        asset.add(ver);
        assertEquals(5,asset.last().versionId);
        
        ver = new AssetVersion();
        asset.add(ver);
        assertEquals(6,asset.last().versionId);
        
        asset.add("local","cos.xml","text.xml");
        assertEquals(7,asset.last().versionId);
        
        
        
    }
    
}
