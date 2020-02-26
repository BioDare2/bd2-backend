/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.web.rest;

import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.SimpleRepoTestConfig;
import ed.biodare2.backend.testutil.PPATestSeeder;
import ed.biodare2.backend.handlers.FileUploadHandler;
import ed.biodare2.backend.repo.dao.ExperimentPackHub;
import ed.biodare2.backend.repo.dao.PPAArtifactsRep;
import ed.biodare2.backend.features.tsdata.datahandling.TSDataHandler;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.system_dom.AssayPack;
import ed.biodare2.backend.features.rdmsocial.RDMSocialHandler;
import ed.biodare2.backend.features.search.lucene.LuceneWriter;
import ed.biodare2.backend.repo.dao.ExperimentsStorage;
import ed.biodare2.backend.repo.db.dao.DBSystemInfoRep;
import ed.biodare2.backend.util.io.FileUtil;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
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
    DBSystemInfoRep systemInfos;
    
    @Autowired
    RDMSocialHandler rdmSocialHandler;
    
    @Autowired
    FileUploadHandler uploads;
    
    @Autowired
    TSDataHandler tsHandler;
    
    @Autowired
    PPAArtifactsRep ppaRep;
    
    static AtomicLong expIds = new AtomicLong(150);
    
    @Autowired
    ExperimentsStorage experimentalStorage; 
    
    @Autowired
    LuceneWriter luceneWriter;
    
    @Before
    @Transactional
    public void cleanTestSpace() throws IOException {
        
        Path experiments = experimentalStorage.getExperimentsDir();
        if (experiments == null) throw new IllegalStateException("Mising runtime exp storage path");
        
        String pathString = experiments.toAbsolutePath().toString();
        
        boolean isTest = pathString.contains("Temp") || pathString.contains("tmp");
        isTest = isTest && pathString.contains("test");
        if (!isTest) {
            throw new IllegalStateException("Cannot clean exp storage from no temporary test location"+pathString);
        }
        
        FileUtil fileUtil = new FileUtil();
        try {
            fileUtil.removeRecursively(experiments);
        } catch (IOException e) {
            System.err.println("Could not clean exp dir"+e.getMessage());
        }
        
        luceneWriter.deleteAll();
        
        systemInfos.deleteAll();
        
        
    }
    
    @Transactional
    AssayPack insertExperiment() {
        AssayPack exp = testSeeder.insertExperiment();
        
        // System.out.println("Inserted: "+exp.getId()+ ": "+exp.getSystemInfo().experimentCharacteristic.hasTSData);
        return exp;
    }
    
    @Transactional(propagation = Propagation.MANDATORY)
    AssayPack insertPublicExperiment() {

        return testSeeder.insertExperiment(true);
    }    
    

    @Transactional
    protected int insertData(AssayPack exp) throws Exception {

        List<DataTrace> data = testSeeder.getData();
        testSeeder.seedData(data, exp);
        return data.size();
    }
    
    @Transactional
    protected int insertData(AssayPack exp, String type) throws Exception {

        List<DataTrace> data = testSeeder.getData(type);
        testSeeder.seedData(data, exp);
        return data.size();
    }    
    
    @Transactional
    protected int insertData(AssayPack exp, int duration) throws Exception {

        List<DataTrace> data = testSeeder.getData();
        data.forEach( dt -> {
            dt.trace = dt.trace.subSeries(0, duration);
        });
        testSeeder.seedData(data, exp);
        return data.size();
    }  

    @Transactional
    protected int insertData(AssayPack exp, String type, int duration) throws Exception {

        List<DataTrace> data = testSeeder.getData(type);
        data.forEach( dt -> {
            dt.trace = dt.trace.subSeries(0, duration);
        });
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
