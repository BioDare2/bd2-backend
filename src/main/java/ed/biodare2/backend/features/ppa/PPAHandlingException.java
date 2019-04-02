/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

/**
 *
 * @author tzielins
 */
public class PPAHandlingException extends Exception {
    
    public PPAHandlingException(String msg) {
        super(msg);
    }
    
    public PPAHandlingException(String msg,Throwable e) {
        super(msg,e);
    }
    
}
