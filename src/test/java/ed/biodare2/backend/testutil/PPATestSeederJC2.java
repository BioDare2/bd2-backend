/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.testutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleResults;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSimpleStats;
import ed.biodare2.backend.repo.isa_dom.ppa_jc2.PPAJobSummary;
import java.util.Collection;

/**
 *
 * @author tzielins
 */
public class PPATestSeederJC2 {
    
    ObjectMapper mapper;
    
    public static ObjectMapper makeMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();        
        return mapper;
    }
    
    public PPATestSeederJC2() {
        this(makeMapper());
    }
    
    public PPATestSeederJC2(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public PPAJobSummary getJobSummary() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public PPAJobSimpleResults getJobSimpleResults(PPAJobSummary job) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public PPAJobSimpleStats getJobSimpleStats(PPAJobSummary job) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Collection<DataTrace> getData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
