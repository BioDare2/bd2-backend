/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

import ed.biodare.jobcentre2.dom.JobResults;
import ed.biodare.jobcentre2.dom.TSResult;
import ed.biodare2.backend.web.rest.NotFoundException;
import ed.biodare2.backend.repo.dao.PPAArtifactsRep;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.ppa.PPARequest;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.ui_dom.ppa.PPAFitPack;
import ed.biodare2.backend.repo.ui_dom.ppa.PPAResultsGroup;
import ed.biodare2.backend.repo.ui_dom.ppa.PPASelectGroup;
import ed.biodare2.backend.util.io.FileUtil;
import ed.biodare2.backend.features.ppa.FakeIdExtractor;
import ed.biodare2.backend.features.ppa.PPAAnalysisService;
import ed.biodare2.backend.features.ppa.PPAHandlingException;
import ed.biodare2.backend.features.ppa.PPAResultsExporter;
import ed.biodare2.backend.features.ppa.PPAResultsHandler;
import ed.biodare2.backend.features.ppa.PPAUtils;
import static ed.biodare2.backend.features.ppa.PPAUtils.simplifyJob;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPASimpleResultEntry;
import ed.biodare2.backend.web.rest.HandlingException;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.robust.jobcenter.dom.state.State;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.jobcenter.JobSummary;
import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.dom.tsprocessing.PhaseType;
import ed.robust.dom.tsprocessing.ResultsEntry;
import ed.robust.dom.tsprocessing.StatsEntry;
import ed.robust.dom.util.Pair;
import ed.robust.jobcenter.dom.job.JobHandle;
import ed.robust.jobcenter.dom.job.JobRequest;
import ed.robust.jobcenter.dom.job.JobResult;
import ed.robust.jobcenter.dom.state.Status;
import ed.robust.jobcenter.error.ConnectionException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@Service
public class PPAHandler {
 
    static int OLD_MINUTES = 5;
    
    final ExperimentHandler experimentHandler;
    final PPAArtifactsRep ppaRep;
    final TSDataHandler dataHandler;
    final PPAUtils ppaUtils;
    final PPAAnalysisService ppaService;
    final PPAResultsHandler ppaResultsHandler;
    final FileUtil fileUtil;
            
    @Autowired
    public PPAHandler(ExperimentHandler experimentHandler,PPAArtifactsRep ppaRep,
            PPAAnalysisService ppaService,TSDataHandler dataHandler,
            PPAResultsHandler ppaResultsHandler) {
        this.ppaRep = ppaRep;
        this.ppaService = ppaService;
        this.dataHandler = dataHandler;
        this.ppaUtils = new PPAUtils();
        this.ppaResultsHandler = ppaResultsHandler; 
        this.fileUtil = new FileUtil();
        this.experimentHandler = experimentHandler;
    }

    @Transactional
    public void clearPPA(AssayPack exp) throws IOException {
        ppaRep.clearAllPPAArtefacts(exp);
        experimentHandler.updateHasPPAJobs(exp,false);
    }    
    
    @Transactional
    public long newPPA(AssayPack exp, PPARequest ppaRequest) throws ArgumentException, IOException, ConnectionException, PPAHandlingException {
        
        if (!ppaRequest.isValid()) throw new ArgumentException("Not valid ppaRequest");
        
        Optional<List<DataTrace>> dataSet = dataHandler.getDataSet(exp,ppaRequest.detrending);
        if (!dataSet.isPresent()) throw new ArgumentException("Missing data set in the experiment");
        
        JobRequest jobRequest = ppaUtils.prepareJobRequest(exp.getId(), ppaRequest, dataSet.get());

        JobHandle jobHandle = submitJob(jobRequest);
        
        JobSummary job = ppaUtils.prepareNewJobSummary(jobHandle, jobRequest, ppaRequest);        
        ppaUtils.convertToLocalRequest(jobRequest,ppaRequest,jobHandle.getJobId());
        
        ppaRep.saveJobRequest(jobRequest,job.getJobId(),exp);
        PPAJobSummary summary = simplifyJob(job);
        ppaRep.saveJobDetails(job,summary,exp);
        
        experimentHandler.updateHasPPAJobs(exp,true);
        return jobHandle.getJobId();
    }

    protected JobHandle submitJob(JobRequest jobRequest) throws ConnectionException, PPAHandlingException {
        
        return ppaService.submitJob(jobRequest);
    }

