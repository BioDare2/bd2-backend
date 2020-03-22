/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa2;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tzielins
 */
public class PPAJobSimpleResults {
    // it used to be long from jobcentre1
    public long jobId;
    
    public String uuid;
    
    
    public List<PPASimpleResultEntry> results = new ArrayList<>();
    
    public PPAJobSimpleResults() {};
    
    PPAJobSimpleResults(long jobId) {
        this.jobId = jobId;
    }
    
    public PPAJobSimpleResults(String jobId) {
        this.uuid = jobId;
    }    
}
