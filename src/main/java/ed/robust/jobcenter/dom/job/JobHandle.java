/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.jobcenter.dom.job;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author tzielins
 */
@XmlAccessorType( XmlAccessType.FIELD )
public class JobHandle implements Serializable {
    
    @XmlAttribute(name="id", required = true)
    protected long jobId;
    
    public JobHandle() {
        
    }
    public JobHandle(long jobId) {
        this();
        this.jobId = jobId;
    }
    
    //protected Status status;

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    /*public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }*/

    @Override
    public int hashCode() {
        return (int)(jobId ^ (jobId >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) return true;
        if (!(obj instanceof JobHandle)) {
            return false;
        }
        final JobHandle other = (JobHandle) obj;
        if (this.jobId != other.jobId) {
            return false;
        }
        return true;
    }
    
    
    
}
