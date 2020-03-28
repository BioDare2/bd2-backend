/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare.jobcentre2.dom.JobStatus;
import ed.biodare.jobcentre2.dom.PPAJobResults;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare.jobcentre2.dom.TSDataSetJobRequest;
import ed.biodare2.backend.features.jobcentre2.JC2HandlingException;
import ed.biodare2.backend.features.ppa.dao.PPAArtifactsRepJC2;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.handlers.ArgumentException;
import ed.biodare2.backend.handlers.ExperimentHandler;

import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.ppa.PPARequest;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAFullResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary;

import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.ui_dom.ppa.PPAFitPack;
import ed.biodare2.backend.repo.ui_dom.ppa.PPASelectGroup;
import ed.biodare2.backend.util.io.FileUtil;
import ed.biodare2.backend.web.rest.HandlingException;
import ed.biodare2.backend.web.rest.NotFoundException;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.dom.tsprocessing.PhaseType;
import ed.robust.dom.tsprocessing.StatsEntry;
import ed.robust.dom.util.Pair;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author tzielins
 */
@Service
public class PPAJC2Handler {

    static int OLD_MINUTES = 5;
    
    final ExperimentHandler experimentHandler;
    final PPAArtifactsRepJC2 ppaRep;
    final TSDataHandler dataHandler;
    final PPAUtilsJC2 ppaUtils;
    final PPAJC2AnalysisService ppaService;
    final PPAJC2ResultsHandler ppaResultsHandler;
    final FileUtil fileUtil;

    
    @Autowired
    public PPAJC2Handler(ExperimentHandler experimentHandler,PPAArtifactsRepJC2 ppaRep,
            PPAJC2AnalysisService ppaService,TSDataHandler dataHandler,
            PPAJC2ResultsHandler ppaResultsHandler) {
        
        this.ppaRep = ppaRep;
        this.ppaService = ppaService;
        this.dataHandler = dataHandler;
        this.ppaResultsHandler = ppaResultsHandler; 
        this.experimentHandler = experimentHandler;
        this.ppaUtils = new PPAUtilsJC2();
        this.fileUtil = new FileUtil();
    }    
    
    public UUID newPPA(AssayPack exp, PPARequest ppaRequest) throws ArgumentException, JC2HandlingException {
        
        if (!ppaRequest.isValid()) throw new ArgumentException("Not valid ppaRequest");
        
        Optional<List<DataTrace>> dataSet = dataHandler.getDataSet(exp,ppaRequest.detrending);
        if (!dataSet.isPresent()) throw new ArgumentException("Missing data set in the experiment");
        
        TSDataSetJobRequest jobRequest = ppaUtils.prepareJC2JobRequest(exp.getId(), ppaRequest, dataSet.get());
        checkRequestSanity(jobRequest);
        
        UUID jobHandle = submitJob(jobRequest);
        PPAJobSummary summary = ppaUtils.prepareNewPPAJobSummary(exp.getId(), ppaRequest, jobHandle);
        
        ppaRep.saveJobSummary(summary, exp);
        experimentHandler.updateHasPPAJobs(exp, true);
        
        return jobHandle;
    }

    void checkRequestSanity(TSDataSetJobRequest jobRequest) {
        
    }

    UUID submitJob(TSDataSetJobRequest jobRequest) throws JC2HandlingException {
        return ppaService.submitJob(jobRequest);
    }
    
    
    
    public void handleResults(AssayPack exp, PPAJobResults results) {
        ppaResultsHandler.handleResults(exp, results);
    }  
    
    public List<PPAJobSummary> getPPAJobs(AssayPack exp) {
        
        return ppaRep.getJobsSummaries(exp);
    } 
    
    public PPAJobSummary getPPAJob(AssayPack exp, UUID jobId) {
        PPAJobSummary summary = tryToFindJobSummary(exp, jobId);
        
        if (isRunning(summary.state)) summary = refreshJob(summary,exp);
        return summary;
    }    
    
    public PPAJobResultsGroups getPPAJobResultsGrouped(AssayPack exp, UUID jobId) {
        //for checking jobId
        PPAJobSummary summary = tryToFindJobSummary(exp,jobId);
        
        FakeIdExtractor idsCache = idsCache(exp, DetrendingType.valueOf(summary.dataSetType));
        
        PPAJobResultsGroups replicates = ppaRep.getJobResultsGroups(exp,jobId);
        
        return ppaUtils.convertToUIPPAJobResultsGroups(replicates,idsCache);
        
    }
    
