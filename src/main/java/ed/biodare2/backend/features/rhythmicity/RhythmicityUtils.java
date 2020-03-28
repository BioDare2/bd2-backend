/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

import ed.biodare.jobcentre2.dom.JobStatus;
import ed.biodare.jobcentre2.dom.RhythmicityConstants;
import ed.biodare.jobcentre2.dom.TSDataSetJobRequest;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityRequest;
import java.util.List;
import static ed.biodare.jobcentre2.dom.RhythmicityConstants.*;
import static ed.biodare.jobcentre2.dom.RhythmicityConstants.BD2EJTK_PRESETS.*;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare2.backend.features.tsdata.TSUtil;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import static ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class RhythmicityUtils {

    static final int DEFAULT_NULL_SIZE = 100_000;
    
    public TSDataSetJobRequest prepareJobRequest(long expId, RhythmicityRequest request, List<DataTrace>  dataSet) {
                
        TSDataSetJobRequest job = new TSDataSetJobRequest();
        job.externalId = ""+expId;
        job.method = request.method;
        job.data = TSUtil.prepareTSData(dataSet, request.windowStart, request.windowEnd);
        job.parameters = prepareParameters(request);
        return job;
        
    }


    Map<String, String> prepareParameters(RhythmicityRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("METHOD", request.method);
        params.put(PRESET_KEY, request.preset);
        if (!request.method.equals(RhythmicityConstants.RHYTHMICITY_METHODS.BD2JTK.name())) {
            params.put(NULL_SIZE_KEY, ""+DEFAULT_NULL_SIZE);
        } 
        params.put(PERIOD_MIN_KEY, ""+request.periodMin);
        params.put(PERIOD_MAX_KEY, ""+request.periodMax);
        return params;
    }

    public RhythmicityJobSummary prepareNewJobSummary(TSDataSetJobRequest jobRequest, RhythmicityRequest request, long expId) {
        
        RhythmicityJobSummary job = new RhythmicityJobSummary();
        job.parentId = expId;
        job.jobStatus = new JobStatus(null,State.SUBMITTED);
        
        
        job.parameters = new HashMap<>(jobRequest.parameters);
        
	job.parameters.put(DW_START, ""+request.windowStart);
	job.parameters.put(DW_END,""+request.windowEnd);
	job.parameters.put(DATA_SET_TYPE, request.detrending.name());
	job.parameters.put(DATA_SET_TYPE_NAME,request.detrending.longName);
	job.parameters.put(DATA_SET_ID,expId+"_"+request.detrending.name());


	String summary = request.detrending.longName+" ";
	if (request.windowStart == 0) summary+="min"; else summary+=request.windowStart;
	summary+="-";
	if (request.windowEnd == 0) summary+="max"; else summary+=request.windowEnd;
        
        if (request.periodMin == request.periodMax) {
            summary+=" p("+request.periodMin+")";
        } else {
            summary+=" p("+request.periodMin+"-"+request.periodMax+")";            
        }
	job.parameters.put(PARAMS_SUMMARY,summary);
        
        return job;
    }

    public void completeRequest(RhythmicityRequest request) {
        
        if (EJTK_CLASSIC.name().equals(request.preset)) {
            request.periodMin = 24;
            request.periodMax =24;
        }
        
        if (BD2_CLASSIC.name().equals(request.preset)) {
            request.periodMin = 18;
            request.periodMax = 35;
        }
        
        if (COS_1H.name().equals(request.preset) || COS_2H.name().equals(request.preset)
                || COS_4H.name().equals(request.preset)) {
            request.periodMin = 24;
            request.periodMax =24;            
        }
        
        
    }
    
    
}
