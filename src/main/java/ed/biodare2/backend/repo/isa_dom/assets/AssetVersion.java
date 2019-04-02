/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.assets;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @author tzielins
 */
public class AssetVersion implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    public long versionId;
    public String localFile;
    public String originalName;
    public String contentType;
    public String description;
    //public TSImportParameters importParams;
    public LocalDate created;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (int) (this.versionId ^ (this.versionId >>> 32));
        hash = 17 * hash + Objects.hashCode(this.localFile);
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
        final AssetVersion other = (AssetVersion) obj;
        if (this.versionId != other.versionId) {
            return false;
        }
        if (!Objects.equals(this.localFile, other.localFile)) {
            return false;
        }
        if (!Objects.equals(this.originalName, other.originalName)) {
            return false;
        }
        if (!Objects.equals(this.contentType, other.contentType)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.created, other.created)) {
            return false;
        }
        return true;
    }
    
    
    
}
