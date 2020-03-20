/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.job;

import ed.robust.jobcenter.dom.state.State;
import ed.robust.jobcenter.dom.state.Stateable;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author tzielins
 */
@XmlAccessorType( XmlAccessType.FIELD )
public class TaskResult<T> implements Serializable, Stateable {
    
    @XmlAttribute(name="id", required = true)    
    protected Long taskId;
    @XmlAttribute(name="state", required = false)    
    protected State state;
    protected String message;
    @XmlElement
    protected T result;

    public TaskResult() {
        this(-1L,null,null,null);
    }
    
    public TaskResult(Long taskId,State state,String message,T result) {
        this.taskId = taskId;
        this.state = state;
        this.message = message;
        this.result = result;
    }
    
    public TaskResult(Long taskId,T result) {
        this(taskId,State.FINISHED,"",result);
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
    
}
