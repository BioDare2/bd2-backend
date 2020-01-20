/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.exp;

import ed.biodare2.backend.repo.isa_dom.GeneralDesc;
import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @author tzielins
 */
public class ExperimentGeneralDescView extends GeneralDesc {
    
    public LocalDate executionDate;
    
    public ExperimentGeneralDescView() {
        
    }
    
    public ExperimentGeneralDescView(GeneralDesc desc, LocalDate executionDate) {
        
        this.name = desc.name;
        this.purpose = desc.purpose;
        this.description = desc.description;
        this.comments = desc.comments;
        this.executionDate = executionDate;        
    }
    

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 79 * hash + Objects.hashCode(this.executionDate);
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
        final ExperimentGeneralDescView other = (ExperimentGeneralDescView) obj;
        if (!Objects.equals(this.executionDate, other.executionDate)) {
            return false;
        }
        return super.equals(other);
    }
    
    
}
