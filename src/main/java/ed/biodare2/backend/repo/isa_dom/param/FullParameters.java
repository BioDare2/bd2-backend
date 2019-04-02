/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.param;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author tzielins
 */
public class FullParameters implements Serializable {
    
    private static final long serialVersionUID = 3L;    
    
    @JsonIgnore
    public Map<String,Parameter> parameters = new HashMap<>();
    
   
    public FullParameters() {
        
    }
    
    @JsonCreator
    protected FullParameters(List<Parameter> params) {
        this();
        params.forEach(this::set);
    }
    

    
    @JsonValue
    protected Collection<Parameter> json() {
        return parameters.values();
    }
    
    /*@JsonAnyGetter
    public Map<String,Parameter> getParameters() {
        return parameters;
    }
    
    @JsonAnySetter
    public void set(String name,Parameter param) {
        if (!name.equals(param.name)) throw new IllegalArgumentException("Param key and name mismatch: "+name+"!="+param.name);
        set(param);
    }*/
    
    public void set(String param,String value) {
        set(new Parameter(param, value, null, null));
    }
    
    public void set(Parameter param) {
        parameters.put(param.name,param);
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
        final FullParameters other = (FullParameters) obj;
        if (!Objects.equals(this.parameters, other.parameters)) {
            return false;
        }
        return true;
    }
    
    
}
