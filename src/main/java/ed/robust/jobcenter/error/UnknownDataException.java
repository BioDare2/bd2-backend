/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.error;

/**
 *
 * @author tzielins
 */
public class UnknownDataException extends Exception {
    
    public UnknownDataException() {
        super();
    }
    
    public UnknownDataException(String msg) {
        super(msg);
    }
}
