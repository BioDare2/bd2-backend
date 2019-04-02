/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 *
 * @author tzielins
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum TimeType {
  NONE,
  TIME_IN_HOURS,
  TIME_IN_MINUTES,
  TIME_IN_SECONDS,  
  IMG_NUMBER;
    
/*    @JsonCreator
    public static TimeType forValue(String name) {
        return TimeType.valueOf(name);
    } */
}
