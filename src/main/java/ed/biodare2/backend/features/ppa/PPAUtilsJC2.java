/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import static ed.biodare.jobcentre2.dom.PeriodConstants.PERIOD_MAX_KEY;
import static ed.biodare.jobcentre2.dom.PeriodConstants.PERIOD_MIN_KEY;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare.jobcentre2.dom.TSDataSetJobRequest;
import ed.biodare2.backend.features.tsdata.TSUtil;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.ppa.PPARequest;

import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAFullResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.ui_dom.ppa.PPAFitPack;



import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAResultsGroupSummary;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleStats;
import ed.biodare2.backend.repo.ui_dom.ppa.PPASelectGroup;
import ed.biodare2.backend.repo.ui_dom.ppa.PPASelectItem;
import ed.biodare2.backend.repo.ui_dom.shared.SimpleOption;
import ed.biodare2.backend.repo.ui_dom.tsdata.Trace;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.robust.dom.data.TimeSeries;

import ed.robust.dom.tsprocessing.CosComponent;
import ed.robust.dom.tsprocessing.FFT_PPA;
import ed.robust.dom.tsprocessing.PPA;
import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.dom.tsprocessing.PPAStats;
import ed.robust.dom.tsprocessing.PhaseRelation;
import ed.robust.dom.tsprocessing.PhaseType;

import ed.robust.dom.tsprocessing.ResultsGroup;
import ed.robust.dom.tsprocessing.Statistics;
import ed.robust.dom.tsprocessing.StatsEntry;
import ed.robust.dom.tsprocessing.WeightingType;
import ed.robust.dom.util.Pair;


import java.util.List;
import ed.biodare2.backend.util.TableBuilder;
import ed.robust.util.timeseries.TSGenerator;
import ed.robust.util.timeseries.TimeSeriesOperations;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 * @author tzielins
 */
public class PPAUtilsJC2 {

    /*
    protected static final String DATA_SET_ID = JobSummary.DATA_SET_ID;
    protected static final String DATA_SET_TYPE = JobSummary.DATA_SET_TYPE;
    protected static final String DATA_SET_TYPE_NAME = JobSummary.DATA_SET_TYPE_NAME;
    protected static final String DW_END = JobSummary.DW_END;
    protected static final String DW_START = JobSummary.DW_START;
    protected static final String METHOD_ID = JobSummary.METHOD_ID;
    protected static final String METHOD_NAME = JobSummary.METHOD_NAME;
    protected static final String PARAMS_SUMMARY = JobSummary.PARAMS_SUMMARY;   
    */
    public static final String JOB_ID = "JOB_ID";
    public static final String ID_SEPARATOR = ",";
    public static final String DATA_IDS = "DATA_IDS";



    
    protected final String SEP = ",";
    protected final String ESC = "\"";
    
    

    
    
    protected String dataType(PPARequest ppaRequest) {
        return ppaRequest.detrending.name();
    }
    
    protected String dataTypeLabel(PPARequest ppaRequest) {
        return ppaRequest.detrending.longName;
    }    
    
    
    
    
 
    
    public PPAJobSimpleStats convertToUIPPAJobSimpleStats(PPAJobSimpleStats stats, PPAJobSummary job, FakeIdExtractor idsCache) {
        
        stats.stats.forEach( stat -> {
            stat.label = idsCache.getBioLabel(stat.bioId);
        });
        return stats;
    }
    

    
    
    protected PPASimpleStats addStatLabel(PPASimpleStats stats, FakeIdExtractor idsCache) {
        stats.label = idsCache.getBioLabel(stats.bioId);
        return stats;
    }
    
    
    
