/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.sorting;

import static ed.biodare2.backend.features.tsdata.sorting.TSSortOption.*;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class TSSortParamsTest {
    
    public TSSortParamsTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void parseAssumesAscending() {
        
        TSSortOption sort = ID;
        String direction = "desc";
        String ppaJobId = null;
        String rhythmJobId = null;
        
        TSSortParams params = TSSortParams.parse(sort, direction, ppaJobId, rhythmJobId);
        assertSame(sort,params.sort);
        assertFalse(params.ascending);
        assertNull(params.jobId);
        
        direction = "";
        params = TSSortParams.parse(sort, direction, ppaJobId, rhythmJobId);
        assertSame(sort,params.sort);
        assertTrue(params.ascending);
        assertNull(params.jobId);
    }

    @Test
    public void parsesJobIdsIfNeeded() {
        
        String direction = "desc";
        String ppaJobId = null;
        String rhythmJobId = null;
        
        List<TSSortOption> options = List.of(NONE, ID, LABEL, CLUSTER);
        for (TSSortOption sort: options) {
            TSSortParams params = TSSortParams.parse(sort, direction, ppaJobId, rhythmJobId);
            assertNull(params.jobId);
        }
        
        options = List.of(PERIOD, PHASE, AMP, ERR);
        for (TSSortOption sort: options) {
            try {
                TSSortParams params = TSSortParams.parse(sort, direction, ppaJobId, rhythmJobId);
                fail("Expected exception");
                assertNull(params.jobId);
            } catch (IllegalArgumentException e) {}
        }        
        
        ppaJobId = "4b5450c1-f7b0-4fdd-a623-ef99765925cb";
        for (TSSortOption sort: options) {
            TSSortParams params = TSSortParams.parse(sort, direction, ppaJobId, rhythmJobId);
            assertNotNull(params.jobId);
        }   
        
        ppaJobId = null;
        rhythmJobId = "f10c47fb-3478-4a50-9d86-5d755272ed56";
        options = List.of(R_PVALUE, R_PEAK, R_PERIOD);
        for (TSSortOption sort: options) {
            TSSortParams params = TSSortParams.parse(sort, direction, ppaJobId, rhythmJobId);
            assertNotNull(params.jobId);
        }   
    }
    
}
