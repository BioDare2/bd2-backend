/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

import ed.biodare.jobcentre2.dom.JobResults;
import ed.biodare.jobcentre2.dom.JobStatus;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityRequest;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

import static ed.biodare.jobcentre2.dom.RhythmicityConstants.*;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare.jobcentre2.dom.TSDataSetJobRequest;
import ed.biodare.jobcentre2.dom.TSResult;
import ed.biodare.rhythm.ejtk.BD2eJTKRes;
import ed.biodare2.backend.features.rhythmicity.RhythmicityService;
import ed.biodare2.backend.features.rhythmicity.RhythmicityUtils;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.features.rhythmicity.dao.RhythmicityArtifactsRep;
import ed.biodare2.backend.handlers.ArgumentException;
import ed.biodare2.backend.handlers.ExperimentHandler;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import ed.biodare2.backend.web.rest.HandlingException;
import ed.biodare2.backend.web.rest.NotFoundException;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.jobcenter.JobSummary;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@Service
public class RhythmicityHandler {

    final Logger log = LoggerFactory.getLogger(this.getClass());
    static int OLD_MINUTES = 60;    
    
    final ExperimentHandler experimentHandler;
    final RhythmicityArtifactsRep rhythmicityRep;
    
    final TSDataHandler dataHandler;
    final RhythmicityUtils utils;
    final RhythmicityService rhythmicityService;
    
    int JOB_WAITING_LIMIT;
    int JOB_WAITING_TIME;    

    @Autowired
    public RhythmicityHandler(ExperimentHandler experimentHandler, 
            TSDataHandler dataHandler, 
            RhythmicityArtifactsRep rhythmicityRep, 
            RhythmicityService rhythmicityService) {
        
        this.experimentHandler = experimentHandler;
        this.rhythmicityRep = rhythmicityRep;
        this.dataHandler = dataHandler;
        this.utils = new RhythmicityUtils();
        this.rhythmicityService = rhythmicityService;
        JOB_WAITING_LIMIT = 6;
        JOB_WAITING_TIME = 250;
    }
    
    
    
    @Transactional
    public UUID newRhythmicity(AssayPack exp, RhythmicityRequest request) throws ArgumentException, RhythmicityHandlingException {
        validateRequest(request);
        
        Optional<List<DataTrace>> dataSet = dataHandler.getDataSet(exp,request.detrending);
        if (!dataSet.isPresent()) throw new ArgumentException("Missing data set in the experiment");
        
        TSDataSetJobRequest jobRequest = utils.prepareJobRequest(exp.getId(), request, dataSet.get());

        UUID jobHandle = submitJob(jobRequest);
        
        RhythmicityJobSummary job = utils.prepareNewJobSummary(jobHandle, jobRequest, request, exp.getId());        

        
        rhythmicityRep.saveJobDetails(job,exp);
        
        experimentHandler.updateHasRhythmicityJobs(exp,true);
        return jobHandle;    
    }
    
    void validateRequest(RhythmicityRequest request) throws ArgumentException {
        if (!request.isValid()) throw new ArgumentException("Not valid ppaRequest"); 
        
        if (!request.method.equals(RHYTHMICITY_METHODS.BD2EJTK.name()))
            throw new ArgumentException("Unsupported method: "+request.method);
        
        try {
            BD2EJTK_PRESETS.valueOf(request.preset);
        } catch (IllegalArgumentException e) {
            throw new ArgumentException("Unsupported preset: "+request.preset);
        }
        
    }
    

    UUID submitJob(TSDataSetJobRequest jobRequest) throws RhythmicityHandlingException {
        
        return rhythmicityService.submitJob(jobRequest);
    }

    @Transactional
    public void handleResults(AssayPack exp, JobResults<TSResult<BD2eJTKRes>> results) {
        
        UUID jobId = results.jobId;
        //sometimes results come faster then job is registered in BioDare
	RhythmicityJobSummary job = waitForJob(jobId, exp.getId());
	
        if (!canBeUpdated(job)) {
            log.warn("Ignoring results for {}, as job {} should not be updated",exp.getId(),jobId);
            return;
        }
        
        try {
            Map<Long,DataTrace> orgData = getOrgData(exp,job);

            matchResultsToData(results.results, orgData);

            rhythmicityRep.saveJobResults(results, job, exp);

            job.jobStatus.state = results.state;
            job.jobStatus.completed = LocalDateTime.now();
            job.jobStatus.message = results.message;
            rhythmicityRep.saveJobDetails(job, exp);
        } catch (Exception e) {
            job.jobStatus.state = State.ERROR;
            job.jobStatus.message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("Could not handle rhythmicity results for job {} {}",jobId, e.getMessage(),e);
            rhythmicityRep.saveJobDetails(job, exp);
            throw new HandlingException("Could not handle rhythmicity results "+e.getMessage(),e);
        }
        
    }    

