/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.biodesc;

import ed.biodare2.backend.repo.isa_dom.shared.ExternalResource;
import ed.biodare2.backend.repo.isa_dom.param.SimpleParameters;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author tzielins
 */
public class BiologicalInfo implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    //Long id;
    //String fullHash;
    public DataCategory dataCategory;    
    public String label;
    public String species;
    public String ecotype;
    public String genotype;
    public String marker;
    public String line;
    public String growthStage;
    public String tagType;    
    public String materialType;
    public String materialOrigin;
    public Set<String> customTags = new HashSet<>();
    public SimpleParameters parameters = new SimpleParameters();
    public Map<ExternalIdCathegory,Set<ExternalResource>> externalIds = new EnumMap(ExternalIdCathegory.class);

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.dataCategory);
        hash = 13 * hash + Objects.hashCode(this.genotype);
        hash = 13 * hash + Objects.hashCode(this.marker);
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
        final BiologicalInfo other = (BiologicalInfo) obj;
        if (this.dataCategory != other.dataCategory) {
            return false;
        }
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        if (!Objects.equals(this.species, other.species)) {
            return false;
        }
        if (!Objects.equals(this.ecotype, other.ecotype)) {
            return false;
        }
        if (!Objects.equals(this.genotype, other.genotype)) {
            return false;
        }
        if (!Objects.equals(this.marker, other.marker)) {
            return false;
        }
        if (!Objects.equals(this.line, other.line)) {
            return false;
        }
        if (!Objects.equals(this.growthStage, other.growthStage)) {
            return false;
        }
        if (!Objects.equals(this.tagType, other.tagType)) {
            return false;
        }
        if (!Objects.equals(this.materialType, other.materialType)) {
            return false;
        }
        if (!Objects.equals(this.materialOrigin, other.materialOrigin)) {
            return false;
        }
        if (!Objects.equals(this.customTags, other.customTags)) {
            return false;
        }
        if (!Objects.equals(this.parameters, other.parameters)) {
            return false;
        }
        if (!Objects.equals(this.externalIds, other.externalIds)) {
            return false;
        }
        return true;
    }


    
    
}
