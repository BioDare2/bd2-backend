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
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleStats;
import ed.biodare2.backend.testutil.PPATestSeederJC2;
import ed.robust.dom.tsprocessing.PhaseType;
import ed.robust.dom.tsprocessing.StatsEntry;
import ed.robust.error.RobustFormatException;
import ed.robust.ppa.PPAMethod;
import ed.biodare2.backend.util.TableBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
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
 * @author tzielins
 */
public class PPAResultsExporterJC2Test {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    PPATestSeederJC2 seeder;  
    PPAResultsExporterJC2 exporter;
    
    public PPAResultsExporterJC2Test() {
    }
    
    @Before
    public void setUp() {
        exporter = new PPAResultsExporterJC2();
        seeder = new PPATestSeederJC2();

    }

    @Test
    public void exportPPAJobSavesToFile() throws IOException, RobustFormatException {
        
        
        //Path file = Paths.get("E:/Temp/exported.csv");
        Path file = testFolder.newFile().toPath();
        
        ExperimentalAssay exp = mock(ExperimentalAssay.class);
        when(exp.getId()).thenReturn(5670L);
        when(exp.getName()).thenReturn("namecontent");        
        
        PPAJobSummary job = seeder.getJobSummary();
        PPAJobSimpleResults results = seeder.getJobSimpleResults(job);
        PPAJobSimpleStats stats = seeder.getJobSimpleStats(job);
        FakeIdExtractor idsCache = new FakeIdExtractor(seeder.getData());
        PhaseType phaseType = PhaseType.ByFit;
        
        
        exporter.exportPPAJob(exp, job, results, stats, idsCache, phaseType, file);
        assertTrue(Files.isRegularFile(file));
        assertTrue(Files.size(file) > 10);
    }
    
    @Test
    public void exportPPAFullStats() throws IOException, RobustFormatException {
        
        
        //Path file = Paths.get("E:/Temp/stats_exported.csv");
        Path file = testFolder.newFile().toPath();
        
        ExperimentalAssay exp = mock(ExperimentalAssay.class);
        when(exp.getId()).thenReturn(5670L);
        when(exp.getName()).thenReturn("namecontent");        
        
        PPAJobSummary job = seeder.getJobSummary();
        StatsEntry stats = seeder.getJobFullStats(job);
        FakeIdExtractor idsCache = new FakeIdExtractor(seeder.getData());
        
        exporter.exportPPAFullStats(exp, job, stats, idsCache, file);
        assertTrue(Files.isRegularFile(file));
        assertTrue(Files.size(file) > 10);

    }    
    
    @Test
    public void exportJoinedFullResults() throws IOException, RobustFormatException {
        
        
        //Path file = Paths.get("E:/Temp/res_exported.csv");
        Path file = testFolder.newFile().toPath();
        
        ExperimentalAssay exp = mock(ExperimentalAssay.class);
        when(exp.getId()).thenReturn(5670L);
        when(exp.getName()).thenReturn("namecontent");        
        
        List<PPAJobSummary> jobs = List.of(
                seeder.getJobSummary(seeder.fftJob),
                seeder.getJobSummary(seeder.mesaJob));
        
        List<PPAFullResultEntry> results = jobs.stream()
                .flatMap( j -> seeder.getJobFullResults(j).results.stream())
                .collect(Collectors.toList());
        
        FakeIdExtractor idsCache = new FakeIdExtractor(seeder.getData());
        
        exporter.exportJoinedFullResults(exp, jobs, results, idsCache, file);
        assertTrue(Files.isRegularFile(file));
        assertTrue(Files.size(file) > 10);

    }    
    
    
    @Test
    public void serializeJobPrintsJobAndExperimentDetails() {
        
        PPAJobSummary job = new PPAJobSummary();
        job.jobId = UUID.randomUUID();
        job.summary = "summarycontent";
        job.method = PPAMethod.NLLS;
        job.submitted = LocalDateTime.now();
        job.completed = LocalDateTime.now();
        job.needsAttention = true;
        
        ExperimentalAssay exp = mock(ExperimentalAssay.class);
        when(exp.getId()).thenReturn(5670L);
        when(exp.getName()).thenReturn("namecontent");
        
        TableBuilder tb = exporter.serializeJob(exp, job);
        String txt = tb.toString();
        
        assertTrue(txt.contains(job.jobId.toString()));
        assertTrue(txt.contains("summarycontent"));
        assertTrue(txt.contains("namecontent"));
        assertTrue(txt.contains("5670"));
        assertTrue(txt.contains("FFT NLLS"));
        assertTrue(txt.contains("WARNING"));
    }
    
    @Test
    public void serializeSimpleStatsDealsWithStats() throws IOException, RobustFormatException {
        
        PPAJobSummary job = seeder.getJobSummary();
        PPAJobSimpleStats stats = seeder.getJobSimpleStats(job);
        PhaseType phaseType = PhaseType.ByFit;
        
        FakeIdExtractor idsCache = new FakeIdExtractor(seeder.getData());

        PPASimpleStats stat = stats.stats.get(0);
        stat.N = 100;
        stat.period = 24;
        stat.periodStd = 1;
        
        
        TableBuilder tb = exporter.serializeSimpleStats(stats, phaseType, idsCache);
        
        String txt = tb.toString();
        assertTrue(txt.contains(""+stat.N));
        assertTrue(txt.contains(""+stat.period));
        assertTrue(txt.contains(""+stat.periodStd));
    }
    
}
