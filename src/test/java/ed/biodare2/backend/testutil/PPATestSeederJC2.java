/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ed.biodare2.backend.features.ppa.dao.PPAArtifactsRepJC2;
import ed.biodare2.backend.features.tsdata.datahandling.DataProcessingException;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRole;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAFullResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobIndResults;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.util.xml.XMLUtil;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.tsprocessing.StatsEntry;
import ed.robust.error.RobustFormatException;
import ed.robust.util.timeseries.TimeSeriesFileHandler;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;
/**
 *
 * @author tzielins
 */
@Service
public class PPATestSeederJC2 {
    
    public ObjectMapper mapper;
    
    public final String fftJob = "ca99";
    public final String mesaJob = "c082";
    public final String data = "p24_48-120.csv";
    
    XMLUtil xmlUtil = new XMLUtil();
    
    @Autowired
    PPAArtifactsRepJC2 ppaRep;
    
    @Autowired
    TSDataHandler tsHandler;    
    
    public static ObjectMapper makeMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();  
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return mapper;
    }
    
    public PPATestSeederJC2() {
        this(makeMapper());
    }
    
    @Autowired
    public PPATestSeederJC2(ObjectMapper mapper) {
        this.mapper = mapper;
    }    
    
    /*public PPATestSeederJC2(ObjectMapper mapper) {
        this.mapper = mapper;
    }*/
    

    public PPAJobSummary getJobSummary() throws IOException {
        return getJobSummary(fftJob);
    }
    
    public PPAJobSummary getJobSummary(String job) throws IOException {
        Path f = getJobFile(job, "PPA_JOB_SUMMARY.json");
        return mapper.readValue(f.toFile(), PPAJobSummary.class);
        
    }
    
    
    public PPAJobSimpleResults getJobSimpleResults(PPAJobSummary job) throws IOException {
        return getJobSimpleResults(job2name(job));
    }
    
    public PPAJobSimpleResults getJobSimpleResults(String job) throws IOException {
        Path f = getJobFile(job, "PPA_SIMPLE_RESULTS.json");
        return mapper.readValue(f.toFile(), PPAJobSimpleResults.class);
    }
    

    public PPAJobSimpleStats getJobSimpleStats(PPAJobSummary job) throws IOException {
        return getJobSimpleStats(job2name(job));
    }
    
    public PPAJobSimpleStats getJobSimpleStats(String job) throws IOException {
        Path f = getJobFile(job, "PPA_SIMPLE_STATS.json");
        return mapper.readValue(f.toFile(), PPAJobSimpleStats.class);
    }

    public StatsEntry getJobFullStats(PPAJobSummary job) throws IOException {
        return getJobFullStats(job2name(job));
    }

    public StatsEntry getJobFullStats(String job) throws IOException {
        Path f = getJobFile(job, "PPA_FULL_STATS.json");
        return mapper.readValue(f.toFile(), StatsEntry.class);
    }     

    public StatsEntry getJobFullXMLStats(String job) throws IOException {
        Path f = getJobFile(job, "PPA_FULL_STATS.xml");
        return xmlUtil.readFromFile(f, StatsEntry.class);
    }  
    
    public PPAJobIndResults getJobFullResults(PPAJobSummary job) {        
        return getJobFullResults(job2name(job));
    }
    
    public PPAJobIndResults getJobFullResults(String job) {
        try {
            Path f = getJobFile(job, "PPA_FULL_RESULTS.json");
            return  mapper.readValue(f.toFile(), PPAJobIndResults.class);    
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    } 
    
    public PPAJobResultsGroups getJobResultsGroups(PPAJobSummary job) {
        return getJobResultsGroups(job2name(job));
    }

    public PPAJobResultsGroups getJobResultsGroups(String job) {
        try {
            Path f = getJobFile(job, "PPA_GROUPED_RESULTS.json");
            return  mapper.readValue(f.toFile(), PPAJobResultsGroups.class);    
        } catch (IOException e) {
            throw new RuntimeException(e);
        }    }
    
    

    public List<DataTrace> getData() throws RobustFormatException, IOException {
        
        Path f = getTestLocation(data);
        List<TimeSeries> series = TimeSeriesFileHandler.readFromText(f.toFile(), ",", 10);
        
        List<DataTrace> traces = new ArrayList<>(series.size());
        for (int i =0; i< series.size(); i++) {

            traces.add(makeTrace(i, series.get(i)));
        }
        return traces;
    }
    
    public Map<Long, TimeSeries> getFits(PPAJobSummary job) {
        return getFits(job2name(job));
    }
    
    public Map<Long, TimeSeries> getFits(String job) {
        Path file = getJobFile(job, "fit."+job+".ser");
        
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
                
                Map<Long, TimeSeries> map = (Map<Long, TimeSeries>)in.readObject();
                return map;
                
            } catch (IOException|ClassNotFoundException e) {
                throw new ServerSideException("Cannot read fits: "+e.getMessage(),e);
            }        
    }    
    
    public DataTrace makeTrace(int i, TimeSeries data) {
            DataTrace trace = new DataTrace();
            trace.dataId = i;
            trace.details = new DataColumnProperties(""+i);
            trace.rawDataId = i;
            trace.role = CellRole.DATA;
            trace.trace = data;
            trace.traceFullRef = ""+i;
            trace.traceNr = i;
            trace.traceRef = ""+i;
            return trace;
    }
    
    Path getTestLocation(String job)  {
        try {
            return new File(this.getClass().getResource(job).toURI()).toPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    Path getJobFile(String job, String file)  {
        return getTestLocation(job).resolve(file);
    }
    
    String job2name(PPAJobSummary job) {
        return job.jobId.toString().substring(0,4);
    }

    public List<PPAJobSummary> getJobs() throws IOException {
        return List.of(getJobSummary(fftJob),getJobSummary(mesaJob));
    }

    public void seedJustJob(PPAJobSummary job, AssayPack exp) {
        job.parentId = exp.getId();
        ppaRep.saveJobSummary(job, exp);
    }

    public void seedFullJob(PPAJobSummary job, AssayPack exp) {
        try {
        job.parentId = exp.getId();
        seedData(getData(),exp);
        seedJustJob(job, exp);
        seedJobArtifacts(job,exp);
        } catch (IOException | RobustFormatException e) {
            throw new RuntimeException("Cannot seed job "+e.getMessage(),e);
        }
        
    }


    void seedJobArtifacts(PPAJobSummary job, AssayPack exp) throws IOException {
        UUID jobId = job.jobId;
        PPAJobIndResults indResults = getJobFullResults(job);
        PPAJobSimpleResults simpleRes = getJobSimpleResults(job);
        PPAJobResultsGroups grouped = getJobResultsGroups(job);
        PPAJobSimpleStats simpleStats = getJobSimpleStats(job);
        StatsEntry fullStats = getJobFullStats(job);
        Map<Long,TimeSeries> fits = getFits(job);
        
        ppaRep.saveJobIndResults(indResults.results, exp, jobId);
        ppaRep.saveJobSimpleResults(simpleRes, exp, jobId);
        ppaRep.saveJobResultsGroups(grouped, exp, jobId);
        ppaRep.saveJobSimpleStats(simpleStats, exp, jobId);
        ppaRep.saveJobFullStats(fullStats, exp, jobId);
        ppaRep.saveFits(fits, jobId, exp);
    }

    public void seedData(List<DataTrace> data, AssayPack exp) {
        
        DataBundle rawData = new DataBundle();
        rawData.data.addAll(data);
        
        try {
            tsHandler.handleNewData(exp, rawData);
        } catch (DataProcessingException e) {
            throw new RuntimeException("Cannot seed data: "+e.getMessage(),e);
        }        
    }
    






    
}
