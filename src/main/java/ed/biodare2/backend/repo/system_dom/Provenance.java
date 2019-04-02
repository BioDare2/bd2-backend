/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.system_dom;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tzielins
 */
public class Provenance implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    public OperationRecord creation;
    public OperationRecord lastChange;
    
    public List<OperationRecord> changes = new ArrayList<>();
        
}
