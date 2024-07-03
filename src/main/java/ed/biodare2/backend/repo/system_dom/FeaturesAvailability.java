/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.system_dom;

import java.time.LocalDate;

/**
 *
 * @author tzielins
 */
public class FeaturesAvailability {
    
    public static int DEFAULT_EMBARGO = 3;
    
    public ServiceLevel serviceLevel = ServiceLevel.BASIC;
    
    public int modifications = 0;
    
    // it is initialised so it will work with the old jscon records
    public LocalDate embargoDate;
}
