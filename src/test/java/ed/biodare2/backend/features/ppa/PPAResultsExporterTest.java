/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.exp.ExperimentalAssay;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPASimpleStats;
import ed.biodare2.backend.testutil.PPATestSeeder;
import ed.robust.dom.jobcenter.JobSummary;
import ed.robust.dom.tsprocessing.PhaseType;
import ed.robust.ppa.PPAMethod;
import ed.robust.util.TableBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
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
public class PPAResultsExporterTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    PPAResultsExporter exporter;
    PPATestSeeder seeder;
    
    public PPAResultsExporterTest() {
    }
    
    @Before
    public void setUp() {
        exporter = new PPAResultsExporter();
        seeder = new PPATestSeeder();
        seeder.mapper = new ObjectMapper();
    }

    @Test
    public void exportSavesToFile() throws IOException {
        
        
        //Path file = Paths.get("E:/Temp/exported.csv");
        Path file = testFolder.newFile().toPath();
        
        ExperimentalAssay exp = mock(ExperimentalAssay.class);
        when(exp.getId()).thenReturn(5670L);
        when(exp.getName()).thenReturn("namecontent");        
        
        JobSummary job = seeder.getJob();
        PPAJobSimpleResults results = seeder.getJobSimpleResults(job);
        PPAJobSimpleStats stats = seeder.getJobSimpleStats(job);
        FakeIdExtractor idsCache = new FakeIdExtractor(seeder.getData());
        PhaseType phaseType = PhaseType.ByFit;
        
        
        exporter.exportPPAJob(exp, PPAUtils.simplifyJob(job), results, stats, idsCache, phaseType, file);
        assertTrue(Files.isRegularFile(file));
        assertTrue(Files.size(file) > 10);
    }
    
    @Test
    public void serializeJobPrintsJobAndExperimentDetails() {
        
        PPAJobSummary job = new PPAJobSummary();
        job.jobId = 12345;
        job.summary = "summarycontent";
        job.method = PPAMethod.NLLS;
        job.submitted = new Date();
        job.completed = new Date();
        job.needsAttention = true;
        
        ExperimentalAssay exp = mock(ExperimentalAssay.class);
        when(exp.getId()).thenReturn(5670L);
        when(exp.getName()).thenReturn("namecontent");
        
        TableBuilder tb = exporter.serializeJob(exp, job);
        String txt = tb.toString();
        
        assertTrue(txt.contains("12345"));
        assertTrue(txt.contains("summarycontent"));
        assertTrue(txt.contains("namecontent"));
        assertTrue(txt.contains("5670"));
        assertTrue(txt.contains("FFT NLLS"));
        assertTrue(txt.contains("WARNING"));
    }
    
    @Test
    public void serializeSimpleStatsDealsWithStats() {
        
        JobSummary job = seeder.getJob();
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
