/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.ui_dom.exp;

import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalDetails;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentCharacteristic;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.GeneralDesc;
import ed.biodare2.backend.repo.isa_dom.biodesc.BiologicalDescription;
import ed.biodare2.backend.repo.isa_dom.biodesc.BiologySummary;
import ed.biodare2.backend.repo.isa_dom.biodesc.DataCategory;
import ed.biodare2.backend.repo.isa_dom.contribution.ContributionDesc;
import ed.biodare2.backend.repo.isa_dom.shared.SimpleProvenance;
import ed.biodare2.backend.repo.ui_dom.security.SecuritySummary;
import java.io.Serializable;

/**
 *
 * @author tzielins
 */
public class ExperimentalAssayView implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    public long id;
    
    public GeneralDesc generalDesc;
    public ContributionDesc contributionDesc;    
    public ExperimentalDetails experimentalDetails;
    public ExperimentCharacteristic features;
    //public BiologicalDescription bioDescription;
    //public BiologySummary bioSummary;
    
    public SimpleProvenance provenance;
    public SecuritySummary security;
    
    public String species;
    public DataCategory dataCategory;
    
    
    public ExperimentalAssayView() {
    };
    

    public ExperimentalAssayView(ExperimentalAssay assay) {
        this.id = assay.getId();
        this.generalDesc = assay.generalDesc;
        this.contributionDesc = assay.contributionDesc;
        this.experimentalDetails = assay.experimentalDetails;
        this.features = assay.characteristic;
        //this.bioDescription = assay.bioDescription;
        //this.bioSummary = assay.bioSummary;
        this.species = assay.species;
        this.dataCategory = assay.dataCategory;
        this.provenance = assay.provenance;
    }
        
    
}
