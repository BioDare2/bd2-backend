/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa2;

import com.fasterxml.jackson.annotation.JsonProperty;
import ed.robust.dom.tsprocessing.PhaseType;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author tzielins
 */
public class PPAResultsGroupSummary {
    
  public long memberDataId;
  public long rawId;      
  public long bioId;
  public long envId;
  public String label;

  public int failures;
  public int excluded;
  public List<Double> periods = new ArrayList<>();
  
  @JsonProperty("phases2Z")
  public ValuesByPhase<List<Double>> phasesToZero = new ValuesByPhase<>();
  @JsonProperty("phases2W")
  public ValuesByPhase<List<Double>> phasesToWindow = new ValuesByPhase<>();
  @JsonProperty("phases2ZCir")
  public ValuesByPhase<List<Double>> phasesToZeroCirc = new ValuesByPhase<>();
  @JsonProperty("phases2WCir")
  public ValuesByPhase<List<Double>> phasesToWindowCirc = new ValuesByPhase<>();
  @JsonProperty("amps")
  public ValuesByPhase<List<Double>> amplitudes = new ValuesByPhase<>();
}
