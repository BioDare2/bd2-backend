/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author tzielins
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class CellRangeDescription implements Serializable {
   
    static final long serialVersionUID = 11L;
  
public CellRange range;
public CellRole role;
public RangeDetails details;

//ingored used only on JS site
private String content;    

/*
@JsonProperty("details")
public void setValue(JsonNode jsonNode) {
        
    
    if (jsonNode.isTextual()) {
        this.value = jsonNode.textValue();
    } else {
        this.value = jsonNode.toString();
    }
}*/

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.range);
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
        final CellRangeDescription other = (CellRangeDescription) obj;
        if (!Objects.equals(this.range, other.range)) {
            return false;
        }
        if (this.role != other.role) {
            return false;
        }
        if (!Objects.equals(this.details, other.details)) {
            return false;
        }
        if (!Objects.equals(this.content, other.content)) {
            return false;
        }
        return true;
    }



}
