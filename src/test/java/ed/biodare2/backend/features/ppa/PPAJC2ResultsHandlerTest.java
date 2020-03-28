/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare.jobcentre2.dom.JobResults;
import ed.biodare.jobcentre2.dom.PPAJobResults;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare.jobcentre2.dom.TSResult;
import static ed.biodare2.backend.features.ppa.PPAUtilsJC2.periodToInt;
import ed.biodare2.backend.features.ppa.dao.PPAArtifactsRepJC2;

import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.handlers.ArgumentException;
import ed.biodare2.backend.repo.dao.ExperimentsStorage;

import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeDataTraces;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAFullResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobResultsGroups;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAResultsGroupSummary;

import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.MockExperimentPack;
import ed.biodare2.backend.web.rest.HandlingException;
import ed.robust.dom.data.DetrendingType;


import ed.robust.dom.tsprocessing.FFT_PPA;
import ed.robust.dom.tsprocessing.FailedPPA;
import ed.robust.dom.tsprocessing.GenericPPAResult;
import ed.robust.dom.tsprocessing.PPA;
import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.dom.tsprocessing.PPAStats;
import ed.robust.dom.tsprocessing.PhaseType;

import ed.robust.dom.tsprocessing.ResultsGroupContainer;
import ed.robust.dom.tsprocessing.StatsEntryContainer;
import ed.robust.dom.tsprocessing.WeightingType;
import ed.robust.dom.util.ComplexId;
import ed.robust.dom.util.ListMap;
import ed.robust.jobcenter.dom.job.JobResult;

import ed.robust.ppa.PPAMethod;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author tzielins
 */
public class PPAJC2ResultsHandlerTest {
    
    public PPAJC2ResultsHandlerTest() {
    }
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    Path expDir;
    
    PPAJC2ResultsHandler instance;
    
    PPAArtifactsRepJC2 ppaRep;
    TSDataHandler dataHandler;
    ExperimentsStorage expStorage;
    