    public PPAJobSimpleStats getPPAJobSimpleStats(AssayPack exp, UUID jobId) {
        //for checking jobId
        PPAJobSummary job = tryToFindJobSummary(exp,jobId);

        FakeIdExtractor idsCache = idsCache(exp, DetrendingType.valueOf(job.dataSetType));
        
        PPAJobSimpleStats stats = ppaRep.getJobSimpleStats(exp,jobId);
        
        return ppaUtils.convertToUIPPAJobSimpleStats(stats,job,idsCache);
    } 
    
    public PPAJobSimpleResults getPPAJobSimpleResults(AssayPack exp, UUID jobId) {
        //for checking jobId
        PPAJobSummary summary = tryToFindJobSummary(exp,jobId);
        
        FakeIdExtractor idsCache = idsCache(exp, DetrendingType.valueOf(summary.dataSetType));
        
        PPAJobSimpleResults res = ppaRep.getJobSimpleResults(exp, jobId);
        return ppaUtils.convertToUIPPAJobSimpleResults(res, idsCache);
    }    
    
    public List<PPASelectGroup> getPPAForSelect(AssayPack exp, UUID jobId) {
        
        //for checking jobId
        PPAJobSummary summary = tryToFindJobSummary(exp,jobId);

        FakeIdExtractor idsCache = idsCache(exp, DetrendingType.valueOf(summary.dataSetType));
        
        List<PPAFullResultEntry> innerResults = ppaRep.getJobIndResults(exp, jobId);
        
        List<PPASelectGroup> list = ppaUtils.convertToUIPPAForSelect(innerResults,idsCache);
        return ppaUtils.sortByUIImportance(list);
    }  

    public long doPPASelection(AssayPack exp, UUID jobId, Map<String, String> selectionParams) throws IOException {
        PPAJobSummary summary = tryToFindJobSummary(exp, jobId);
        
        Map<Long, Integer> selection = parseSelectionParams(selectionParams);
        return ppaResultsHandler.doPPASelection(summary, exp, selection);
    }
    
    public void clearPPA(AssayPack exp) throws IOException {
        ppaRep.clearAllPPAArtefacts(exp);
        experimentHandler.updateHasPPAJobs(exp,false);
    }    

    public PPAJobSummary deletePPAJob(AssayPack exp, UUID jobId) {

        PPAJobSummary job = tryToFindJobSummary(exp, jobId);        
        ppaRep.deleteJobArtefacts(exp,jobId);
        return job;
        
    }   
    