    public void handleResults(long jobId, AssayPack exp, JobResult<PPAResult> results) {
        
        ppaResultsHandler.handleResults(jobId, exp, results);
    }

    public String serviceStatus() throws ConnectionException {
        return ppaService.serviceStatus();
    }

    public List<PPAJobSummary> getPPAJobs(AssayPack exp) {
        
        return ppaRep.getJobsSummaries(exp);
    }

    protected Map<Long, List<PPASimpleResultEntry>> dataJoinedSimpleResults(AssayPack exp,List<PPAJobSummary> jobs) {
     
        Map<Long, List<PPASimpleResultEntry>> innerResults = jobs.parallelStream()
                .filter( job -> job.state.equals(State.FINISHED))
                .flatMap( job -> ppaRep.getJobSimpleResults(exp, job.jobId).results.parallelStream())
                .collect(Collectors.groupingBy( entry -> entry.rawId));

        return innerResults;
        
    }
    
    protected Map<Long, List<ResultsEntry>> dataJoinedFullResults(AssayPack exp,List<PPAJobSummary> jobs) {
     
        Map<Long, List<ResultsEntry>> innerResults = jobs.parallelStream()
                .filter( job -> job.state.equals(State.FINISHED))
                .flatMap( job -> ppaRep.getJobIndResults(exp, job.jobId).parallelStream())
                .collect(Collectors.groupingBy( entry -> entry.rawDataId));

        return innerResults;
        
    }    
    
    
    public List<PPAResultsGroup> getJoinedPPAResults(AssayPack exp) throws IOException {
        
        List<PPAJobSummary> jobs = ppaRep.getJobsSummaries(exp);
        if (jobs.isEmpty()) return Collections.emptyList();
        
        Set<String> types = jobs.stream().map(j -> j.dataSetType).collect(Collectors.toSet());
        Map<String,FakeIdExtractor> idsCaches = idsCacheByType(exp, types);

        Map<Long, List<PPASimpleResultEntry>> innerResults = dataJoinedSimpleResults(exp, jobs);
        
        
        List<PPAResultsGroup> results = ppaUtils.convertToUIPPAResults(innerResults, jobs, idsCaches);
        results.sort( Comparator.comparing(e -> e.rawDataId));
        return results;
    }  
    
    public PPAJobSimpleStats getPPAJobSimpleStats(AssayPack exp, long jobId) {
        //for checking jobId
        PPAJobSummary job = tryToFindJobSummary(exp,jobId);

        FakeIdExtractor idsCache = idsCache(exp, DetrendingType.valueOf(job.dataSetType));
        
        PPAJobSimpleStats stats = ppaRep.getJobSimpleStats(exp,jobId);
        
        return ppaUtils.convertToUIPPAJobSimpleStats(stats,job,idsCache);
    }    
    
    
    public PPAJobResultsGroups getPPAJobResultsGrouped(AssayPack exp, long jobId) {
        //for checking jobId
        PPAJobSummary summary = tryToFindJobSummary(exp,jobId);
        
        FakeIdExtractor idsCache = idsCache(exp, DetrendingType.valueOf(summary.dataSetType));
        
        PPAJobResultsGroups replicates = ppaRep.getJobResultsGroups(exp,jobId);
        
        return ppaUtils.convertToUIPPAJobResultsGroups(replicates,idsCache);
        
    }
    
    
    public PPAJobSimpleResults getPPAJobSimpleResults(AssayPack exp, long jobId) {
        //for checking jobId
        PPAJobSummary summary = tryToFindJobSummary(exp,jobId);
        
        FakeIdExtractor idsCache = idsCache(exp, DetrendingType.valueOf(summary.dataSetType));
        
        PPAJobSimpleResults res = ppaRep.getJobSimpleResults(exp, jobId);
        return ppaUtils.convertToUIPPAJobSimpleResults(res, idsCache);
    }
    
    public List<PPASelectGroup> getPPAForSelect(AssayPack exp,long jobId) throws IOException {
        
        //for checking jobId
        PPAJobSummary summary = tryToFindJobSummary(exp,jobId);

        FakeIdExtractor idsCache = idsCache(exp, DetrendingType.valueOf(summary.dataSetType));
        
        List<ResultsEntry> innerResults = ppaRep.getJobIndResults(exp, jobId);
        
        List<PPASelectGroup> list = ppaUtils.convertToUIPPAForSelect(innerResults,idsCache);
        return ppaUtils.sortByUIImportance(list);
    }      

    
    protected DataTrace getDataTrace(Optional<List<DataTrace>> dataSet,long dataId) {
        
        return dataSet.flatMap(
                    l -> l.stream().
                            filter(dt -> dt.dataId == dataId ).
                            findFirst()                            
                ).orElseThrow(()->new NotFoundException("Data trace: "+dataId+" not found"));        
    }
    
