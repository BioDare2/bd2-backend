/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

/**
 *
 * @author tzielins
 */
public class RhythmicityHandlingException extends Exception {
    
    public RhythmicityHandlingException(String msg) {
        super(msg);
    }
    
    public RhythmicityHandlingException(String msg,Throwable e) {
        super(msg,e);
    }
    
}
