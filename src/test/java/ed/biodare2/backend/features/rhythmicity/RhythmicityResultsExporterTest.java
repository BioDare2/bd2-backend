/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.rhythmicity;

import ed.biodare.jobcentre2.dom.JobResults;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare.jobcentre2.dom.TSResult;
import ed.biodare.rhythm.ejtk.BD2eJTKRes;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeBD2EJTKResults;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeDataTraces;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeRhythmicityJobSummary;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.rhythmicity.RhythmicityJobSummary;
import ed.biodare2.backend.util.TableBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.*;
/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class RhythmicityResultsExporterTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    public RhythmicityResultsExporterTest() {
    }
    RhythmicityResultsExporter instance;
    
    @Before
    public void setUp() {
        instance = new RhythmicityResultsExporter();
    }

    @Test
    public void serializeJobPrintsJobAndExperimentDetails() {

        ExperimentalAssay exp = mock(ExperimentalAssay.class);
        when(exp.getId()).thenReturn(5670L);
        when(exp.getName()).thenReturn("exp name");
        
        RhythmicityJobSummary job = makeRhythmicityJobSummary(UUID.randomUUID(), 5670L);
        job.jobStatus.state = State.SUCCESS; 
        job.jobStatus.completed = LocalDateTime.now();
        
        
        TableBuilder tb = instance.serializeJob(exp, job);        
        String txt = tb.toString();
        
        assertTrue(txt.contains("5670"));
        assertTrue(txt.contains("exp name"));
        assertTrue(txt.contains(job.jobId.toString()));
        assertTrue(txt.contains("BD2EJTK"));
        assertTrue(txt.contains("BD2_CLASSIC"));
    }
    
    @Test
    public void serlializeResutlsPrintsResultsAndLabels() {
        
        List<DataTrace> traces = makeDataTraces(1,2);
        traces.get(0).details.dataLabel = "first";
        traces.get(1).details.dataLabel = "second";
        
        Map<Long,DataTrace> orgData = traces.stream().collect(Collectors.toMap( dt -> dt.dataId, dt -> dt)); 
        
        JobResults<TSResult<BD2eJTKRes>> results = makeBD2EJTKResults(UUID.randomUUID(), 123, 1, 2); 
        results.results.get(0).result.empP = 3.14567;
        
        TableBuilder tb = instance.serializeResults(results, orgData);
        
        String content = tb.toString();
        
        assertTrue(content.contains("first"));
        assertTrue(content.contains("second"));
        assertTrue(content.contains("3.14567"));
    }
    
    @Test
    public void exportSavesToFile() throws IOException {
        
        Path file = testFolder.newFile().toPath();
        
        ExperimentalAssay exp = mock(ExperimentalAssay.class);
        when(exp.getId()).thenReturn(5670L);
        when(exp.getName()).thenReturn("exp name");
        
        RhythmicityJobSummary job = makeRhythmicityJobSummary(UUID.randomUUID(), 5670L);
        job.jobStatus.state = State.SUCCESS; 
        job.jobStatus.completed = LocalDateTime.now();
        
        List<DataTrace> traces = makeDataTraces(1,2);
        traces.get(0).details.dataLabel = "first";
        traces.get(1).details.dataLabel = "second";
        
        Map<Long,DataTrace> orgData = traces.stream().collect(Collectors.toMap( dt -> dt.dataId, dt -> dt)); 
        
        JobResults<TSResult<BD2eJTKRes>> results = makeBD2EJTKResults(UUID.randomUUID(), 123, 1, 2); 
        results.results.get(0).result.empP = 3.14567;

        instance.exportJob(exp, job, results, orgData, file);
        assertTrue(Files.isRegularFile(file));
        assertTrue(Files.size(file) > 10);
    }
    
    
}
