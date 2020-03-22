/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

import ed.biodare2.backend.features.jobcentre2.JC2HandlingException;

/**
 *
 * @author tzielins
 */
public class RhythmicityHandlingException extends JC2HandlingException {

    public RhythmicityHandlingException(JC2HandlingException e) {
        super(e.getMessage(), e.getCause());
    }
    
    public RhythmicityHandlingException(String msg) {
        super(msg);
    }
    
    public RhythmicityHandlingException(String msg,Throwable e) {
        super(msg,e);
    }
    
}
