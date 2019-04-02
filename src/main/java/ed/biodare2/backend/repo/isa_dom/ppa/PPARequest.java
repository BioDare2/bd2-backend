/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ed.robust.dom.data.DetrendingType;
import ed.robust.ppa.PPAMethod;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;

/**
 *
 * @author tzielins
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class PPARequest implements Serializable {
 
    static final long serialVersionUID = 11L;
    
    @Min(0)
    public double windowStart;
    @Min(0)
    public double windowEnd;
    @Min(0)
    public double periodMin;
    @Min(0)
    public double periodMax;
    
    @NotNull
    public PPAMethod method;
    @NotNull
    public DetrendingType detrending; 
    
    @JsonIgnore
    public boolean isValid() {
        if (windowStart < 0 || windowEnd < 0 || periodMin <= 0 || periodMax <= 0) return false;
        if (periodMax < periodMin) return false;
        if (windowEnd > 0 && windowStart >= windowEnd) return false;
        if (method == null) return false;
        if (detrending == null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.windowStart) ^ (Double.doubleToLongBits(this.windowStart) >>> 32));
        hash = 97 * hash + Objects.hashCode(this.method);
        hash = 97 * hash + Objects.hashCode(this.detrending);
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
        final PPARequest other = (PPARequest) obj;
        if (Double.doubleToLongBits(this.windowStart) != Double.doubleToLongBits(other.windowStart)) {
            return false;
        }
        if (Double.doubleToLongBits(this.windowEnd) != Double.doubleToLongBits(other.windowEnd)) {
            return false;
        }
        if (Double.doubleToLongBits(this.periodMin) != Double.doubleToLongBits(other.periodMin)) {
            return false;
        }
        if (Double.doubleToLongBits(this.periodMax) != Double.doubleToLongBits(other.periodMax)) {
            return false;
        }
        if (this.method != other.method) {
            return false;
        }
        if (this.detrending != other.detrending) {
            return false;
        }
        return true;
    }
    
    
}
