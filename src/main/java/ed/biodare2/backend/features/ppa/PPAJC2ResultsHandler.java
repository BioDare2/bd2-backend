/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare.jobcentre2.dom.JobResults;
import ed.biodare.jobcentre2.dom.PPAJobResults;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare.jobcentre2.dom.TSResult;
import ed.biodare2.backend.handlers.ArgumentException;
import ed.biodare2.backend.web.rest.HandlingException;

import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import static ed.biodare2.backend.features.ppa.PPAUtilsJC2.*;
import ed.biodare2.backend.features.ppa.dao.PPAArtifactsRepJC2;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAFullResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAResultsGroupSummary;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleStats;

import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.data.TimeSeries;

import ed.robust.dom.tsprocessing.CosComponent;
import ed.robust.dom.tsprocessing.FFT_PPA;
import ed.robust.dom.tsprocessing.FailedPPA;
import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.dom.tsprocessing.PPAStats;
import ed.robust.dom.tsprocessing.PhaseType;

import ed.robust.dom.tsprocessing.StatsEntry;
import ed.robust.dom.util.ComplexId;
import ed.robust.dom.util.ListMap;
import ed.robust.error.RobustDBException;
import ed.robust.error.RobustProcessException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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
public class PPAJC2ResultsHandler {

    final boolean DEBUG = true;
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    final TSDataHandler dataHandler;
    final PPAArtifactsRepJC2 ppaRep;
    final int JOB_WAITING_LIMIT;
    final int JOB_WAITING_TIME;
    
    @Autowired
    public PPAJC2ResultsHandler(PPAArtifactsRepJC2 ppaRep,TSDataHandler dataHandler) {
        this(ppaRep, dataHandler, 6, 250);
    }
    