    RhythmicityJobSummary waitForJob(UUID jobId, long expId) {
        
        //active wait for the job in case it has been submitted but its description not saved in the BioDare
        for (int i = 0;i<JOB_WAITING_LIMIT;i++) {
            
                Optional<RhythmicityJobSummary> ojob = rhythmicityRep.findJob(jobId,expId);
                if (ojob.isPresent()) {
                    return ojob.get();
                }
                log.debug("Wating: {}# for the job: {}",i,jobId);
                try {
                    Thread.sleep(JOB_WAITING_TIME);                
                } catch (InterruptedException e) {break;}
        }
        
        throw new HandlingException("Job: "+jobId+" cannot be found in biodare");    
    }

    boolean canBeUpdated(RhythmicityJobSummary job) {
        return !job.jobStatus.state.equals(State.SUCCESS);
    }

    Map<Long, DataTrace> getOrgData(AssayPack exp, RhythmicityJobSummary job) throws ArgumentException {
        
        String dataSetType = job.parameters.get(job.DATA_SET_TYPE);
        DetrendingType detrendig = DetrendingType.valueOf(dataSetType);


        Optional<List<DataTrace>> dataSet = dataHandler.getDataSet(exp, detrendig);
        if (!dataSet.isPresent()) throw new ArgumentException("Missing data set in the experiment "+exp.getId());            

        return dataSet.get().stream().collect(Collectors.toMap( dt -> dt.dataId, dt -> dt));        
    }

    void matchResultsToData(List<TSResult<BD2eJTKRes>> results, Map<Long, DataTrace> orgData) throws ArgumentException {
        
        if (results.size() < orgData.size())
            throw new ArgumentException("Got less results: "+results.size()+" than original data: "+orgData.size());
        
        for (TSResult<BD2eJTKRes> result : results) {
            
            if (!orgData.containsKey(result.id))
                throw new ArgumentException("Missing data trace of id "+result.id);
            
        }
    }

    public List<RhythmicityJobSummary> getRhythmicityJobs(AssayPack exp) {
        
        return rhythmicityRep.getJobs(exp);
    }

    public RhythmicityJobSummary getRhythmicityJob(AssayPack exp, UUID jobId) {
        RhythmicityJobSummary summary = tryToFindJobSummary(exp, jobId);
        
        if (isRunning(summary.jobStatus.state)) summary = refreshJob(summary, exp);
        return summary;    
    }
    
    public JobResults<TSResult<BD2eJTKRes>> getRhythmicityResults(AssayPack exp, UUID jobId) {
        RhythmicityJobSummary job = tryToFindJobSummary(exp, jobId);
        
        JobResults<TSResult<BD2eJTKRes>> res = rhythmicityRep.findJobResults(jobId, exp.getId())
                .orElseThrow(() -> new NotFoundException("Results for job "+jobId+" not found"));    
        
        return res;
    }    

    RhythmicityJobSummary tryToFindJobSummary(AssayPack exp, UUID jobId) {
        return rhythmicityRep.findJob(jobId, exp.getId())
                            .orElseThrow(() -> new NotFoundException("Job "+jobId+" not found"));    
    }

    boolean isRunning(State state) {
        return State.SUBMITTED.equals(state) || State.PROCESSING.equals(state);
    }

    RhythmicityJobSummary refreshJob(RhythmicityJobSummary summary, AssayPack exp) {
        
        try {
            RhythmicityJobSummary job = tryToFindJobSummary(exp, summary.jobId);
            JobStatus status = rhythmicityService.getJobStatus(job.jobId);
            if (hasFailed(status)) {
                //ignore other status as they may get updated in the background
                return updateJobStatus(job,status,exp);
            }
            summary.jobStatus = status;
            return summary;
        } catch (RhythmicityHandlingException e) {
            throw new HandlingException(e);
        }    
    }

    boolean isOldJob(RhythmicityJobSummary job) {
        LocalDateTime old = LocalDateTime.now().minus(OLD_MINUTES, ChronoUnit.MINUTES);
        
        if (job.jobStatus.modified != null) {
            return job.jobStatus.modified.isBefore(old);
        }        
        return job.jobStatus.submitted.isBefore(old);    
    }

    boolean hasFailed(JobStatus status) {
        return State.ERROR.equals(status.getState()) || State.FAILED.equals(status.getState());    
    }

    RhythmicityJobSummary updateJobStatus(RhythmicityJobSummary job, JobStatus status, AssayPack exp) {
        job.jobStatus.state = status.state;
        job.jobStatus.message = status.message;
        job.jobStatus.modified = status.modified != null ? status.modified : LocalDateTime.now();
        
        job.jobStatus.completed = status.completed != null ? status.completed : LocalDateTime.now();

        return rhythmicityRep.saveJobDetails(job, exp);
    }


    
}
