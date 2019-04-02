/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.system_dom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tzielins
 */

public class VersionsInfo implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    public List<VersionRecord> versions = new ArrayList<>();
    
    @JsonIgnore
    public VersionRecord getLast() {
        return versions.get(versions.size()-1);
    }
}
