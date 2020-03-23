/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare.jobcentre2.dom.JobResults;
import ed.biodare.jobcentre2.dom.PPAJobResults;
import ed.biodare.jobcentre2.dom.TSDataSetJobRequest;
import ed.biodare.jobcentre2.dom.TSResult;
import ed.biodare2.backend.features.jobcentre2.JC2HandlingException;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.handlers.ArgumentException;
import ed.biodare2.backend.handlers.ExperimentHandler;
import ed.biodare2.backend.repo.dao.PPAArtifactsRep;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.ppa.PPARequest;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.ui_dom.ppa.PPAFitPack;
import ed.biodare2.backend.repo.ui_dom.ppa.PPASelectGroup;
import ed.biodare2.backend.util.io.FileUtil;
import ed.biodare2.backend.web.rest.NotFoundException;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.dom.tsprocessing.PhaseType;
import ed.robust.dom.tsprocessing.ResultsEntry;
import ed.robust.dom.tsprocessing.StatsEntry;
import ed.robust.dom.util.Pair;
import ed.robust.jobcenter.dom.state.State;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    
    final ExperimentHandler experimentHandler;
    final PPAArtifactsRep ppaRep;
    final TSDataHandler dataHandler;
    final PPAUtils ppaUtils;
    final PPAJC2AnalysisService ppaService;
    final PPAJC2ResultsHandler ppaResultsHandler;
    final FileUtil fileUtil;

    
    @Autowired
    public PPAJC2Handler(ExperimentHandler experimentHandler,PPAArtifactsRep ppaRep,
            PPAJC2AnalysisService ppaService,TSDataHandler dataHandler,
            PPAJC2ResultsHandler ppaResultsHandler) {
        
        this.ppaRep = ppaRep;
        this.ppaService = ppaService;
        this.dataHandler = dataHandler;
        this.ppaResultsHandler = ppaResultsHandler; 
        this.experimentHandler = experimentHandler;
        this.ppaUtils = new PPAUtils();
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
    
    public PPAJobResultsGroups getPPAJobResultsGrouped(AssayPack exp, String jobId) {
        //for checking jobId
        PPAJobSummary summary = tryToFindJobSummary(exp,jobId);
        
        FakeIdExtractor idsCache = idsCache(exp, DetrendingType.valueOf(summary.dataSetType));
        
        PPAJobResultsGroups replicates = ppaRep.getJobResultsGroups(exp,jobId);
        
        return ppaUtils.convertToUIPPAJobResultsGroups(replicates,idsCache);
        
    }
    
    public PPAJobSimpleStats getPPAJobSimpleStats(AssayPack exp, String jobId) {
        //for checking jobId
        PPAJobSummary job = tryToFindJobSummary(exp,jobId);

        FakeIdExtractor idsCache = idsCache(exp, DetrendingType.valueOf(job.dataSetType));
        
        PPAJobSimpleStats stats = ppaRep.getJobSimpleStats(exp,jobId);
        
        return ppaUtils.convertToUIPPAJobSimpleStats(stats,job,idsCache);
    } 
    
    public PPAJobSimpleResults getPPAJobSimpleResults(AssayPack exp, String jobId) {
        //for checking jobId
        PPAJobSummary summary = tryToFindJobSummary(exp,jobId);
        
        FakeIdExtractor idsCache = idsCache(exp, DetrendingType.valueOf(summary.dataSetType));
        
        PPAJobSimpleResults res = ppaRep.getJobSimpleResults(exp, jobId);
        return ppaUtils.convertToUIPPAJobSimpleResults(res, idsCache);
    }    
    
    public List<PPASelectGroup> getPPAForSelect(AssayPack exp, String jobId) {
        
        //for checking jobId
        PPAJobSummary summary = tryToFindJobSummary(exp,jobId);

        FakeIdExtractor idsCache = idsCache(exp, DetrendingType.valueOf(summary.dataSetType));
        
        List<ResultsEntry> innerResults = ppaRep.getJobIndResults(exp, jobId);
        
        List<PPASelectGroup> list = ppaUtils.convertToUIPPAForSelect(innerResults,idsCache);
        return ppaUtils.sortByUIImportance(list);
    }  

    public long doPPASelection(AssayPack exp, String jobId, Map<String, String> selectionParams) throws IOException {
        PPAJobSummary summary = tryToFindJobSummary(exp, jobId);
        
        Map<Long, Integer> selection = parseSelectionParams(selectionParams);
        return ppaResultsHandler.doPPASelection(summary, exp, selection);
    }

    public PPAJobSummary deletePPAJob(AssayPack exp, String jobId) {

        PPAJobSummary job = tryToFindJobSummary(exp, jobId);        
        ppaRep.deleteJobArtefacts(exp,jobId);
        return job;
        
    }   
    
    public PPAFitPack getDataFit(AssayPack exp, String jobId, long dataId, boolean selectable) {
        
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
    
    public Path exportPPAJob(AssayPack exp, String jobId,PhaseType phaseType) {
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

    public Path packResults(AssayPack exp) throws IOException {
        
        List<PPAJobSummary> jobs = ppaRep.getJobsSummaries(exp).stream()
                .filter( job -> job.state.equals(State.FINISHED))
                .collect(Collectors.toList());
                
        if (jobs.isEmpty()) throw new NotFoundException("No finished jobs fould in exp: "+exp.getId());
        
        Set<String> types = jobs.stream().map(j -> j.dataSetType).collect(Collectors.toSet());
        Map<String,FakeIdExtractor> idsCaches = idsCacheByType(exp,types);        

        Map<Long, List<ResultsEntry>> innerResults = dataJoinedFullResults(exp, jobs); 
        
        List<StatsEntry> innerStats = jobs.stream()
                .map( job -> ppaRep.getJobFullStats(exp, job.uuid))
                .collect(Collectors.toList());
        
        
        
        Map<String,Path> files = ppaUtils.saveToFiles2(jobs, innerResults, innerStats, exp.getAssay(), idsCaches);
        
        try {
            Path pack = fileUtil.zip(files,Files.createTempFile(null, null));
            return pack;
        } catch (IOException e) {
            throw new ServerSideException("Cannot created tmp zip file "+e.getMessage(),e);
        } finally {
            for (Path p : files.values()) Files.delete(p);
        }
    }
    
    
    protected Map<Long, List<ResultsEntry>> dataJoinedFullResults(AssayPack exp,List<PPAJobSummary> jobs) {
     
        Map<Long, List<ResultsEntry>> innerResults = jobs.parallelStream()
                .filter( job -> job.state.equals(State.FINISHED))
                .flatMap( job -> ppaRep.getJobIndResults(exp, job.uuid).parallelStream())
                .collect(Collectors.groupingBy( entry -> entry.rawDataId));

        return innerResults;
        
    }    
    
    protected Map<String,FakeIdExtractor> idsCacheByType(AssayPack exp,Collection<String> types) {
        Map<String,FakeIdExtractor> map = new HashMap<>();
        types.forEach( type -> {
            map.put(type,idsCache(exp, DetrendingType.valueOf(type)));
        });
        return map;
    }    
    
    protected TimeSeries getFit(AssayPack exp, String jobId, long dataId) {

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
    
    
    protected PPAResult getPPAResult(AssayPack exp, String jobId, long dataId) throws NotFoundException {
        
        List<ResultsEntry> innerResults = ppaRep.getJobIndResults(exp, jobId);
        
        return innerResults.stream()
                .filter( r -> r.dataId == dataId)
                .findFirst()
                .map( r -> r.getResult())
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
    
    protected PPAJobSummary tryToFindJobSummary(AssayPack exp, String jobId) throws NotFoundException {
        return ppaRep.getJobSummary(exp,jobId)
                            .orElseThrow(() -> new NotFoundException("Job "+jobId+" not found"));
    }    

    protected FakeIdExtractor idsCache(AssayPack exp,DetrendingType detrending) {
        List<DataTrace> dataSet = dataHandler.getDataSet(exp,detrending)
                .orElseThrow( () -> new NotFoundException("No TS data in "+exp.getId()));        
        FakeIdExtractor idsCache = new FakeIdExtractor(dataSet);

        return idsCache;
    }    
    
}
