/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

/**
 *
 * @author tzielins
 */
public class ArgumentException extends Exception {
    
    public ArgumentException(String msg) {
        super(msg);
    }     
    
    public ArgumentException(String msg,Throwable e) {
        super(msg,e);
    }        
}
