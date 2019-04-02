/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.param;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author tzielins
 */
public class SimpleParameters implements Serializable {
    
    private static final long serialVersionUID = 3L;    
    
    @JsonIgnore
    Map<String,String> parameters = new HashMap<>();
    
   
    @JsonAnyGetter
    public Map<String,String> getParameters() {
        return parameters;
    }
    
    @JsonAnySetter
    public void set(String name,String value) {
        parameters.put(name, value);
    }
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.parameters);
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
        final SimpleParameters other = (SimpleParameters) obj;
        if (!Objects.equals(this.parameters, other.parameters)) {
            return false;
        }
        return true;
    }

    public SimpleParameters unmodifiable() {
        SimpleParameters other = new SimpleParameters();
        other.parameters = Collections.unmodifiableMap(parameters);
        return other;
    }
    
    
}
