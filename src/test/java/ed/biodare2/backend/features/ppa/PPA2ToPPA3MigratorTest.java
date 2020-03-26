/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare2.backend.features.ppa.dao.PPAArtifactsRepJC2;
import ed.biodare2.backend.repo.dao.PPAArtifactsRep;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobIndResults;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAFullResultEntry;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.MockExperimentPack;
import ed.biodare2.backend.testutil.PPATestSeederJC2;
import ed.biodare2.backend.util.xml.XMLUtil;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.tsprocessing.ResultsEntry;
import ed.robust.error.RobustFormatException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author tzielins
 */
public class PPA2ToPPA3MigratorTest {
    
    public PPA2ToPPA3MigratorTest() {
    }
    
    ObjectMapper mapper = PPATestSeederJC2.makeMapper();
    PPAArtifactsRep ppa2Rep;
    PPAArtifactsRepJC2 ppa3RepJC2; 
    PPAJC2ResultsHandler resultsHandler;
    XMLUtil xmlUtil = new XMLUtil();
    
    PPA2ToPPA3Migrator instance;
    
    @Before
    public void setUp() {
        
     ppa2Rep = mock(PPAArtifactsRep.class);
     ppa3RepJC2 = mock(PPAArtifactsRepJC2.class);           
     resultsHandler = mock(PPAJC2ResultsHandler.class);
     instance = new PPA2ToPPA3Migrator(ppa2Rep, ppa3RepJC2, resultsHandler);
    }

    @Test
    public void toNewStateWorks() {
        
        assertEquals(State.FINISHED, instance.toNewState(ed.robust.jobcenter.dom.state.State.FINISHED));
        assertEquals(State.SUCCESS, instance.toNewState(ed.robust.jobcenter.dom.state.State.SUCCESS));
        assertEquals(State.SUBMITTED, instance.toNewState(ed.robust.jobcenter.dom.state.State.SUBMITTED));
    }
    
    @Test
    public void toUUIDWorks() {
        String id = instance.toUUID(1000, 123).toString();
        UUID uuid = UUID.fromString(id);
        assertNotNull(uuid);
        
    }
    
    @Test
    public void toLocalDateWorks() {
        
        LocalDateTime org = LocalDateTime.now();
        long time = org.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Date date = new Date(time);
        
        LocalDateTime cpy = instance.toLocalDate(date);
        org  = org.truncatedTo(ChronoUnit.SECONDS);
        cpy = cpy.truncatedTo(ChronoUnit.SECONDS);
        
        assertEquals(org, cpy);
    }
    
    @Test
    public void convertsJobSummary() throws IOException {
        Path file = getFileLocation("old_PPA_JOB_SUMMARY.json");
        
        PPAJobSummary oldJob = mapper.readValue(file.toFile(), PPAJobSummary.class);
        
        ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary upgraded = instance.upgradeJob(oldJob, 123);
        
        assertNotNull(upgraded.jobId);
        assertEquals(oldJob.jobId, upgraded.oldId);
        assertEquals(123, upgraded.parentId);
        assertEquals(oldJob.summary, upgraded.summary);
        assertEquals(LocalDate.of(2020, 3, 18), upgraded.submitted.toLocalDate());
        
    }
    
    @Test
    public void convertsFullResults() throws Exception {
        Path file = getFileLocation("old_PPA_RESULTS.xml");
        PPAJobIndResults oldResults = xmlUtil.readFromFile(file, PPAJobIndResults.class);
        
        ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary job = new ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary();
        job.jobId = UUID.randomUUID();
        
        List<PPAFullResultEntry> newResults = instance.upgradeResults(oldResults.results, job);
        assertEquals(oldResults.results.size(), newResults.size());
        
        for (int i =0; i< newResults.size(); i++) {
            ResultsEntry oldRes = oldResults.results.get(i);
            PPAFullResultEntry newRes = newResults.get(i);
            assertEquals(job.jobId, newRes.jobId);
            assertEquals(oldRes.dataId, newRes.dataId);
            assertEquals(oldRes.getResult(), newRes.result);
        };
        
    }
    
    @Test
    public void migratesJob() throws IOException, RobustFormatException {
        
        AssayPack exp = new MockExperimentPack(123);
        
        Path file = getFileLocation("old_PPA_JOB_SUMMARY.json");        
        PPAJobSummary oldJob = mapper.readValue(file.toFile(), PPAJobSummary.class);
        
        Map<Long, TimeSeries> fits = fakeFits();
        
        file = getFileLocation("old_PPA_RESULTS.xml");
        List<ResultsEntry> oldResults = xmlUtil.readFromFile(file, PPAJobIndResults.class).results;
        
        when(ppa2Rep.getFits(oldJob.jobId, exp)).thenReturn(Optional.of(fits));
        when(ppa2Rep.getJobIndResults(exp, oldJob.jobId)).thenReturn(oldResults);
        
        ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary job = instance.migrate(oldJob, exp);
        assertNotNull(job);
        assertEquals(oldJob.jobId, job.oldId);
        
        verify(ppa3RepJC2).saveJobSummary(job, exp);
        verify(ppa3RepJC2).saveFits(fits, job.jobId, exp);
        verify(resultsHandler).processJobResults(any(), eq(job), eq(exp));
        
        
    }    
    
    Map<Long, TimeSeries> fakeFits() throws RobustFormatException, IOException {
        PPATestSeederJC2 seeder = new PPATestSeederJC2();
        List<DataTrace> traces = seeder.getData();
        assertNotNull(traces);
        
        
        return traces.stream().collect(Collectors.toMap( d -> d.dataId, d -> d.trace));
    }
    
    Path getFileLocation(String fName)  {
        try {
            return new File(this.getClass().getResource(fName).toURI()).toPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }    
    
}
