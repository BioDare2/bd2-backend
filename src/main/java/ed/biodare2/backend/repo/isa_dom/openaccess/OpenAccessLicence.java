/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.openaccess;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author tzielins
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum OpenAccessLicence {
    
    CC_BY("CC-BY v4.0","Creative Commons Attribution","http://creativecommons.org/licenses/by/4.0/legalcode","4.0");
    //OTHER("OTHER v4.0","Creative Commons Attribution","http://creativecommons.org/licenses/by/4.0/legalcode","3.0");
    
    public final String version;
    public final String shortName;
    public final String friendlyName;
    public final String licenceURL;

    private OpenAccessLicence(String shortName,String friendlyName, String licenceURL,String version) {
        this.shortName = shortName;
        this.friendlyName = friendlyName;
        this.licenceURL = licenceURL;
        this.version = version;
    }
    
    
    /*
    public static final Map<OpenAccessLicence,String> friendlyNames;
    
    static {
            Map<OpenAccessLicence,String> map = new EnumMap<>(OpenAccessLicence.class);
            for (OpenAccessLicence type : values())
                if (type.version != null && !type.version.isEmpty())
                    map.put(type,type.friendlyName+" v"+type.version);
                else map.put(type,type.friendlyName);
            friendlyNames = Collections.unmodifiableMap(map);
    }*/
    
}
