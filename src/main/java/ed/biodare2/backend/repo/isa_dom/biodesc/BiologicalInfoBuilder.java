/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.biodesc;

import ed.biodare2.backend.repo.isa_dom.shared.ExternalResource;
import ed.biodare2.backend.repo.isa_dom.param.SimpleParameters;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author tzielins
 */
public class BiologicalInfoBuilder implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    public Long id;
    public String fullHash;
    public DataCategory dataCategory = DataCategory.NONE;    
    public String label = "";
    public String species = "";
    public String ecotype = "";
    public String genotype ="";
    public String marker ="";
    public String line ="";
    public String growthStage ="";
    public String tagType = "";    
    public String materialType = "";
    public String materialOrigin ="";
    public Set<String> customTags = new HashSet<>();
    public SimpleParameters parameters = new SimpleParameters();
    public Map<ExternalIdCathegory,Set<ExternalResource>> externalIds = new EnumMap(ExternalIdCathegory.class);
    
    public BiologicalInfo build() {
        
        BiologicalInfo info = new BiologicalInfo();
        
        //info.id = id;
        info.dataCategory=dataCategory;    
        info.label=label;
        info.species=species;
        info.ecotype=ecotype;
        info.genotype=genotype;
        info.marker=marker;
        info.line=line;
        info.growthStage=growthStage;
        info.tagType=tagType;    
        info.materialType=materialType;
        info.materialOrigin=materialOrigin;
        info.customTags = Collections.unmodifiableSet(new HashSet<>(customTags));
        info.parameters = parameters.unmodifiable();
        info.externalIds = Collections.unmodifiableMap(new EnumMap(externalIds));
        
        //info.fullHash = calculateHash();
        return info;
    }

    protected String calculateHash() {
        try {
        MessageDigest md = MessageDigest.getInstance("MD5");
        
        md.update(dataCategory.name().getBytes());    
        md.update(label.getBytes()); 
        md.update(species.getBytes());
        md.update(ecotype.getBytes());
        md.update(genotype.getBytes());
        md.update(marker.getBytes());
        md.update(line.getBytes()); 
        md.update(growthStage.getBytes());
        md.update(tagType.getBytes());   
        md.update(materialType.getBytes()); 
        md.update(materialOrigin.getBytes()); 
        customTags.forEach(s -> md.update(s.getBytes()));
        parameters.getParameters()
                .entrySet()
                .forEach(e -> {
                    md.update(e.getKey().getBytes());
                    md.update(e.getValue().getBytes());
                });
        
        externalIds.forEach( (k,set) ->{
            md.update(k.name().getBytes());
            set.forEach( external -> {
                md.update(external.externalId.getBytes());
                md.update(external.path.getBytes());
                md.update(external.service.getBytes());
            });
        });
        
        return new String(encodeHex(md.digest()));
        
        } catch (NoSuchAlgorithmException e){
            throw new RuntimeException(e);
        }
    }
    
    protected static final char[] HEX_CHARS =
                    {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        
    protected static char[] encodeHex(byte[] bytes) {
            char chars[] = new char[32];
            for (int i = 0; i < chars.length; i = i + 2) {
                    byte b = bytes[i / 2];
                    chars[i] = HEX_CHARS[(b >>> 0x4) & 0xf];
                    chars[i + 1] = HEX_CHARS[b & 0xf];
            }
            return chars;
    }    
}
