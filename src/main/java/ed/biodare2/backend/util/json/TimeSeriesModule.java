/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.json;

import org.springframework.boot.jackson.JsonComponent;


/**
 * It has to be here explicitly as otherwise it is not being picked up by jackson
 * @author Zielu
 */
@JsonComponent
public class TimeSeriesModule extends ed.biodare.data.json.TimeSeriesModule {
    
}