    public PPAFitPack getDataFit(AssayPack exp, UUID jobId, long dataId, boolean selectable) {
        
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
    
    public Path exportPPAJob(AssayPack exp, UUID jobId,PhaseType phaseType) {
        PPAJobSummary summary = tryToFindJobSummary(exp, jobId);
        
        if (!State.FINISHED.equals(summary.state) && !State.SUCCESS.equals(summary.state))
                throw new NotFoundException("No results found in job "+jobId+" in exp: "+exp.getId());
        
        FakeIdExtractor idsCache = idsCache(exp, DetrendingType.valueOf(summary.dataSetType));
        
        PPAJobSimpleResults results = ppaRep.getJobSimpleResults(exp, jobId);
        PPAJobSimpleStats stats = ppaRep.getJobSimpleStats(exp, jobId);
        
        PPAResultsExporterJC2 exporter = new PPAResultsExporterJC2();
        
        try {
            Path file = Files.createTempFile(null,null);
        
            exporter.exportPPAJob(exp.getAssay(),summary,results,stats,idsCache,phaseType,file);
        
            return file;
        } catch (IOException e) {
            throw new ServerSideException("Cannot create export file: "+e.getMessage(),e);
        }
        
    }  

    public Path exportFullPPAResults(AssayPack exp) throws IOException {
        
        List<PPAJobSummary> jobs = ppaRep.getJobsSummaries(exp).stream()
                .filter( job -> job.state.equals(State.FINISHED))
                .collect(Collectors.toList());
                
        if (jobs.isEmpty()) throw new NotFoundException("No finished jobs fould in exp: "+exp.getId());
        
        PPAResultsExporterJC2 exporter = new PPAResultsExporterJC2();
        FakeIdExtractor idsCache = idsCache(exp, DetrendingType.LIN_DTR);
        
        Map<String,Path> files = new HashMap<>();
        
        Path joinedResults = exportJoinedResults(exp, jobs, idsCache, tempFile(), exporter);
        files.put(""+exp.getId()+".results.csv", joinedResults);
        
        for (PPAJobSummary job : jobs) {
            Path file = exportPPAStats(exp, job, idsCache, tempFile(), exporter);
            files.put("statistics."+job.shortId()+".csv", file);
        }
        
        try {
            Path pack = fileUtil.zip(files,tempFile());
            return pack;
        } catch (IOException e) {
            throw new ServerSideException("Cannot created tmp zip file "+e.getMessage(),e);
        } finally {
            for (Path p : files.values()) Files.delete(p);
        }
    }
    
    
    protected Path tempFile() throws IOException {
        return Files.createTempFile(null, null);
    }
    
    protected TimeSeries getFit(AssayPack exp, UUID jobId, long dataId) {

        Map<Long,TimeSeries> fits = ppaRep.getFits(jobId, exp).orElseThrow(()->new NotFoundException("Fits for job "+jobId+" not found"));
        if (!fits.containsKey(dataId))
            throw new NotFoundException("Fit for data: "+dataId+" not found in job "+jobId);

        return fits.get(dataId);
        
    }    
    
    protected DataTrace getDataTrace(Optional<List<DataTrace>> dataSet,long dataId) {
        
        return dataSet.flatMap(
                    l -> l.stream().
                            filter(dt -> dt.dataId == dataId ).
                            findFirst()                            
                ).orElseThrow(()->new NotFoundException("Data trace: "+dataId+" not found"));        
    }    
    
    
    protected PPAResult getPPAResult(AssayPack exp, UUID jobId, long dataId) throws NotFoundException {
        
        List<PPAFullResultEntry> innerResults = ppaRep.getJobIndResults(exp, jobId);
        
        return innerResults.stream()
                .filter( r -> r.dataId == dataId)
                .findFirst()
                .map( r -> r.result)
                .orElseThrow(()-> new NotFoundException("PPAResult for data: "+dataId+" in job: "+jobId+" not found"));
                
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
    
    protected PPAJobSummary tryToFindJobSummary(AssayPack exp, UUID jobId) throws NotFoundException {
        return ppaRep.getJobSummary(exp,jobId)
                            .orElseThrow(() -> new NotFoundException("Job "+jobId+" not found"));
    }    

    protected FakeIdExtractor idsCache(AssayPack exp,DetrendingType detrending) {
        List<DataTrace> dataSet = dataHandler.getDataSet(exp,detrending)
                .orElseThrow( () -> new NotFoundException("No TS data in "+exp.getId()));        
        FakeIdExtractor idsCache = new FakeIdExtractor(dataSet);

        return idsCache;
    }  
    
    protected boolean isRunning(State state) {
        return State.SUBMITTED.equals(state) || State.PROCESSING.equals(state);
    }

    protected PPAJobSummary refreshJob(PPAJobSummary summary, AssayPack exp) {
        if (!isOldJob(summary)) return summary;
        
        try {
            JobStatus status = ppaService.getJobStatus(summary.jobId);
            
            summary.state = (status.state);
            summary.message = (status.message);
            summary.modified = LocalDateTime.now();  
            
            if (hasFailed(status)) {
                //ignore other status as they may get updated in the background
                summary = updateFailedJob(summary,status);
                ppaRep.saveJobSummary(summary, exp);

            }
            return summary; 
        } catch (JC2HandlingException e) {
            throw new HandlingException(e);
        }             
    }

    protected boolean isOldJob(PPAJobSummary job) {
        LocalDateTime old = LocalDateTime.now().minus(OLD_MINUTES, ChronoUnit.MINUTES);
        
        if (job.modified != null) {
            return job.modified.isBefore(old);
        }        
        return job.submitted.isBefore(old);
    }
    
    protected boolean hasFailed(JobStatus status) {
        return State.ERROR.equals(status.state) || State.FAILED.equals(status.state);
    }    
    
    protected PPAJobSummary updateFailedJob(PPAJobSummary job, JobStatus status) {
        

        //if (job.modified == null) job.modified = new Date();
        //if (job.completed == null) job.completed = new Date();
        job.completed = LocalDateTime.now();
        job.closed = true;
        job.lastError = status.message;
        //job.setLastError(status.getMessage());
        //job.setClosed(true);
        
        // PPAJobSummary summary = PPAUtils.simplifyJob(job);
        return job;
    }    

    Path exportJoinedResults(AssayPack exp, List<PPAJobSummary> jobs, FakeIdExtractor idsCache, Path tempFile, PPAResultsExporterJC2 exporter) throws IOException {
        
        List<PPAFullResultEntry> results = jobs.stream().flatMap( job -> ppaRep.getJobIndResults(exp, job.jobId).stream())
                .collect(Collectors.toList());
        exporter.exportJoinedFullResults(exp.getAssay(), jobs, results, idsCache, tempFile);
        return tempFile;
    }

    Path exportPPAStats(AssayPack exp, PPAJobSummary job, FakeIdExtractor idsCache, Path tempFile, PPAResultsExporterJC2 exporter) throws IOException {
        
        StatsEntry stats = ppaRep.getJobFullStats(exp, job.jobId);
        exporter.exportPPAFullStats(exp.getAssay(), job, stats, idsCache, tempFile);
        return tempFile;
    }


    
}
