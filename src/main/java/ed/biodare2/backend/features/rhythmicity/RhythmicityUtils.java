/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

import ed.biodare.jobcentre2.dom.JobStatus;
import ed.biodare.jobcentre2.dom.TSDataSetJobRequest;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityRequest;
import java.util.List;
import static ed.biodare.jobcentre2.dom.RhythmicityConstants.*;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare.jobcentre2.dom.TSData;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import static ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class RhythmicityUtils {

    static final int DEFAULT_NULL_SIZE = 1000;
    
    public TSDataSetJobRequest prepareJobRequest(long expId, RhythmicityRequest request, List<DataTrace>  dataSet) {
        
        TSDataSetJobRequest job = new TSDataSetJobRequest();
        job.externalId = ""+expId;
        job.method = request.method;
        job.data = prepareData(dataSet);
        job.parameters = prepareParameters(request);
        return job;
        
    }

    List<TSData> prepareData(List<DataTrace> dataSet) {
        
        return dataSet.stream()
                .map( dt -> trace2TSData(dt))
                .collect(Collectors.toList());
    }

    TSData trace2TSData(DataTrace trace) {
        TSData data = new TSData(trace.dataId, trace.trace);
        return data;
    }

    Map<String, String> prepareParameters(RhythmicityRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("METHOD", request.method);
        params.put(PRESET_KEY, request.preset);
        params.put(NULL_SIZE_KEY, ""+DEFAULT_NULL_SIZE);
        params.put(PERIOD_MIN_KEY, ""+request.periodMin);
        params.put(PERIOD_MAX_KEY, ""+request.periodMax);
        return params;
    }

    public RhythmicityJobSummary prepareNewJobSummary(UUID jobId, TSDataSetJobRequest jobRequest, RhythmicityRequest request, long expId) {
        
        RhythmicityJobSummary job = new RhythmicityJobSummary();
        job.jobId = jobId;
        job.jobStatus = new JobStatus(jobId, State.SUBMITTED);
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
	summary+=" p("+request.periodMin+"-"+request.periodMax+")";
	job.parameters.put(PARAMS_SUMMARY,summary);
        
        return job;
    }
    
    
}
