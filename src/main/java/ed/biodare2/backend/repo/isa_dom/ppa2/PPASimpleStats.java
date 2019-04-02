/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa2;

import com.fasterxml.jackson.annotation.JsonProperty;
import ed.robust.dom.tsprocessing.PhaseType;
import java.util.EnumMap;
import java.util.Map;

/**
 *
 * @author tzielins
 */
public class PPASimpleStats {
    
    public long memberDataId;
    public long rawId;      
    public long bioId;
    public long envId;    
    public String label; 
    
    public long N;
    public double ERR;
    public double GOF;
    
    @JsonProperty("per")
    public double period; 
    @JsonProperty("perStd")
    public double periodStd;
    
    @JsonProperty("ph2Z")
    public ValuesByPhase<Double> phaseToZero = new ValuesByPhase<>();
    @JsonProperty("ph2W")
    public ValuesByPhase<Double> phaseToWindow = new ValuesByPhase<>();
    @JsonProperty("ph2ZCir")
    public ValuesByPhase<Double> phaseToZeroCirc = new ValuesByPhase<>();
    @JsonProperty("ph2WCir")
    public ValuesByPhase<Double> phaseToWindowCirc = new ValuesByPhase<>();
    
    //std are same regardless of data window
    @JsonProperty("phStd")
    public ValuesByPhase<Double> phaseStd = new ValuesByPhase<>();
    @JsonProperty("phStdCir")
    public ValuesByPhase<Double> phaseCircStd = new ValuesByPhase<>();
    
    @JsonProperty("amp")    
    public ValuesByPhase<Double> amplitude = new ValuesByPhase<>();
    @JsonProperty("ampStd")    
    public ValuesByPhase<Double> amplitudeStd = new ValuesByPhase<>();

}
