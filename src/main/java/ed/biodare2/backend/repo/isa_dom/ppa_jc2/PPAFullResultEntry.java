/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.ppa_jc2;

import com.fasterxml.jackson.annotation.JsonProperty;
import ed.robust.dom.tsprocessing.PPAResult;
import java.util.UUID;

/**
 *
 * @author tzielins
 */
class PPAFullResultEntry {
    
    public UUID jobId;
    public long dataId;
    
    public String dataType;
    
    public String orgId;
    @JsonProperty("rawId")
    public long rawDataId;    
    @JsonProperty("biolId")
    public long biolDescId;
    @JsonProperty("envId")
    public long environmentId;
    
    public boolean ignored;
    
    public PPAResult result;    
}
