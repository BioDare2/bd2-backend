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
import java.util.UUID;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RhythmicityJobSummary implements Serializable {

    private static final long serialVersionUID = 10L;
    
    public static final String METHOD_ID = "METHOD_ID";
    public static final String METHOD_NAME = "METHOD_NAME";
    public static final String PARAMS_SUMMARY = "PARAMS_SUMMARY";
    public static final String DW_END = "DW_END";
    public static final String DW_START = "DW_START";
    public static final String DATA_SET_TYPE = "DATA_SET_TYPE";
    public static final String DATA_SET_TYPE_NAME = "DATA_SET_TYPE_NAME";
    public static final String DATA_SET_ID = "DATA_SET_ID";
    public static final String SELECTIONS = "PERIOD_SELECTIONS";
    
    
    public UUID jobId;
    public JobStatus jobStatus;
    
    public Map<String,String> parameters = new HashMap<>();
    
    
}
