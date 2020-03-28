/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAFullResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleResultEntry;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.robust.dom.tsprocessing.PPA;
import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.dom.tsprocessing.PPAStats;
import ed.robust.dom.tsprocessing.PhaseRelation;
import ed.robust.dom.tsprocessing.PhaseType;
import ed.robust.dom.tsprocessing.Statistics;
import ed.robust.dom.tsprocessing.StatsEntry;
import ed.robust.dom.tsprocessing.WeightingType;
import ed.biodare2.backend.util.TableBuilder;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *
 * @author tzielins
 */
public class PPAResultsExporterJC2 {

    protected final String SEP = ",";
    protected final String ESC = "\"";
    
    public void exportPPAJob(ExperimentalAssay exp,PPAJobSummary job, PPAJobSimpleResults results, PPAJobSimpleStats stats, FakeIdExtractor idsCache, PhaseType phaseType, Path file) throws IOException {
        
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {        
    
            printJobSummary(exp,job,writer);
            writer.newLine();
            
            printSimpleStats(stats,phaseType,idsCache,writer);
            writer.newLine();
            
            printSimpleResults(results,phaseType,idsCache,writer);
            
        }
    }
    
    public void exportPPAFullStats(ExperimentalAssay exp, PPAJobSummary job, StatsEntry stats, FakeIdExtractor idsCache, Path file) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {        
    
            printJobSummary(exp,job,writer);
            writer.newLine();
            
            printStatsLegend(job, writer);
            printStats(job, stats, idsCache, writer);
        
        }
    }
    
    public void exportJoinedFullResults(ExperimentalAssay exp, List<PPAJobSummary> jobs, List<PPAFullResultEntry> fullResults,  FakeIdExtractor idsCache, Path file) throws IOException { 
        
        Map<UUID, PPAJobSummary> jobsMap = jobs.stream().collect(Collectors.toMap(j -> j.jobId, j -> j));
        
        Map<Long, List<PPAFullResultEntry>> joinedResults = fullResults.stream().collect(Collectors.groupingBy(r -> r.dataId));
        
        saveJoinedFullResults(exp, jobsMap, joinedResults, idsCache, file);

    }    
    public void saveJoinedFullResults(ExperimentalAssay exp, Map<UUID, PPAJobSummary> jobs, 
            Map<Long, List<PPAFullResultEntry>> joinedResults, FakeIdExtractor idsCache, Path file) throws IOException {
        
	try (BufferedWriter writer = Files.newBufferedWriter(file)) {

                printResultsLegend(exp, writer);
                printJoinedFullResults(jobs,joinedResults, idsCache, writer);
            
	}
    } 
    
    protected void printResultsLegend(ExperimentalAssay exp, BufferedWriter writer) throws IOException {
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
    
    protected void printJoinedFullResults(Map<UUID, PPAJobSummary> jobs, Map<Long, List<PPAFullResultEntry>> resultsGroups, 
            FakeIdExtractor idsCache, BufferedWriter writer) {
        
        resultsGroups.values().stream()
                .sorted(Comparator.comparing((List<PPAFullResultEntry> rg) -> rg.isEmpty() ? 0 : rg.get(0).dataId))
                .forEachOrdered( group -> {
                    try {
                        printResultsGroup(jobs, group, idsCache,writer);
                        writer.newLine();
                    } catch (IOException e) {
                        throw new ServerSideException("Cannot write results: "+e.getMessage(),e);
                    }
                });
                ;
        
    }       
    
    protected void printResultsGroup(Map<UUID,PPAJobSummary> jobs, List<PPAFullResultEntry> resultsGr,  
            FakeIdExtractor idsCache, BufferedWriter writer) throws IOException {
        
        for (PPAFullResultEntry jobEntry : resultsGr) {
            
            PPAJobSummary job = jobs.get(jobEntry.jobId);
            TableBuilder tb = new TableBuilder(SEP, ESC);

            tb.printLabel(idsCache.getDataRef(jobEntry.dataId)); //tb.printLabel(resultsGr.getOrgId());
            tb.printLabel(getLabel(jobEntry, idsCache)); //it should be traced based
            tb.printLabel("");//tb.printLabel(idsCache.getBioLabel(resultsGr.getBiolDescId()));
            tb.printLabel("");//tb.printLabel(idsCache.getCondLabel(resultsGr.getEnvironmentId()));

            tb.printLabel("");
            
            
            tb.printVal(job.jobId.toString());
            tb.printLabel(job.submitted.toString());
            tb.printLabel(job.method.name());
            tb.printLabel(job.summary);
            tb.printLabel(job.dataSetTypeName);
            tb.printLabel(job.dataWindow);
            
            
            PPAResult result = jobEntry.result;
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
                printResult(result,job.dataWindowStart,tb);
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
    
    protected String getLabel(PPAFullResultEntry entry,FakeIdExtractor idsCache) {
        String bioLabel = idsCache.getBioLabel(entry.biolDescId);
        String condLabel = idsCache.getCondLabel(entry.environmentId);
        return bioLabel.equals(condLabel) ? bioLabel : bioLabel+"; "+condLabel;       
    }     
    
    protected void printStatsLegend(PPAJobSummary job, BufferedWriter writer) throws IOException {
        TableBuilder tb = new TableBuilder(SEP, ESC);
        
        
        tb.printLabel("Data");
        tb.printLabel("Conditions");
        
        
        tb.printLabel("");
        
        tb.printLabel("Period");
        writeStatsHead(tb);
        tb.printLabel("");
        
        PhaseType[] phases = {PhaseType.ByFit,PhaseType.ByMethod,PhaseType.ByAvgMax,PhaseType.ByFirstPeak};
        
        double[] phaseRelations;
        if (job.dataWindowStart != 0) phaseRelations = new double[]{0,job.dataWindowStart};
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
    

    protected void printStats(PPAJobSummary job, StatsEntry stats, FakeIdExtractor idsCache, BufferedWriter writer) throws IOException {
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
            if (job.dataWindowStart != 0) phaseRelations = new double[]{0,job.dataWindowStart};
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
    
    
    protected void printJobSummary(ExperimentalAssay exp, PPAJobSummary job, BufferedWriter writer) throws IOException {
        
        TableBuilder tb = serializeJob(exp,job);
        
        writer.write(tb.toString());
    }

    protected TableBuilder serializeJob(ExperimentalAssay exp, PPAJobSummary job) {
        
        TableBuilder tb = new TableBuilder(SEP, ESC);
        tb.printlnParam("Experiment ID", exp.getId());
        tb.printlnParam("Experiment name", exp.getName());
        tb.printlnParam("Job", job.jobId.toString());
        tb.printParam("Submitted", job.submitted);
        tb.printlnParam("Finished", job.completed);
        tb.printlnParam("Method",job.method.friendlyName);
        tb.printlnParam("Params",job.summary);
        if (job.needsAttention) tb.printlnLabel("WARNING some results still need manual selection to be included in the summary stats");
        tb.endln();

        return tb;
    }

    protected void printSimpleStats(PPAJobSimpleStats stats, PhaseType phaseType, FakeIdExtractor idsCache,BufferedWriter writer) throws IOException {
        TableBuilder tb = serializeSimpleStats(stats,phaseType,idsCache);
        
        writer.write(tb.toString());    
    
    }

    protected TableBuilder serializeSimpleStats(PPAJobSimpleStats stats, PhaseType phaseType,FakeIdExtractor idsCache) {
        
        TableBuilder tb = new TableBuilder(SEP, ESC);
        tb.printlnLabel("Summary statistics");
        tb.printlnParam("Phase/Amplitude method", phaseType.friendlyName);
        tb.endln();
        
        tb.printLabel("Group Label");
        tb.printLabel("N");
        tb.printLabel("");
        tb.printLabel("Period");
        tb.printLabel("Period Std");
        tb.printLabel("");
        tb.printLabel("Circ. Phase To Zero");
        tb.printLabel("Circ. Phase To Window");
        tb.printLabel("Circ. Phase Std");
        tb.printLabel("Abs Phase To Zero");
        tb.printLabel("Abs Phase To Window");
        tb.printLabel("Abs Phase Std");
        tb.printLabel("");
        tb.printLabel("Amplitude");
        tb.printLabel("Amplitude Std");
        tb.printLabel("");
        tb.printLabel("GOF");
        tb.printLabel("ERR");
        tb.endln();
        
        stats.stats.forEach( stat -> {
        
            tb.printVal(idsCache.getBioLabel(stat.bioId));
            tb.printVal(stat.N);
            tb.printLabel("");
            tb.printVal(stat.period);
            tb.printVal(stat.periodStd);
            tb.printLabel("");
            tb.printVal(stat.phaseToZeroCirc.get(phaseType));
            tb.printVal(stat.phaseToWindowCirc.get(phaseType));
            tb.printVal(stat.phaseCircStd.get(phaseType));
            tb.printVal(stat.phaseToZero.get(phaseType));
            tb.printVal(stat.phaseToWindow.get(phaseType));
            tb.printVal(stat.phaseStd.get(phaseType));
            tb.printLabel("");
            tb.printVal(stat.amplitude.get(phaseType));
            tb.printVal(stat.amplitudeStd.get(phaseType));
            tb.printLabel("");
            tb.printVal(stat.GOF);
            tb.printVal(stat.ERR);
            tb.endln();
            
        });
        
        return tb;
        
    }

    protected void printSimpleResults(PPAJobSimpleResults results, PhaseType phaseType, FakeIdExtractor idsCache, BufferedWriter writer) throws IOException {
        TableBuilder tb = serializeSimpleResults(results,phaseType,idsCache);
        
        writer.write(tb.toString());      }

    protected TableBuilder serializeSimpleResults(PPAJobSimpleResults results, PhaseType phaseType, FakeIdExtractor idsCache) {
        TableBuilder tb = new TableBuilder(SEP, ESC);
        tb.printlnLabel("Individual Results");
        tb.printlnParam("Phase/Amplitude method", phaseType.friendlyName);
        tb.endln();
        

        tb.printLabel("Data Ref");
        tb.printLabel("Data Label");
        tb.printLabel("Status");
        tb.printLabel("");
        tb.printLabel("Period");
        tb.printLabel("Period Err");
        tb.printLabel("");
        tb.printLabel("Circ. Phase To Zero");
        tb.printLabel("Circ. Phase To Window");
        tb.printLabel("Circ. Phase Err");
        tb.printLabel("Abs Phase To Zero");
        tb.printLabel("Abs Phase To Window");
        tb.printLabel("Abs Phase Err");
        tb.printLabel("");
        tb.printLabel("Amplitude");
        tb.printLabel("Amplitude Err");
        tb.printLabel("");
        tb.printLabel("GOF");
        tb.printLabel("ERR");
        tb.endln();
        
        results.results.forEach( res -> {
            tb.printVal(idsCache.getDataRef(res.dataId));
            tb.printVal(idsCache.getBioLabel(res.bioId));
            tb.printVal(resStatus(res));
            
            if (!res.failed) {
                tb.printLabel("");
                tb.printVal(res.period);
                tb.printVal(res.periodErr);

                tb.printLabel("");
                tb.printVal(res.phaseToZeroCirc.get(phaseType));
                tb.printVal(res.phaseToWindowCirc.get(phaseType));
                tb.printVal(res.phaseCircErr);
                tb.printVal(res.phaseToZero.get(phaseType));
                tb.printVal(res.phaseToWindow.get(phaseType));
                tb.printVal(res.phaseErr);
                
                tb.printLabel("");
                tb.printVal(res.amplitude.get(phaseType));
                tb.printVal(res.amplitudeErr);
                
                tb.printLabel("");
                tb.printVal(res.GOF);
                tb.printVal(res.ERR);
            }
            tb.endln();
        });
        return tb;
    }

    protected String resStatus(PPASimpleResultEntry res) {
        if (res.failed) return "FAILED "+res.message;
        if (res.ignored) return "IGNORED";
        if (res.attention) return "ATTENTION";
        return "";
    }
    
}
