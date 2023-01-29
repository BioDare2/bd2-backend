/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.measure;

import ed.biodare2.backend.repo.isa_dom.param.FullParameters;
import java.util.Objects;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

/**
 *
 * @author Zielu
 */
public class MeasurementDesc {
   
    @NotNull
    @NotBlank
    public String technique;
    public String equipment;
    public String description;
    public FullParameters parameters = new FullParameters(); 

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.technique);
        hash = 59 * hash + Objects.hashCode(this.equipment);
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
        final MeasurementDesc other = (MeasurementDesc) obj;
        if (!Objects.equals(this.technique, other.technique)) {
            return false;
        }
        if (!Objects.equals(this.equipment, other.equipment)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.parameters, other.parameters)) {
            return false;
        }
        return true;
    }
    
    
}
