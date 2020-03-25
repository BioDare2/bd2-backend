/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare2.backend.features.ppa.dao.PPAArtifactsRepJC2;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.handlers.ExperimentHandler;

import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeDataTraces;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.ppa.PPARequest;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.MockExperimentPack;
import ed.biodare2.backend.testutil.PPATestSeederJC2;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
public class PPAJC2HandlerTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    ExperimentHandler experimentHandler;
    PPAArtifactsRepJC2 ppaRep;
    TSDataHandler dataHandler;
    PPAJC2AnalysisService ppaService;
    PPAJC2ResultsHandler ppaResultsHandler;
    

    PPATestSeederJC2 seeder =new PPATestSeederJC2();     
    
    PPAJC2Handler instance;
    
    public PPAJC2HandlerTest() {
    }
    
    @Before
    public void setUp() {
        
        experimentHandler = mock(ExperimentHandler.class);
        ppaRep = mock(PPAArtifactsRepJC2.class);
        dataHandler = mock(TSDataHandler.class);
        ppaService = mock(PPAJC2AnalysisService.class);
        ppaResultsHandler = mock(PPAJC2ResultsHandler.class);
        
        instance = new PPAJC2Handler(experimentHandler, ppaRep, ppaService, dataHandler, ppaResultsHandler);
    }

    @Test
    public void newRhythmicitySubmitsJob() throws Exception {
        
        long expId = 123;
        AssayPack exp = new MockExperimentPack(expId);    
        
        PPARequest req = DomRepoTestBuilder.makePPARequest();        
        
        List<DataTrace> dataSet = makeDataTraces(1, 1);    
        when(dataHandler.getDataSet(exp, req.detrending)).thenReturn(Optional.of(dataSet));     
        
        UUID uid = UUID.randomUUID();
        when(ppaService.submitJob(any())).thenReturn(uid);
        
        UUID res = instance.newPPA(exp, req);
        
        assertEquals(uid, res);
        verify(ppaRep).saveJobSummary(any(), any());
        verify(experimentHandler).updateHasPPAJobs(exp,true);
        
    }

    @Test
    public void exportsFullResults() throws Exception {
        
        long expId = 123;
        AssayPack exp = new MockExperimentPack(expId);    
        
        List<DataTrace> dataSet = seeder.getData();            
        when(dataHandler.getDataSet(any(), any())).thenReturn(Optional.of(dataSet));     
        
        List<PPAJobSummary> jobs = List.of(
                seeder.getJobSummary(seeder.fftJob),
                seeder.getJobSummary(seeder.mesaJob));        
        
        when(ppaRep.getJobsSummaries(exp)).thenReturn(jobs);
        
        when(ppaRep.getJobIndResults(exp, jobs.get(0).jobId)).thenReturn(seeder.getJobFullResults(jobs.get(0)).results);
        when(ppaRep.getJobIndResults(exp, jobs.get(1).jobId)).thenReturn(seeder.getJobFullResults(jobs.get(1)).results);
        
        when(ppaRep.getJobFullStats(exp, jobs.get(0).jobId)).thenReturn(seeder.getJobFullStats(jobs.get(0)));
        when(ppaRep.getJobFullStats(exp, jobs.get(1).jobId)).thenReturn(seeder.getJobFullStats(jobs.get(1)));
        
        Path exported = null;
        try {
            exported = instance.exportFullPPAResults(exp);
            assertTrue(Files.isRegularFile(exported));
            assertTrue(Files.size(exported) > 100);
        } finally {
            if (exported != null) {
                Files.delete(exported);
            }
        }
    }
}
