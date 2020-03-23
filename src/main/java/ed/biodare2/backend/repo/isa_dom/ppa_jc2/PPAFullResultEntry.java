/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa_jc2;

import com.fasterxml.jackson.annotation.JsonProperty;
import ed.robust.dom.tsprocessing.PPAResult;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author tzielins
 */
public class PPAFullResultEntry {
    
    public UUID jobId;
    public long dataId;
    
    public String dataType;
    
    public String orgId;
    @JsonProperty("rawId")
    public long rawDataId;    
    @JsonProperty("biolId")
    public long biolDescId;
    @JsonProperty("envId")
    public long environmentId;
    
    public boolean ignored;
    
    public PPAResult result;    

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.jobId);
        hash = 41 * hash + (int) (this.dataId ^ (this.dataId >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PPAFullResultEntry other = (PPAFullResultEntry) obj;
        if (this.dataId != other.dataId) {
            return false;
        }
        if (this.rawDataId != other.rawDataId) {
            return false;
        }
        if (this.biolDescId != other.biolDescId) {
            return false;
        }
        if (this.environmentId != other.environmentId) {
            return false;
        }
        if (this.ignored != other.ignored) {
            return false;
        }
        if (!Objects.equals(this.dataType, other.dataType)) {
            return false;
        }
        if (!Objects.equals(this.orgId, other.orgId)) {
            return false;
        }
        if (!Objects.equals(this.jobId, other.jobId)) {
            return false;
        }
        if (!Objects.equals(this.result, other.result)) {
            return false;
        }
        return true;
    }
    
    
    
}
