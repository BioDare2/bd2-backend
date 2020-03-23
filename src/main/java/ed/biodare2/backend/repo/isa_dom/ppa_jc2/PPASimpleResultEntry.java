/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa_jc2;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 *
 * @author tzielins
 */
public class PPASimpleResultEntry {
    
    
    public UUID jobId;

    public long dataId;
    public long rawId;      
    public long bioId;
    public long envId; 
    @JsonProperty("dType")
    public String dataType;    
    public String orgId;
    public String dataRef;
    public String label; 

    @JsonProperty("summary")
    public String jobSummary;
    public String message;
    
    public boolean ignored;
    public boolean circadian;
    public boolean attention;
    public boolean failed;
    
            
    public double ERR;
    public double GOF;
    
    @JsonProperty("per")
    public double period;
    @JsonProperty("perE")
    public double periodErr;
    
    @JsonProperty("ph2Z")
    public ValuesByPhase<Double> phaseToZero = new ValuesByPhase<>();
    @JsonProperty("ph2W")
    public ValuesByPhase<Double> phaseToWindow = new ValuesByPhase<>();
    @JsonProperty("ph2ZCir")
    public ValuesByPhase<Double> phaseToZeroCirc  = new ValuesByPhase<>();
    @JsonProperty("ph2WCir")
    public ValuesByPhase<Double> phaseToWindowCirc = new ValuesByPhase<>();
    
    @JsonProperty("phE")
    public double phaseErr;
    @JsonProperty("phECir")
    public double phaseCircErr;
    
    @JsonProperty("amp")    
    public ValuesByPhase<Double> amplitude  = new ValuesByPhase<>();
    @JsonProperty("ampE")
    public double amplitudeErr;
    
}
