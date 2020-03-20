/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.state;

import java.io.Serializable;
import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author tzielins
 */
@XmlAccessorType( XmlAccessType.FIELD )
public class Status implements Serializable, Stateable {
   
    @XmlAttribute(name="state", required = true)
    protected State state;
    
    @XmlAttribute(required = false)
    protected Date submitted;
    
    @XmlAttribute(required = false)
    protected Date modified;
    
    @XmlAttribute(required = false)
    protected Date completed;
    
    protected String message;

    public Status() {
        
    }
    
    public Status(State state) {
        this(state,null);
    }
    
    public Status(State state,String msg) {
        this(state,msg,new Date());
    }
    
    public Status(State state,String msg,Date submitted) {
        this();
        this.state = state;
        this.message = msg;
        this.submitted = submitted;
        this.modified = submitted;
    }
    
    public Date getCompleted() {
        return completed;
    }

    public void setCompleted(Date completed) {
        this.completed = completed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Date getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Date submitted) {
        this.submitted = submitted;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.state != null ? this.state.hashCode() : 0);
        hash = 59 * hash + (this.submitted != null ? this.submitted.hashCode() : 0);
        hash = 59 * hash + (this.modified != null ? this.modified.hashCode() : 0);
        hash = 59 * hash + (this.completed != null ? this.completed.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Status other = (Status) obj;
        if (this.state != other.state) {
            return false;
        }
        if (this.submitted != other.submitted && (this.submitted == null || !this.submitted.equals(other.submitted))) {
            return false;
        }
        if (this.modified != other.modified && (this.modified == null || !this.modified.equals(other.modified))) {
            return false;
        }
        if (this.completed != other.completed && (this.completed == null || !this.completed.equals(other.completed))) {
            return false;
        }
        if ((this.message == null) ? (other.message != null) : !this.message.equals(other.message)) {
            return false;
        }
        return true;
    }
    
    
}
