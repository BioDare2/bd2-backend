/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa2;

import ed.robust.jobcenter.dom.state.State;
import ed.robust.ppa.PPAMethod;
import java.util.Date;

/**
 *
 * @author Zielu
 */
public class PPAJobSummary {
    
    public long jobId;
    public long parentId;
    public State state;
    public Date submitted;
    public Date modified;
    public Date completed;
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
    
    
}
