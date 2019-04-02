/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRole;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.ppa.PPARequest;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPASimpleResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa2.PPASimpleStats;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.MockExperimentPack;
import ed.biodare2.backend.testutil.PPATestSeeder;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.jobcenter.JobSummary;
import ed.robust.dom.tsprocessing.PPA;
import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.dom.tsprocessing.PPAStats;
import ed.robust.dom.tsprocessing.PhaseType;
import ed.robust.dom.tsprocessing.ResultsEntry;
import ed.robust.jobcenter.dom.job.JobRequest;
import ed.robust.jobcenter.dom.state.State;
import ed.robust.jobcenter.dom.task.SimpleTaskProvider;
import ed.robust.jobcenter.dom.task.TaskProvider;
import ed.robust.jobcenter.error.ConnectionException;
import ed.robust.jobcenter.error.ExpiredDataException;
import ed.robust.jobcenter.error.UnknownDataException;
import ed.robust.ppa.PPAMethod;
import ed.robust.util.timeseries.TSGenerator;
import ed.robust.util.timeseries.TimeSeriesFileHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tzielins
 */
public class PPAUtilsTest {
    
    static final double EPS = 1E-6;
    public PPAUtilsTest() {
    }
    
    PPAUtils instance;
    
    @Before
    public void init() {
        instance = new PPAUtils();
    }

    @Test
    public void prepareJobRequestCreatesTheRequest() throws ConnectionException, UnknownDataException, ExpiredDataException {
        
        AssayPack exp = new MockExperimentPack(123);
        PPARequest req = DomRepoTestBuilder.makePPARequest();
        req.windowStart = 5;
        req.windowEnd = 130;
        
        List<DataTrace> dataSet= new ArrayList<>();
        
        DataTrace trace = new DataTrace();
        trace.coordinates = DomRepoTestBuilder.makeCellCoordinates();
        trace.details = new DataColumnProperties("WT");
        trace.dataId = 1;
        trace.traceRef = "B";
        trace.role = CellRole.DATA;
        trace.trace = TSGenerator.makeCos(200, 1, 24, 2);
        dataSet.add(trace);
        
        JobRequest res = instance.prepareJobRequest(exp.getId(), req, dataSet);
        assertNotNull(res);
        
        assertEquals(""+exp.getId(),res.getExternalId());
        assertEquals(req.method.name(),res.getMethod());
        assertEquals(1,res.getTasks().size());
        
        TimeSeries ser = (TimeSeries)res.getTasks().getTask(0).getData().getData();
        
        assertEquals(5,ser.getFirst().getTime(),1E-6);
        assertEquals(130,ser.getLast().getTime(),1E-6);
        
    }
    
    @Test 
    public void prepareTasksCreatesCorrectTasksBasedOnDataId() throws Exception {
        
        List<DataTrace> set = new ArrayList<>();
        
        DataTrace data = new DataTrace();
        //data.traceNr = 2;
        data.dataId = 3;
        data.rawDataId = 4;
        data.trace = new TimeSeries();
        set.add(data);
        
        data = new DataTrace();
        //data.traceNr = 4;
        data.dataId = 5;
        data.rawDataId = 6;
        data.trace = new TimeSeries();
        data.trace.add(1,0);
        set.add(data);        
        
        PPARequest ppaRequest = new PPARequest();
        
        SimpleTaskProvider tasks = (SimpleTaskProvider)instance.prepareTasks(set, ppaRequest);
        assertEquals(2,tasks.size());
        assertEquals(3L,(long)tasks.getTask(0).getId());
        assertEquals(5L,(long)tasks.getTask(1).getId());
        assertEquals(set.get(1).trace,tasks.getTask(1).getData().getData());
        
        
    }
    
