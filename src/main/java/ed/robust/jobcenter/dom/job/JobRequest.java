/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.job;

import ed.robust.dom.param.Parameters;
import ed.robust.jobcenter.dom.task.TaskProvider;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author tzielins
 */
@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
@XmlSeeAlso( {})
public class JobRequest implements Serializable {
    
    @XmlAttribute(name="m", required = true)    
    protected String method;
    
    @XmlAttribute(name="priority")    
    protected int priority;
    
    @XmlAttribute(name="extId", required = true)    
    protected String externalId;
    
    @XmlElement
    protected Parameters params;
    
    protected TaskProvider tasks;
    
    protected ResultsCallBack resultsHandler;


    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    
    
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Parameters getParams() {
        return params;
    }

    public void setParams(Parameters params) {
        this.params = params;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    public TaskProvider getTasks() {
        return tasks;
    }

    public void setTasks(TaskProvider tasks) {
        this.tasks = tasks;
    }

    public ResultsCallBack getResultsHandler() {
        return resultsHandler;
    }

    public void setResultsHandler(ResultsCallBack resultHandler) {
        this.resultsHandler = resultHandler;
    }
   
    
    
}
