/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPASimpleResultEntry;
import ed.robust.dom.tsprocessing.PhaseType;
import ed.robust.util.TableBuilder;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 *
 * @author tzielins
 */
public class PPAResultsExporter {

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

    protected void printJobSummary(ExperimentalAssay exp, PPAJobSummary job, BufferedWriter writer) throws IOException {
        
        TableBuilder tb = serializeJob(exp,job);
        
        writer.write(tb.toString());
    }

    protected TableBuilder serializeJob(ExperimentalAssay exp, PPAJobSummary job) {
        
        TableBuilder tb = new TableBuilder(SEP, ESC);
        tb.printlnParam("Experiment ID", exp.getId());
        tb.printlnParam("Experiment name", exp.getName());
        tb.printlnParam("Job", job.jobId);
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