    protected PPASimpleStats convertToUIStat(PPAStats stats,double windowStart,FakeIdExtractor idsCache) {
        
        PPASimpleStats sum = simplifyStats(stats, windowStart);
        
        sum.label = idsCache.getBioLabel(stats.getBiolDescId());
        return sum;
        
    }
    
    
    public static PPASimpleResultEntry simplifyResultsEntry(PPAFullResultEntry org, double windowStart) {
        
        PPAResult result = org.result;

        PPASimpleResultEntry simple = new PPASimpleResultEntry();
        
        simple.jobId = org.jobId;
        simple.dataId = org.dataId;
        simple.rawId = org.rawDataId;      
        simple.bioId = org.biolDescId;
        simple.envId = org.environmentId;    
        simple.dataType = org.dataType;    
        simple.orgId = org.orgId;
        simple.message = result.hasFailed() ? result.getMessage() : null;        

        simple.ERR = roundToCenty(result.getPPAMethodSpecific().getJoinedError());
        simple.GOF = roundToCenty(result.getPPAMethodSpecific().getGOF());
        
        simple.failed = result.hasFailed();
        simple.circadian = result.isCircadian();
        simple.ignored = result.isIgnored();
        simple.attention = result.needsAttention();
        
        
        simple.period = roundToCenty(result.getPeriod());
        simple.periodErr = roundToCenty(result.getPPAMethodSpecific().getPeriodError());
        
        for (PhaseType type : PhaseType.values()) {
            simple.amplitude.put(type, roundToCenty(result.getPPA(type).getAmplitude()));
            simple.phaseToZero.put(type, roundToCenty(result.getPPA(type).getPhase()));
            simple.phaseToWindow.put(type, roundToCenty(relativePhase(result.getPPA(type).getPhase(),result.getPeriod(),windowStart)));
            simple.phaseToZeroCirc.put(type, roundToCenty(24*result.getPPA(type).getPhase()/result.getPeriod()));
            simple.phaseToWindowCirc.put(type, roundToCenty(24*relativePhase(result.getPPA(type).getPhase(),result.getPeriod(),windowStart)/result.getPeriod()));
        }        
        simple.amplitudeErr = result.getPPAMethodSpecific().getAmplitudeError();
        simple.phaseErr = roundToCenty(result.getPPAMethodSpecific().getPhaseError());
        simple.phaseCircErr = circadianPhase(result.getPPAMethodSpecific().getPhaseError(), result.getPeriod());
        
        return simple;
    }
    
    public static PPASimpleStats simplifyStats(PPAStats stats, double windowStart) {
        
        WeightingType weight = WeightingType.None;
        
        PPASimpleStats sum = new PPASimpleStats();
        
        sum.bioId = stats.getBiolDescId();
        sum.envId = stats.getEnvironmentId();
        sum.memberDataId = stats.getMemberDataId();
        sum.rawId = stats.getRawId();
        
        sum.ERR = roundToCenty(stats.getJoinedError().getMean());
        sum.GOF= roundToCenty(stats.getGOF().getMean());
        sum.N = stats.getPeriod(weight).getN();
        sum.period = roundToCenty(stats.getPeriod(weight).getMean());
        sum.periodStd = roundToCenty(stats.getPeriod(weight).getStdDev());
        
        for (PhaseType type : PhaseType.values()) {
            sum.amplitude.put(type, roundToCenty(stats.getAmpStats(type).getMean(weight)));
            sum.amplitudeStd.put(type, roundToCenty(stats.getAmpStats(type).getStdDev(weight)));
            
            sum.phaseToZero.put(type, roundToCenty(stats.getPhase(type, weight).getMean()));
            sum.phaseToWindow.put(type, roundToCenty(relativePhase(stats.getPhase(type, weight).getMean(),sum.period,windowStart)));

            sum.phaseStd.put(type, roundToCenty(stats.getPhase(type, weight).getStdDev()));
            
            sum.phaseToZeroCirc.put(type, roundToCenty(stats.getPhaseCirc(type, weight).getMean()));
            sum.phaseToWindowCirc.put(type, roundToCenty(relativePhase(stats.getPhaseCirc(type, weight).getMean()*sum.period/24.0,sum.period,windowStart)*24/sum.period));
            
            sum.phaseCircStd.put(type, roundToCenty(stats.getPhaseCirc(type, weight).getStdDev()));
            
        }
        return sum;
    }    

    
    /*
    public List<PPAResultsGroupJC2> convertToUIPPAResults(Map<Long, List<PPASimpleResultEntry>> groups, List<PPAJobSummary> jobs,
            Map<String,FakeIdExtractor> idsCaches) {

        
        return groups.values().parallelStream()
                    .map (gr -> convertToUIResultsGroup(gr,jobs,idsCaches))
                    .collect(Collectors.toList());
        
    }
    
    

    protected PPAResultsGroupJC2 convertToUIResultsGroup(List<PPASimpleResultEntry> entryGroup,List<PPAJobSummary> jobs,
            Map<String,FakeIdExtractor> idsCaches) {
        
        Map<UUID,PPAJobSummary> jobsMap = jobs.stream().collect(Collectors.toMap( j -> j.jobId, j -> j));
        
        PPAResultsGroupJC2 ui = new PPAResultsGroupJC2();

        ui.results = entryGroup.stream()
                    .map( e -> {
                        final PPAJobSummary job = jobsMap.get(e.jobId);
                        e.jobSummary = job.method+" "+job.summary;
                        return e;
                    })
                    .sorted(Comparator.comparing( (PPASimpleResultEntry e) -> e.jobId).reversed())
                    .collect(Collectors.toList());

        PPASimpleResultEntry first = ui.results.get(0);
        FakeIdExtractor idsCache = idsCaches.get(jobsMap.get(first.jobId).dataSetType);
        
        ui.label = getLabel(first, idsCache);
        ui.dataRef = idsCache.getDataRef(first.dataId);
        ui.rawDataId = first.rawId;
        return ui;
    }*/
    
    
    protected String getLabel(ResultsGroup group,FakeIdExtractor idsCache) {
        String bioLabel = idsCache.getBioLabel(group.getBiolDescId());
        String condLabel = idsCache.getCondLabel(group.getEnvironmentId());
        return bioLabel.equals(condLabel) ? bioLabel : bioLabel+"; "+condLabel;       
    }
    
