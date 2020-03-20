/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import static ed.biodare.jobcentre2.dom.PeriodConstants.PERIOD_MAX_KEY;
import static ed.biodare.jobcentre2.dom.PeriodConstants.PERIOD_MIN_KEY;
import ed.biodare.jobcentre2.dom.TSDataSetJobRequest;
import ed.biodare2.backend.features.tsdata.TSUtil;
import ed.robust.dom.param.Parameters;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.ppa.PPARequest;
import ed.biodare2.backend.repo.ui_dom.ppa.PPAFitPack;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAResultsGroupSummary;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPASimpleResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPASimpleStats;
import ed.biodare2.backend.repo.ui_dom.ppa.PPAResultsGroup;
import ed.biodare2.backend.repo.ui_dom.ppa.PPASelectGroup;
import ed.biodare2.backend.repo.ui_dom.ppa.PPASelectItem;
import ed.biodare2.backend.repo.ui_dom.shared.SimpleOption;
import ed.biodare2.backend.repo.ui_dom.tsdata.Trace;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.jobcenter.JobSummary;
import ed.robust.dom.tsprocessing.CosComponent;
import ed.robust.dom.tsprocessing.FFT_PPA;
import ed.robust.dom.tsprocessing.PPA;
import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.dom.tsprocessing.PPAStats;
import ed.robust.dom.tsprocessing.PhaseRelation;
import ed.robust.dom.tsprocessing.PhaseType;
import ed.robust.dom.tsprocessing.ResultsEntry;
import ed.robust.dom.tsprocessing.ResultsGroup;
import ed.robust.dom.tsprocessing.Statistics;
import ed.robust.dom.tsprocessing.StatsEntry;
import ed.robust.dom.tsprocessing.WeightingType;
import ed.robust.dom.util.Pair;
import ed.robust.jobcenter.dom.job.JobHandle;
import ed.robust.jobcenter.dom.job.JobRequest;
import ed.robust.jobcenter.dom.state.State;
import ed.robust.jobcenter.dom.state.Status;
import ed.robust.jobcenter.dom.task.SimpleDataProvider;
import ed.robust.jobcenter.dom.task.SimpleTaskProvider;
import ed.robust.jobcenter.dom.task.Task;
import ed.robust.jobcenter.dom.task.TaskProvider;
import java.util.List;
import ed.robust.ppa.PPAConstants;
import ed.robust.ppa.PPAMethod;
import ed.robust.util.TableBuilder;
import ed.robust.util.timeseries.TSGenerator;
import ed.robust.util.timeseries.TimeSeriesOperations;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
public class PPAUtils {
    
    protected static final String DATA_SET_ID = JobSummary.DATA_SET_ID;
    protected static final String DATA_SET_TYPE = JobSummary.DATA_SET_TYPE;
    protected static final String DATA_SET_TYPE_NAME = JobSummary.DATA_SET_TYPE_NAME;
    protected static final String DW_END = JobSummary.DW_END;
    protected static final String DW_START = JobSummary.DW_START;
    protected static final String METHOD_ID = JobSummary.METHOD_ID;
    protected static final String METHOD_NAME = JobSummary.METHOD_NAME;
    protected static final String PARAMS_SUMMARY = JobSummary.PARAMS_SUMMARY;   
    public static final String JOB_ID = "JOB_ID";
    public static final String ID_SEPARATOR = ",";
    public static final String DATA_IDS = "DATA_IDS";



    
    protected final String SEP = ",";
    protected final String ESC = "\"";
    
    

    
    public JobRequest prepareJobRequest(long expId,PPARequest ppaRequest,List<DataTrace> dataSet)  {

            TaskProvider tasks = prepareTasks(dataSet,ppaRequest);

            Parameters params = new Parameters();
            params.put(PPAConstants.MIN_PERIOD, ppaRequest.periodMin);
            params.put(PPAConstants.MAX_PERIOD, ppaRequest.periodMax);
            params.put(DATA_SET_TYPE,dataType(ppaRequest)); //dataType.getCode());
            params.put(DATA_SET_ID,expId+"_"+ppaRequest.detrending.name());

            JobRequest request = new JobRequest();
            request.setExternalId(""+expId);
            request.setMethod(ppaRequest.method.name());
            request.setParams(params);
            request.setTasks(tasks);
            //request.setResultsHandler(getBioDareCallBack());

            return request;
            
    }
    
