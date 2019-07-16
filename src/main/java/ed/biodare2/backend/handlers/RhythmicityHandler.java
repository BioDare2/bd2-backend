/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.handlers;

import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityRequest;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

import static ed.biodare.jobcentre2.dom.RhythmicityConstants.*;
import ed.biodare.jobcentre2.dom.TSDataSetJobRequest;
import ed.biodare2.backend.features.rhythmicity.RhythmicityService;
import ed.biodare2.backend.features.rhythmicity.RhythmicityUtils;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.repo.dao.RhythmicityArtifactsRep;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@Service
public class RhythmicityHandler {

    ExperimentHandler experimentHandler;
    RhythmicityArtifactsRep rhythmicityRep;
    
    TSDataHandler dataHandler;
    RhythmicityUtils utils;
    RhythmicityService rhythmicityService;
    
    @Transactional
    public UUID newRhythmicity(AssayPack exp, RhythmicityRequest request) throws ArgumentException {
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

    UUID submitJob(TSDataSetJobRequest jobRequest) {
        
        return rhythmicityService.submitJob(jobRequest);
    }
    
}