    protected String getLabel(PPAFullResultEntry entry,FakeIdExtractor idsCache) {
        String bioLabel = idsCache.getBioLabel(entry.biolDescId);
        String condLabel = idsCache.getCondLabel(entry.environmentId);
        return bioLabel.equals(condLabel) ? bioLabel : bioLabel+"; "+condLabel;       
    }    
    
    protected String getLabel(PPASimpleResultEntry entry,FakeIdExtractor idsCache) {
        String bioLabel = idsCache.getBioLabel(entry.bioId);
        String condLabel = idsCache.getCondLabel(entry.envId);
        return bioLabel.equals(condLabel) ? bioLabel : bioLabel+"; "+condLabel;       
    }        
    
    
    
    
    public static double roundToCenty(double nr) {
        return Math.rint(nr*100.0)/100;
    }
    
    public static double relativePhase(double phase,double period, double phaseRelation) {
        if (phaseRelation == 0) return roundToCenty(phase);

        double reference = phaseRelation % period;
        double val = phase - reference;
        if (val < 0) val = val+period;
        return roundToCenty(val % period);
        
    }   
    
    public static double relativePhase(PPA ppa, double phaseRelation) {
        return relativePhase(ppa.getPhase(),ppa.getPeriod(), phaseRelation);
    }

    public static double circadianRelativePhase(PPA ppa, double phaseRelation) {
        return circadianPhase(relativePhase(ppa, phaseRelation),ppa.getPeriod());
    }
    
    public static double circadianPhase(double phase,double period) {
        return roundToCenty(phase*24/period);
    }
    
    public static double circadianPhase(PPA ppa) {
        return circadianPhase(ppa.getPhase(),ppa.getPeriod());
    }
    

    public List<PPASelectGroup> convertToUIPPAForSelect(List<PPAFullResultEntry> entries, FakeIdExtractor idsCache) {
        
        return entries.stream()
                    .filter( e-> e.result != null)
                    .filter( e -> !e.result.hasFailed())
                    .map( entry -> buildSelectGroup(entry,idsCache))
                    .collect(Collectors.toList());

    }
    


    protected PPASelectGroup buildSelectGroup(PPAFullResultEntry result, FakeIdExtractor idsCache) {
        final PhaseType phaseType = PhaseType.ByFit;
        
        PPASelectGroup entry = new PPASelectGroup();
        entry.dataId = result.dataId;
        entry.dataRef = idsCache.getDataRef(result.dataId);
	entry.label = getLabel(result, idsCache);
        

	entry.periods = new ArrayList<>();

	PPAResult ppaResult = result.result;
	if (ppaResult instanceof FFT_PPA) {
	    FFT_PPA fftResult = (FFT_PPA)ppaResult;
	    for (CosComponent comp : fftResult) {
		entry.periods.add(buildItem(comp,comp.getPPA(phaseType).getGOF()));
	    }
	} else {
	    entry.periods.add(buildItem(ppaResult.getPPA(phaseType),ppaResult.getPPA(phaseType).getGOF()));
	}

        entry.isCircadian = ppaResult.isCircadian();
	entry.isIgnored = ppaResult.isIgnored();
	entry.needsAttention = ppaResult.needsAttention();
	if (!entry.isIgnored && !entry.needsAttention && !entry.periods.isEmpty()) {
            //entry.periods.get(0).selected = true;
            entry.selected = ""+entry.periods.get(0).id;
        } else if (entry.isIgnored) {
            entry.selected = "dismiss";
        }

	return entry;        
    }
    