    protected PPAJC2ResultsHandler(PPAArtifactsRepJC2 ppaRep,TSDataHandler dataHandler, int jobWaitingLimit, int jobWaitingTime) {
        this.dataHandler = dataHandler;
        this.ppaRep = ppaRep;
        this.JOB_WAITING_LIMIT = jobWaitingLimit;
        this.JOB_WAITING_TIME = jobWaitingTime;
    } 
    
    
    protected PPAJobSummary waitForJob(UUID jobId, AssayPack exp) {
        
        //active wait for the job in case it has been submitted but its description not saved in the BioDare
        for (int i = 0;i<JOB_WAITING_LIMIT;i++) {
            
                Optional<PPAJobSummary> ojob = ppaRep.getJobSummary(exp, jobId);
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
    
    
    public void handleResults(AssayPack exp, PPAJobResults rawResults) {
        
        UUID jobId = rawResults.jobId;
        if (DEBUG) debug("Handling ppa results",jobId,exp.getId());
        
        //sometimes results come faster then job is registered in BioDare
	PPAJobSummary job = waitForJob(jobId, exp);
	
        if (!canBeUpdated(job)) {
            warn("Ignoring results, as this job should not be updated",jobId,exp.getId());
            return;
        }

        
        try {
	    Map<Long,DataTrace> orgData = getOrgData(exp,job);

	    List<PPAFullResultEntry> jobResultsEntries = buildResultsEntries(rawResults, orgData, job);
            
            //deal with fits firts so they are not passed over to containers
            saveFits(jobResultsEntries, jobId, exp);
	    clearFits(rawResults);
            clearFits(jobResultsEntries);
            
            processJobResults(jobResultsEntries, job, exp);
            
            //cause we allow overwriting
            updateOrgResults(rawResults, exp);
            
	    updateFinishedJobState(job, jobResultsEntries);  
            //PPAJobSummary summary = PPAUtils.simplifyJob(job);
            saveJob(job, exp);
            
	} catch (Exception e) {
	    error("Could not process results",jobId,exp.getId(),e);
	    job.state = State.ERROR;
	    job.message = "Cannot locally save the results";
	    job.completed = LocalDateTime.now();
	    job.lastError = e.getMessage();
	    job.closed = true;
            
            try {
                saveJob(job,exp);
            } catch (Exception ex) {
                error("Could not update the job",jobId,exp.getId(),ex);
            }            
	    throw new HandlingException("Could not handle the results locally");
	}

    }
    
   
    
    protected void processJobResults(List<PPAFullResultEntry> jobResultsEntries, PPAJobSummary job,AssayPack exp) {
        
            PPAJobSimpleResults simpleResults = simplifyJobResults(jobResultsEntries,job);
            
            ListMap<ComplexId<Long>, PPAFullResultEntry> resultsGroups = groupResults(jobResultsEntries);
            
            PPAJobResultsGroups groupedResults = assemblyJobGroups(resultsGroups,job);
            
            List<PPAStats> groupsStats = calculateGroupsStats(resultsGroups);
            
            StatsEntry fullStats = assemblyFullStats(groupsStats,job.jobId);            
            PPAJobSimpleStats simpleStats = simplifyJobStats(groupsStats,job);

            saveJobIndResults(jobResultsEntries,exp,job);
            saveJobSimpleResults(simpleResults,exp,job);
            saveJobResultsGroups(groupedResults,exp,job);
            saveJobSimpleStats(simpleStats,exp,job);
            saveJobFullStats(fullStats, exp, job);


    }

    protected void updateFinishedJobState(PPAJobSummary job,List<PPAFullResultEntry> results) {
	long failed = countFailed(results);
	long needsAttention = countNeedsAttention(results);

	State state = State.FINISHED;
	//if (failed > 0) state = State.FINISHED;
	String msg = null; //results.getJobMessage();
	if (failed > 0) 
            msg = "Failed: "+failed;

	//Status status = job.getStatus();
	//status.setState(state);
	//status.setMessage(msg);
        job.message = msg;
        job.state = state;
        job.modified = LocalDateTime.now();
	job.completed = LocalDateTime.now();

        job.needsAttention = needsAttention > 0;
	job.attentionCount = needsAttention;
        job.failures = failed;

        job.closed = true;

    }


    protected void updateSelectedJobState(PPAJobSummary job,List<PPAFullResultEntry> results,Map<Long, Integer> selection) {
	long needsAttention = countNeedsAttention(results);

	job.attentionCount = (needsAttention);
	job.needsAttention = (needsAttention > 0);
        job.selections = mapSelection2String(selection);        
        job.modified = LocalDateTime.now();
    }


    protected boolean canBeUpdated(PPAJobSummary job) {
	return !job.closed;
    }

    
    protected void updateOrgResults(PPAJobResults results, AssayPack exp) throws IOException  {
        ppaRep.saveJC2JobRawResults(results, results.jobId,exp,true);
    }
    

    protected FailedPPA makeFailedResult(TSResult<PPAResult> taskResult,PPAJobSummary job) {
        
        String msg = "Null result received";
        if (taskResult.result != null) {
            msg = taskResult.result.getMessage();
        }
        return new FailedPPA(msg);
    }
    
    protected void debug(String msg,long jobId,long innerId) {
        logger.debug(msg+", job:"+jobId+"("+innerId+")");
    }
    
    protected void debug(String msg,UUID jobId,long innerId) {
        logger.debug(msg+", job:"+jobId+"("+innerId+")");
    }
    
    
    protected void warn(String msg,long jobId,long innerId) {
        logger.warn(msg+", job:"+jobId+"("+innerId+")");
    }
    
    protected void warn(String msg,String jobId,long innerId) {
        logger.warn(msg+", job:"+jobId+"("+innerId+")");
    }    
    
    protected void warn(String msg,UUID jobId,long innerId) {
        logger.warn(msg+", job:"+jobId+"("+innerId+")");
    }    

    protected void error(String msg,long jobId,long innerId) {
        logger.error(msg+", job:"+jobId+"("+innerId+")");
    }

    protected void error(String msg,long jobId,long innerId,Throwable e) {
        logger.error(msg+", job:"+jobId+"("+innerId+")"+", "+e.getMessage(),e);
    }
    protected void error(String msg,UUID jobId,long innerId,Throwable e) {
        logger.error(msg+", job:"+jobId+"("+innerId+")"+", "+e.getMessage(),e);
    }
        
    protected Map<Long, DataTrace> getOrgData(AssayPack exp,PPAJobSummary job) throws IOException, ArgumentException {
        
        
	    String dataSetType = job.dataSetType;
            DetrendingType detrendig = DetrendingType.valueOf(dataSetType);
            
            
            Optional<List<DataTrace>> dataSet = dataHandler.getDataSet(exp, detrendig);
            if (!dataSet.isPresent()) throw new ArgumentException("Missing data set in the experiment");            
            
            
            return dataSet.get().stream().collect(Collectors.toMap( dt -> dt.dataId, dt -> dt));

    }
    
    protected List<PPAFullResultEntry> buildResultsEntries(JobResults<TSResult<PPAResult>> results, Map<Long, DataTrace> orgData, PPAJobSummary job) {
       
	List<PPAFullResultEntry> container = new ArrayList<>();
        
        FakeIdExtractor fakeIds = new FakeIdExtractor(orgData.values());

	Set<Long> submittedResults = new HashSet<>();

	for (TSResult<PPAResult> result : results.results) {
	    DataTrace data = orgData.get(result.id);
	    if (data == null)
	    {
		warn("Assembly results: No org data for result: "+result.id,job.jobId,0);
		continue;
	    }
	    if (!submittedResults.add(result.id)) {
		warn("Duplicate results for task: "+result.id,job.jobId,0);
	    }

	    PPAResult ppa = result.result;
	    PPAFullResultEntry entry;
	    if (ppa != null) {
                entry = buildResultsEntry(data,job,ppa,fakeIds);
	    }
	    else {

		entry = buildResultsEntry(data,job,makeFailedResult(result, job),fakeIds);
	    }
	    container.add(entry);

	}

	Set<Long> missed = new HashSet<>(orgData.keySet());
	missed.removeAll(submittedResults);

	if (!missed.isEmpty()) {
	    warn(missed.size()+ " results are missing, "+submittedResults.size()+" present",job.jobId,0);
	    for (Long dataId : missed) {
		DataTrace data = orgData.get(dataId);

		PPAFullResultEntry entry = buildResultsEntry(data,job,new FailedPPA("No result received"),fakeIds);
		container.add(entry);

	    }
	}

        container.sort(Comparator.comparing( e -> e.dataId));
	return container;
    }
    
    protected PPAFullResultEntry buildResultsEntry(DataTrace data, PPAJobSummary job, PPAResult result, FakeIdExtractor fakeIds) {
        
        
	PPAFullResultEntry entry = new PPAFullResultEntry();
	entry.dataId = data.dataId;
	entry.dataType = (job.dataSetType);
	entry.jobId = (job.jobId);
	entry.result = (result);
        
	entry.orgId = data.traceRef;
        entry.rawDataId = data.rawDataId;
	entry.biolDescId = fakeIds.getBioId(data);//data.getBiolDesc().getId());
	entry.environmentId = (fakeIds.getCondId(data));//data.getEnvDesc().getId());
        //if (data.isIgnored()) entry.ignored = (true);

	return entry;
    }
    
    
    
    protected List<PPAStats> calculateGroupsStats(ListMap<ComplexId<Long>, PPAFullResultEntry> results) {
        
        ListMap<ComplexId<Long>,PPAResult> groups = filterResultsForStats(results);
        
        List<PPAStats> allStats = new ArrayList<>();
        
	for (ComplexId<Long> key : groups.keySet()) {
	    //Long bioId = key.getKey(0);
	    //Long envId = key.getKey(1);

	    PPAStats stats = calculateStats(groups.get(key));
            //it is from full data set cause there maybe nothign for stats subset
            //entries input groups are never empty            
            PPAFullResultEntry firstBfFileter = results.get(key).get(0);
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
    
    
    protected StatsEntry assemblyFullStats(List<PPAStats> entries,UUID jobId) {
	StatsEntry statsEntry = new StatsEntry();
	statsEntry.setUuid(jobId);

        entries.forEach( stats -> statsEntry.add(stats));
        
        return statsEntry;
    }
    
    protected PPAJobResultsGroups assemblyJobGroups(ListMap<ComplexId<Long>, PPAFullResultEntry> groups, PPAJobSummary job) {
        
	PPAJobResultsGroups clusters = new PPAJobResultsGroups(job.jobId);
        clusters.periodMin = job.min_period;
        clusters.periodMax = job.max_period;

	for (ComplexId<Long> key : groups.keySet()) {
	    //Long bioId = key.getKey(0);
	    //Long envId = key.getKey(1);
            
	    PPAResultsGroupSummary cluster = joinResults(groups.get(key),job.dataWindowStart);
            //cluster.bioId = bioId;
            //cluster.envId = envId;
            clusters.groups.add(cluster);

	}
        
        clusters.groups.sort( Comparator.comparing(g -> g.memberDataId));


	return clusters;    
    }  
    
    protected PPAResultsGroupSummary joinResults(List<PPAFullResultEntry> entries, double dataWindowStart) {
        
        List<PPAResult> ppas = entries.stream().map( r -> r.result).collect(Collectors.toList());
        
        PPAResultsGroupSummary cluster = joinPPAResults(ppas, dataWindowStart);
        final PPAFullResultEntry first = entries.get(0);
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
  
    protected long countFailed(List<PPAFullResultEntry> results) {
	return results.stream()
                .filter( r -> r.result == null || r.result.hasFailed())
                .count();
    }


    protected long countNeedsAttention(List<PPAFullResultEntry> results) {
        
        return results.stream()
                .filter( r -> r.result != null)
                .filter( r -> !r.result.isIgnored())
                .filter( r -> r.result.needsAttention())
                .count();
    }


    
    protected void saveJobFullStats(StatsEntry stats, AssayPack exp, PPAJobSummary job) {
	ppaRep.saveJobFullStats(stats, exp,job.jobId);
    }
    
    protected void saveJobSimpleStats(PPAJobSimpleStats stats, AssayPack exp, PPAJobSummary job) {
	ppaRep.saveJobSimpleStats(stats, exp, job.jobId);
    }
    
    
    protected void saveJobResultsGroups(PPAJobResultsGroups groupedResults, AssayPack exp, PPAJobSummary job) {
        ppaRep.saveJobResultsGroups(groupedResults, exp, job.jobId);
    }    

    protected void saveJobIndResults(List<PPAFullResultEntry> results, AssayPack exp, PPAJobSummary job) {
        ppaRep.saveJobIndResults(results,exp,job.jobId);
    }  
    
    protected void saveJobSimpleResults(PPAJobSimpleResults results, AssayPack exp, PPAJobSummary job) {
        ppaRep.saveJobSimpleResults(results,exp,job.jobId);
    }    

    protected void saveFits(List<PPAFullResultEntry> entries, UUID jobId, AssayPack exp) throws RobustDBException, RobustProcessException, IOException {

	Map<Long,TimeSeries> fits = new HashMap<>();
        for (PPAFullResultEntry entry : entries) {
            PPAResult result = entry.result;
            TimeSeries fit = result.getFit();
            if (fit != null)
                fits.put(entry.dataId, fit);
        }
        
        ppaRep.saveFits(fits,jobId,exp);
    }

    
    protected void clearFits(List<PPAFullResultEntry> entries) {
        entries.forEach( entry -> {
            if (entry.result != null)
                entry.result.setFit(null);
        });
    }    
    
    protected void clearFits(JobResults<TSResult<PPAResult>> results) {
        results.results.forEach( entry -> {
            if (entry.result != null)
                entry.result.setFit(null);
        });
    }
    


    protected void saveJob(PPAJobSummary job, AssayPack exp) {
        ppaRep.saveJobSummary(job, exp);
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

    protected ListMap<ComplexId<Long>, PPAFullResultEntry> groupResults(List<PPAFullResultEntry> entries) {
	ListMap<ComplexId<Long>, PPAFullResultEntry> groups = new ListMap<>();
        
        for (PPAFullResultEntry entry : entries) {
            if (entry.ignored) continue;
    
            ComplexId<Long> key = new ComplexId<>(entry.biolDescId,entry.environmentId);
            groups.add(key, entry);
        }
        
        groups.values().forEach( list -> list.sort(Comparator.comparing( entry -> entry.dataId)));
        
	return groups;
        
    }

    protected ListMap<ComplexId<Long>, PPAResult> filterResultsForStats(ListMap<ComplexId<Long>, PPAFullResultEntry> resultsGroups) {
	ListMap<ComplexId<Long>, PPAResult> groups = new ListMap<>();
        
        resultsGroups.entrySet().forEach( entry -> {
        
            ComplexId<Long> key = entry.getKey();
            
            List<PPAResult> ppas = entry.getValue().stream()
                    .map( e -> e.result)
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

    public long doPPASelection(PPAJobSummary summary, AssayPack exp, Map<Long, Integer> selection) {

        PPAJobSummary job = ppaRep.getJobSummary(exp, summary.jobId).orElseThrow(() -> new HandlingException("JobSummary: "+summary.jobId+" cannot be found in biodare"));
        List<PPAFullResultEntry> jobIndResults = ppaRep.getJobIndResults(exp, job.jobId);
        jobIndResults = applyUserResultsSelection(jobIndResults, selection);
        
        processJobResults(jobIndResults, job, exp);
        
        updateSelectedJobState(job, jobIndResults, selection);
        
        saveJob(job,exp);
        return job.attentionCount;
        
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
            List<PPAFullResultEntry> jobIndResults = ppaRep.getJobIndResults(exp, job.jobId);
            jobIndResults.sort(Comparator.comparing( e -> e.dataId));
            
            jobIndResults.stream().filter( e -> e.rawDataId == 0L).forEach( e -> {e.rawDataId = e.dataId;});
            
            //JobSummary desc = ppaRep.getJobFullDescription(exp, job.jobId).get();
            
            //logger.debug("J:"+job.jobId+"\n"+jobIndResults.stream().map(e -> ""+e.rawDataId).collect(Collectors.joining(",")));
            processJobResults(jobIndResults, job, exp);
            
        };

        return finishedJobs.size();
    }
    
    
    
    
    
    protected String mapSelection2String(Map<Long, Integer> selection) {
        
        return selection.entrySet().stream()
                .map( entry -> entry.getKey().toString()+":"+entry.getValue().toString())
                .collect(Collectors.joining(";"));
    }
    
    protected List<PPAFullResultEntry> applyUserResultsSelection(List<PPAFullResultEntry> entries, Map<Long, Integer> selectedValues) {
	List<PPAFullResultEntry> updatedResults = new ArrayList<>();


	    for (PPAFullResultEntry entry : entries) {

                Integer selection = selectedValues.get(entry.dataId);
		if (selection == null) {
		    updatedResults.add(entry);
		} else {
		    updatedResults.add(updateEntrySelection(entry,selection));
		}

	    }
            
	return updatedResults;
    }
    
    
    
    protected PPAFullResultEntry updateEntrySelection(PPAFullResultEntry entry, int selected) {

	PPAFullResultEntry updated = new PPAFullResultEntry();
        updated.biolDescId = entry.biolDescId;
        updated.dataId = entry.dataId;
        updated.dataType = entry.dataType;
        updated.environmentId = entry.environmentId;
        updated.ignored = entry.ignored;
        updated.jobId = entry.jobId;
        updated.orgId = entry.orgId;
        updated.ignored = entry.ignored;
        updated.rawDataId = entry.rawDataId;
	updated.result = updateResultSelection(entry.result,selected);

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

    protected PPAJobSimpleStats simplifyJobStats(List<PPAStats> fullStats,PPAJobSummary job) {
        
        PPAJobSimpleStats stats = new PPAJobSimpleStats(job.jobId);
        double dw = job.dataWindowStart;
        
        fullStats.forEach( entry -> {
            PPASimpleStats st = simplifyStats(entry,dw);
            //st.memberDataId = entry.getKey();
            stats.stats.add(st);
        });

        return stats;
    }



    protected PPAJobSimpleResults simplifyJobResults(List<PPAFullResultEntry> entries, PPAJobSummary job) {
        PPAJobSimpleResults res = new PPAJobSimpleResults(job.jobId);
        
        //final long jobId = job.getJobId();
        final double windowStart = job.dataWindowStart;
        
        entries.forEach( entry -> {
            PPASimpleResultEntry simple = PPAUtilsJC2.simplifyResultsEntry(entry,windowStart);
            res.results.add(simple);
        });
        
        return res;
    }


}
