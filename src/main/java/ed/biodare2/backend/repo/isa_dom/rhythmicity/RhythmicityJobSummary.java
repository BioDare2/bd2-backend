/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.rhythmicity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ed.biodare.jobcentre2.dom.JobStatus;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RhythmicityJobSummary implements Serializable {

    private static final long serialVersionUID = 10L;
    
    //public static final String METHOD_ID = "METHOD_ID";
    //public static final String METHOD_NAME = "METHOD_NAME";
    public static final String PARAMS_SUMMARY = "PARAMS_SUMMARY";
    public static final String DW_END = "DW_END";
    public static final String DW_START = "DW_START";
    public static final String DATA_SET_TYPE = "DATA_SET_TYPE";
    public static final String DATA_SET_TYPE_NAME = "DATA_SET_TYPE_NAME";
    public static final String DATA_SET_ID = "DATA_SET_ID";
    //public static final String SELECTIONS = "PERIOD_SELECTIONS";
    
    
    public UUID jobId;
    public long parentId;
    public JobStatus jobStatus;
    
    public Map<String,String> parameters = new HashMap<>();

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.jobId);
        return hash;
    }

    /*
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
        final RhythmicityJobSummary other = (RhythmicityJobSummary) obj;
        if (!Objects.equals(this.jobId, other.jobId)) {
            return false;
        }
        if (!Objects.equals(this.jobStatus, other.jobStatus)) {
            return false;
        }
        if (!Objects.equals(this.parameters, other.parameters)) {
            return false;
        }
        return true;
    }*/

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
        final RhythmicityJobSummary other = (RhythmicityJobSummary) obj;
        if (!Objects.equals(this.jobId, other.jobId)) {
            return false;
        }
        if (this.parentId != other.parentId) {
            return false;
        }
        if (!Objects.equals(this.jobStatus, other.jobStatus)) {
            return false;
        }
        if (!Objects.equals(this.parameters, other.parameters)) {
            return false;
        }
        return true;
    }
    
    
}
