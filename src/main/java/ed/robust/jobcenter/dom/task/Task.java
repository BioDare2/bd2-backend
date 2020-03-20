/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.task;

import ed.robust.dom.param.Parameters;
import ed.robust.jobcenter.dom.state.State;
import ed.robust.jobcenter.dom.state.Stateable;
import ed.robust.jobcenter.dom.state.Status;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 *
 * @author tzielins
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso( {Parameters.class})
public class Task<T> implements Serializable, Stateable {
    
    @XmlAttribute(required=true)
    protected Long id;
    
    protected Status status;
    
    @XmlElement
    protected Parameters params;

    protected DataProvider<T> data;

    public DataProvider<T> getData() {
        return data;
    }

    public void setData(DataProvider<T> data) {
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Parameters getParams() {
        if (params == null) params = new Parameters();
        return params;
    }

    public void setParams(Parameters params) {
        this.params = params;
    }

    public Status getStatus() {
        if (status == null) status = new Status();
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    
    public State getState() {
        return getStatus().getState();
    }
    
}
