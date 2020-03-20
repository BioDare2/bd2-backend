/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.state;

import java.io.Serializable;

/**
 *
 * @author tzielins
 */
public interface Stateable extends Serializable {
    
    public State getState();
}
