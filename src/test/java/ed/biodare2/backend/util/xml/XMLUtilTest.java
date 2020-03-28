/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.xml;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 *
 * @author tzielins
 */
public class XMLUtilTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
    public XMLUtilTest() {
    }
    
    XMLUtil util;
    
    @Before
    public void setUp() {
        util = new XMLUtil();
    }

    /*@Test
    public void canSaveToXMLAndReadBack() throws Exception {
        
        PPAJobSummary org = (new PPATestSeederJC2()).getJobSummary();
        Path file = testFolder.newFile().toPath();
        
        util.saveToFile(org, file);
        
        assertTrue(Files.isRegularFile(file));
        PPAJobSummary cpy = util.readFromFile(file, PPAJobSummary.class);
        assertNotNull(cpy);
        
        assertReflectionEquals(org,cpy);
    }*/
    
    /*@Test
    public void canSavePPAResultsAndBack() throws Exception {
        
        JobResult<PPAResult> jRes = new JobResult<>(123,State.SUCCESS,"OK");        
        PPAResult pRes = new MESA_PPA(new PPA(24, 1, 2));
        
        TaskResult<PPAResult> tRes = new TaskResult(1L, pRes);
        jRes.addResult(tRes);        
        
        Path file = testFolder.newFile().toPath();
        
        util.saveToFile(jRes, file);
        
        assertTrue(Files.isRegularFile(file));
        
        JobResult<PPAResult> cpy = util.readFromFile(file, JobResult.class);
        assertNotNull(cpy);
        assertEquals(cpy.getTaskResults().get(0).getResult().getPeriod(),24,1E-6);
        
        //assertReflectionEquals(jRes,cpy);
        
    }*/
    
}
