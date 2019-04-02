/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.testutil.PPATestSeeder;
import ed.biodare2.backend.handlers.FileUploadHandler;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.dao.PPAArtifactsRep;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.features.rdmsocial.RDMSocialHandler;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Import(SimpleRepoTestConfig.class)
@Import({SimpleRepoTestConfig.class})
public abstract class ExperimentBaseIntTest extends AbstractIntTestBase {
 
    @Autowired
    PPATestSeeder testSeeder;
    
    @Autowired
    ExperimentPackHub expBoundles;
    
    @Autowired
    RDMSocialHandler rdmSocialHandler;
    
    @Autowired
    FileUploadHandler uploads;
    
    @Autowired
    TSDataHandler tsHandler;
    
    @Autowired
    PPAArtifactsRep ppaRep;
    
    static AtomicLong expIds = new AtomicLong(150);
    
    @Transactional
    AssayPack insertExperiment() {
        AssayPack exp = testSeeder.insertExperiment();
        
        // System.out.println("Inserted: "+exp.getId()+ ": "+exp.getSystemInfo().experimentCharacteristic.hasTSData);
        return exp;
    }
    
    @Transactional
    AssayPack insertPublicExperiment() {

        return testSeeder.insertExperiment(true);
    }    
    

    @Transactional
    protected int insertData(AssayPack exp) throws Exception {

        List<DataTrace> data = testSeeder.getData();
        testSeeder.seedData(data, exp);
        return data.size();
    }
    
/*    
    @Transactional
    protected void insertData(AssayPack exp) throws Exception {
        DataBundle rawData = new DataBundle();
        
        DataTrace trace = new DataTrace();
        trace.traceNr = 1;
        trace.traceRef = "B";
        trace.details = new DataColumnProperties("WT");
        trace.role = CellRole.DATA;
        trace.trace = TSGenerator.makeCos(200, 1, 24, 2);
        rawData.data.add(trace);
        
        trace = new DataTrace();
        trace.traceNr = 2;
        trace.traceRef = "C";
        trace.details = new DataColumnProperties("WT");
        trace.role = CellRole.DATA;
        trace.trace = TSGenerator.makeCos(200, 1, 25, 2);
        rawData.data.add(trace); 
        
        trace = new DataTrace();
        trace.traceNr = 3;
        trace.traceRef = "D";
        trace.details = new DataColumnProperties("TOC");
        trace.role = CellRole.DATA;
        trace.trace = TSGenerator.makeCos(200, 1, 26, 2);
        rawData.data.add(trace);         
        
        tsHandler.handleNewData(exp, rawData);
    }
*/    
    
    
    
}
