/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.contribution;

import ed.biodare2.backend.repo.isa_dom.actors.Institution;
import java.io.Serializable;
import java.util.Objects;
import jakarta.validation.constraints.NotNull;

/**
 *
 * @author tzielins
 */
public class Funding implements Serializable {
    
    private static final long serialVersionUID = 3L;
    
    @NotNull
    public Institution institution;
    public String grantNr;   

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.institution);
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
        final Funding other = (Funding) obj;
        if (!Objects.equals(this.institution, other.institution)) {
            return false;
        }
        if (!Objects.equals(this.grantNr, other.grantNr)) {
            return false;
        }
        return true;
    }
    
    
}
