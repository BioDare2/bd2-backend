/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.biodesc;

import ed.biodare2.backend.repo.isa_dom.shared.ExternalResource;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author tzielins
 */
public class BiologySummary implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    public Set<DataCategory> dataCategories = new HashSet<>();    
    public Set<String> labels = new HashSet<>();
    public Set<String> species = new HashSet<>();
    public Set<String> ecotypes = new HashSet<>();
    public Set<String> genotypes = new HashSet<>();
    public Set<String> markers = new HashSet<>();
    public Set<String> lines = new HashSet<>();
    public Set<String> growthStages = new HashSet<>();
    public Set<String> tagTypes = new HashSet<>();    
    public Set<String> materialTypes = new HashSet<>();
    public Set<String> materialOrigins = new HashSet<>();
    public Set<String> customTags = new HashSet<>();
    int dataSetSize;
    int avgReplicates;
    public Set<ExternalResource> externalIds = new HashSet<>();
    
    public BiologySummary() {};
    
    public BiologySummary(BiologicalDescription desc) {
        super();
        
        for (BiologicalInfo info:desc.bios) {
            dataCategories.add(info.dataCategory);
            labels.add(info.label);
            species.add(info.species);
            ecotypes.add(info.ecotype);
            genotypes.add(info.genotype);
            markers.add(info.marker);
            lines.add(info.line);
            growthStages.add(info.growthStage);
            tagTypes.add(info.tagType);
            materialTypes.add(info.materialType);
            materialOrigins.add(info.materialOrigin);
            customTags.addAll(info.customTags);
            externalIds.addAll(info.externalIds.values()
                                    .stream().
                                    flatMap( c -> c.stream())
                                    .collect(Collectors.toSet()));
        }
    }
}