    @Test 
    public void prepareTasksTrimsTheInputDataAccordingToParams() throws Exception {
        
        List<DataTrace> set = new ArrayList<>();
        
        DataTrace data = new DataTrace();
        data.dataId = 3;
        data.trace = new TimeSeries();
        data.trace.add(1, 10);
        data.trace.add(5, 11);
        data.trace.add(10, 12);
        data.trace.add(20, 11);
        set.add(data);
                
        PPARequest ppaRequest = new PPARequest();
        
        SimpleTaskProvider tasks = (SimpleTaskProvider)instance.prepareTasks(set, ppaRequest);
        TimeSeries ser = (TimeSeries)tasks.getTask(0).getData().getData();
        
        assertEquals(1,ser.getFirst().getTime(),1E-6);
        assertEquals(20,ser.getLast().getTime(),1E-6);

        ppaRequest.windowStart = 6;
        tasks = (SimpleTaskProvider)instance.prepareTasks(set, ppaRequest);
        ser = (TimeSeries)tasks.getTask(0).getData().getData();
        
        assertEquals(10,ser.getFirst().getTime(),1E-6);
        assertEquals(20,ser.getLast().getTime(),1E-6);

        ppaRequest.windowStart = 5;
        ppaRequest.windowEnd = 11;
        tasks = (SimpleTaskProvider)instance.prepareTasks(set, ppaRequest);
        ser = (TimeSeries)tasks.getTask(0).getData().getData();
        
        assertEquals(5,ser.getFirst().getTime(),1E-6);
        assertEquals(10,ser.getLast().getTime(),1E-6);
        
        ppaRequest.windowStart = 0;
        ppaRequest.windowEnd = 11;
        tasks = (SimpleTaskProvider)instance.prepareTasks(set, ppaRequest);
        ser = (TimeSeries)tasks.getTask(0).getData().getData();
        
        assertEquals(1,ser.getFirst().getTime(),1E-6);
        assertEquals(10,ser.getLast().getTime(),1E-6);         
        
    }
    
    @Test
    public void testCircadianPhaseCalculatesCorrectrly() {
        
        double phase = 10;
        double period = 24;
        assertEquals(10,PPAUtils.circadianPhase(phase, period),1E-6);
        
        period = 20;
        assertEquals(12,PPAUtils.circadianPhase(phase, period),1E-6);
        
    }
    
    @Test
    public void testCircadianPhaseRoundsToCenty() {
        
        double phase = 10.129;
        double period = 24;
        assertEquals(10.13,PPAUtils.circadianPhase(phase, period),1E-6);
        
        
    }    
    
    @Test
    public void testCircadianPhasePPACalculatesCorrectrly() {
        
        PPA ppa = new PPA(24, 5, 1);
        assertEquals(5,PPAUtils.circadianPhase(ppa),1E-6);
        
        ppa.setPeriod(20);
        assertEquals(6,PPAUtils.circadianPhase(ppa),1E-6);
        
    } 
    
    @Test
    public void circadianRelativePhaseCalculatesCorrectly() {
        PPA ppa = new PPA(20, 5, 1);
        double dw = 0;
        assertEquals(6,PPAUtils.circadianRelativePhase(ppa,dw),1E-6);
        
        dw = 1;
        assertEquals(24.0/5,PPAUtils.circadianRelativePhase(ppa,dw),1E-6);
        
    }
    
    @Test
    public void relativePhaseRoundsToCenty() {
        
        double phase = 10.129;
        double period = 24;
        double phaseRelation = 1;
        assertEquals(9.13,PPAUtils.relativePhase(phase, period, phaseRelation),1E-6);
        
        phase = 10.2001;
        period = 24;
        phaseRelation = 0;
        assertEquals(10.20,PPAUtils.relativePhase(phase, period, phaseRelation),1E-6);
        
    }

    @Test
    public void relativePhaseCalculateCorrectly() {
        
        double phase = 10;
        double period = 20;
        double phaseRelation = 0;
        assertEquals(10,PPAUtils.relativePhase(phase, period, phaseRelation),1E-6);
        
        phaseRelation = 10;
        assertEquals(0,PPAUtils.relativePhase(phase, period, phaseRelation),1E-6);
        
        phaseRelation = 39;
        assertEquals(11,PPAUtils.relativePhase(phase, period, phaseRelation),1E-6);
    }    
    
