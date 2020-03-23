/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare2.backend.handlers.ArgumentException;
import ed.biodare2.backend.web.rest.HandlingException;
import ed.biodare2.backend.repo.dao.PPAArtifactsRep;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import static ed.biodare2.backend.features.ppa.PPAUtils.periodToInt;
import static ed.biodare2.backend.features.ppa.PPAUtils.simplifyStats;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAResultsGroupSummary;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPASimpleResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPASimpleStats;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.jobcenter.JobSummary;
import ed.robust.dom.tsprocessing.CosComponent;
import ed.robust.dom.tsprocessing.FFT_PPA;
import ed.robust.dom.tsprocessing.FailedPPA;
import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.dom.tsprocessing.PPAStats;
import ed.robust.dom.tsprocessing.PhaseType;
import ed.robust.dom.tsprocessing.ResultsEntry;
import ed.robust.dom.tsprocessing.StatsEntry;
import ed.robust.dom.util.ComplexId;
import ed.robust.dom.util.ListMap;
import ed.robust.error.RobustDBException;
import ed.robust.error.RobustProcessException;
import ed.robust.jobcenter.dom.job.JobResult;
import ed.robust.jobcenter.dom.job.TaskResult;
import ed.robust.jobcenter.dom.state.State;
import ed.robust.jobcenter.dom.state.Status;
import ed.robust.ppa.PPAConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class PPAResultsHandler {

    final boolean DEBUG = true;
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    final TSDataHandler dataHandler;
    final PPAArtifactsRep ppaRep;
    final int JOB_WAITING_LIMIT;
    final int JOB_WAITING_TIME;
    
    @Autowired
    public PPAResultsHandler(PPAArtifactsRep ppaRep,TSDataHandler dataHandler) {
        this(ppaRep, dataHandler, 6, 250);
    }
    
    protected PPAResultsHandler(PPAArtifactsRep ppaRep,TSDataHandler dataHandler, int jobWaitingLimit, int jobWaitingTime) {
        this.dataHandler = dataHandler;
        this.ppaRep = ppaRep;
        this.JOB_WAITING_LIMIT = jobWaitingLimit;
        this.JOB_WAITING_TIME = jobWaitingTime;
    } 
    
    
    protected JobSummary waitForJob(long jobId, AssayPack exp) {
        
        //active wait for the job in case it has been submitted but its description not saved in the BioDare
        for (int i = 0;i<JOB_WAITING_LIMIT;i++) {
            
                Optional<JobSummary> ojob = ppaRep.findJob(jobId,exp);
                if (ojob.isPresent()) {
                    return ojob.get();
                }
                logger.debug("Wating: {}# for the job: {}",i,jobId);
                try {
                    Thread.sleep(JOB_WAITING_TIME);                
                } catch (InterruptedException e) {break;}
        }
        
        throw new HandlingException("Job: "+jobId+" cannot be found in biodare");
        
    }
    
    
    public void handleResults(long jobId, AssayPack exp, JobResult<PPAResult> rawResults) {
        
        if (DEBUG) debug("Handling results",jobId,exp.getId());
        
        if (rawResults.getJobId() != jobId) {
	    String msg = "Job ids mismatch: "+jobId+"!-"+rawResults.getJobId();
	    error(msg,jobId,exp.getId());
            throw new HandlingException(msg);
	}
        
        //sometimes results come faster then job is registered in BioDare
	JobSummary job = waitForJob(jobId, exp);
	
        if (!canBeUpdated(job)) {
            warn("Ignoring results, as this job should not be updated",jobId,exp.getId());
            return;
        }

        
        try {
	    Map<Long,DataTrace> orgData = getOrgData(exp,job);

	    List<ResultsEntry> jobResultsEntries = buildResultsEntries(rawResults,orgData,job);
            
            //deal with fits firts so they are not passed over to containers
            saveFits(jobResultsEntries,jobId,exp);
	    clearFits(rawResults);
            clearFits(jobResultsEntries);
            
            processJobResults(jobResultsEntries, job, exp);
            
            //cause we allow overwriting
            updateOrgResults(rawResults, jobId, exp);
            
	    updateFinishedJobState(job, jobResultsEntries);  
            PPAJobSummary summary = PPAUtils.simplifyJob(job);
            saveJob(job,summary,exp);
            
	} catch (Exception e) {
	    error("Could not process results",jobId,exp.getId(),e);
	    job.getStatus().setState(State.ERROR);
	    job.getStatus().setMessage("Cannot locally save the results");
	    job.getStatus().setCompleted(new Date());
	    job.setLastError(e.getMessage());
	    job.setClosed(true);
            
            try {
                PPAJobSummary summary = PPAUtils.simplifyJob(job);
                saveJob(job,summary,exp);
            } catch (Exception ex) {
                error("Could not update the job",jobId,exp.getId(),ex);
            }            
	    throw new HandlingException("Could not handle the results locally");
	}

    }
    
    protected void processJobResults(List<ResultsEntry> jobResultsEntries,JobSummary job,AssayPack exp) {
        
            PPAJobSimpleResults simpleResults = simplifyJobResults(jobResultsEntries,job);
            
            ListMap<ComplexId<Long>, ResultsEntry> resultsGroups = groupResults(jobResultsEntries);
            
            PPAJobResultsGroups groupedResults = assemblyJobGroups(resultsGroups,job);
            
            List<PPAStats> groupsStats = calculateGroupsStats(resultsGroups);
            
            StatsEntry fullStats = assemblyFullStats(groupsStats,job.getJobId());            
            PPAJobSimpleStats simpleStats = simplifyJobStats(groupsStats,job);

            saveJobIndResults(jobResultsEntries,exp,job);
            saveJobSimpleResults(simpleResults,exp,job);
            saveJobResultsGroups(groupedResults,exp,job);
            saveJobSimpleStats(simpleStats,exp,job);
            saveJobFullStats(fullStats, exp, job);


    }

    protected void updateFinishedJobState(JobSummary job,List<ResultsEntry> results) {
	long failed = countFailed(results);
	long needsAttention = countNeedsAttention(results);

	State state = State.FINISHED;
	//if (failed > 0) state = State.FINISHED;
	String msg = null; //results.getJobMessage();
	if (failed > 0) 
            msg = "Failed: "+failed;

	Status status = job.getStatus();
	status.setState(state);
	status.setMessage(msg);
        status.setModified(new Date());
	status.setCompleted(new Date());

	job.setAttentionCount((int)needsAttention);
        job.setFailures((int)failed);
	job.setNeedsAttention(needsAttention > 0);
	job.setClosed(true);

    }


    protected void updateSelectedJobState(JobSummary job,List<ResultsEntry> results,Map<Long, Integer> selection) {
	long needsAttention = countNeedsAttention(results);

	job.setAttentionCount((int)needsAttention);
	job.setNeedsAttention(needsAttention > 0);
        job.getParams().put(JobSummary.SELECTIONS,  mapSelection2String(selection));        
        job.getStatus().setModified(new Date());
    }

    protected Optional<JobSummary> findJob(long jobId, AssayPack exp)  {
        return ppaRep.findJob(jobId,exp);
    }


    protected boolean canBeUpdated(JobSummary job) {
	return !job.isClosed();
    }

    protected void saveJobRawResults(JobResult<PPAResult> results, long jobId, AssayPack exp)   {
        ppaRep.saveJobRawResults(results,jobId,exp,false);
    }
    
    protected void updateOrgResults(JobResult<PPAResult> results, long jobId, AssayPack exp) throws IOException  {
        ppaRep.saveJobRawResults(results,jobId,exp,true);
    }
    

    protected FailedPPA makeFailedResult(TaskResult<PPAResult> taskResult,long jobId) {
        
        String msg;
        switch (taskResult.getState()) {
            case TIMED_OUT : 
                msg = taskResult.getMessage() == null ? "Computation timeout" : "Computation timeout "+taskResult.getMessage(); break;
            case FINISHED:
                msg = "Null result received"; break;
            default:
                warn("TaskResult failed: "+taskResult.getState()+", "+taskResult.getMessage(),jobId,0);
                String err = taskResult.getMessage();
                if (err != null && !err.isEmpty()) msg = err;
                else msg = "";
        }
        
        return new FailedPPA(msg);
    }
    
    protected void debug(String msg,long jobId,long innerId) {
        logger.debug(msg+", job:"+jobId+"("+innerId+")");
    }
    
    protected void warn(String msg,long jobId,long innerId) {
        logger.warn(msg+", job:"+jobId+"("+innerId+")");
    }

    protected void error(String msg,long jobId,long innerId) {
        logger.error(msg+", job:"+jobId+"("+innerId+")");
    }

    protected void error(String msg,long jobId,long innerId,Throwable e) {
        logger.error(msg+", job:"+jobId+"("+innerId+")"+", "+e.getMessage(),e);
    }

        
    protected Map<Long, DataTrace> getOrgData(AssayPack exp,JobSummary job) throws IOException, ArgumentException {
        
        
	    String dataSetType = job.getParams().getString(JobSummary.DATA_SET_TYPE);
            DetrendingType detrendig = DetrendingType.valueOf(dataSetType);
            
            
            Optional<List<DataTrace>> dataSet = dataHandler.getDataSet(exp, detrendig);
            if (!dataSet.isPresent()) throw new ArgumentException("Missing data set in the experiment");            
            
            
            return dataSet.get().stream().collect(Collectors.toMap( dt -> dt.dataId, dt -> dt));

    }
    
    protected List<ResultsEntry> buildResultsEntries(JobResult<PPAResult> results, Map<Long, DataTrace> orgData, JobSummary job) {
       
	List<ResultsEntry> container = new ArrayList<>();
        
        FakeIdExtractor fakeIds = new FakeIdExtractor(orgData.values());

	Set<Long> submittedResults = new HashSet<>();

	for (TaskResult<PPAResult> result : results) {
	    DataTrace data = orgData.get(result.getTaskId());
	    if (data == null)
	    {
		warn("Assembly results: No org data for result: "+result.getTaskId(),job.getJobId(),0);
		continue;
	    }
	    if (!submittedResults.add(result.getTaskId())) {
		warn("Duplicate results for task: "+result.getTaskId(),job.getJobId(),0);
	    }

	    PPAResult ppa = result.getResult();
	    ResultsEntry entry;
	    if (ppa != null) {
		if (!State.FINISHED.equals(result.getState()) && (!(ppa instanceof FailedPPA)))
		    warn("Wrong state but not null results: "+result.getState()+" "+result.getTaskId(),job.getJobId(),0);
		
                entry = buildResultsEntry(data,job,ppa,fakeIds);
	    }
	    else {

		entry = buildResultsEntry(data,job,makeFailedResult(result, job.getJobId()),fakeIds);
	    }
	    container.add(entry);

	}

	Set<Long> missed = new HashSet<>(orgData.keySet());
	missed.removeAll(submittedResults);

	if (!missed.isEmpty()) {
	    warn(missed.size()+ " results are missing, "+submittedResults.size()+" present",job.getJobId(),0);
	    for (Long dataId : missed) {
		DataTrace data = orgData.get(dataId);

		ResultsEntry entry = buildResultsEntry(data,job,new FailedPPA("No result received"),fakeIds);
		container.add(entry);

	    }
	}

        container.sort(Comparator.comparing( e -> e.dataId));
	return container;
    }
    
    protected ResultsEntry buildResultsEntry(DataTrace data, JobSummary job, PPAResult result, FakeIdExtractor fakeIds) {
        
        
	ResultsEntry entry = new ResultsEntry();
	entry.setDataId(data.dataId);
	entry.setDataType(job.getDataSetType());
	entry.setJobId(job.getJobId());
	entry.setResult(result);
        
	entry.orgId = data.traceRef;
        entry.rawDataId = data.rawDataId;
	entry.biolDescId = fakeIds.getBioId(data);//data.getBiolDesc().getId());
	entry.environmentId = (fakeIds.getCondId(data));//data.getEnvDesc().getId());
        //if (data.isIgnored()) entry.ignored = (true);

	return entry;
    }
    
    
    
    protected List<PPAStats> calculateGroupsStats(ListMap<ComplexId<Long>, ResultsEntry> results) {
        
        ListMap<ComplexId<Long>,PPAResult> groups = filterResultsForStats(results);
        
        List<PPAStats> allStats = new ArrayList<>();
        
	for (ComplexId<Long> key : groups.keySet()) {
	    //Long bioId = key.getKey(0);
	    //Long envId = key.getKey(1);

	    PPAStats stats = calculateStats(groups.get(key));
            //it is from full data set cause there maybe nothign for stats subset
            //entries input groups are never empty            
            ResultsEntry firstBfFileter = results.get(key).get(0);
	    stats.setBiolDescId(firstBfFileter.biolDescId);
	    stats.setEnvironmentId(firstBfFileter.environmentId);
            stats.setMemberDataId(firstBfFileter.dataId);
            stats.setRawId(firstBfFileter.rawDataId);
            
            //Long dataId = results.get(key).get(0).getDataId();
            //statsByMemberId.put(dataId,stats);
            allStats.add(stats);

	}

        allStats.sort(Comparator.comparing( stat -> stat.getMemberDataId()));
        //return statsByMemberId;
        return allStats;
        
    }
    
    
    protected StatsEntry assemblyFullStats(List<PPAStats> entries,long jobId) {
	StatsEntry statsEntry = new StatsEntry();
	statsEntry.setJobId(jobId);

        entries.forEach( stats -> statsEntry.add(stats));
        
        return statsEntry;
    }
    
    protected PPAJobResultsGroups assemblyJobGroups(ListMap<ComplexId<Long>, ResultsEntry> groups, JobSummary job) {
        
	PPAJobResultsGroups clusters = new PPAJobResultsGroups(job.getJobId());
        clusters.periodMin = job.getParams().getDouble(PPAConstants.MIN_PERIOD,0);
        clusters.periodMax = job.getParams().getDouble(PPAConstants.MAX_PERIOD,0);

	for (ComplexId<Long> key : groups.keySet()) {
	    //Long bioId = key.getKey(0);
	    //Long envId = key.getKey(1);
            
	    PPAResultsGroupSummary cluster = joinResults(groups.get(key),job.getDataWindowStart());
            //cluster.bioId = bioId;
            //cluster.envId = envId;
            clusters.groups.add(cluster);

	}
        
        clusters.groups.sort( Comparator.comparing(g -> g.memberDataId));


	return clusters;    
    }  
    
    protected PPAResultsGroupSummary joinResults(List<ResultsEntry> entries, double dataWindowStart) {
        
        List<PPAResult> ppas = entries.stream().map( r -> r.getResult()).collect(Collectors.toList());
        
        PPAResultsGroupSummary cluster = joinPPAResults(ppas, dataWindowStart);
        final ResultsEntry first = entries.get(0);
        cluster.memberDataId = first.dataId;
        cluster.rawId = first.rawDataId;
        cluster.bioId = first.biolDescId;
        cluster.envId = first.environmentId;
        
        return cluster;
    }
    
    protected PPAResultsGroupSummary joinPPAResults(List<PPAResult> results, double dataWindowStart) {
        
        PPAResultsGroupSummary cluster = new PPAResultsGroupSummary();
        
        cluster.failures = (int)results.stream()
                .filter( res -> res.hasFailed())
                .count();
        
        cluster.excluded = (int)results.stream()
                .filter( res -> !res.hasFailed())
                .filter( res -> res.needsAttention() || res.isIgnored())
                .count();

        List<PPAResult> okResults = results.stream()
                .filter( res -> !res.hasFailed())
                .filter( res -> !res.needsAttention() && !res.isIgnored())
                .collect(Collectors.toList());
        
        cluster.periods = okResults.stream()
                    .map( res -> res.getPeriod())
                    .collect(Collectors.toList());
        
        for (PhaseType phase: PhaseType.values()) {

            cluster.amplitudes.put(phase, 
                        okResults.stream()
                            .map( res -> res.getPPA(phase))
                            .map( ppa -> ppa.getAmplitude())
                            .collect(Collectors.toList())
                    );
            
            cluster.phasesToZero.put(phase,
                        okResults.stream()
                            .map( res -> res.getPPA(phase))
                            .map( ppa -> ppa.getPhase())
                            .collect(Collectors.toList())
                    
                    );
            
            cluster.phasesToZeroCirc.put(phase,
                        okResults.stream()
                            .map( res -> res.getPPA(phase))
                            .map( ppa -> PPAUtils.circadianPhase(ppa))
                            .collect(Collectors.toList())
                    
                    );

            cluster.phasesToWindow.put(phase,
                        okResults.stream()
                            .map( res -> res.getPPA(phase))
                            .map( ppa -> PPAUtils.relativePhase(ppa,dataWindowStart))
                            .collect(Collectors.toList())                    
                    );
            
            cluster.phasesToWindowCirc.put(phase,
                        okResults.stream()
                            .map( res -> res.getPPA(phase))
                            .map( ppa -> PPAUtils.circadianRelativePhase(ppa,dataWindowStart))
                            .collect(Collectors.toList())                    
                    );
            
        }
        
        return cluster;
    }
  
    protected long countFailed(List<ResultsEntry> results) {
	return results.stream()
                .filter( r -> r.getResult() == null || r.getResult().hasFailed())
                .count();
    }


    protected long countNeedsAttention(List<ResultsEntry> results) {
        
        return results.stream()
                .filter( r -> r.getResult() != null)
                .filter( r -> !r.getResult().isIgnored())
                .filter( r -> r.getResult().needsAttention())
                .count();
    }


    
    protected void saveJobFullStats(StatsEntry stats, AssayPack exp, JobSummary job) {
	ppaRep.saveJobFullStats(stats, exp,job.getJobId());
    }
    
    protected void saveJobSimpleStats(PPAJobSimpleStats stats, AssayPack exp, JobSummary job) {
	ppaRep.saveJobSimpleStats(stats, exp,job.getJobId());
    }
    
    
    protected void saveJobResultsGroups(PPAJobResultsGroups groupedResults, AssayPack exp, JobSummary job) {
        ppaRep.saveJobResultsGroups(groupedResults, exp, job.getJobId());
    }    

    protected void saveJobIndResults(List<ResultsEntry> results, AssayPack exp, JobSummary job) {
        ppaRep.saveJobIndResults(results,exp,job.getJobId());
    }  
    
    protected void saveJobSimpleResults(PPAJobSimpleResults results, AssayPack exp, JobSummary job) {
        ppaRep.saveJobSimpleResults(results,exp,job.getJobId());
    }    

    protected void saveFits(List<ResultsEntry> entries, long jobId, AssayPack exp) throws RobustDBException, RobustProcessException, IOException {

	Map<Long,TimeSeries> fits = new HashMap<>();
        for (ResultsEntry entry : entries) {
            PPAResult result = entry.getResult();
            TimeSeries fit = result.getFit();
            if (fit != null)
                fits.put(entry.getDataId(), fit);
        }
        
        ppaRep.saveFits(fits,jobId,exp);
    }

    
    protected void clearFits(List<ResultsEntry> entries) {
        entries.forEach( entry -> {
            if (entry.getResult() != null)
                entry.getResult().setFit(null);
        });
    }    
    
    protected void clearFits(JobResult<PPAResult> results) {
        for (TaskResult<PPAResult> taskRes : results) {
            if (taskRes.getResult() != null)
                taskRes.getResult().setFit(null);
        }
    }
    


    protected void saveJob(JobSummary job, PPAJobSummary summary, AssayPack exp) {
	ppaRep.saveJobDetails(job,summary, exp);
    }


    public Long stringToLong(String num) {
        if (num == null) return null;
        try {
            return Long.valueOf(num);
        } catch (NumberFormatException e) {
            logger.warn("Could not convert string "+num+" to long: "+e.getMessage());
            return null;
        }
    }

    protected ListMap<ComplexId<Long>, ResultsEntry> groupResults(List<ResultsEntry> entries) {
	ListMap<ComplexId<Long>, ResultsEntry> groups = new ListMap<>();
        
        for (ResultsEntry entry : entries) {
            if (entry.ignored) continue;
    
            ComplexId<Long> key = new ComplexId<>(entry.biolDescId,entry.environmentId);
            groups.add(key, entry);
        }
        
        groups.values().forEach( list -> list.sort(Comparator.comparing( entry -> entry.dataId)));
        
	return groups;
        
    }

    protected ListMap<ComplexId<Long>, PPAResult> filterResultsForStats(ListMap<ComplexId<Long>, ResultsEntry> resultsGroups) {
	ListMap<ComplexId<Long>, PPAResult> groups = new ListMap<>();
        
        resultsGroups.entrySet().forEach( entry -> {
        
            ComplexId<Long> key = entry.getKey();
            
            List<PPAResult> ppas = entry.getValue().stream()
                    .map( e -> e.getResult())
                    .filter( ppa -> ppa != null)
                    .filter( ppa -> !ppa.hasFailed())
                    .filter( ppa -> !ppa.needsAttention())
                    .filter( ppa -> !ppa.isIgnored())
                    .collect(Collectors.toList());
                    ;

            //if (!ppas.isEmpty())
            groups.put(key, ppas);                    
        });
        
        return groups;
        
    }
    

    protected PPAStats calculateStats(List<PPAResult> results) {
        return PPAStatsCalculator.calculateStats(results);
    }

    public int doPPASelection(PPAJobSummary summary, AssayPack exp, Map<Long, Integer> selection) {

        //PPAJobSummary summary = ppaRep.getJobSummary(exp, job.getJobId()).orElseThrow(() -> new HandlingException("JobSummary: "+job.getJobId()+" cannot be found in biodare"));
        JobSummary job = ppaRep.getJobFullDescription(exp, summary.jobId).orElseThrow(() -> new HandlingException("JobSummary: "+summary.jobId+" cannot be found in biodare"));

        List<ResultsEntry> jobIndResults = ppaRep.getJobIndResults(exp, job.getJobId());
        jobIndResults = applyUserResultsSelection(jobIndResults, selection);
        
        processJobResults(jobIndResults, job, exp);
        
        updateSelectedJobState(job, jobIndResults, selection);
        summary.attentionCount = job.getAttentionCount();
        summary.needsAttention = job.needsAttention();
        
        saveJob(job,summary,exp);
        return job.getAttentionCount();
        
    }

    public boolean isFinished(State state) {
        switch(state) {
            case SUCCESS:
            case FINISHED:
            case COMPLETED: return true;
            default: return false;
        }
    }
    
    @Deprecated
    public int redoResultsProcessing(AssayPack exp) throws IOException {
        
        List<PPAJobSummary> finishedJobs = ppaRep.getJobsSummaries(exp).stream()
                .filter( job -> isFinished(job.state))
                .collect(Collectors.toList());
        
        
        for (PPAJobSummary job: finishedJobs) {  
            
            logger.info("Redoing ppa processing for {}:{}",exp.getId(),job.jobId);
            List<ResultsEntry> jobIndResults = ppaRep.getJobIndResults(exp, job.jobId);
            jobIndResults.sort(Comparator.comparing( e -> e.dataId));
            
            jobIndResults.stream().filter( e -> e.rawDataId == 0L).forEach( e -> {e.rawDataId = e.dataId;});
            
            JobSummary desc = ppaRep.getJobFullDescription(exp, job.jobId).get();
            
            //logger.debug("J:"+job.jobId+"\n"+jobIndResults.stream().map(e -> ""+e.rawDataId).collect(Collectors.joining(",")));
            processJobResults(jobIndResults, desc, exp);
            
        };

        return finishedJobs.size();
    }
    
    
    
    
    
    protected String mapSelection2String(Map<Long, Integer> selection) {
        
        return selection.entrySet().stream()
                .map( entry -> entry.getKey().toString()+":"+entry.getValue().toString())
                .collect(Collectors.joining(";"));
    }
    
    protected List<ResultsEntry> applyUserResultsSelection(List<ResultsEntry> entries, Map<Long, Integer> selectedValues) {
	List<ResultsEntry> updatedResults = new ArrayList<>();


	    for (ResultsEntry entry : entries) {

                Integer selection = selectedValues.get(entry.getDataId());
		if (selection == null) {
		    updatedResults.add(entry);
		} else {
		    updatedResults.add(updateEntrySelection(entry,selection));
		}

	    }
            
	return updatedResults;
    }
    
    
    
    protected ResultsEntry updateEntrySelection(ResultsEntry entry, int selected) {

	ResultsEntry updated = new ResultsEntry();
        updated.biolDescId = entry.biolDescId;
        updated.dataId = entry.dataId;
        updated.dataType = entry.dataType;
        updated.environmentId = entry.environmentId;
        updated.ignored = entry.ignored;
        updated.jobId = entry.jobId;
        updated.orgId = entry.orgId;
        updated.ignored = entry.ignored;
        updated.rawDataId = entry.rawDataId;
	updated.setResult(updateResultSelection(entry.getResult(),selected));

	return updated;
    }
    

    protected PPAResult updateResultSelection(PPAResult result, int selected) {

	if (selected == -1) {
	    result.setIgnored(true);
	    return result;
	}

	if (result instanceof FFT_PPA) {
	    FFT_PPA fftResult = (FFT_PPA) result;
	    CosComponent selectedCos = null;
	    for (CosComponent cos : fftResult) {
		if (selected == periodToInt(cos.getPeriod())) {
		    selectedCos = cos;
		    break;
		}
	    }
	    if (selectedCos == null) {
		throw new HandlingException("Mismatch between selcted period: "+selected+" and present cos components");
	    } else {
		fftResult.getComponents().remove(selectedCos);
		fftResult.getComponents().add(0,selectedCos);
		fftResult.setNeedsAttention(false);
		fftResult.setIgnored(false);
		return fftResult;
	    }

	} else {
	    if (selected == periodToInt(result.getPeriod())) {

		result.setNeedsAttention(false);
		result.setIgnored(false);
		return result;
	    } else {
		throw new HandlingException("Mismatch between selcted period: "+selected+" and present: "+result.getPeriod());
	    }
	}
	
    }

    protected PPAJobSimpleStats simplifyJobStats(List<PPAStats> fullStats,JobSummary job) {
        
        PPAJobSimpleStats stats = new PPAJobSimpleStats(job.getJobId());
        double dw = job.getDataWindowStart();
        
        fullStats.forEach( entry -> {
            PPASimpleStats st = simplifyStats(entry,dw);
            //st.memberDataId = entry.getKey();
            stats.stats.add(st);
        });

        return stats;
    }



    protected PPAJobSimpleResults simplifyJobResults(List<ResultsEntry> entries, JobSummary job) {
        PPAJobSimpleResults res = new PPAJobSimpleResults(job.getJobId());
        
        //final long jobId = job.getJobId();
        final double windowStart = job.getDataWindowStart();
        
        entries.forEach( entry -> {
            PPASimpleResultEntry simple = PPAUtils.simplifyResultsEntry(entry,windowStart);
            res.results.add(simple);
        });
        
        return res;
    }


}
