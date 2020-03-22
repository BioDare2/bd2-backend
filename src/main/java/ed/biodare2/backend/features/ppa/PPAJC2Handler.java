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
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.util.io.FileUtil;
import ed.robust.dom.tsprocessing.PPAResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
}
