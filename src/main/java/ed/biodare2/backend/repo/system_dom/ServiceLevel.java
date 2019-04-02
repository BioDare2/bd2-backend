/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.system_dom;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 *
 * @author tzielins
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ServiceLevel {
    
    BASIC,
    FULL_GRATIS,
    FULL_PURCHASED,
    FULL_SUBSCRIBED,
    FULL_FOR_OPEN;
}
