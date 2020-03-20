/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.job;

import ed.robust.dom.param.Parameters;
import ed.robust.jobcenter.dom.state.State;
import ed.robust.jobcenter.dom.state.Stateable;
import ed.robust.jobcenter.dom.state.Status;
import ed.robust.jobcenter.dom.task.TaskProvider;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 *
 * @author tzielins
 */
@XmlSeeAlso( {Parameters.class})
public class JobDescription<M extends Serializable> implements Serializable, Stateable {
    
    private Long id;
    
    protected String methodName;
    
    protected int priority;
    
    protected String externalId;
    
    protected Status status;
    
    @XmlElement
    protected Parameters params;
    
    protected TaskProvider tasks;
    
    protected ResultsCallBack resultsHandler;
    
    protected long dispatchId;
    protected M methodType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    
    
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String method) {
        this.methodName = method;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public State getState() {
        return getStatus().getState();
    }

    public long getDispatchId() {
        return dispatchId;
    }

    public void setDispatchId(long dispatchId) {
        this.dispatchId = dispatchId;
    }

    public M getMethodType() {
        return methodType;
    }

    public void setMethodType(M methodType) {
        this.methodType = methodType;
    }
    
    
}
