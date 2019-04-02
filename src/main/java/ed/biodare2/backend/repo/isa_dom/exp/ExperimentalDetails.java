/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.exp;

import ed.biodare2.backend.repo.isa_dom.measure.MeasurementDesc;
import ed.biodare2.backend.repo.isa_dom.conditions.Environments;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * @author Zielu
 */
public class ExperimentalDetails implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    

    public MeasurementDesc measurementDesc = new MeasurementDesc();
    public Environments growthEnvironments = new Environments();
    public Environments experimentalEnvironments = new Environments();
    
    public LocalDate executionDate;

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.measurementDesc);
        hash = 13 * hash + Objects.hashCode(this.executionDate);
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
        final ExperimentalDetails other = (ExperimentalDetails) obj;
        if (!Objects.equals(this.measurementDesc, other.measurementDesc)) {
            return false;
        }
        if (!Objects.equals(this.growthEnvironments, other.growthEnvironments)) {
            return false;
        }
        if (!Objects.equals(this.experimentalEnvironments, other.experimentalEnvironments)) {
            return false;
        }
        if (!Objects.equals(this.executionDate, other.executionDate)) {
            return false;
        }
        return true;
    }


}
