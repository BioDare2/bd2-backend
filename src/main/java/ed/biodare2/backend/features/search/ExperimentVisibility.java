/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.search;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author tzielins
 */
public class ExperimentVisibility implements Serializable {
    
    public Optional<String> user = Optional.empty();
    public boolean showPublic = false;

    public ExperimentVisibility() {
        this(Optional.empty(), false);
    }
    
    public ExperimentVisibility(String user) {
        this(user, false);
    }
    
    public ExperimentVisibility(String user, boolean showPublic) {
        this(Optional.of(user), showPublic);
    }
    
    public ExperimentVisibility(Optional<String> user, boolean showPublic) {
        this.user = user;
        this.showPublic = showPublic;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.user);
        hash = 47 * hash + (this.showPublic ? 1 : 0);
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
        final ExperimentVisibility other = (ExperimentVisibility) obj;
        if (this.showPublic != other.showPublic) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "VIS:"+user.orElse("-")+(showPublic ? "+PUB" : "");
    }
    
    
    
}
