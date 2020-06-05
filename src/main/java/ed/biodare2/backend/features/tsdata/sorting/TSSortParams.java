/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.sorting;

import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author tzielins
 */
public class TSSortParams {
    
    public TSSortOption sort;
    public UUID jobId;
    public boolean ascending;

    public TSSortParams(TSSortOption sort, boolean ascending, UUID jobId) {
        this.sort = sort;
        this.jobId = jobId;
        this.ascending = ascending;
    }
    
    protected TSSortParams() {        
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.sort);
        hash = 59 * hash + Objects.hashCode(this.jobId);
        hash = 59 * hash + (this.ascending ? 1 : 0);
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
        final TSSortParams other = (TSSortParams) obj;
        if (this.ascending != other.ascending) {
            return false;
        }
        if (this.sort != other.sort) {
            return false;
        }
        if (!Objects.equals(this.jobId, other.jobId)) {
            return false;
        }
        return true;
    }
     
    
    
    public static TSSortParams parse(TSSortOption sort, String direction, String ppaJobId, String rhythmJobId) {
        
        boolean ascending = !"desc".equals(direction);
        UUID jobId = null;
        
        switch(sort) {
            case PERIOD:
            case PHASE:
            case AMP:
            case ERR: jobId = parseUUID(ppaJobId); break;
            
            case R_PVALUE:
            case R_PEAK:
            case R_PERIOD: jobId = parseUUID(rhythmJobId); break;
            
            default: break;
        }
        
        TSSortParams params = new TSSortParams(sort, ascending, jobId);
        
        return params;
    }
    
    static final UUID parseUUID(String id) {
        if (id == null) throw new IllegalArgumentException("jobId is required");
        return UUID.fromString(id);
    }
}
