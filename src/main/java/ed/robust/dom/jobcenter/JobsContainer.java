/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ed.robust.dom.jobcenter;

import ed.robust.jobcenter.dom.job.JobHandle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author tzielins
 */
@XmlRootElement
@XmlAccessorType( XmlAccessType.FIELD )
public class JobsContainer implements Iterable<JobSummary> {

    @XmlTransient
    Map<JobHandle,JobSummary> jobsMap = new HashMap<>();
    
    @XmlTransient
    Map<Long,JobHandle> jobsIds = new HashMap<>();

    @XmlElement(name="j")
    List<JobSummary> jobs = new ArrayList<>();

    transient boolean dirty = true;

    boolean beforeMarshal(Marshaller mar) {
        //System.out.println("Before marsharller called on "+this.getClass().getSimpleName());
        
        jobs = getSorted();
        return true;
    }

    void afterUnmarshal(Unmarshaller unm, Object parent) {
        //System.out.println("After unmarsharller called"+this.getClass().getSimpleName());
        
        //jobsMap = new HashMap<JobHandle, JobSummary>();
        //jobsIds = new HashMap<Long,JobHandle>();

        if (jobs != null) {
            for (JobSummary job : jobs) {
                jobsMap.put(job.getJob(),job);
                jobsIds.put(job.getJob().getJobId(),job.getJob());
            }
            dirty = true;
        }
    }
    
    
    private List<JobSummary> getJobs() {
	return getSorted();
    }
    
    public Optional<JobSummary> getLast() {
        if (isEmpty()) return Optional.empty();
        List<JobSummary> l = getSorted();
        return Optional.of(l.get(l.size()-1));
    }


    public int size() {
	return jobsMap.size();
    }
    
    public boolean isEmpty() {
        return jobsMap.isEmpty();
    }
    
    public void putJob(JobSummary job) {
	jobsMap.put(job.getJob(), job);
	jobsIds.put(job.getJob().getJobId(),job.getJob());
        jobs.add(job);
	dirty = true;
    }
    
    public void removeJob(long jobId) {
        JobSummary job = null;
        JobHandle jH = jobsIds.get(jobId);
        if (jH != null) job = jobsMap.remove(jH);
        jobsIds.remove(jobId);
        if (job != null) jobs.remove(job);
        dirty = true;
    }

    public JobSummary getJob(JobHandle jH) {
	return jobsMap.get(jH);
    }

    public JobSummary getJob(long jobId) {
	JobHandle jH = jobsIds.get(jobId);
	if (jH != null) return getJob(jH);
	return null;
	/*
	for (JobHandle jH : jobs.keySet())
	    if (jH.getJobId() == jobId) return getJob(jH);
	return null;
	 *
	 */
    }


    @Override
    public Iterator<JobSummary> iterator() {
	return getSorted().iterator();
	//return jobs.values().iterator();
    }

    public List<JobSummary> getSorted() {
	if (!dirty && jobs != null) return jobs;

	List<JobSummary> list = new ArrayList<>();
	list.addAll(jobsMap.values());
	Collections.sort(list,new SubmittedComparator());

	jobs = list;
	dirty = false;

	return jobs;

    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.jobsMap != null ? this.jobsMap.hashCode() : 0);
        hash = 97 * hash + (this.jobsIds != null ? this.jobsIds.hashCode() : 0);
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
        final JobsContainer other = (JobsContainer) obj;
        if (this.jobsMap != other.jobsMap && (this.jobsMap == null || !this.jobsMap.equals(other.jobsMap))) {
            return false;
        }
        if (this.jobsIds != other.jobsIds && (this.jobsIds == null || !this.jobsIds.equals(other.jobsIds))) {
            return false;
        }
        return true;
    }
    
    

    public static class SubmittedComparator implements Comparator<JobSummary> {

	@Override
	public int compare(JobSummary o1, JobSummary o2) {
	    Date s1 = o1.getStatus().getSubmitted();
	    Date s2 = o2.getStatus().getSubmitted();

	    if (s1 == null) return (s2 == null ? 0 : -1);
	    return s1.compareTo(s2);
	}

    }

}
