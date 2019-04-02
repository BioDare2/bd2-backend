/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Zielu
 */
public class Environments {
    
    public List<Environment> environments = new ArrayList<>();

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.environments);
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
        final Environments other = (Environments) obj;
        if (!Objects.equals(this.environments, other.environments)) {
            return false;
        }
        return true;
    }
 
    
}
