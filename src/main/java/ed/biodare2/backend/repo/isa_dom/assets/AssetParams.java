/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.assets;

import java.io.Serializable;

/**
 *
 * @author tzielins
 */
public class AssetParams implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    public long assetId;
    public long assetVersion;
    public AssetType assetType;
    public String paramsClass;
    public String params;
    
    protected AssetParams() {        
    }
    
    public AssetParams(long assetId,long assetVersion) {
        this.assetId = assetId;
        this.assetVersion = assetVersion;
    }
    
}
