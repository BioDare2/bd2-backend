/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.error;

/**
 *
 * @author tzielins
 */
public class SystemError extends Exception {
 
    public SystemError() {
        super();
    }
    
    public SystemError(String msg) {
        super(msg);
    }
    
    public SystemError(Throwable exception) {
        super(exception);
    }
    
    public SystemError(String msg,Throwable exception) {
        super(msg,exception);
    }
}
