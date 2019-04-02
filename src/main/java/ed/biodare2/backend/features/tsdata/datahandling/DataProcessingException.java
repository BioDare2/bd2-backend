/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.datahandling;

/**
 *
 * @author tzielins
 */
public class DataProcessingException extends Exception {
    
    public DataProcessingException(String msg) {
        super(msg);
    }
    
    public DataProcessingException(String msg,Throwable e) {
        super(msg,e);
    }
    
}