    @Before
    public void init() throws IOException {
        dataHandler = mock(TSDataHandler.class);
        
        expDir = testFolder.newFolder().toPath();
        //expDir = Paths.get("D:/Temp/ppaResTest");
        //Files.createDirectories(expDir);
        
        expStorage = mock(ExperimentsStorage.class);
        when(expStorage.getExperimentDir(anyLong())).thenReturn(expDir);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); 
        ppaRep = new PPAArtifactsRepJC2(expStorage, mapper);        
        instance = new PPAJC2ResultsHandler(ppaRep, dataHandler);
    }   
    
    

    
    protected JobResult<PPAResult> readTestData(String fName) {
     
        try {
        File file = new File(this.getClass().getResource(fName).toURI());
        JAXBContext context = JAXBContext.newInstance(JobResult.class,PPAResult.class);
        
        JobResult<PPAResult> res = (JobResult<PPAResult>)context.createUnmarshaller().unmarshal(file);        
        return res;
        } catch (URISyntaxException| JAXBException e) {
            throw new RuntimeException("xml reading error: "+e.getMessage(),e);
        }
    }
    
    
    private static StatsEntryContainer giveTestsStats() {
        return readTestStats("PPA_STATS.xml");
    }
    
    
    protected static StatsEntryContainer readTestStats(String fName) {
     
        try {
        File file = new File(PPAJC2ResultsHandlerTest.class.getResource(fName).toURI());
        JAXBContext context = JAXBContext.newInstance(StatsEntryContainer.class);
        
        StatsEntryContainer res = (StatsEntryContainer)context.createUnmarshaller().unmarshal(file);        
        return res;
        } catch (URISyntaxException| JAXBException e) {
            throw new RuntimeException("xml reading error: "+e.getMessage(),e);
        }
    }      
    
    private static ResultsGroupContainer giveTestsResults() {
        return readTestResults("PPA_RESULTS.xml");
    }
    
    
    protected static ResultsGroupContainer readTestResults(String fName) {
     
        try {
        File file = new File(PPAJC2ResultsHandlerTest.class.getResource(fName).toURI());
        JAXBContext context = JAXBContext.newInstance(ResultsGroupContainer.class);
        
        ResultsGroupContainer res = (ResultsGroupContainer)context.createUnmarshaller().unmarshal(file);        
        return res;
        } catch (URISyntaxException| JAXBException e) {
            throw new RuntimeException("xml reading error: "+e.getMessage(),e);
        }
    } 
    
    @Test
    public void waitForJobWaitsForJobIfNotPressent() throws IOException {
        dataHandler = mock(TSDataHandler.class);
        ppaRep = mock(PPAArtifactsRepJC2.class);
        
        instance = new PPAJC2ResultsHandler(ppaRep, dataHandler,5,50);
        
        AssayPack exp = new MockExperimentPack(123);
        PPAJobSummary job = new PPAJobSummary();
        UUID jobId = UUID.randomUUID();
        when(ppaRep.getJobSummary(eq(exp), eq(jobId))).thenReturn(Optional.empty());
        
        Executors.newSingleThreadExecutor().submit( () -> {
            try {
                Thread.sleep(40);
                when(ppaRep.getJobSummary(eq(exp), eq(jobId))).thenReturn(Optional.of(job));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        
        PPAJobSummary res = instance.waitForJob(jobId, exp);
        assertSame(job,res);
    }     
    
    @Test
    public void waitForJobThrowsExceptionIfWaitedInVain() throws IOException {
        dataHandler = mock(TSDataHandler.class);
        ppaRep = mock(PPAArtifactsRepJC2.class);
        
        instance = new PPAJC2ResultsHandler(ppaRep, dataHandler,2,5);
        
        AssayPack exp = new MockExperimentPack(123);
        UUID jobId = UUID.randomUUID();
        when(ppaRep.getJobSummary(eq(exp), eq(jobId))).thenReturn(Optional.empty());
        
        try {
            PPAJobSummary res = instance.waitForJob(jobId, exp);
            fail("Exeption expected");
        } catch(HandlingException e){};
    }    
    
    @Test
    public void handleResultsSavesTheResults() throws ArgumentException, IOException {
        

        AssayPack exp = new MockExperimentPack(123);
        UUID jobId = UUID.randomUUID();
        
        JobResult<PPAResult> jobOld = readTestData("res.11.xml");
        PPAJobResults job = new PPAJobResults();
        job.jobId = jobId;
        job.externalId = ""+exp.getId();
        
        job.results = jobOld.getTaskResults().stream()
                .map( t -> {
                    TSResult<PPAResult> r = new TSResult<>(t.getTaskId(), t.getResult());
                    return r;
                }).collect(Collectors.toList());
                
        
        PPAJobSummary jobDesc = new PPAJobSummary();
        jobDesc.jobId = jobId;
        jobDesc.parentId = exp.getId();
        jobDesc.dataSetType = DetrendingType.NO_DTR.name();
        jobDesc.state = State.SUBMITTED;
        jobDesc.method = PPAMethod.NLLS;
        
        //ppaRep.saveJob(jobDesc, exp);
        //ppaRep.saveJobFullDescription(jobDesc, exp);
        ppaRep.saveJobSummary(jobDesc, exp);
        
        List<DataTrace> dataSet = makeDataTraces(2, 7); 
        
        when(dataHandler.getDataSet(exp, DetrendingType.NO_DTR)).thenReturn(Optional.of(dataSet));
        
        assertTrue(ppaRep.getJobResultsGroups(exp, jobDesc.jobId).groups.isEmpty());
        instance.handleResults(exp, job);
        
        
        //StatsEntry stats = ppaRep.getStatsContainer(exp).get(jobId);
        //assertTrue(ppaRep.getJobFullDescription(exp, jobId).isPresent());
        assertTrue(ppaRep.getJobSummary(exp, jobId).isPresent());
        assertEquals(State.FINISHED,ppaRep.getJobSummary(exp, jobId).get().state);
        assertEquals(6,ppaRep.getJobIndResults(exp, jobId).size());
        assertEquals(6,ppaRep.getJobSimpleResults(exp, jobId).results.size());
        assertEquals(2,ppaRep.getJobResultsGroups(exp, jobId).groups.size());
        assertEquals(2,ppaRep.getJobFullStats(exp, jobId).getStats().size());
        assertEquals(2,ppaRep.getJobSimpleStats(exp, jobId).stats.size());
        
        
    }
    
    @Test
    public void groupResultsGroupsThemByBioAndEnvIds() throws Exception {
        List<PPAFullResultEntry> results = new ArrayList<>();
        PPAFullResultEntry ent;
        
        ent = new PPAFullResultEntry();
        ent.dataId= 4;
        ent.biolDescId = 1;
        ent.environmentId = 2;
        results.add(ent);
        
        ent = new PPAFullResultEntry();
        ent.dataId= 2;
        ent.biolDescId = 2;
        ent.environmentId = 2;
        results.add(ent);
        
        ent = new PPAFullResultEntry();
        ent.dataId= 1;
        ent.biolDescId = 1;
        ent.environmentId = 2;
        results.add(ent);
        
        ent = new PPAFullResultEntry();
        ent.dataId= 3;
        ent.biolDescId = 2;
        ent.environmentId = 1;
        results.add(ent);
        
        ListMap<ComplexId<Long>, PPAFullResultEntry> groups = instance.groupResults(results);
        assertEquals(3,groups.size());
        assertEquals(2,groups.get(new ComplexId<>(1L,2L)).size());
        assertEquals(1,groups.get(new ComplexId<>(2L,2L)).size());
        assertEquals(1,groups.get(new ComplexId<>(2L,1L)).size());
        
    }
    
    @Test
    public void groupResultsSortsGroupsMembersByDataId() throws Exception {
        List<PPAFullResultEntry> results = new ArrayList<>();
        PPAFullResultEntry ent;
        
        ent = new PPAFullResultEntry();
        ent.dataId= 4;
        ent.biolDescId = 1;
        ent.environmentId = 2;
        results.add(ent);
        
        ent = new PPAFullResultEntry();
        ent.dataId= 2;
        ent.biolDescId = 1;
        ent.environmentId = 2;
        results.add(ent);
        
        ent = new PPAFullResultEntry();
        ent.dataId= 1;
        ent.biolDescId = 1;
        ent.environmentId = 2;
        results.add(ent);
        
        ent = new PPAFullResultEntry();
        ent.dataId= 3;
        ent.biolDescId = 1;
        ent.environmentId = 2;
        results.add(ent);
        
        ListMap<ComplexId<Long>, PPAFullResultEntry> groups = instance.groupResults(results);
        assertEquals(Arrays.asList(1L,2L,3L,4L),
                groups.get(new ComplexId<>(1L,2L)).stream().map( e -> e.dataId).collect(Collectors.toList())
                );
        
    }
    
    
    @Test
    public void assemblyJobGroupsUsesJobParameters() throws Exception {
        
        List<PPAFullResultEntry> results = new ArrayList<>();
        UUID jobId = UUID.randomUUID();
        
        PPAFullResultEntry ent = new PPAFullResultEntry();
        ent.dataId = 4;
        ent.rawDataId = 5;
        ent.biolDescId = 2;
        ent.environmentId =3;
        ent.jobId = jobId;
        GenericPPAResult ppa = new GenericPPAResult(24, 12, 3);
        ent.result = ppa;
        results.add(ent);
        
        ent = new PPAFullResultEntry();
        ent.dataId = 2;
        ent.jobId = jobId;
        ppa = new GenericPPAResult(24, 12, 3);
        ent.result = (ppa);
        results.add(ent);        
        
        ListMap<ComplexId<Long>, PPAFullResultEntry> groups = new ListMap<>();
        groups.put(new ComplexId(2l,3L), results);
        
        PPAJobSummary job = new PPAJobSummary();
        job.jobId = jobId;
        job.dataSetType = DetrendingType.NO_DTR.name();
        job.dataWindowStart= 1;
        job.state = (State.SUBMITTED);        
        
        PPAJobResultsGroups ans = instance.assemblyJobGroups(groups,job);
        assertEquals(1,ans.groups.size());
        PPAResultsGroupSummary res = ans.groups.get(0);
        assertEquals(4,res.memberDataId);
        assertEquals(5,res.rawId);
        assertEquals(3,res.envId);
        assertEquals(2,res.bioId);
    }
    
    @Test
    public void assemblyJobGroupsSortsGroupsByDataIds() throws Exception {
        
        UUID jobId = UUID.randomUUID();
        
        PPAFullResultEntry ent = new PPAFullResultEntry();
        ent.dataId = (4);
        ent.jobId = jobId;
        GenericPPAResult ppa = new GenericPPAResult(24, 12, 3);
        ent.result = (ppa);
        
        ListMap<ComplexId<Long>, PPAFullResultEntry> groups = new ListMap<>();
        groups.add(new ComplexId(2l,3L), ent);
        
        ent = new PPAFullResultEntry();
        ent.dataId = (2);
        ent.jobId= jobId;
        ppa = new GenericPPAResult(24, 12, 3);
        ent.result = (ppa);        
        groups.add(new ComplexId(3l,3L), ent);
        
        ent = new PPAFullResultEntry();
        ent.dataId = (12);
        ent.jobId = jobId;
        ppa = new GenericPPAResult(24, 12, 3);
        ent.result = (ppa);        
        groups.add(new ComplexId(4l,3L), ent);

        ent = new PPAFullResultEntry();
        ent.dataId = (1);
        ent.jobId = jobId;
        ppa = new GenericPPAResult(24, 12, 3);
        ent.result = (ppa);        
        groups.add(new ComplexId(5l,3L), ent);
        
        PPAJobSummary job = new PPAJobSummary();
        job.jobId = jobId;
        job.dataSetType = DetrendingType.NO_DTR.name();
        job.dataWindowStart= 1;
        job.state = (State.SUBMITTED);         
        
        PPAJobResultsGroups ans = instance.assemblyJobGroups(groups,job);
        ans.groups.stream().map( d -> d.memberDataId).toArray();
        assertEquals(Arrays.asList(1L,2L,4L,12L),ans.groups.stream().map( d -> d.memberDataId).collect(Collectors.toList()));
    }    
    
    @Test
    public void joinResultsPopluatesTheFieldsCountsAndSizesCorrectly() throws ArgumentException, IOException {
        
        List<PPAFullResultEntry> results = new ArrayList<>();
        UUID jobId = UUID.randomUUID();
        
        PPAFullResultEntry ent = new PPAFullResultEntry();
        ent.dataId = (1);
        ent.rawDataId = 2;
        ent.biolDescId = 2;
        ent.environmentId = 3;
        ent.jobId = jobId;
        GenericPPAResult ppa = new GenericPPAResult(24, 12, 3);
        ent.result = (ppa);
        results.add(ent);
        
        ent = new PPAFullResultEntry();
        ent.dataId = (2);
        ent.jobId = jobId;
        ppa = new GenericPPAResult(25, 12, 3);
        ppa.setNeedsAttention(true);
        ent.result = (ppa);
        results.add(ent);
        
        ent = new PPAFullResultEntry();
        ent.dataId = (3);
        ent.jobId = jobId;
        ppa = new GenericPPAResult(26, 12, 3);
        ppa.setNeedsAttention(true);
        ent.result = (ppa);
        results.add(ent);
        
        ent = new PPAFullResultEntry();
        ent.dataId = (4);
        ent.jobId = jobId;
        ent.result = (new FailedPPA());
        results.add(ent);
        
        ent = new PPAFullResultEntry();
        ent.dataId = (3);
        ent.jobId = jobId;
        ppa = new GenericPPAResult(28, 2, 4);
        ent.result = (ppa);
        results.add(ent);

        PPAResultsGroupSummary ans = instance.joinResults(results, 1);
        assertNotNull(ans);
        assertEquals(1,ans.memberDataId);
        assertEquals(2,ans.rawId);
        assertEquals(2,ans.bioId);
        assertEquals(3,ans.envId);        
        assertEquals(1,ans.failures);
        assertEquals(2,ans.excluded);
        assertEquals(2,ans.periods.size());
        assertEquals(true,ans.amplitudes.areAllSet());
        assertEquals(true,ans.phasesToZero.areAllSet());
        assertEquals(true,ans.phasesToZeroCirc.areAllSet());
        assertEquals(true,ans.phasesToWindow.areAllSet());
        assertEquals(true,ans.phasesToWindowCirc.areAllSet());
        
        ans.amplitudes.forEach( vals -> assertEquals(2,vals.size()));
        ans.phasesToZero.forEach( vals -> assertEquals(2,vals.size()));
        ans.phasesToZeroCirc.forEach( vals -> assertEquals(2,vals.size()));
        ans.phasesToWindow.forEach( vals -> assertEquals(2,vals.size()));
        ans.phasesToWindowCirc.forEach( vals -> assertEquals(2,vals.size()));
        
    }
    
    @Test
    public void joinPPAResultsAggregatesValuesCorrectly() {
        
        List<PPAResult> results = new ArrayList<>();
        
        GenericPPAResult res = new GenericPPAResult(20, 10, 2);
        results.add(res);
        
        res = new GenericPPAResult(20, 10, 2);
        res.setPPAByAvgMax(new PPA(21, 11, 3));
        res.setPPAByFirstPeak(new PPA(22, 12, 4));
        res.setPPAByFit(new PPA(23, 13, 5));
        res.setPPAMethodSpecific(new PPA(24, 14, 6));
        results.add(res);
        
        PPAResultsGroupSummary ans = instance.joinPPAResults(results, 1);
        
        List<Double> exp = Arrays.asList(20.0, 24.0);        
        assertEquals(exp, ans.periods);
        
        exp = Arrays.asList(2.0, 6.0);        
        assertEquals(exp, ans.amplitudes.get(PhaseType.ByMethod));        
        exp = Arrays.asList(2.0, 3.0);        
        assertEquals(exp, ans.amplitudes.get(PhaseType.ByAvgMax));        
        exp = Arrays.asList(2.0, 4.0);        
        assertEquals(exp, ans.amplitudes.get(PhaseType.ByFirstPeak));        
        exp = Arrays.asList(2.0, 5.0);        
        assertEquals(exp, ans.amplitudes.get(PhaseType.ByFit));        
        
        exp = Arrays.asList(10.0, 14.0);        
        assertEquals(exp, ans.phasesToZero.get(PhaseType.ByMethod));        
        exp = Arrays.asList(10.0, 11.0);        
        assertEquals(exp, ans.phasesToZero.get(PhaseType.ByAvgMax));        
        exp = Arrays.asList(10.0, 12.0);        
        assertEquals(exp, ans.phasesToZero.get(PhaseType.ByFirstPeak));        
        exp = Arrays.asList(10.0, 13.0);        
        assertEquals(exp, ans.phasesToZero.get(PhaseType.ByFit));        
        
        exp = Arrays.asList(9.0, 13.0);        
        assertEquals(exp, ans.phasesToWindow.get(PhaseType.ByMethod));        
        exp = Arrays.asList(9.0, 10.0);        
        assertEquals(exp, ans.phasesToWindow.get(PhaseType.ByAvgMax));        
        exp = Arrays.asList(9.0, 11.0);        
        assertEquals(exp, ans.phasesToWindow.get(PhaseType.ByFirstPeak));        
        exp = Arrays.asList(9.0, 12.0);        
        assertEquals(exp, ans.phasesToWindow.get(PhaseType.ByFit));        
    }
    
    @Test
    public void joinPPAResultsDoesNotIncludedIngoredOrWithAttention() {
        
        List<PPAResult> results = new ArrayList<>();
        
        GenericPPAResult res = new GenericPPAResult(20, 10, 2);
        res.setNeedsAttention(true);
        results.add(res);
        
        res = new GenericPPAResult(21, 11, 3);
        results.add(res);        
        
        res = new GenericPPAResult(22, 12, 4);
        res.setIgnored(true);
        results.add(res); 
        
        results.add(new FailedPPA());
        
        PPAResultsGroupSummary ans = instance.joinPPAResults(results, 1);
        
        List<Double> exp = Arrays.asList(21.0);        
        assertEquals(exp, ans.periods);
        
        assertEquals(1,ans.failures);
        assertEquals(2,ans.excluded);
    }
    
    
    @Test
    public void calculateGroupsStatsSetsBioDataIdsBasedOnOrginalMembers() {
        
        ListMap<ComplexId<Long>, PPAFullResultEntry> results = new ListMap<>();
        
        PPAFullResultEntry ent = new PPAFullResultEntry();
        ent.biolDescId = 2;
        ent.environmentId =3;
        ent.dataId =4;
        ent.rawDataId = 4;
        ent.result = (new FailedPPA());
        results.add(new ComplexId<>(2L),ent);
        
        ent = new PPAFullResultEntry();
        ent.biolDescId = 3;
        ent.environmentId = 4;
        ent.dataId = 2;
        ent.rawDataId = 4;
        ent.result = (new GenericPPAResult(25, 10, 1));
        results.add(new ComplexId<>(3L),ent);       
        
        List<PPAStats> stats = instance.calculateGroupsStats(results);
        //cuase sorted by dataId
        PPAStats stat = stats.get(0);
        assertEquals(3,stat.getBiolDescId());
        assertEquals(4,stat.getEnvironmentId());
        assertEquals(2,stat.getMemberDataId());
        assertEquals(4,stat.getRawId());
        assertEquals(25,stat.getPeriodStats().getMean(WeightingType.None),1E-6);
        
        stat = stats.get(1);
        assertEquals(2,stat.getBiolDescId());
        assertEquals(3,stat.getEnvironmentId());
        assertEquals(4,stat.getMemberDataId());
        assertEquals(4,stat.getRawId());
        assertEquals(Double.NaN,stat.getPeriodStats().getMean(WeightingType.None),1E-6);
        
    }
    
    @Test
    public void calculateGroupsStatsSortsByMembersId() {
        
        ListMap<ComplexId<Long>, PPAFullResultEntry> results = new ListMap<>();
        
        PPAFullResultEntry ent = new PPAFullResultEntry();
        ent.biolDescId = 2;
        ent.environmentId =3;
        ent.dataId =4;
        ent.result = (new FailedPPA());
        results.add(new ComplexId<>(2L),ent);
        
        ent = new PPAFullResultEntry();
        ent.biolDescId = 3;
        ent.environmentId = 4;
        ent.dataId = 2;
        ent.result = (new GenericPPAResult(25, 10, 1));
        results.add(new ComplexId<>(3L),ent);   
        
        ent = new PPAFullResultEntry();
        ent.biolDescId = 4;
        ent.environmentId = 4;
        ent.dataId = 1;
        ent.result = (new GenericPPAResult(25, 10, 1));
        results.add(new ComplexId<>(4L),ent);         
        
        ent = new PPAFullResultEntry();
        ent.biolDescId = 5;
        ent.environmentId = 4;
        ent.dataId = 5;
        ent.result = (new GenericPPAResult(25, 10, 1));
        results.add(new ComplexId<>(5L),ent);         
        
        
        List<PPAStats> stats = instance.calculateGroupsStats(results);
        
        assertEquals(Arrays.asList(1L,2L,4L,5L),
                stats.stream().map( s -> s.getMemberDataId())
                .collect(Collectors.toList())
        );
        
        
    }
    
    @Test
    public void simplifyJobStatsPreservesDataIdOrder() {
        PPAStats sta1 = PPAStatsCalculator.calculateStats(Collections.emptyList());
        sta1.setMemberDataId(1);
        PPAStats sta2 = PPAStatsCalculator.calculateStats(Collections.emptyList());
        sta2.setMemberDataId(2);
        PPAStats sta3 = PPAStatsCalculator.calculateStats(Collections.emptyList());
        sta3.setMemberDataId(3);
        
        UUID jobId = UUID.randomUUID();
        PPAJobSummary job = new PPAJobSummary();
        job.jobId = jobId;
        job.dataSetType = DetrendingType.NO_DTR.name();
        job.dataWindowStart= 5;
        job.state = (State.SUBMITTED);         
        
        PPAJobSimpleStats simple = instance.simplifyJobStats(Arrays.asList(sta1,sta2,sta3), job);
        assertNotNull(simple);
        
        assertEquals(Arrays.asList(1L,2L,3L), 
                simple.stats.stream().map( e -> e.memberDataId)
                .collect(Collectors.toList())
        );
    }
    
    @Test
    public void buildPPAFullResultEntryCreatesCorrectEntry() {
        DataTrace data = new DataTrace();
        //data.traceNr = 2;
        data.dataId = 3;
        data.rawDataId = 4;
        data.traceRef = "A";
        data.details = new DataColumnProperties("WT");
        
        UUID jobId = UUID.randomUUID();
        PPAJobSummary job = new PPAJobSummary();
        job.jobId = (jobId);
        
        job.dataSetType = "ASet";
        job.dataWindowStart= 1;
        job.state = (State.SUBMITTED); 
        
        //PPAJobSummary job = new PPAJobSummary();
        //job.setJob(new JobHandle(3));
        //job.getParams().put(JobSummary.DATA_SET_TYPE, "ASet");
        
        GenericPPAResult res = new GenericPPAResult(20, 10, 2);
        
        FakeIdExtractor fakeIds = new FakeIdExtractor(Arrays.asList(data));
        
        PPAFullResultEntry entry = instance.buildResultsEntry(data, job, res, fakeIds);
        assertNotNull(entry);
        assertEquals(1,entry.biolDescId);
        assertEquals(3,entry.dataId);
        assertEquals(4,entry.rawDataId);
        assertEquals("ASet",entry.dataType);
        assertEquals(1,entry.environmentId);
        assertEquals(false,entry.ignored);
        assertEquals(job.jobId,entry.jobId);
        assertEquals("A",entry.orgId);
        assertSame(res,entry.result);
    }
    
    @Test
    public void buildResultsEntriesCreatesCorrectEntriesWithMissingAsFailed() {
        UUID jobId = UUID.randomUUID();
        
        JobResults<TSResult<PPAResult>> results = new JobResults<>();
        results.jobId = jobId;
        results.externalId = "123";
        results.state = ed.biodare.jobcentre2.dom.State.SUCCESS;

        
        GenericPPAResult res1 = new GenericPPAResult(20, 10, 2);
        TSResult<PPAResult> t = new TSResult<>(1L, res1);
        results.results.add(t);

        GenericPPAResult res2 = new GenericPPAResult(20, 10, 2);
        t = new TSResult<>(3L, res2);
        results.results.add(t);
        
        Map<Long, DataTrace> orgData = new LinkedHashMap<>();
        DataTrace data = new DataTrace();
        data.dataId = 2;
        data.traceRef = "A";
        data.details = new DataColumnProperties("WT");
        orgData.put(1L,data);
        
        data = new DataTrace();
        data.dataId = 4;
        data.traceRef = "B";
        data.details = new DataColumnProperties("toc");
        orgData.put(3L,data);
        
        data = new DataTrace();
        data.dataId = 5;
        data.traceRef = "C";
        data.details = new DataColumnProperties("missing");
        orgData.put(2L,data);

        PPAJobSummary job = new PPAJobSummary();
        job.jobId = (jobId);        
        job.dataSetType = DetrendingType.NO_DTR.name();
        job.dataWindowStart= 1;
        job.state = (State.SUBMITTED); 
        
        //PPAJobSummary job = new PPAJobSummary();
        //job.setJob(new JobHandle(3));
        //job.getParams().put(JobSummary.DATA_SET_TYPE, "ASet");
        
        List<PPAFullResultEntry> entries = instance.buildResultsEntries(results, orgData, job);
        assertNotNull(entries);
        assertEquals(3,entries.size());
        
        PPAFullResultEntry entry = entries.get(1);
        assertNotNull(entry);
        assertEquals(2,entry.biolDescId);
        assertEquals(4,entry.dataId);
        assertEquals("NO_DTR",entry.dataType);
        assertEquals(2,entry.environmentId);
        assertEquals(false,entry.ignored);
        assertEquals(job.jobId,entry.jobId);
        assertEquals("B",entry.orgId);
        assertSame(res2,entry.result);
        
        entry = entries.get(2);
        assertTrue(entry.result.hasFailed());
    }
    
    
    @Test
    public void buildResultsEntriesSortsByDataId() {
        UUID jobId = UUID.randomUUID();
        JobResults<TSResult<PPAResult>> results = new JobResults<>();
        results.jobId = jobId;
        results.externalId = "123";
        results.state = ed.biodare.jobcentre2.dom.State.SUCCESS;
        
        GenericPPAResult res1 = new GenericPPAResult(20, 10, 2);
        TSResult<PPAResult> t = new TSResult<>(1L, res1);
        results.results.add(t);

        GenericPPAResult res2 = new GenericPPAResult(20, 10, 2);
        t = new TSResult<>(3L, res2);
        results.results.add(t);
        
        res2 = new GenericPPAResult(20, 10, 2);
        t = new TSResult<>(2L, res2);
        results.results.add(t);
        
        res2 = new GenericPPAResult(20, 10, 2);
        t = new TSResult<>(4L, res2);
        results.results.add(t);
        
        
        Map<Long, DataTrace> orgData = new LinkedHashMap<>();
        DataTrace data = new DataTrace();
        data.dataId = 2;
        data.traceRef = "A";
        data.details = new DataColumnProperties("WT");
        orgData.put(1L,data);
        
        data = new DataTrace();
        data.dataId = 4;
        data.traceRef = "B";
        data.details = new DataColumnProperties("toc");
        orgData.put(3L,data);
        
        data = new DataTrace();
        data.dataId = 3;
        data.traceRef = "C";
        data.details = new DataColumnProperties("missing");
        orgData.put(2L,data);
        
        data = new DataTrace();
        data.dataId = 6;
        data.traceRef = "C";
        data.details = new DataColumnProperties("missing");
        orgData.put(4L,data);
        
        
        PPAJobSummary job = new PPAJobSummary();
        job.jobId = jobId;
        job.dataSetType = DetrendingType.NO_DTR.name();
        job.dataWindowStart= 1;
        job.state = (State.SUBMITTED);
        
        List<PPAFullResultEntry> entries = instance.buildResultsEntries(results, orgData, job);
        assertNotNull(entries);
        assertEquals(4,entries.size());
        
        assertEquals(Arrays.asList(2L,3L,4L,6L),entries.stream().map( e -> e.dataId).collect(Collectors.toList()));
        
    }
    
    @Test
    public void updateResultSelectionSetsIgnoredOnMinus() {
        
        GenericPPAResult ppa = new GenericPPAResult(24, 2, 1);
        int selection = -1;
        assertFalse(ppa.isIgnored());
        instance.updateResultSelection(ppa, selection);
        assertTrue(ppa.isIgnored());
        
    }
    
    @Test
    public void updateResultSelectionOnFFTMovesSelectedToFrontAndClearsFlags() {
        
        FFT_PPA ppa = new FFT_PPA();
        ppa.addCOS(1, 0, 24, 0, 3, 0);
        ppa.addCOS(1, 0, 25, 0, 3, 0);
        ppa.addCOS(1, 0, 26, 0, 3, 0);
        ppa.setNeedsAttention(true);
        ppa.setIgnored(true);
        
        int selection = periodToInt(25);
        assertTrue(ppa.isIgnored());
        instance.updateResultSelection(ppa, selection);
        assertFalse(ppa.isIgnored());
        assertFalse(ppa.needsAttention());
        
        assertEquals(25,ppa.getPeriod(),1E-6);
        
    }  
    
    @Test
    public void updateResultSelectionOnGenericClearsFlags() {
        
        GenericPPAResult ppa = new GenericPPAResult(24, 2, 1);
        ppa.setIgnored(true);
        ppa.setNeedsAttention(true);
        
        int selection = periodToInt(24);
        assertTrue(ppa.isIgnored());
        instance.updateResultSelection(ppa, selection);
        assertFalse(ppa.isIgnored());
        assertFalse(ppa.needsAttention());
        
        assertEquals(24,ppa.getPeriod(),1E-6);
        
    }    
    
    @Test
    public void updateResultSelectionThrowsExceptionIfSelectionNotFound() {
        
        FFT_PPA ppa = new FFT_PPA();
        ppa.addCOS(1, 0, 24, 0, 3, 0);
        ppa.addCOS(1, 0, 25, 0, 3, 0);
        ppa.addCOS(1, 0, 26, 0, 3, 0);
        
        int selection = periodToInt(24.01);
        
        try {
            instance.updateResultSelection(ppa, selection);
            fail("Exception expected");
        } catch (HandlingException e) {};
        
        GenericPPAResult ppa2 = new GenericPPAResult(24, 2, 1);        
        selection = periodToInt(25);

        try {
            instance.updateResultSelection(ppa2, selection);
            fail("Exception expected");
        } catch (HandlingException e) {};
        
    }  

    @Test
    public void updateJobIndResultsSelectionDoesSelection() {
        
        List<PPAFullResultEntry> entries = new ArrayList<>();
        PPAFullResultEntry ent = new PPAFullResultEntry();
        ent.dataId = 2;
        ent.result =(new GenericPPAResult(24, 1, 2));
        entries.add(ent);
        
        ent = new PPAFullResultEntry();
        ent.dataId = 4;
        ent.result = (new GenericPPAResult(25, 1, 2));
        ent.result.setNeedsAttention(true);
        entries.add(ent);

        ent = new PPAFullResultEntry();
        ent.dataId = 6;
        ent.result = (new GenericPPAResult(26, 1, 2));
        entries.add(ent);        
        
        Map<Long, Integer> selectedValues = new HashMap<>();
        selectedValues.put(6L, -1);
        selectedValues.put(4L, periodToInt(25));
        
        List<PPAFullResultEntry> updated = instance.applyUserResultsSelection(entries, selectedValues);
        assertSame(entries.get(0),updated.get(0));
        
        assertFalse(updated.get(1).result.needsAttention());
        assertEquals(25,updated.get(1).result.getPeriod(),1E-6);
        
        assertTrue(updated.get(2).result.isIgnored());
        assertEquals(26,updated.get(2).result.getPeriod(),1E-6);
        
    }    
    
}
