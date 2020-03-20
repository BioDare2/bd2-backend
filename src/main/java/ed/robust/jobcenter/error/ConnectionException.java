/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.error;

/**
 *
 * @author tzielins
 */
public class ConnectionException extends Exception {
    
    public ConnectionException(String msg) {
        super(msg);
    }
    
    public ConnectionException(String msg,Throwable e) {
        super(msg,e);
    }
    
}