    public static int periodToInt(double period) {
	return (int)(period*1000);
    }
    
    protected PPASelectItem buildItem(PPA ppa, double gof) {
	int id = periodToInt(ppa.getPeriod());

	PPASelectItem item = new PPASelectItem();
        item.id = id;
        item.ERR = roundToCenty(ppa.getJoinedError());
        item.GOF = roundToCenty(ppa.getGOF());
        item.amplitude = roundToCenty(ppa.getAmplitude());
        item.period = roundToCenty(ppa.getPeriod());
        item.phase = roundToCenty(ppa.getPhase());
	return item;
    }

    protected PPASelectItem buildItem(CosComponent comp, double gof) {
        final PhaseType phaseType = PhaseType.ByFit;
        return buildItem(comp.getPPA(phaseType),gof);
    }

    public List<PPASelectGroup> sortByUIImportance(List<PPASelectGroup> entries) {
        
	List<PPASelectGroup> attention = new ArrayList<>();
	List<PPASelectGroup> normal = new ArrayList<>();
	List<PPASelectGroup> ignored = new ArrayList<>();

	for (PPASelectGroup entry : entries) {
	    if (entry.isIgnored) {
		ignored.add(entry); continue;
	    }
	    if (entry.needsAttention) {
		attention.add(entry); continue;
	    }
	    normal.add(entry);
	}

        attention.sort(Comparator.comparing(e -> e.dataId));
        normal.sort(Comparator.comparing(e -> e.dataId));
        ignored.sort(Comparator.comparing(e -> e.dataId));
        
	attention.addAll(normal);
	attention.addAll(ignored);
	return attention;    
    }
    
    




    
    

    
    


    

    public PPAFitPack convertToUIPPAFitPack(PPAResult ppaResult,DataTrace dataTrace, TimeSeries fit, FakeIdExtractor idsCache,boolean withOptions) {
        
        PPAFitPack pack = new PPAFitPack();
        String dataRef = idsCache.getDataRef(dataTrace.dataId);
        String dataLabel = dataTrace.details.dataLabel;
        String title = dataRef + " "+dataLabel;
        
        pack.traces.title = title;
        
        double min = fit.getFirst().getTime();
        double max = fit.getLast().getTime();
        
        TimeSeries org = dataTrace.trace;
        if (!fit.isEmpty()) org = org.subSeries(min, max);
        
        Trace trace = new Trace();
        trace.label = dataLabel;
        trace.setTimeseries(org);
        pack.traces.traces.add(trace);
        
        trace = new Trace();
        trace.label = "FIT";
        trace.setTimeseries(fit);
        pack.traces.traces.add(trace);
        
        if (withOptions) {
            
            pack.traces.traces.addAll(simulatePeriodWaves(ppaResult,org));
            
            pack.options = encodePeriodOptions(ppaResult);
        }
        return pack;
    }


    protected List<Trace> simulatePeriodWaves(PPAResult ppaResult,TimeSeries org) {
        List<Trace> waves = new ArrayList<>();
        if (ppaResult.hasFailed()) return waves;
        
        double min = org.getFirst().getTime();
        double max = org.getLast().getTime(); 
        Pair<Double,Double> trend = TimeSeriesOperations.getLinTrendParams(org);
        
	if (ppaResult instanceof FFT_PPA) {
	    FFT_PPA fftResult = (FFT_PPA)ppaResult;
	    for (CosComponent cos : fftResult) {
		TimeSeries wave = simulateWave(cos.getPPAByFit(),trend,min,max);
                waves.add(makePeriodTrace(cos.getPeriod(),wave));
	    }

	} else {
            TimeSeries wave = simulateWave(ppaResult.getPPAByFit(),trend,min,max);
            waves.add(makePeriodTrace(ppaResult.getPeriod(),wave));
	}  
        return waves;
        
    }

    protected List<SimpleOption> encodePeriodOptions(PPAResult ppaResult) {
        
        List<SimpleOption> options = new ArrayList<>();
        if (ppaResult.hasFailed()) return options;
        
	if (ppaResult instanceof FFT_PPA) {
	    FFT_PPA fftResult = (FFT_PPA)ppaResult;
	    for (CosComponent comp : fftResult) {
		options.add(buildPeriodOption(comp.getPPAByFit()));
	    }
	} else {
	    options.add(buildPeriodOption(ppaResult.getPPAByFit()));
	}        
        
        SimpleOption ignore = new SimpleOption("dismiss","ignore");
        options.add(ignore);
        
	if (!ppaResult.isIgnored() && !ppaResult.needsAttention()) {
            options.get(0).selected = true;
        } else if (ppaResult.isIgnored()) {
            ignore.selected = true;
        }        
        return options;
    }

