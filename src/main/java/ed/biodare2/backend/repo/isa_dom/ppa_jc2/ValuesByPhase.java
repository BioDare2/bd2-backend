/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa_jc2;

import com.fasterxml.jackson.annotation.JsonProperty;
import ed.robust.dom.tsprocessing.PhaseType;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * @author tzielins
 */
class ValuesByPhase<T> {
  
  @JsonProperty("m")  
  T ByMethod;// = Double.NaN;
  @JsonProperty("f")  
  T ByFit;// = Double.NaN;
  @JsonProperty("p")  
  T ByFirstPeak;// = Double.NaN;
  @JsonProperty("a")  
  T ByAvgMax;// = Double.NaN;
  
  public void put(PhaseType key,T value) {
      switch (key) {
          case ByMethod: this.ByMethod = value; break;
          case ByFit: this.ByFit = value; break;
          case ByFirstPeak: this.ByFirstPeak = value; break;
          case ByAvgMax: this.ByAvgMax = value; break;
          default:
              throw new IllegalArgumentException("Unsuported phases type: "+key);
      }
  }
  
  public T get(PhaseType key) {
      switch (key) {
          case ByMethod: return this.ByMethod;
          case ByFit: return this.ByFit;
          case ByFirstPeak: return this.ByFirstPeak;
          case ByAvgMax: return this.ByAvgMax;
          default:
              throw new IllegalArgumentException("Unsuported phases type: "+key);
      }
  }
  
  public boolean areAllSet() {
      if (ByMethod == null) return false;
      if (ByFit == null) return false;
      if (ByFirstPeak == null) return false;
      if (ByAvgMax == null) return false;
      return true;
  }
  
  public void forEach(Consumer<T> cons) {
      cons.accept(ByMethod);
      cons.accept(ByFit);
      cons.accept(ByFirstPeak);
      cons.accept(ByAvgMax);
  }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.ByMethod);
        hash = 37 * hash + Objects.hashCode(this.ByFit);
        hash = 37 * hash + Objects.hashCode(this.ByFirstPeak);
        hash = 37 * hash + Objects.hashCode(this.ByAvgMax);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValuesByPhase<?> other = (ValuesByPhase<?>) obj;
        if (!Objects.equals(this.ByMethod, other.ByMethod)) {
            return false;
        }
        if (!Objects.equals(this.ByFit, other.ByFit)) {
            return false;
        }
        if (!Objects.equals(this.ByFirstPeak, other.ByFirstPeak)) {
            return false;
        }
        if (!Objects.equals(this.ByAvgMax, other.ByAvgMax)) {
            return false;
        }
        return true;
    }

  
  
}
