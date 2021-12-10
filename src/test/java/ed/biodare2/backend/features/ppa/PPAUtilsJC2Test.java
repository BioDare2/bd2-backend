/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import static ed.biodare.jobcentre2.dom.PeriodConstants.PERIOD_MAX_KEY;
import static ed.biodare.jobcentre2.dom.PeriodConstants.PERIOD_MIN_KEY;
import ed.biodare.jobcentre2.dom.State;
import ed.biodare.jobcentre2.dom.TSDataSetJobRequest;
import ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder;
import static ed.biodare2.backend.repo.isa_dom.DomRepoTestBuilder.makeDataTraces;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRole;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.ppa.PPARequest;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAFullResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleResultEntry;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleStats;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.repo.system_dom.MockExperimentPack;
import ed.biodare2.backend.testutil.PPATestSeederJC2;

import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.tsprocessing.PPA;
import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.dom.tsprocessing.PPAStats;
import ed.robust.dom.tsprocessing.PhaseType;
import ed.robust.ppa.PPAMethod;
import ed.robust.util.timeseries.TSGenerator;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class PPAUtilsJC2Test {
    
    static final double EPS = 1E-6;
    
    public PPAUtilsJC2Test() {
    }
    
    PPAUtilsJC2 instance = new PPAUtilsJC2();
    
    @Before
    public void setUp() {
    }

    @Test
    public void prepareJobRequestCreatesTheRequest() throws Exception {
        
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

        TSDataSetJobRequest res = instance.prepareJC2JobRequest(exp.getId(), req, dataSet);        
        assertNotNull(res);
        
        assertEquals(""+exp.getId(),res.externalId);
        assertEquals(req.method.name(),res.method);
        assertEquals(1,res.data.size());
        
        TimeSeries ser = (TimeSeries)res.data.get(0).trace;
        
        assertEquals(5,ser.getFirst().getTime(),1E-6);
        assertEquals(130,ser.getLast().getTime(),1E-6);
        
    }
    

    
     
    @Test
    public void testCircadianPhaseCalculatesCorrectrly() {
        
        double phase = 10;
        double period = 24;
        assertEquals(10,instance.circadianPhase(phase, period),1E-6);
        
        period = 20;
        assertEquals(12,instance.circadianPhase(phase, period),1E-6);
        
    }
    
    @Test
    public void testCircadianPhaseRoundsToCenty() {
        
        double phase = 10.129;
        double period = 24;
        assertEquals(10.13,instance.circadianPhase(phase, period),1E-6);
        
        
    }    
    
    @Test
    public void testCircadianPhasePPACalculatesCorrectrly() {
        
        PPA ppa = new PPA(24, 5, 1);
        assertEquals(5,instance.circadianPhase(ppa),1E-6);
        
        ppa.setPeriod(20);
        assertEquals(6,instance.circadianPhase(ppa),1E-6);
        
    } 
    
    @Test
    public void circadianRelativePhaseCalculatesCorrectly() {
        PPA ppa = new PPA(20, 5, 1);
        double dw = 0;
        assertEquals(6,instance.circadianRelativePhase(ppa,dw),1E-6);
        
        dw = 1;
        assertEquals(24.0/5,instance.circadianRelativePhase(ppa,dw),1E-6);
        
    }
    
    @Test
    public void relativePhaseRoundsToCenty() {
        
        double phase = 10.129;
        double period = 24;
        double phaseRelation = 1;
        assertEquals(9.13,instance.relativePhase(phase, period, phaseRelation),1E-6);
        
        phase = 10.2001;
        period = 24;
        phaseRelation = 0;
        assertEquals(10.20,instance.relativePhase(phase, period, phaseRelation),1E-6);
        
    }

    @Test
    public void relativePhaseCalculateCorrectly() {
        
        double phase = 10;
        double period = 20;
        double phaseRelation = 0;
        assertEquals(10,instance.relativePhase(phase, period, phaseRelation),1E-6);
        
        phaseRelation = 10;
        assertEquals(0,instance.relativePhase(phase, period, phaseRelation),1E-6);
        
        phaseRelation = 39;
        assertEquals(11,instance.relativePhase(phase, period, phaseRelation),1E-6);
    }    
    
    @Test
    public void simplifyStatsCanHandleEmptyStats() {

        double EPS = 1E-6;
        List<PPAResult> results = new ArrayList<>();
        PPAStats stas = PPAStatsCalculator.calculateStats(results);
        
        PPASimpleStats res = instance.simplifyStats(stas, 1);
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
        
        PPASimpleStats res = instance.simplifyStats(stas, 1);
        assertNotNull(res);
        
        assertEquals(1,res.bioId);
        assertEquals(2,res.envId);
        assertEquals(3,res.memberDataId);
        assertEquals(4,res.rawId);
    }
    
    @Test 
    public void simplifyResultsEntryWorks() throws IOException {
        PPATestSeederJC2 seeder = new PPATestSeederJC2();
        PPAJobSummary job = seeder.getJobSummary();
        List<PPAFullResultEntry> entries = seeder.getJobFullResults(job).results;
        PPAFullResultEntry entry = entries.get(0);
        
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
        PPASimpleResultEntry simple = instance.simplifyResultsEntry(entry, 0.5);
        
        assertNotNull(simple);
        assertEquals(0.25,simple.ERR,EPS);
        assertEquals(0.51,simple.GOF,EPS);
        assertEquals(2.25,simple.amplitude.get(PhaseType.ByMethod),EPS);
        assertEquals(0.557391,simple.amplitudeErr,EPS);
        assertEquals(1,simple.bioId);
        assertEquals(1,simple.dataId);
        assertEquals(null,simple.dataRef);
        assertEquals("LIN_DTR",simple.dataType);
        assertEquals(1,simple.envId);
        assertEquals(false,simple.failed);
        assertEquals(true,simple.circadian);
        assertEquals(false,simple.ignored);
        assertEquals(job.jobId,simple.jobId);
        assertEquals(null,simple.jobSummary);
        assertEquals(null,simple.label);
        assertEquals(null,simple.message);
        assertEquals(false,simple.attention);
        assertEquals("B11",simple.orgId);
        assertEquals(23.47,simple.period,EPS);
        assertEquals(1.61,simple.periodErr,EPS);
        //assertEquals(0.26507*24/simple.period,simple.phaseCircErr,0.01);
        assertEquals(5.09-0.5,simple.phaseToWindow.get(PhaseType.ByMethod),EPS);
        assertEquals(5.09,simple.phaseToZero.get(PhaseType.ByMethod),EPS);
        assertEquals(1,simple.rawId);
    }
    
    
    @Test
    public void preparesCorrectlyNewPPAJobSummary() {
    
        long expId = 1234;
        
        PPARequest req = DomRepoTestBuilder.makePPARequest();
        req.windowStart = 5;
        req.windowEnd = 130;
        
        UUID id = UUID.randomUUID();
        
        PPAJobSummary sum = instance.prepareNewPPAJobSummary(expId, req, id);
        assertEquals(0,sum.attentionCount);
        assertEquals(false,sum.closed);
        assertEquals(null,sum.completed);
        assertEquals("1234_POLY_DTR",sum.dataSetId);
        assertEquals("POLY_DTR",sum.dataSetType);
        assertEquals("cubic dtr",sum.dataSetTypeName);
        assertEquals("5.0-130.0",sum.dataWindow);
        assertEquals(130,sum.dataWindowEnd,1E-6);
        assertEquals(5,sum.dataWindowStart,1E-6);
        assertEquals(0,sum.failures);
        //assertEquals(id.toString(),sum.uuid);
        assertEquals(id, sum.jobId);
        assertEquals(null,sum.lastError);
        assertEquals(35.0,sum.max_period,1E-6);
        assertEquals(null,sum.message);
        assertEquals(PPAMethod.MESA,sum.method);
        assertEquals(LocalDate.now(),sum.modified.toLocalDate());
        assertEquals(false,sum.needsAttention);
        assertEquals(State.SUBMITTED,sum.state);
        assertEquals(LocalDate.now(),sum.submitted.toLocalDate());
        assertEquals("cubic dtr 5.0-130.0 p(18.0-35.0)",sum.summary);
    
    }

    @Test
    public void preparesCorrectlyParameters() {
    
        PPARequest req = DomRepoTestBuilder.makePPARequest();
        req.windowStart = 5;
        req.windowEnd = 130;
        
        Map<String, String> p = instance.prepareParameters(req);
        assertEquals(PPAMethod.MESA.name(),p.get("METHOD"));   
        assertEquals("35.0",p.get("PERIOD_MAX"));
        assertEquals("18.0",p.get("PERIOD_MIN"));
        
    
    }
    
    @Test
    public void preparesCorrectlyJC2JobRequest() {
    
        long expId = 1234;
        
        PPARequest req = DomRepoTestBuilder.makePPARequest();
        req.windowStart = 2;
        req.windowEnd = 0;
        List<DataTrace> dataSet = makeDataTraces(1,5,48);
        TSDataSetJobRequest result = instance.prepareJC2JobRequest(expId, req, dataSet);
        
        assertNotNull(result);
        assertEquals("1234", result.externalId);
        assertEquals("MESA",result.method);
        assertEquals(""+req.periodMin, result.parameters.get(PERIOD_MIN_KEY));
        assertEquals(""+req.periodMax, result.parameters.get(PERIOD_MAX_KEY));
        
        assertEquals(5, result.data.size());
        assertEquals(2.0, result.data.get(0).trace.getFirst().getTime(),1E-6);
       
    }    
    
    
    
}
