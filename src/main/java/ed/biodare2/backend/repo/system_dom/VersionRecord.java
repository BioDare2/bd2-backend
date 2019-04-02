/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.system_dom;

import ed.biodare2.backend.repo.isa_dom.param.SimpleParameters;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 *
 * @author tzielins
 */
public class VersionRecord implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    @NotNull
    public long descVersion;
    
    @NotNull
    public long dataVersion;
    
    public SimpleParameters parameters = new SimpleParameters();
    
    public String descriptionCoordinates;
    public String dataCoordinates;
    public List<String> filesCoordinates = new ArrayList<>();

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (int) (this.descVersion ^ (this.descVersion >>> 32));
        hash = 23 * hash + (int) (this.dataVersion ^ (this.dataVersion >>> 32));
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
        final VersionRecord other = (VersionRecord) obj;
        if (this.descVersion != other.descVersion) {
            return false;
        }
        if (this.dataVersion != other.dataVersion) {
            return false;
        }
        if (!Objects.equals(this.parameters, other.parameters)) {
            return false;
        }
        if (!Objects.equals(this.descriptionCoordinates, other.descriptionCoordinates)) {
            return false;
        }
        if (!Objects.equals(this.dataCoordinates, other.dataCoordinates)) {
            return false;
        }
        if (!Objects.equals(this.filesCoordinates, other.filesCoordinates)) {
            return false;
        }
        return true;
    }
    

}
