/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.system_dom;

import ed.biodare2.backend.repo.isa_dom.exp.ExperimentCharacteristic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ed.biodare2.backend.repo.isa_dom.openaccess.OpenAccessInfo;
import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author tzielins
 */
public class SystemInfo implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    @Version
    protected long recVersion; 
    
    @NotNull
    public long parentId;
    
    @NotNull
    public EntityType entityType;
    
    @NotNull
    public ACLInfo security = new ACLInfo();
    
    @NotNull   
    public Provenance provenance = new Provenance();
    
    @NotNull   
    public long currentDescVersion;
    
    @NotNull   
    public long currentDataVersion;
    
    @NotNull   
    public long currentBioVersion;    
    
    public ExperimentCharacteristic experimentCharacteristic;
    
    public FeaturesAvailability featuresAvailability;
    
    public OpenAccessInfo openAccessInfo;
    
    @NotNull   
    public VersionsInfo versionsInfo = new VersionsInfo();

    @JsonIgnore    
    public String getVersionId() {
        return currentDataVersion+"."+currentBioVersion+"."+currentDescVersion;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (int) (this.parentId ^ (this.parentId >>> 32));
        hash = 19 * hash + Objects.hashCode(this.entityType);
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
        final SystemInfo other = (SystemInfo) obj;
        if (this.parentId != other.parentId) {
            return false;
        }
        if (this.entityType != other.entityType) {
            return false;
        }
        return true;
    }
    
   
    
}
