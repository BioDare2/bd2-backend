/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
@RunWith(SpringRunner.class)
@JsonTest
public class DataTableImportParametersTest {
    
    @Autowired
    JacksonTester<DataTableImportParameters> jsonParser;    
    
    @Autowired
    ObjectMapper mapper;        
    
    public DataTableImportParametersTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testCanDeserializeAngularJSON() throws Exception {
        
        String json = "{ \"inRows\": true, \"timeOffset\": 0, \"importLabels\": true, \"_class_name\": \".DataTableImportParameters\", \"timeType\": \"TIME_IN_HOURS\", \"firstTimeCell\": { \"col\": 1, \"row\": 0 }, \"fileId\": \"_upload541534594694376721\", \"fileName\": \"wt_prr_simpl_inRows.csv\", \"importFormat\": \"COMA_SEP\", \"labelsSelection\": { \"col\": 0, \"row\": 0 } }";
        
        DataTableImportParameters res = mapper.readValue(json, DataTableImportParameters.class);
        
        assertEquals("_upload541534594694376721",res.fileId);
        assertEquals(ImportFormat.COMA_SEP, res.importFormat);
        
        CellCoordinates cell = new CellCoordinates(1, 0);
        assertEquals(cell, res.firstTimeCell);
        
    }
    
    @Test
    public void testCanDeserializeAngularJSON2() throws Exception {
        
        String json = "{ \"inRows\": true, \"timeOffset\": 0, \"importLabels\": false, \"userLabels\": [ null, null, \"label1\", \"label1\", \"label1\", null, null, \"TZ1\", \"TZ1\", \"TZ2\", \"TZ2\", null, null, \"before last\", \"\" ], \"_class_name\": \".DataTableImportParameters\", \"timeType\": \"TIME_IN_HOURS\", \"fileId\": \"_upload12073675894254425902\", \"fileName\": \"wt_prr_simpl_inRows.csv\", \"importFormat\": \"COMA_SEP\", \"firstTimeCell\": { \"col\": 1, \"row\": 0 } }";
        
        DataTableImportParameters res = mapper.readValue(json, DataTableImportParameters.class);
        
        assertEquals("_upload12073675894254425902",res.fileId);
        assertEquals(ImportFormat.COMA_SEP, res.importFormat);
        
        CellCoordinates cell = new CellCoordinates(1, 0);
        assertEquals(cell, res.firstTimeCell);
        
        assertFalse(res.importLabels);
        assertTrue(res.inRows);
        assertEquals(TimeType.TIME_IN_HOURS, res.timeType);
        assertEquals(ImportFormat.COMA_SEP, res.importFormat);
        
        List<String> labels = Arrays.asList(
        null, null, "label1", "label1", "label1", null, null, "TZ1", "TZ1", "TZ2", "TZ2", null, null, "before last", ""
        );
        
        assertEquals(labels, res.userLabels);
        
    }    
    
    @Test
    public void transposeWorks() throws Exception {
        
        String colJSON = "{ \"inRows\": false, \"timeOffset\": 0, \"importLabels\": true, \"_class_name\": \".DataTableImportParameters\", \"timeType\": \"TIME_IN_HOURS\", \"fileId\": \"_upload3243260365139669172\", \"fileName\": \"wt_prr_simpl.csv\", \"importFormat\": \"COMA_SEP\", \"firstTimeCell\": { \"col\": 0, \"row\": 1 }, \"labelsSelection\": { \"col\": 0, \"row\": 0 }, \"dataStart\": { \"col\": 1, \"row\": 2 } }";
        
        DataTableImportParameters colPar = mapper.readValue(colJSON, DataTableImportParameters.class);
        
        String rowJSON = "{ \"inRows\": true, \"timeOffset\": 0, \"importLabels\": true, \"_class_name\": \".DataTableImportParameters\", \"timeType\": \"TIME_IN_HOURS\", \"fileId\": \"_upload3243260365139669172\", \"fileName\": \"wt_prr_simpl.csv\", \"importFormat\": \"COMA_SEP\", \"firstTimeCell\": { \"col\": 1, \"row\": 0 }, \"labelsSelection\": { \"col\": 0, \"row\": 0 }, \"dataStart\": { \"col\": 2, \"row\": 1 } }";
        
        DataTableImportParameters rowPar = mapper.readValue(rowJSON, DataTableImportParameters.class);
        
        DataTableImportParameters trans = colPar.transpose();
        assertEquals(rowPar, trans);
    }
    
}
