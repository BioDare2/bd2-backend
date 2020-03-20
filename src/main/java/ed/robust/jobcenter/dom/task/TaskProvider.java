/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.task;

import ed.robust.jobcenter.dom.state.State;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author tzielins
 */
@XmlSeeAlso({SimpleTaskProvider.class})
public abstract class TaskProvider implements Iterable<Task>, Serializable {
    
    
    public abstract int size();
    
    public abstract Task getTask(int index);
    
    public abstract List<Task> getTasks();
    
    public abstract Iterable<Task> getTasks(State state);
    
    //public Iterator<Task> iterator(State state);
    
    
}