    protected String dataType(PPARequest ppaRequest) {
        return ppaRequest.detrending.name();
    }
    
    protected String dataTypeLabel(PPARequest ppaRequest) {
        return ppaRequest.detrending.longName;
    }    
    
    public void convertToLocalRequest(JobRequest request,PPARequest ppaRequest,long jobId) {

	Parameters params = request.getParams();
	params.put(DW_START, ppaRequest.windowStart);
	params.put(DW_END,ppaRequest.windowEnd);
	params.put(DATA_SET_TYPE,dataType(ppaRequest));
	params.put(JOB_ID,jobId);
	StringBuilder ids = new StringBuilder();
	for (Task task : request.getTasks()) {
	    ids.append(task.getId()).append(ID_SEPARATOR);
	}
	params.put(DATA_IDS,ids.toString());


	request.setTasks(null);
	request.setResultsHandler(null);
    }
    
    public JobSummary prepareNewJobSummary(JobHandle jobHandle,JobRequest jobRequest,PPARequest ppaRequest) {
        
	JobSummary jobSummary = new JobSummary();
	jobSummary.setJob(jobHandle);

	Status status = new Status();
	status.setState(State.SUBMITTED);
	status.setSubmitted(new Date());
	jobSummary.setStatus(status);


	Parameters params = new Parameters();
	params.put(PPAConstants.MIN_PERIOD, ppaRequest.periodMin);
	params.put(PPAConstants.MAX_PERIOD, ppaRequest.periodMax);
	params.put(METHOD_ID,ppaRequest.method.name());
	params.put(METHOD_NAME,ppaRequest.method.getFriendlyName());
	params.put(DW_START, ppaRequest.windowStart);
	params.put(DW_END,ppaRequest.windowEnd);
	params.put(DATA_SET_TYPE, dataType(ppaRequest));
	//params.put(DATA_SET_TYPE_NAME,DataSetTypes.getDataSetTypeLabel(dataType));
	params.put(DATA_SET_TYPE_NAME,dataTypeLabel(ppaRequest));
	params.put(DATA_SET_ID,jobRequest.getParams().get(DATA_SET_ID));


	//String summary = DataSetTypes.getDataSetTypeLabel(dataType)+" ";
	String summary = dataTypeLabel(ppaRequest)+" ";
	if (ppaRequest.windowStart == 0) summary+="min"; else summary+=ppaRequest.windowStart;
	summary+="-";
	if (ppaRequest.windowEnd == 0) summary+="max"; else summary+=ppaRequest.windowEnd;
	summary+=" p("+ppaRequest.periodMin+"-"+ppaRequest.periodMax+")";
	params.put(PARAMS_SUMMARY,summary);

	jobSummary.setParams(params);
        
        return jobSummary;
        
    }
    
    
    protected TaskProvider prepareTasks(List<DataTrace> dataSet,PPARequest ppaRequest) {

	SimpleTaskProvider provider = new SimpleTaskProvider();

	double min = ppaRequest.windowStart;
	double max = ppaRequest.windowEnd;
	if (min == 0) min = Double.NEGATIVE_INFINITY;
	if (max == 0) max = Double.POSITIVE_INFINITY;        

	for (DataTrace ds : dataSet) {
            //if (ds.isIgnored()) continue;
	    //TimeSeries ts = ds.getData().subSeries(min, max);//makeTimeSeries(ds,min,max);
            TimeSeries ts = ds.trace.subSeries(min, max);
	    SimpleDataProvider<TimeSeries> data = new SimpleDataProvider<>(ts);
	    Task<TimeSeries> task = new Task<>();
	    task.setId(ds.dataId);
	    task.setData(data);

	    provider.add(task);

	}
	return provider;
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
    
    public static PPAJobSummary simplifyJob(JobSummary job) {

        PPAJobSummary summary = new PPAJobSummary();
        summary.attentionCount = job.getAttentionCount();
        summary.closed = job.isClosed();
        summary.completed = job.getStatus().getCompleted();
        summary.dataSetId = job.getParams().getString(JobSummary.DATA_SET_ID);
        summary.dataSetType = job.getDataSetType();
        summary.dataSetTypeName = job.getDataSetTypeName();
        summary.dataWindow = job.getDataWindow();
        summary.dataWindowEnd = job.getParams().getDouble(JobSummary.DW_END, 0);
        summary.dataWindowStart = job.getDataWindowStart();

        summary.failures = job.getFailures();
        summary.jobId = job.getJobId();
        summary.lastError = job.getLastError();
        summary.max_period = job.getParams().getDouble(PPAConstants.MAX_PERIOD,35);
        summary.min_period = job.getParams().getDouble(PPAConstants.MIN_PERIOD,18);
        summary.message = job.getMessage();
        summary.method = PPAMethod.valueOf(job.getParams().getString(JobSummary.METHOD_ID));
        summary.modified = job.getStatus().getModified();
        summary.needsAttention = job.needsAttention();
        summary.state = job.getStatus().getState();
        summary.submitted = job.getStatus().getSubmitted();
        summary.summary = job.getSummary();
        return summary;
        
    }
    
    public static PPASimpleResultEntry simplifyResultsEntry(ResultsEntry org, double windowStart) {
        
        PPAResult result = org.getResult();

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

    
    
    public List<PPAResultsGroup> convertToUIPPAResults(Map<Long, List<PPASimpleResultEntry>> groups, List<PPAJobSummary> jobs,
            Map<String,FakeIdExtractor> idsCaches) {

        
        return groups.values().parallelStream()
                    .map (gr -> convertToUIResultsGroup(gr,jobs,idsCaches))
                    .collect(Collectors.toList());
        
    }
    
    

    protected PPAResultsGroup convertToUIResultsGroup(List<PPASimpleResultEntry> entryGroup,List<PPAJobSummary> jobs,
            Map<String,FakeIdExtractor> idsCaches) {
        
        Map<Long,PPAJobSummary> jobsMap = jobs.stream().collect(Collectors.toMap( j -> j.jobId, j -> j));
        
        PPAResultsGroup ui = new PPAResultsGroup();

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
    }
    
    
    protected String getLabel(ResultsGroup group,FakeIdExtractor idsCache) {
        String bioLabel = idsCache.getBioLabel(group.getBiolDescId());
        String condLabel = idsCache.getCondLabel(group.getEnvironmentId());
        return bioLabel.equals(condLabel) ? bioLabel : bioLabel+"; "+condLabel;       
    }
    
    protected String getLabel(ResultsEntry entry,FakeIdExtractor idsCache) {
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
    

    public List<PPASelectGroup> convertToUIPPAForSelect(List<ResultsEntry> entries, FakeIdExtractor idsCache) {
        
        return entries.stream()
                    .filter( e-> e.getResult() != null)
                    .filter( e -> !e.getResult().hasFailed())
                    .map( entry -> buildSelectGroup(entry,idsCache))
                    .collect(Collectors.toList());

    }
    


    protected PPASelectGroup buildSelectGroup(ResultsEntry result, FakeIdExtractor idsCache) {
        final PhaseType phaseType = PhaseType.ByFit;
        
        PPASelectGroup entry = new PPASelectGroup();
        entry.dataId = result.getDataId();
        entry.dataRef = idsCache.getDataRef(result.getDataId());
	entry.label = getLabel(result, idsCache);
        

	entry.periods = new ArrayList<>();

	PPAResult ppaResult = result.getResult();
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
    
    
    public Map<String, Path> saveToFiles(List<JobSummary> jobs, Map<Long, List<ResultsEntry>> resultsGroups, List<StatsEntry> jobsStats,
            ExperimentalAssay exp,Map<String,FakeIdExtractor> idsCaches) throws IOException   {
        
        Map<Long,JobSummary> jobsMap = jobs.stream().collect(Collectors.toMap( j -> j.getJobId(), j -> j));
        
	Map<String,Path> map = new HashMap<>();

        Set<State> finished = EnumSet.of(State.FINISHED,State.SUCCESS);
        
        for (StatsEntry stats : jobsStats) {
            JobSummary job = jobsMap.get(stats.getJobId());
            if (job == null) continue;
            FakeIdExtractor idsCache = idsCaches.get(job.getDataSetType());
            
	    if (finished.contains(job.getStatus().getState())) {
		Path file = saveStatsToFile(job,stats,exp,idsCache);
		map.put("Statistics "+job.getJobId()+".csv",file);
	    }
	}
	
	Path file = saveResultsToFile(resultsGroups, jobsMap,exp,idsCaches);
	map.put("Individual results.csv",file);

	return map;
        
    }



    
    protected Path saveStatsToFile(JobSummary job, StatsEntry stats,ExperimentalAssay exp,FakeIdExtractor idsCache) throws IOException {
        
	Path file = Files.createTempFile(null, null);

	try (BufferedWriter writer = Files.newBufferedWriter(file)) {

            if (stats == null || stats.getStats().isEmpty()) {
                printNoJobMessage(job,writer,exp);
            } else {
                printStatsLegend(job,writer,exp);
                printStats(job,stats,writer,idsCache);
            }
	}
	return file;        
    }

    protected void printNoJobMessage(JobSummary job, BufferedWriter writer,ExperimentalAssay exp) throws IOException {
        TableBuilder tb = new TableBuilder(SEP, ESC);
        
        tb.printlnLabel("No statistics found");
        tb.endln();
        tb.printlnParam("Experiment ID", exp.getId());
        tb.printlnParam("Experiment name", exp.getName());
        
        tb.printlnParam("Job", job.getJobId());
        tb.printlnParam("Summary",job.getSummary());
        
        writer.write(tb.toString());
    }

    protected void printStatsLegend(JobSummary job, BufferedWriter writer, ExperimentalAssay exp) throws IOException {
        TableBuilder tb = new TableBuilder(SEP, ESC);
        tb.printlnParam("Experiment ID", exp.getId());
        tb.printlnParam("Experiment name", exp.getName());
        tb.printlnParam("Job", job.getJobId());
        tb.printParam("Submitted", job.getStatus().getSubmitted());
        tb.printlnParam("Finished", job.getStatus().getCompleted());
        tb.printlnParam("Method",job.getMethod());
        tb.printlnParam("Params",job.getSummary());
        if (job.needsAttention()) tb.printlnLabel("WARNING some results still need manual editing to be included in the stats");
        tb.endln();
        
        
        tb.printLabel("Data");
        tb.printLabel("Conditions");
        
        
        tb.printLabel("");
        
        tb.printLabel("Period");
        writeStatsHead(tb);
        tb.printLabel("");
        
        PhaseType[] phases = {PhaseType.ByFit,PhaseType.ByMethod,PhaseType.ByAvgMax,PhaseType.ByFirstPeak};
        
        double[] phaseRelations;
        if (job.getDataWindowStart() != 0) phaseRelations = new double[]{0,job.getDataWindowStart()};
        else phaseRelations = new double[]{0};
        
        
        
        for (PhaseType phase : phases) {
            for (double phaseRelation : phaseRelations) {
                
                tb.printLabel("Phase "+phase.getFriendlyName());
                tb.printLabel("Relative to "+phaseRelation);
                writeStatsHead(tb);
                tb.printLabel("");
                
                tb.printLabel("Circadian Phase "+phase.getFriendlyName());
                tb.printLabel("Relative to "+phaseRelation);
                writeStatsHead(tb);
                tb.printLabel("");
                
                
            }
        }
        
        for (PhaseType phase : phases) {
            tb.printLabel("Amplitude "+phase.getFriendlyName());
            writeStatsHead(tb);
            tb.printLabel("");
        }
        
        tb.printLabel("GOF");
        writeStatsHead(tb);
        tb.printLabel("");
        
        tb.printLabel("ERR");
        writeStatsHead(tb);
        tb.printLabel("");
        
        tb.endln();
        
        writer.write(tb.toString());
        
    }
    
    protected void writeStatsHead(TableBuilder tb)
    {
        
	tb.printLabel("N");
	tb.printLabel("GOF W Mean");
	tb.printLabel("GOF W SE");
	tb.printLabel("GOF W SD");
	tb.printLabel("Mean");
	tb.printLabel("SE");
	tb.printLabel("SD");
	tb.printLabel("Median");
	tb.printLabel("Variance");
	tb.printLabel("Kurtosis");
	tb.printLabel("Skewness");
	tb.printLabel("Min");
	tb.printLabel("Max");
	//tb.printLabel("Range");
	//tb.printLabel("Sum");
        
        
    }

    protected void printStats(JobSummary job, StatsEntry stats, BufferedWriter writer, FakeIdExtractor idsCache) throws IOException {
        List<PPAStats> entries = sortEntries(stats,idsCache);
        
        for (PPAStats entry : entries) {
            
            TableBuilder tb = new TableBuilder(SEP, ESC);
            tb.printLabel(idsCache.getBioLabel(entry.getBiolDescId()));
            tb.printLabel(idsCache.getCondLabel(entry.getEnvironmentId()));
            
            
            tb.printLabel("");
            tb.printLabel("period");

            WeightingType reqWeight = WeightingType.ByGOF;
            Statistics stat = entry.getPeriod(WeightingType.None);
            Statistics wStat = entry.getPeriod(reqWeight);
            writeStats(tb,stat,wStat);
            tb.printLabel("");
            
            PhaseType[] phases = {PhaseType.ByFit,PhaseType.ByMethod,PhaseType.ByAvgMax,PhaseType.ByFirstPeak};

            double[] phaseRelations;
            if (job.getDataWindowStart() != 0) phaseRelations = new double[]{0,job.getDataWindowStart()};
            else phaseRelations = new double[]{0};



            for (PhaseType phase : phases) {
                for (double phaseRelation : phaseRelations) {

                    tb.printLabel("Phase "+phase.getFriendlyName());
                    tb.printLabel("Relative to "+phaseRelation);
           
                    stat = entry.getPhase(phase,WeightingType.None);
                    wStat = entry.getPhase(phase,reqWeight);
                    if (phaseRelation != 0)
                        writePhaseStats(tb,stat,wStat,entry.getPeriod(WeightingType.None).getMean(),entry.getPeriod(reqWeight).getMean(),phaseRelation);
                    else 
                        writeStats(tb,stat,wStat);
                    tb.printLabel("");

                    tb.printLabel("Circadian Phase "+phase.getFriendlyName());
                    tb.printLabel("Relative to "+phaseRelation);
           
                    stat = entry.getPhaseCirc(phase,WeightingType.None);
                    wStat = entry.getPhaseCirc(phase,reqWeight);
                    if (phaseRelation != 0)
                        writePhaseStats(tb,stat,wStat,entry.getPeriod(WeightingType.None).getMean(),entry.getPeriod(reqWeight).getMean(),phaseRelation);
                    else 
                        writeStats(tb,stat,wStat);
                    tb.printLabel("");                }
            }
            
            for (PhaseType phase : phases) {
                tb.printLabel("Amplitude "+phase.getFriendlyName());

                    stat = entry.getAmplitude(phase,WeightingType.None);
                    wStat = entry.getAmplitude(phase,reqWeight);
                    writeStats(tb,stat,wStat);
                    tb.printLabel("");
            }
            
            
            tb.printLabel("GOF");

            stat = entry.getGOF();
            writeStats(tb,stat,null);
            tb.printLabel("");
            
            tb.printLabel("ERR");

            stat = entry.getJoinedError();
            writeStats(tb,stat,null);
            tb.printLabel("");
            
            tb.endln();
            
            writer.write(tb.toString());    
        }
    }
    
    protected void writeStats(TableBuilder tb, Statistics stat, Statistics wStat) {
        tb.printVal(stat.getN());
        if (wStat != null) {
            tb.printVal(wStat.getMean());
            tb.printVal(wStat.getStdErr());
            tb.printVal(wStat.getStdDev());
        } else {
            tb.printLabel("");
            tb.printLabel("");
            tb.printLabel("");
        }
        
        tb.printVal(stat.getMean());
        tb.printVal(stat.getStdErr());
        tb.printVal(stat.getStdDev());
        
        tb.printVal(stat.getMedian());
        tb.printVal(stat.getVariance());
        tb.printVal(stat.getKurtosis());
        tb.printVal(stat.getSkewness());
        tb.printVal(stat.getMin());
        tb.printVal(stat.getMax());
        //tb.printVal(stat.getMax()-stat.getMin());
        //tb.printVal(stat.getSum());
        
    }
  
    protected void writePhaseStats(TableBuilder tb, Statistics stat, Statistics wStat, double period, double wPeriod, double phaseRelation) {
        tb.printVal(stat.getN());
        tb.printVal(PhaseRelation.relativePhase(wStat.getMean(),wPeriod,phaseRelation));
        tb.printVal(wStat.getStdErr());
        tb.printVal(wStat.getStdDev());
        
        tb.printVal(PhaseRelation.relativePhase(stat.getMean(),period,phaseRelation));
        tb.printVal(stat.getStdErr());
        tb.printVal(stat.getStdDev());
        
        tb.printVal(PhaseRelation.relativePhase(stat.getMedian(),period,phaseRelation));
        tb.printVal(stat.getVariance());
        tb.printVal("not calc."); //kurtosis
        tb.printVal("not calc."); //skewness
        double m1 = PhaseRelation.relativePhase(stat.getMin(), period, phaseRelation);
        double m2 = PhaseRelation.relativePhase(stat.getMax(), period, phaseRelation);
        tb.printVal(Math.min(m1, m2));
        tb.printVal(Math.max(m1, m2));
        //tb.printVal(stat.getMax()-stat.getMin());
        //tb.printVal(stat.getSum());
    }

    protected List<PPAStats> sortEntries(StatsEntry stats,FakeIdExtractor idsCache) {
        
        Comparator<PPAStats> comp = Comparator.comparing((PPAStats s) -> idsCache.getBioLabel(s.getBiolDescId()));
        
        return stats.getStats().stream()
                .sorted(comp)
                .collect(Collectors.toList());
    }

    protected Path saveResultsToFile(Map<Long, List<ResultsEntry>> resultsGroups, Map<Long,JobSummary> jobs, 
            ExperimentalAssay exp, Map<String,FakeIdExtractor> idsCaches) throws IOException {
        
	Path file = Files.createTempFile(null, null);

	try (BufferedWriter writer = Files.newBufferedWriter(file)) {

            if (resultsGroups.isEmpty()) {
                printNoResultsMessage(writer,exp);
            } else {
                printResultsLegend(writer,exp);
                printResultsGroups(resultsGroups,jobs,writer,idsCaches);
            }
	}
	return file;
    }

    
    protected void printNoResultsMessage(BufferedWriter writer, ExperimentalAssay exp) throws IOException {
        TableBuilder tb = new TableBuilder(SEP, ESC);
        
        tb.printlnLabel("No results found");
        tb.endln();
        tb.printlnParam("Experiment ID", exp.getId());
        tb.printlnParam("Experiment name", exp.getName());
        
        tb.endln();
        writer.write(tb.toString());
    }

    protected void printResultsLegend(BufferedWriter writer, ExperimentalAssay exp) throws IOException {
        TableBuilder tb = new TableBuilder(SEP, ESC);
	tb.printlnParam("Experiment ID:",exp.getId());
	tb.printlnParam("Experimetn Name:",exp.getName());
	tb.endln();

	tb.printLabel("sample");
        tb.printLabel("label");
        tb.printLabel("biol. desc.");
	/*tb.printLabel("background");
	tb.printLabel("genotype");
	tb.printLabel("marker");
	for (String tag : tags) {
	    tb.printLabel(tag);
	}*/
	tb.printLabel("Exp. Cond");

	tb.printLabel("");
	tb.printLabel("anal.");
	tb.printLabel("date");
	tb.printLabel("method");
	tb.printLabel("params");
	tb.printLabel("data type");
	tb.printLabel("data window");
	tb.printLabel("attention");
        //tb.printLabel("");
        
	tb.printLabel("period");
	tb.printLabel("period err");
        
        PhaseType[] phases = {PhaseType.ByFit,PhaseType.ByMethod,PhaseType.ByAvgMax,PhaseType.ByFirstPeak};
        for (PhaseType pt : phases) {
            tb.printLabel("phase "+pt.friendlyName);
            tb.printLabel("circadian phase "+pt.friendlyName);
            tb.printLabel("phase to DW");
            tb.printLabel("circadian phase to DW");
            tb.printLabel("phase err");
            tb.printLabel("amplitude "+pt.friendlyName);
            tb.printLabel("amplitude err");
        }
	tb.printLabel("GOF");
        tb.printLabel("ERR");
        tb.endln();
        
        writer.write(tb.toString());
    }


        
    protected void printResultsGroups(Map<Long, List<ResultsEntry>> resultsGroups, Map<Long,JobSummary> jobs, 
            BufferedWriter writer, Map<String,FakeIdExtractor> idsCaches) {
        
        resultsGroups.values().stream()
                .sorted(Comparator.comparing((List<ResultsEntry> rg) -> rg.isEmpty() ? 0 : rg.get(0).getDataId()))
                .forEachOrdered( group -> {
                    try {
                        printResultsGroup(group,jobs,writer,idsCaches);
                        writer.newLine();
                    } catch (IOException e) {
                        throw new ServerSideException("Cannot write results: "+e.getMessage(),e);
                    }
                });
                ;
        
    }
    
    


    protected void printResultsGroup(List<ResultsEntry> resultsGr, Map<Long,JobSummary> jobs, 
            BufferedWriter writer, Map<String,FakeIdExtractor> idsCaches) throws IOException {
        for (ResultsEntry jobEntry : resultsGr) {
            
            JobSummary job = jobs.get(jobEntry.getJobId());
            FakeIdExtractor idsCache = idsCaches.get(job.getDataSetType());
            TableBuilder tb = new TableBuilder(SEP, ESC);

            tb.printLabel(idsCache.getDataRef(jobEntry.getDataId())); //tb.printLabel(resultsGr.getOrgId());
            tb.printLabel(getLabel(jobEntry, idsCache)); //it should be traced based
            tb.printLabel("");//tb.printLabel(idsCache.getBioLabel(resultsGr.getBiolDescId()));
            tb.printLabel("");//tb.printLabel(idsCache.getCondLabel(resultsGr.getEnvironmentId()));

            tb.printLabel("");
            
            
            tb.printVal(job.getJobId());
            tb.printLabel(job.getSubmitted());
            tb.printLabel(job.getMethod());
            tb.printLabel(job.getSummary());
            tb.printLabel(job.getDataSetTypeName());
            tb.printLabel(job.getDataWindow());
            
            
            PPAResult result = jobEntry.getResult();
            String attention = "";
            if (result.hasFailed()) {
                attention = "FAILED "+result.getMessage();
            } else if (result.isIgnored()) {
                attention = "IGNORED";
            } else if (result.needsAttention()) {
                attention = "X";
            }
            tb.printLabel(attention);
            //tb.printLabel("");
            
            if (!result.hasFailed()) {
                printResult(result,job.getDataWindowStart(),tb);
            }
            
            tb.endln();
            writer.write(tb.toString());
            
        }
    }
        

    protected void printResult(PPAResult result, double dataWindowStart, TableBuilder tb) {
        tb.printVal(result.getPPAMethodSpecific().getPeriod());
        tb.printVal(result.getPPAMethodSpecific().getPeriodError());
        
        PhaseType[] phases = {PhaseType.ByFit,PhaseType.ByMethod,PhaseType.ByAvgMax,PhaseType.ByFirstPeak};
        for (PhaseType pt : phases) {
            PPA ppa = result.getPPA(pt);
            tb.printVal(ppa.getPhase());
            tb.printVal(ppa.getPhase()*24/ppa.getPeriod());
            tb.printVal(PhaseRelation.relativePhase(ppa.getPhase(), ppa.getPeriod(), dataWindowStart));
            tb.printVal(PhaseRelation.relativePhase(ppa.getPhase(), ppa.getPeriod(), dataWindowStart)*24/ppa.getPeriod());
            tb.printVal(ppa.getPhaseError());
            tb.printVal(ppa.getAmplitude());
            tb.printVal(ppa.getAmplitudeError());
        }        
        tb.printVal(result.getPPAMethodSpecific().getGOF());
        tb.printVal(result.getPPAMethodSpecific().getJoinedError());
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

    PPAJobSummary prepareNewPPAJobSummary(long expId, PPARequest ppaRequest, UUID jobId) {
        
        
        PPAJobSummary jobSummary = new PPAJobSummary();
        jobSummary.id = jobId.toString();
        jobSummary.jobId = uuid2long(jobId);

        jobSummary.state = State.SUBMITTED;
        jobSummary.submitted = new Date();
        
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

    static final long uuid2long(UUID id) {
        return id.getLeastSignificantBits()+id.getMostSignificantBits();
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
