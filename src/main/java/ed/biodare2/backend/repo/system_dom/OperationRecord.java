/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.system_dom;

import ed.biodare2.backend.repo.isa_dom.param.SimpleParameters;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author tzielins
 */
public class OperationRecord implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    @NotNull
    @NotBlank    
    public OperationType operation;
    
    @NotNull
    @NotBlank    
    public LocalDateTime dateTime;
    
    @NotNull
    @NotBlank    
    public String actorLogin;
    
    public String actorName;
    
    public String versionId;
    
    public SimpleParameters parameters = new SimpleParameters();

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.operation);
        hash = 79 * hash + Objects.hashCode(this.dateTime);
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
        final OperationRecord other = (OperationRecord) obj;
        if (this.operation != other.operation) {
            return false;
        }
        if (!Objects.equals(this.dateTime, other.dateTime)) {
            return false;
        }
        if (!Objects.equals(this.actorLogin, other.actorLogin)) {
            return false;
        }
        if (!Objects.equals(this.actorName, other.actorName)) {
            return false;
        }
        if (!Objects.equals(this.versionId, other.versionId)) {
            return false;
        }
        if (!Objects.equals(this.parameters, other.parameters)) {
            return false;
        }
        return true;
    }
    
    
    
}