    @Test
    public void simplifyStatsCanHandleEmptyStats() {

        double EPS = 1E-6;
        List<PPAResult> results = new ArrayList<>();
        PPAStats stas = PPAStatsCalculator.calculateStats(results);
        
        PPASimpleStats res = PPAUtils.simplifyStats(stas, 1);
        assertNotNull(res);
        
        assertEquals(0,res.N);
        assertEquals(Double.NaN,res.ERR,EPS);
        assertEquals(Double.NaN,res.GOF,EPS);
        assertEquals(Double.NaN,res.period,EPS);
        assertEquals(Double.NaN,res.periodStd,EPS);
        assertEquals(Double.NaN,res.phaseToZero.get(PhaseType.ByFit),EPS);
        assertEquals(Double.NaN,res.phaseToZeroCirc.get(PhaseType.ByFit),EPS);
        assertEquals(Double.NaN,res.phaseToWindow.get(PhaseType.ByFit),EPS);
        assertEquals(Double.NaN,res.phaseToWindowCirc.get(PhaseType.ByFit),EPS);
        assertEquals(Double.NaN,res.phaseStd.get(PhaseType.ByFit),EPS);
        assertEquals(Double.NaN,res.phaseCircStd.get(PhaseType.ByFit),EPS);
        assertEquals(Double.NaN,res.amplitudeStd.get(PhaseType.ByFit),EPS);
        assertEquals(Double.NaN,res.amplitude.get(PhaseType.ByFit),EPS);
    }
    
    @Test
    public void simplifyStatsTransfersIds() {

        double EPS = 1E-6;
        List<PPAResult> results = new ArrayList<>();
        PPAStats stas = PPAStatsCalculator.calculateStats(results);
        stas.setBiolDescId(1);
        stas.setEnvironmentId(2);
        stas.setMemberDataId(3);
        stas.setRawId(4);
        
        PPASimpleStats res = PPAUtils.simplifyStats(stas, 1);
        assertNotNull(res);
        
        assertEquals(1,res.bioId);
        assertEquals(2,res.envId);
        assertEquals(3,res.memberDataId);
        assertEquals(4,res.rawId);
    }
    
    @Test 
    public void simplifyResultsEntryWorks() {
        PPATestSeeder seeder = new PPATestSeeder();
        JobSummary job = seeder.getJob();
        List<ResultsEntry> entries = seeder.getJobIndResults(job);
        ResultsEntry entry = entries.get(0);
        
        /*

    <ppa jobId="171" dataId="1" dataType="LIN_DTR" orgId="B" rawId="1" biolId="1" envId="1" ignored="false">
        <result xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="ns2:FFT_PPA" attention="false" circadian="true" ignored="false" method="NLLS_1.2">
            <processingTime>0</processingTime>
            <message>Circ: 1, NonCirc: 0</message>
            <COS>
                <method per="25.04" ph="0.87" amp="0.98689" off="0.0" perE="0.09685" phE="0.26507" ampE="0.033079" offE="0.0" jERR="0.033518" mERR="0.033518" GOF="0.119651"/>
                <pbam per="25.04" ph="0.86" amp="0.986868" off="0.0" phE="0.068313" ampE="4.305019645282847E-5" jERR="0.033518" mERR="0.033518" GOF="0.119651"/>
                <pbf per="25.04" ph="0.87" amp="0.986893" off="-3.8584587827235925E-5" jERR="0.033518" mERR="0.033518" GOF="0.119651"/>
                <pbfp per="25.04" ph="0.9" amp="0.986874" off="0.0" jERR="0.033518" mERR="0.033518" GOF="0.119651"/>
            </COS>
            <shift>0.021708</shift>
            <slope>1.4738206154155376E-18</slope>
            <inter>-0.020941</inter>
        </result>
    </ppa>        
        
        */
        PPASimpleResultEntry simple = PPAUtils.simplifyResultsEntry(entry, 0.5);
        
        assertNotNull(simple);
        assertEquals(0.03,simple.ERR,EPS);
        assertEquals(0.12,simple.GOF,EPS);
        assertEquals(0.99,simple.amplitude.get(PhaseType.ByMethod),EPS);
        assertEquals(0.033079,simple.amplitudeErr,EPS);
        assertEquals(1,simple.bioId);
        assertEquals(1,simple.dataId);
        assertEquals(null,simple.dataRef);
        assertEquals("LIN_DTR",simple.dataType);
        assertEquals(1,simple.envId);
        assertEquals(false,simple.failed);
        assertEquals(true,simple.circadian);
        assertEquals(false,simple.ignored);
        assertEquals(job.getJobId(),simple.jobId);
        assertEquals(null,simple.jobSummary);
        assertEquals(null,simple.label);
        assertEquals(null,simple.message);
        assertEquals(false,simple.attention);
        assertEquals("B",simple.orgId);
        assertEquals(25.04,simple.period,EPS);
        assertEquals(0.10,simple.periodErr,EPS);
        assertEquals(0.26507*24/simple.period,simple.phaseCircErr,0.01);
        assertEquals(0.87-0.5,simple.phaseToWindow.get(PhaseType.ByMethod),EPS);
        assertEquals(0.87,simple.phaseToZero.get(PhaseType.ByMethod),EPS);
        assertEquals(1,simple.rawId);
    }
    
