/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.ppa;

import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.error.RobustProcessException;

/**
 *
 * @author tzielins
 */
public interface PPAAnalyser {
    
    /**
     * Analyses data using specific period analysis method.
     * If the period is found, then results in the circadian range (defined by periodMin and periodMax) parameters
     * are marked as circadian. Thes circadian boundaries can also be used to limit the method scanning range (for example for periodograms).
     * <br/>
     * The output phases should follow the BioDare convention being [0,Period), and coincide with data peak interpretation.
     * <br/>
     * Implementation should return a fit based on its finding.
     * <p>
     * The recomended error hadnling is by returning the FailePPA instance if the computation could not be completed due to data problem (for example
     * too short data, or lack of convergance). RobustProcessExceptio is also ok, especially to more system caused errors.
     * <p>The implementations should be thread-safe. 
     * @param data data to be analysed
     * @param periodMin lower boundary for periods to be checked and considered circadian (inclusive)
     * @param periodMax higher boundary for periods to be checked and considered circadian (inclusive)
     * @return The results containing the best period and its fit, or FailedPPA
     * @throws InterruptedException - if computation was stop by interrupt signal
     * @throws RobustProcessException - if analysis could not be completed
     */
    public PPAResult analyse(TimeSeries data,double periodMin,double periodMax) throws InterruptedException, RobustProcessException;
    
}