    protected PPAResult getPPAResult(AssayPack exp, long jobId, long dataId) throws NotFoundException {
        
        List<ResultsEntry> innerResults = ppaRep.getJobIndResults(exp, jobId);
        
        return innerResults.stream()
                .filter( r -> r.dataId == dataId)
                .findFirst()
                .map( r -> r.getResult())
                .orElseThrow(()-> new NotFoundException("PPAResult for data: "+dataId+" in job: "+jobId+" not found"));
                
    }
    
    protected TimeSeries getFit(AssayPack exp, long jobId, long dataId) {

        Map<Long,TimeSeries> fits = ppaRep.getFits(jobId, exp).orElseThrow(()->new NotFoundException("Fits for job "+jobId+" not found"));
        if (!fits.containsKey(dataId))
            throw new NotFoundException("Fit for data: "+dataId+" not found in job "+jobId);

        return fits.get(dataId);
        
    }
    
    public PPAFitPack getDataFit(AssayPack exp, long jobId, long dataId, boolean selectable) {
        
        PPAJobSummary job = tryToFindJobSummary(exp,jobId);
        PPAResult ppaResult = getPPAResult(exp, jobId, dataId);
        
        
        DetrendingType detrending = DetrendingType.valueOf(job.dataSetType);
        
        Optional<List<DataTrace>> dataSet = dataHandler.getDataSet(exp,detrending);
        DataTrace dataTrace = getDataTrace(dataSet, dataId);
                       
        TimeSeries fit = getFit(exp, jobId, dataId);
        
        FakeIdExtractor idsCache = new FakeIdExtractor(dataSet.orElse(Collections.emptyList()));
        
        PPAFitPack pack = ppaUtils.convertToUIPPAFitPack(ppaResult,dataTrace,fit,idsCache,selectable);
        return pack;
    }
    
    public PPAJobSummary deletePPAJob(AssayPack exp, long jobId) {

        PPAJobSummary job = tryToFindJobSummary(exp, jobId);        
        ppaRep.deleteJobArtefacts(exp,jobId);
        return job;
        
    }
    
    protected JobSummary tryToFindJob(AssayPack exp, long jobId) throws NotFoundException {
        return ppaRep.findJob(jobId,exp)
                            .orElseThrow(() -> new NotFoundException("Job "+jobId+" not found"));
    }
    
    protected PPAJobSummary tryToFindJobSummary(AssayPack exp, long jobId) throws NotFoundException {
        return ppaRep.getJobSummary(exp,jobId)
                            .orElseThrow(() -> new NotFoundException("Job "+jobId+" not found"));
    }    
    
    public PPAJobSummary getPPAJob(AssayPack exp, long jobId) {
        PPAJobSummary summary = tryToFindJobSummary(exp, jobId);
        
        if (isRunning(summary.state)) summary = refreshJob(summary,exp);
        return summary;
    }
    
    protected FakeIdExtractor idsCache(AssayPack exp,DetrendingType detrending) {
        List<DataTrace> dataSet = dataHandler.getDataSet(exp,detrending)
                .orElseThrow( () -> new NotFoundException("No TS data in "+exp.getId()));        
        FakeIdExtractor idsCache = new FakeIdExtractor(dataSet);

        return idsCache;
    }
    
    protected Map<String,FakeIdExtractor> idsCacheByType(AssayPack exp,Collection<String> types) {
        Map<String,FakeIdExtractor> map = new HashMap<>();
        types.forEach( type -> {
            map.put(type,idsCache(exp, DetrendingType.valueOf(type)));
        });
        return map;
    }
    
    public Path exportPPAJob(AssayPack exp, long jobId,PhaseType phaseType) {
        PPAJobSummary summary = tryToFindJobSummary(exp, jobId);
        
        if (!State.FINISHED.equals(summary.state) && !State.SUCCESS.equals(summary.state))
                throw new NotFoundException("No results found in job "+jobId+" in exp: "+exp.getId());
        
        FakeIdExtractor idsCache = idsCache(exp, DetrendingType.valueOf(summary.dataSetType));
        
        PPAJobSimpleResults results = ppaRep.getJobSimpleResults(exp, jobId);
        PPAJobSimpleStats stats = ppaRep.getJobSimpleStats(exp, jobId);
        
        PPAResultsExporter exporter = new PPAResultsExporter();
        
        try {
            Path file = Files.createTempFile(null,null);
        
            exporter.exportPPAJob(exp.getAssay(),summary,results,stats,idsCache,phaseType,file);
        
            return file;
        } catch (IOException e) {
            throw new ServerSideException("Cannot create export file: "+e.getMessage(),e);
        }
        
    }      

