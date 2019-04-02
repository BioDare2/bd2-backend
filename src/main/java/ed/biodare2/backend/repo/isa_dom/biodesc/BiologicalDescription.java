/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.biodesc;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author tzielins
 */
public class BiologicalDescription implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    public Set<BiologicalInfo> bios = new HashSet<>();
    
    public void add(BiologicalInfo bio) {
        bios.add(bio);
    }
    
}
