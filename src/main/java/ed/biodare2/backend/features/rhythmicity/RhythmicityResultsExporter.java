/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

import ed.biodare.jobcentre2.dom.JobResults;
import ed.biodare.jobcentre2.dom.TSResult;
import ed.biodare.rhythm.ejtk.BD2eJTKRes;
import ed.biodare2.backend.features.ppa.FakeIdExtractor;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import ed.robust.util.TableBuilder;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class RhythmicityResultsExporter {

    protected final String SEP = ",";
    protected final String ESC = "\"";
    
    public void exportJob(ExperimentalAssay exp, RhythmicityJobSummary job, JobResults<TSResult<BD2eJTKRes>> results, Map<Long, DataTrace> orgData, Path file) throws IOException {
        
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {        
    
            printJobSummary(exp,job,writer);
            writer.newLine();
            
            printResults(results,orgData, writer);
            
        }
    }

    void printJobSummary(ExperimentalAssay exp, RhythmicityJobSummary job, BufferedWriter writer) throws IOException {
        TableBuilder tb = serializeJob(exp,job);
        
        writer.write(tb.toString());
    }

    void printResults(JobResults<TSResult<BD2eJTKRes>> results, Map<Long, DataTrace> orgData, BufferedWriter writer) throws IOException {
        
        TableBuilder tb = serializeResults(results, orgData);
        
        writer.write(tb.toString());
    }
    
    protected TableBuilder serializeResults(JobResults<TSResult<BD2eJTKRes>> results, Map<Long, DataTrace> orgData) {
        TableBuilder tb = new TableBuilder(SEP, ESC);
        tb.printlnLabel("Individual Results");
        tb.endln();
        

        tb.printLabel("Data Id");
        tb.printLabel("Data Label");
        tb.printLabel("");
        tb.printLabel("tau");
        tb.printLabel("emp p");
        tb.printLabel("emp p BH Corrected");
        tb.printLabel("Pattern");
        tb.printLabel("");
        tb.printLabel("p from tau");
        tb.printLabel("bf corrected p");
        tb.endln();
        
        results.results.forEach( entry -> {
            tb.printVal(entry.id);
            tb.printVal(labelTrace(orgData.get(entry.id)));
            tb.printVal("");

            BD2eJTKRes result = entry.result;
            
            tb.printVal(result.tau);
            tb.printVal(result.empP);
            tb.printVal(result.empPBH);
            tb.printVal(result.pattern.toString());
            tb.printVal("");
            tb.printVal(result.p);
            tb.printVal(result.bfP);

            tb.endln();
        });
        return tb;
    }
    
    String labelTrace(DataTrace data) {
        String label = data.details.dataLabel;
        return label;
    }    
    
    
    protected TableBuilder serializeJob(ExperimentalAssay exp, RhythmicityJobSummary job) {
        
        TableBuilder tb = new TableBuilder(SEP, ESC);
        tb.printlnParam("Experiment ID", exp.getId());
        tb.printlnParam("Experiment name", exp.getName());
        tb.printlnParam("Job", job.jobId.toString());
        tb.printParam("Submitted", job.jobStatus.submitted);
        tb.printlnParam("Finished", job.jobStatus.completed);
        job.parameters.forEach( (key, val) -> {
            tb.printlnParam(key, val);
        });
        tb.endln();

        return tb;
    }    
    
}