    public int doPPASelection(AssayPack exp, long jobId, Map<String, String> selectionParams) throws IOException {
        PPAJobSummary summary = tryToFindJobSummary(exp, jobId);
        
        Map<Long, Integer> selection = parseSelectionParams(selectionParams);
        return ppaResultsHandler.doPPASelection(summary,exp,selection);
    }
    
    protected Map<Long, Integer> parseSelectionParams(Map<String, String> selectionParams) {
        
        return selectionParams.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("s_"))
                .filter(entry -> entry.getValue() != null)
                .map( entry -> {
                    long key = Long.parseLong(entry.getKey().substring(2));
                    int period = entry.getValue().equals("dismiss") ? -1 : Integer.parseInt(entry.getValue());
                    return new Pair<>(key,period);
                })
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }
    
  

    public Path packResults(AssayPack exp) throws IOException {
        
        List<PPAJobSummary> jobs = ppaRep.getJobsSummaries(exp).stream()
                .filter( job -> job.state.equals(State.FINISHED))
                .collect(Collectors.toList());
                
        if (jobs.isEmpty()) throw new NotFoundException("No finished jobs fould in exp: "+exp.getId());
        
        Set<String> types = jobs.stream().map(j -> j.dataSetType).collect(Collectors.toSet());
        Map<String,FakeIdExtractor> idsCaches = idsCacheByType(exp,types);        

        Map<Long, List<ResultsEntry>> innerResults = dataJoinedFullResults(exp, jobs); 
        
        List<StatsEntry> innerStats = jobs.stream()
                .map( job -> ppaRep.getJobFullStats(exp, job.jobId))
                .collect(Collectors.toList());
        
        List<JobSummary> fullJobs = jobs.stream()
                .map( job -> ppaRep.getJobFullDescription(exp, job.jobId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        
        
        Map<String,Path> files = ppaUtils.saveToFiles(fullJobs, innerResults, innerStats, exp.getAssay(), idsCaches);
        
        try {
            Path pack = fileUtil.zip(files,Files.createTempFile(null, null));
            return pack;
        } catch (IOException e) {
            throw new ServerSideException("Cannot created tmp zip file "+e.getMessage(),e);
        } finally {
            for (Path p : files.values()) Files.delete(p);
        }
    }

    protected boolean isRunning(State state) {
        return State.SUBMITTED.equals(state) || State.PROCESSING.equals(state);
    }
    
    protected boolean hasFailed(Status status) {
        return State.ERROR.equals(status.getState()) || State.FAILED.equals(status.getState());
    }
    

    protected PPAJobSummary refreshJob(PPAJobSummary summary, AssayPack exp) {
        if (!isOldJob(summary)) return summary;
        
        try {
            JobSummary job = tryToFindJob(exp, summary.jobId);
            Status status = ppaService.getJobStatus(job.getJob());
            if (hasFailed(status)) {
                return updateFailedJob(job,status,exp);
            }
            //ignore other status as they may get updated in the background
            return summary;
        } catch (PPAHandlingException e) {
            throw new HandlingException(e);
        }
    }

    protected boolean isOldJob(PPAJobSummary job) {
        Instant old = Instant.now().minus(OLD_MINUTES, ChronoUnit.MINUTES);
        
        if (job.modified != null) {
            return job.modified.toInstant().isBefore(old);
        }        
        return job.submitted.toInstant().isBefore(old);
    }

    protected PPAJobSummary updateFailedJob(JobSummary job, Status status, AssayPack exp) {
        job.getStatus().setState(status.getState());
        job.getStatus().setMessage(status.getMessage());
        job.getStatus().setModified(status.getModified() != null ? status.getModified() : new Date());
        job.getStatus().setCompleted(status.getCompleted() != null ? status.getCompleted() : new Date());
        job.setLastError(status.getMessage());
        //job.setClosed(true);
        
        PPAJobSummary summary = PPAUtils.simplifyJob(job);
        ppaRep.saveJobDetails(job,summary, exp);
        return summary;
    }













    
}
