/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.assets;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;
import jakarta.persistence.Version;

/**
 *
 * @author tzielins
 */
public class FileAssets implements Serializable {
    
    private static final long serialVersionUID = 3L; 
    
    @Version
    protected long recVersion;    
    
    //@JsonIgnore
    Map<Long,FileAsset> assets = new TreeMap<>();
    
    //@JsonAnyGetter
    @JsonGetter
    protected Map<Long,FileAsset> getAssets() {
        return assets;
    }
    
    @JsonSetter
    protected void setAssets(Map<Long,FileAsset> values) {
        Map<Long,FileAsset> map = new TreeMap<>();
        map.putAll(values);
        this.assets = map;
        //return assets;
    }    
    
    //@JsonAnySetter
    public void set(FileAsset value) {
        assets.put(value.id, value);
    }    
    
    /*protected void set(long id,FileAsset value) {        
        value.id = id;
        set(value);
    }*/   
    
    
    @JsonIgnore
    public Optional<FileAsset> get(long id) {
       return Optional.ofNullable(assets.get(id));
    }
    
    public Optional<FileAsset> findByNameAndType(String name,AssetType type) {
       
        return assets.values().stream()
                .filter( asset -> asset.assetName.equals(name) && asset.assetType.equals(type))
                .findAny();
    }    
    
    public Stream<FileAsset> stream() {
        return assets.values().stream();
    }    
    

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.assets);
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
        final FileAssets other = (FileAssets) obj;
        if (!Objects.equals(this.assets, other.assets)) {
            return false;
        }
        return true;
    }


    
    
}
