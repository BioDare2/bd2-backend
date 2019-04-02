/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.exp;

import ed.biodare2.backend.repo.isa_dom.GeneralDesc;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentCharacteristic;
import ed.biodare2.backend.repo.isa_dom.shared.SimpleProvenance;
import java.io.Serializable;

/**
 *
 * @author tzielins
 */
public class ExperimentSummary implements Serializable {
    
    private static final long serialVersionUID = 3L;     
    
    public long id;
    public GeneralDesc generalDesc;
    
    public SimpleProvenance provenance;
    public ExperimentCharacteristic features;

    public ExperimentSummary() {};
    
    public ExperimentSummary(ExperimentalAssay exp) {
        this.id = exp.getId();
        this.generalDesc = exp.generalDesc;
        this.provenance = exp.provenance;
        this.features = exp.characteristic;
    }
    
}
