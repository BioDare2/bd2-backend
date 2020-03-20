/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.job;

import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.jobcenter.dom.state.State;
import ed.robust.jobcenter.dom.state.Stateable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
//added explicit PPAResults dependency so it can be autoamtically unmarshalled by Spring and such without registering
//in the context.
@XmlSeeAlso({PPAResult.class})
public class JobResult<T extends Serializable> implements Serializable, Stateable, Iterable<TaskResult<T>> {

    @XmlAttribute(name="jobId", required = true)    
    protected long jobId;
    @XmlAttribute(name="innerState", required = false)    
    protected State innerState;
    protected String innerMessage;
    @XmlAttribute(name="state", required = false)    
    protected State jobState;
    protected String jobMessage;
    
    @XmlElement(name="t")
    protected List<TaskResult<T>> taskResults;

    public JobResult() {
        taskResults = new ArrayList<TaskResult<T>>();
    }
    
    public JobResult(long jobId,State jobState,String jobMessage) {
        this();
        this.jobId = jobId;
        this.jobState = jobState;
        this.jobMessage = jobMessage;                
    }
    

    public List<TaskResult<T>> getTaskResults() {
        return taskResults;
    }

    public void setTaskResults(List<TaskResult<T>> taskResults) {
        this.taskResults = taskResults;
    }

    public Iterator<TaskResult<T>> iterator() {
        return taskResults.iterator();
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public String getInnerMessage() {
        return innerMessage;
    }

    public void setInnerMessage(String innerMessage) {
        this.innerMessage = innerMessage;
    }

    public State getInnerState() {
        return innerState;
    }

    public void setInnerState(State innerState) {
        this.innerState = innerState;
    }

    public String getJobMessage() {
        return jobMessage;
    }

    public void setJobMessage(String jobMessage) {
        this.jobMessage = jobMessage;
    }

    public State getJobState() {
        return jobState;
    }

    public void setJobState(State jobState) {
        this.jobState = jobState;
    }

    public State getState() {
        return getInnerState();
    }
    
    
    public void addResult(TaskResult<T> result) {
        taskResults.add(result);
    }
    
}
