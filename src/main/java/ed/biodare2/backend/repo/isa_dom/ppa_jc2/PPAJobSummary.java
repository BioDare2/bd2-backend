/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa_jc2;

import ed.biodare.jobcentre2.dom.State;
import ed.robust.ppa.PPAMethod;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 *
 * @author Zielu
 */
public class PPAJobSummary {
    
    public UUID jobId;
    public long oldId;
    public long parentId;
    public State state;
    public LocalDateTime submitted;
    public LocalDateTime modified;
    public LocalDateTime completed;
    public String message;
    public String lastError;
    public long attentionCount;
    public long failures;
    public boolean needsAttention;
    public boolean closed;
    
    public String summary;
    
    public PPAMethod method;
    public String dataWindow;
    public double dataWindowStart;
    public double dataWindowEnd;
    public double min_period;
    public double max_period;
    public String dataSetId;
    public String dataSetType;
    public String dataSetTypeName;
    
    public String selections;
    
    /*public void setID(UUID id) {
        this.uuid = id.toString();
        this.jobId = uuid2long(id);
    }*/
    
    public static final long uuid2long(UUID id) {
        return id.getLeastSignificantBits()+id.getMostSignificantBits();
    } 
    
    public String shortId() {
        final String id = jobId.toString();
        return jobId.toString().substring(0, id.indexOf("-") );
    }
}