    @Test
    public void simplifyJobCorrectlyConverts() {
        JobSummary job = (new PPATestSeeder()).getJob();
        
    /*
    <j closed="true" attention="true" attCount="4">
        <job id="171"/>
        <status state="FINISHED" submitted="2017-06-10T00:03:14.003+01:00" completed="2017-06-10T00:03:16.628+01:00">
            <message>Attention: 0; </message>
        </status>
        <params>
            <p n="DW_END" v="0.0"/>
            <p n="METHOD_ID" v="NLLS"/>
            <p n="DATA_SET_ID" v="10050_LIN_DTR"/>
            <p n="min_period" v="18.0"/>
            <p n="DW_START" v="0.0"/>
            <p n="DATA_SET_TYPE_NAME" v="linear dtr"/>
            <p n="PARAMS_SUMMARY" v="linear dtr min-max p(18.0-35.0)"/>
            <p n="METHOD_NAME" v="FFT NLLS"/>
            <p n="DATA_SET_TYPE" v="LIN_DTR"/>
            <p n="max_period" v="35.0"/>
        </params>
    </j>        
        
        */
    
        PPAJobSummary sum = PPAUtils.simplifyJob(job);
        assertEquals(4,sum.attentionCount);
        assertEquals(true,sum.closed);
        assertEquals(job.getStatus().getCompleted(),sum.completed);
        assertEquals("10050_LIN_DTR",sum.dataSetId);
        assertEquals("LIN_DTR",sum.dataSetType);
        assertEquals("linear dtr",sum.dataSetTypeName);
        assertEquals("min-max",sum.dataWindow);
        assertEquals(0,sum.dataWindowEnd,1E-6);
        assertEquals(0,sum.dataWindowStart,1E-6);
        assertEquals(0,sum.failures);
        assertEquals(171,sum.jobId);
        assertEquals(null,sum.lastError);
        assertEquals(35.0,sum.max_period,1E-6);
        assertEquals("Attention: 0; ",sum.message);
        assertEquals(PPAMethod.NLLS,sum.method);
        assertEquals(job.getStatus().getModified(),sum.modified);
        assertEquals(true,sum.needsAttention);
        assertEquals(State.FINISHED,sum.state);
        assertEquals(job.getStatus().getSubmitted(),sum.submitted);
        assertEquals("linear dtr min-max p(18.0-35.0)",sum.summary);
    }
    
}
