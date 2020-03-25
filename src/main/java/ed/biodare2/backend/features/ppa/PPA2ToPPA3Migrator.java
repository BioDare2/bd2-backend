/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare.jobcentre2.dom.State;
import ed.biodare2.backend.features.ppa.dao.PPAArtifactsRepJC2;
import ed.biodare2.backend.repo.dao.PPAArtifactsRep;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tzielins
 */
public class PPA2ToPPA3Migrator {

    final Logger log = LoggerFactory.getLogger(this.getClass());
    
    PPAArtifactsRep ppa2Rep;
    PPAArtifactsRepJC2 ppa3RepJC2;
    
    public PPA2ToPPA3Migrator(PPAArtifactsRep ppa2Rep, PPAArtifactsRepJC2 ppa3RepJC2) {
        this.ppa2Rep = ppa2Rep;
        this.ppa3RepJC2 = ppa3RepJC2;
    }

    public void migrate(AssayPack exp) {
        
        if (!ppa3RepJC2.getJobsSummaries(exp).isEmpty()) {
            log.warn("No migrating {}, ppa3 jobs exists",exp.getId());
            return;
        }
        
        List<PPAJobSummary> oldJobs = ppa2Rep.getJobsSummaries(exp);
        if (oldJobs.isEmpty()) {
            log.warn("No migrating {}, no ppa2 jobs",exp.getId());
            return;            
        }
        
        for (PPAJobSummary oldJob: oldJobs) {
            migrate(oldJob, exp);
        }
    }

    void migrate(PPAJobSummary oldJob, AssayPack exp) {
        
        ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary job = upgradeJob(oldJob, exp.getId());
    }

    ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary upgradeJob(PPAJobSummary oldJob, long expId) {
        
        ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary job = new ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary();
        job.attentionCount = oldJob.attentionCount;
        job.closed = oldJob.closed;
        job.completed = toLocalDate(oldJob.completed);
        job.dataSetId = oldJob.dataSetId;
        job.dataSetType = oldJob.dataSetType;
        job.dataSetTypeName = oldJob.dataSetTypeName;
        job.dataWindow = oldJob.dataWindow;
        job.dataWindowEnd = oldJob.dataWindowEnd;
        job.dataWindowStart = oldJob.dataWindowStart;
        job.failures = oldJob.failures;
        job.jobId = toUUID(oldJob.jobId, expId);
        job.lastError = oldJob.lastError;
        job.max_period = oldJob.max_period;
        job.message = oldJob.message;
        job.method = oldJob.method;
        job.min_period = oldJob.min_period;
        job.modified = toLocalDate(oldJob.modified);
        job.needsAttention = oldJob.needsAttention;
        job.oldId = oldJob.jobId;
        job.parentId = expId;
        job.selections = oldJob.selections;
        job.state = toNewState(oldJob.state);
        job.submitted = toLocalDate(oldJob.submitted);
        job.summary = oldJob.summary;
        return job;
    }
    
        
        
    

    LocalDateTime toLocalDate(Date date) {
        
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    UUID toUUID(long jobId, long expId) {
        return UUID.randomUUID();
    }

    State toNewState(ed.robust.jobcenter.dom.state.State state) {
        
        return State.valueOf(state.name());
    }
    
}