    protected SimpleOption buildPeriodOption(PPA ppa) {
        
	int id = periodToInt(ppa.getPeriod());
        double period = roundToCenty(ppa.getPeriod());
        
        return new SimpleOption(""+id,""+period);
    }

    protected TimeSeries simulateWave(PPA ppa, Pair<Double, Double> trend, double min, double max) {
        
	int N = (int)(max);
	if (N < 1) N = 1;
	//System.out.println("N: "+N+", p:"+ppa.getPeriod()+", ph:"+ppa.getPhase()+", a:"+ppa.getAmplitude());
	TimeSeries cos = TSGenerator.makeCos(N, 1, ppa.getPeriod(), ppa.getPhase(), ppa.getAmplitude());

        cos = cos.subSeries(min, max);
	return TimeSeriesOperations.addTrend(cos, trend.getLeft(), trend.getRight()); 
    }

    protected Trace makePeriodTrace(double period, TimeSeries wave) {
        Trace trace = new Trace();
        trace.setTimeseries(wave);
        trace.label = ""+ roundToCenty(period);
        return trace;
    }

    public PPAJobResultsGroups convertToUIPPAJobResultsGroups(PPAJobResultsGroups results, FakeIdExtractor idsCache) {

        for ( PPAResultsGroupSummary group : results.groups) {
            group.label = idsCache.getBioLabel(group.bioId);
        }
        

        return results;
    }

    public PPAJobSimpleResults convertToUIPPAJobSimpleResults(PPAJobSimpleResults results, FakeIdExtractor idsCache) {
        
        results.results.forEach( entry -> {
            entry.label = getLabel(entry, idsCache);
            entry.dataRef = idsCache.getDataRef(entry.dataId);
        });
        
        return results;
    }

    TSDataSetJobRequest prepareJC2JobRequest(long expId, PPARequest request, List<DataTrace> dataSet) {
        
        TSDataSetJobRequest job = new TSDataSetJobRequest();
        job.externalId = ""+expId;
        job.method = request.method.name();
        job.data = TSUtil.prepareTSData(dataSet, request.windowStart, request.windowEnd);
        job.parameters = prepareParameters(request);
        return job;
    }

    public PPAJobSummary prepareNewPPAJobSummary(long expId, PPARequest ppaRequest, UUID jobId) {
        
        
        PPAJobSummary jobSummary = new PPAJobSummary();
        jobSummary.jobId = (jobId);
        jobSummary.parentId = expId;

        jobSummary.state = State.SUBMITTED;
        jobSummary.submitted = LocalDateTime.now();
        
        jobSummary.dataSetId = expId+"_"+ppaRequest.detrending.name();
        jobSummary.dataSetType = dataType(ppaRequest);
        jobSummary.dataSetTypeName = dataTypeLabel(ppaRequest);
        jobSummary.dataWindow = dataWindow2String(ppaRequest.windowStart, ppaRequest.windowEnd);
        jobSummary.dataWindowEnd = ppaRequest.windowEnd;
        jobSummary.dataWindowStart = ppaRequest.windowStart;
        jobSummary.max_period = ppaRequest.periodMax;
        jobSummary.min_period = ppaRequest.periodMin;
        jobSummary.method = ppaRequest.method;
        jobSummary.modified = jobSummary.submitted;
        
	String summary = dataTypeLabel(ppaRequest)+" ";
        summary+= jobSummary.dataWindow;
	summary+= " p("+ppaRequest.periodMin+"-"+ppaRequest.periodMax+")";
        
        jobSummary.summary = summary;
        


        return jobSummary;
    }


    
    String dataWindow2String(double windowStart, double windowEnd) {
	String min = windowStart == 0 ? "min" : ""+windowStart;
	String max = windowEnd == 0 ? "max" : ""+windowEnd;
	return min+"-"+max;
    }    

    Map<String, String> prepareParameters(PPARequest request) {
        
        Map<String, String> params = new HashMap<>();
        params.put("METHOD", request.method.name());
        params.put(PERIOD_MIN_KEY, ""+request.periodMin);
        params.put(PERIOD_MAX_KEY, ""+request.periodMax);
        return params;        
    }

 


    
    
}
