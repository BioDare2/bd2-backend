/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.exp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ed.biodare2.backend.repo.isa_dom.GeneralDesc;
import ed.biodare2.backend.repo.isa_dom.biodesc.BiologicalDescription;
import ed.biodare2.backend.repo.isa_dom.biodesc.BiologySummary;
import ed.biodare2.backend.repo.isa_dom.biodesc.DataCategory;
import ed.biodare2.backend.repo.isa_dom.contribution.ContributionDesc;
import ed.biodare2.backend.repo.isa_dom.shared.SimpleProvenance;
import java.io.Serializable;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

/**
 *
 * @author tzielins
 */
public class ExperimentalAssay implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    @Id
    private Long id;
    @Version
    protected long recVersion;    
    
    public String versionId;
    
    public GeneralDesc generalDesc = new GeneralDesc();
    public ContributionDesc contributionDesc = new ContributionDesc();    
    public ExperimentalDetails experimentalDetails = new ExperimentalDetails();
    public ExperimentCharacteristic characteristic = new ExperimentCharacteristic(); 
    public BiologicalDescription bioDescription = new BiologicalDescription();
    public BiologySummary bioSummary = new BiologySummary();
    
    public String species;
    public DataCategory dataCategory;
    
    public SimpleProvenance provenance = new SimpleProvenance();
    
    String termsVersions;
    
    @JsonCreator
    public ExperimentalAssay(@JsonProperty("id") long id) {
        this.id = id;
    };
    
    public long getId() {
        return this.id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
        
    @JsonIgnore
    public String getName() {
        return generalDesc.name;
    }

    @Override
    public String toString() {
        return "EAssay[" + id + ']';
    }
    
    
    
}
