/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.biodesc;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author tzielins
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum DataCategory {
 
    //order mathers!!!! IN DB and JS side!!!!
    NONE("NONE","None",true), //NONE is here only to assure 1-based of numbering for the real values
    EXPR_REPORTER("EXPR_REP","Expression reporter (e.g. Luc-imaging)"),
    SIGNALLING_REPORTER("SIG_REP","Signalling reporter (e.g. Aequorin)"),
    DELAYED_FLUORESCENCE("DF","Delayed fluorescence"),
    LEAF_MOVEMENT("LEAF_MV","Leaf movement"),
    GEN_IMAGING("IMG","Other imaging"),
    PROTEIN("PROT","Protein assay (e.g. Western blot)"),
    TRANSCRIPT("RNA","RNA assay (e.g. qRT-PCR)"),
    METABOLITE("MET","Metabolite assay (e.g. LC-MS)"),
    BEHAVIOUR("BEHAVE","Behaviour (e.g. wheel running)"),
    PHYSIOLOGY("PHYS","Physiology (e.g. biomass)"),
    DEVELOPMENT("DEVEL","Development"),
    OTHER("OTHER","OTHER"),
    UNKNOWN("UNKNOWN","Unknown",true),
    PCR("PCR","qRT-PCR",true),
    TRANSC_FUSION("TSCR_FS","Transcriptional fusion",true),
    TRANSL_FUSION("TSLT_FS","Translational fusion",true),
    ;
    

    DataCategory(String shortName,String longName) {
        this(shortName,longName,false);
    }
    
    DataCategory(String shortName,String longName,boolean disabled) {
        this.shortName = shortName;
        this.longName = longName;
        this.disabled = disabled;
    }
    
    
    private final String shortName;
    public final String longName;
    public final boolean disabled;

    /*
    private static final Map<DataCategory,String> longNames = initLongNames();
    
    static Map<DataCategory,String> initLongNames() {
        Map<DataCategory,String> map = new EnumMap<>(DataCategory.class);
        for (DataCategory type : values())
                map.put(type,type.longName);
        return Collections.unmodifiableMap(map);        
    }
    
    public static Map<DataCategory,String> getLongNames() {
        return longNames;        
    }*/

}
