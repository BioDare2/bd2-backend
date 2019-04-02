/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author tzielins
 */
public class TimeColumnProperties extends RangeDetails implements Serializable {
   
    static final long serialVersionUID = 11L;
  
  public TimeType timeType;
  public double timeOffset;
  public double imgInterval;
  public int firstRow;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.timeType);
        hash = 59 * hash + this.firstRow;
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
        final TimeColumnProperties other = (TimeColumnProperties) obj;
        if (this.timeType != other.timeType) {
            return false;
        }
        if (Double.doubleToLongBits(this.timeOffset) != Double.doubleToLongBits(other.timeOffset)) {
            return false;
        }
        if (Double.doubleToLongBits(this.imgInterval) != Double.doubleToLongBits(other.imgInterval)) {
            return false;
        }
        if (this.firstRow != other.firstRow) {
            return false;
        }
        return true;
    }

   
  
}
