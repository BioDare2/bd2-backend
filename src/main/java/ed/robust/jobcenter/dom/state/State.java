/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.state;

/**
 *
 * @author tzielins
 */
public enum State {
    
    SUCCESS,
    FAILED,
    ERROR,
    COMPLETED,
    SUBMITTED,
    PROCESSING,
    FINISHED,
    WAITING,
    TIMED_OUT,
    INTERRUPTED,
    DISPATCHED,
    REMOVED,
    ACKNOWLEDGED
}
