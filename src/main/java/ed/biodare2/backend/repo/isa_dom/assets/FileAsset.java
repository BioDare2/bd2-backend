/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.assets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author tzielins
 */
public class FileAsset implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    public long id;
    public String assetName;
    public String originalName;
    public AssetType assetType = AssetType.NONE;
    public List<AssetVersion> versions = new ArrayList<>();
    
    protected FileAsset() {        
    }
    
    public FileAsset(long id,String originalName) {
        this(id,originalName,originalName,AssetType.NONE);
    }
    
    public FileAsset(long id,String assetName,String originalName,AssetType assetType) {
        this.id = id;
        this.assetName = assetName;
        this.originalName = originalName;
        this.assetType = assetType;
    }    
    
    @JsonIgnore
    public String getLocalFile() {
        return last().localFile;
    }
    
    @JsonIgnore
    public String getContentType() {
        return last().contentType;
    }
    
    @JsonIgnore
    public String getDescription() {
        return last().description;
    }  
    
    @JsonIgnore
    public AssetVersion last() {
        return versions.get(0);
    }
    
    public void add(AssetVersion version) {
        if (version.versionId == 0)
            version.versionId = versions.isEmpty() ? 1 : last().versionId+1;
        
        versions.add(0,version);
    }
    
    public void add(String localFile,String originalName,String contentType) {
        AssetVersion version = new AssetVersion();
        version.originalName = originalName;//this.originalName;
        version.localFile = localFile;
        version.contentType = contentType;
        version.created = LocalDate.now();
        version.description = versions.isEmpty() ? null : last().description;
        this.add(version);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.originalName);
        hash = 97 * hash + Objects.hashCode(this.assetType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileAsset other = (FileAsset) obj;
        if (!Objects.equals(this.originalName, other.originalName)) {
            return false;
        }
        if (this.assetType != other.assetType) {
            return false;
        }
        if (!Objects.equals(this.versions, other.versions)) {
            return false;
        }
        return true;
    }
    
    
}
