/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.jobcentre2;


/**
 *
 * @author tzielins
 */
public class JC2HandlingException extends Exception {
    
    public JC2HandlingException(String msg) {
        super(msg);
    }
    
    public JC2HandlingException(String msg,Throwable e) {
        super(msg,e);
    }
    
}
