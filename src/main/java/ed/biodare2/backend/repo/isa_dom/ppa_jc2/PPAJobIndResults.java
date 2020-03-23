/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa_jc2;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author tzielins
 */
public class PPAJobIndResults {
    
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.jobId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PPAJobIndResults other = (PPAJobIndResults) obj;
        if (!Objects.equals(this.jobId, other.jobId)) {
            return false;
        }
        if (!Objects.equals(this.results, other.results)) {
            return false;
        }
        return true;
    }
    
    
    
}
