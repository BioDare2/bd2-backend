/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ed.robust.dom.jobcenter;

import ed.robust.dom.param.Parameters;
import ed.robust.error.RobustProcessException;
import ed.robust.jobcenter.dom.job.JobHandle;
import ed.robust.jobcenter.dom.job.JobRequest;
import ed.robust.jobcenter.dom.state.Status;
import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
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
@XmlSeeAlso({Parameters.class})
public class JobSummary implements Serializable {
    private static final long serialVersionUID = 10L;

    public static final String METHOD_ID = "METHOD_ID";
    public static final String METHOD_NAME = "METHOD_NAME";
    public static final String PARAMS_SUMMARY = "PARAMS_SUMMARY";
    public static final String DW_END = "DW_END";
    public static final String DW_START = "DW_START";
    public static final String DATA_SET_TYPE = "DATA_SET_TYPE";
    public static final String DATA_SET_TYPE_NAME = "DATA_SET_TYPE_NAME";
    public static final String DATA_SET_ID = "DATA_SET_ID";
    public static final String SELECTIONS = "PERIOD_SELECTIONS";

    protected JobHandle job;
    protected Status status;
    protected String lastError;
    protected String requestFileName;
    protected String requestDir;
    @XmlAttribute(required=false)
    protected boolean closed;
    @XmlAttribute(name = "attention")
    protected boolean needsAttention;
    @XmlAttribute(name = "attCount")
    protected int attentionCount;

    protected long failures;
    @XmlElement
    protected Parameters params = new Parameters();

    /*public JobRequest getRequest() throws RobustProcessException {

	File file = new File(new File(requestDir),requestFileName);
	if (!file.exists()) throw new RobustProcessException("Cannot access request, file does not exist: "+file.getPath(),"");

	return RobustXMLUtil.getInstance().readFromXML(file, JobRequest.class);
	
    }*/

    public long getJobId() {
	return getJob().getJobId();
    }

    public JobHandle getJob() {
	return job;
    }

    public void setJob(JobHandle job) {
	this.job = job;
    }

    public String getLastError() {
	return lastError;
    }

    public void setLastError(String lastError) {
	this.lastError = lastError;
    }

    public String getRequestFileName() {
	return requestFileName;
    }

    public void setRequestFileName(String requestFileName) {
	this.requestFileName = requestFileName;
    }

    public String getRequestDir() {
	return requestDir;
    }

    public void setRequestDir(String requestDir) {
	this.requestDir = requestDir;
    }

    
    public Status getStatus() {
	return status;
    }

    public void setStatus(Status status) {
	this.status = status;
    }

    public boolean isClosed() {
	return closed;
    }

    public void setClosed(boolean closed) {
	this.closed = closed;
    }

    public Parameters getParams() {
	if (params == null) params = new Parameters();
	return params;
    }

    public void setParams(Parameters params) {
	this.params = params;
    }

    public boolean needsAttention() {
    	return needsAttention;
    }

    public void setNeedsAttention(boolean needsAttention) {
		this.needsAttention = needsAttention;
    }

    public String getSubmitted() {
	return DateFormat.getDateInstance().format(status.getSubmitted());
    }

    public String getMethod() {
	return params.getString(METHOD_NAME, params.getString(METHOD_ID,""));
    }

    public String getMessage() {
	return status.getMessage();
    }

    public String getSummary() {
	return params.getString(PARAMS_SUMMARY,"");
    }

    public int getAttentionCount() {
	return attentionCount;
    }

    public void setAttentionCount(int attentionCount) {
	this.attentionCount = attentionCount;
    }

    public String getDataWindow() {
	String min = getParams().getString(DW_START,"min");
	if (min.equals("0") || min.equals("0.0")) min = "min";
	String max = getParams().getString(DW_END,"max");
	if (max.equals("0") || max.equals("0.0")) max = "max";
	return min+"-"+max;
    }

    public double getDataWindowStart() {
	return getParams().getDouble(DW_START, 0);
    }

    public String getDataSetType() {
	return getParams().getString(DATA_SET_TYPE,"unknown");
    }
    
    public String getDataSetTypeName() {
        return getParams().getString(DATA_SET_TYPE_NAME,"unknown"); 
    }
    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.job != null ? this.job.hashCode() : 0);
        hash = 37 * hash + (this.status != null ? this.status.hashCode() : 0);
        hash = 37 * hash + (this.lastError != null ? this.lastError.hashCode() : 0);
        hash = 37 * hash + (this.requestFileName != null ? this.requestFileName.hashCode() : 0);
        hash = 37 * hash + (this.requestDir != null ? this.requestDir.hashCode() : 0);
        hash = 37 * hash + (this.closed ? 1 : 0);
        hash = 37 * hash + (this.needsAttention ? 1 : 0);
        hash = 37 * hash + this.attentionCount;
        hash = 37 * hash + (this.params != null ? this.params.hashCode() : 0);
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
        final JobSummary other = (JobSummary) obj;
        if (this.job != other.job && (this.job == null || !this.job.equals(other.job))) {
            return false;
        }
        if (this.status != other.status && (this.status == null || !this.status.equals(other.status))) {
            return false;
        }
        if ((this.lastError == null) ? (other.lastError != null) : !this.lastError.equals(other.lastError)) {
            return false;
        }
        if ((this.requestFileName == null) ? (other.requestFileName != null) : !this.requestFileName.equals(other.requestFileName)) {
            return false;
        }
        if ((this.requestDir == null) ? (other.requestDir != null) : !this.requestDir.equals(other.requestDir)) {
            return false;
        }
        if (this.closed != other.closed) {
            return false;
        }
        if (this.needsAttention != other.needsAttention) {
            return false;
        }
        if (this.attentionCount != other.attentionCount) {
            return false;
        }
        if (this.params != other.params && (this.params == null || !this.params.equals(other.params))) {
            return false;
        }
        return true;
    }

    public long getFailures() {
        return failures;
    }
    
    public void setFailures(int i) {
        failures = i;
    }



}
