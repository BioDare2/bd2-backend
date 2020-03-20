/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.task;

import ed.robust.jobcenter.dom.state.State;
//import ed.robust.jobcenter.util.IterableWrapper;
//import ed.robust.jobcenter.util.StateSelectiveIterator;
import java.util.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 *
 * @author tzielins
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SimpleTaskProvider extends TaskProvider {

    protected List<Task> tasks;
    
    public SimpleTaskProvider() {
        tasks = new ArrayList<Task>();
    }
    @Override
    public int size() {
        return tasks.size();
    }

    @Override
    public Task getTask(int index) {
        return tasks.get(index);
    }

    @Override
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    //public List<Task> getTasks(State state) {
    @Override
    public Iterable<Task> getTasks(State state) {
        //Iterator<Task> iter = new StateSelectiveIterator<Task>(iterator(),state);
        //return new IterableWrapper<Task>(iter);
        throw new UnsupportedOperationException("Not implemented after dom simplification");
    }

    @Override
    public Iterator<Task> iterator() {
        return tasks.iterator();
    }
    
    public void add(Task task) {
        tasks.add(task);
    }
    
    public void addAll(Collection<? extends Task> tasks) {
        this.tasks.addAll(tasks);
    }

    /*public Iterator<Task> iterator(State state) {
        return new StateSelectiveIterator<Task>(iterator(),state);
    }*/
    
}
