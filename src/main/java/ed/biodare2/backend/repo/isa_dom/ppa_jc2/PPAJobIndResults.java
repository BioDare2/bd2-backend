/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa_jc2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author tzielins
 */
class PPAJobIndResults {
    
    public UUID jobId;
    
    public List<PPAFullResultEntry> results = new ArrayList<>();
    
    protected PPAJobIndResults() {};
    
    public PPAJobIndResults(UUID jobId) {
        this.jobId = jobId;
    };
    
    public PPAJobIndResults(UUID jobId, List<PPAFullResultEntry> results) {
        this.jobId = jobId;
        this.results = results;
    };
    
}
