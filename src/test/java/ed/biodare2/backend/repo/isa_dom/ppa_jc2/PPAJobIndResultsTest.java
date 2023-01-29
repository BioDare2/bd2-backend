/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa_jc2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import static ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPASimpleResultEntryTest.makePPASimpleResultEntry;
import ed.robust.dom.tsprocessing.FFT_PPA;
import ed.robust.dom.tsprocessing.MFF_PPA;
import ed.robust.dom.tsprocessing.PPA;
import ed.robust.dom.tsprocessing.PPAResult;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
//import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 *
 * @author tzielins
 */
public class PPAJobIndResultsTest {
    
    public PPAJobIndResultsTest() {
    }
    
    ObjectMapper mapper;
    
    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);        
    }

    @Test
    public void serializesToJSONAndBack() throws Exception {
        UUID id = UUID.randomUUID();
        PPAJobIndResults org = makePPAJobIndResults(id);
        
        
        
        String json = mapper.writeValueAsString(org);
        assertNotNull(json);
        System.out.println(json);
        
        PPAJobIndResults cpy = mapper.readValue(json, PPAJobIndResults.class); 
        
        assertEquals(org, cpy);
        //// [TODO find reflective eq] assertReflectionEquals(org,cpy); 
    }    

    public static PPAJobIndResults makePPAJobIndResults(UUID id) {
        PPAJobIndResults org = new PPAJobIndResults(id);
        
        FFT_PPA ppa1 = new FFT_PPA();
        ppa1.addCOS(10, 1, 25, 0.2, 3, 0.1);        
        org.results.add(makePPAFullResultEntry(id, 1, ppa1));
        
        MFF_PPA ppa2 = new MFF_PPA(new PPA(24, 1, 2));
        org.results.add(makePPAFullResultEntry(id, 2, ppa2));

        return org;
    }
    
    public static PPAFullResultEntry makePPAFullResultEntry(UUID jobId, long dataId, PPAResult ppa) {
        
        PPAFullResultEntry entry = new PPAFullResultEntry();
        entry.biolDescId = 1;
        entry.dataId = dataId;
        entry.dataType = "AType";
        entry.environmentId = 2;
        entry.ignored = false;
        entry.jobId = jobId;
        entry.orgId = ""+dataId;
        entry.rawDataId = dataId;
        entry.result = ppa;
        
        return entry;
    }
}
