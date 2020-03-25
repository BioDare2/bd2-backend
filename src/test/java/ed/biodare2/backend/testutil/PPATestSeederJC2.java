/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRole;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAFullResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobIndResults;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary;
import ed.biodare2.backend.util.xml.XMLUtil;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.tsprocessing.StatsEntry;
import ed.robust.error.RobustFormatException;
import ed.robust.util.timeseries.TimeSeriesFileHandler;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author tzielins
 */
public class PPATestSeederJC2 {
    
    public ObjectMapper mapper;
    
    public final String fftJob = "ca99";
    public final String mesaJob = "c082";
    public final String data = "p24_48-120.csv";
    
    XMLUtil xmlUtil = new XMLUtil();
    
    public static ObjectMapper makeMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();        
        return mapper;
    }
    
    public PPATestSeederJC2() {
        mapper = makeMapper();
    }
    
    /*public PPATestSeederJC2(ObjectMapper mapper) {
        this.mapper = mapper;
    }*/
    
    @Test
    public void testFiles() {
        
        Path f = getJobFile(fftJob, "PPA_JOB_SUMMARY.json");
        assertTrue(Files.isRegularFile(f));
        
    }
    
    @Test
    public void testJobSummary() throws IOException {
        assertNotNull(getJobSummary());
    }
    
    @Test
    public void testJobSimpleResults() throws IOException {
        assertNotNull(getJobSimpleResults(getJobSummary()));
        
    }
    
    @Test
    public void testJobSimpleStats() throws IOException {
        assertNotNull(getJobSimpleStats(getJobSummary()));
        
    } 
    
    @Test
    public void testJobFullStats() throws IOException {
        assertEquals(5, getJobFullStats(getJobSummary()).getStats().size());
        
    }    
    
    @Test
    public void testJobFullResults() throws IOException {
        assertEquals(10, getJobFullResults(getJobSummary()).results.size());
        
    }     
    
    @Test
    public void testGetData() throws RobustFormatException, IOException {
        assertEquals(10, getData().size());
    }

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
    

    public List<DataTrace> getData() throws RobustFormatException, IOException {
        
        Path f = getTestLocation(data);
        List<TimeSeries> series = TimeSeriesFileHandler.readFromText(f.toFile(), ",", 10);
        
        List<DataTrace> traces = new ArrayList<>(series.size());
        for (int i =0; i< series.size(); i++) {

            traces.add(makeTrace(i, series.get(i)));
        }
        return traces;
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




    
}
