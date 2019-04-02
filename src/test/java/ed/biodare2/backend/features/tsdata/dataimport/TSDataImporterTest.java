/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare2.backend.repo.isa_dom.dataimport.DataBlock;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class TSDataImporterTest {
    
    TSDataImporter instance;
    
    public TSDataImporterTest() {
    }
    
    @Before
    public void setUp() {
        instance = new TSDataImporter();
    }

    @Test
    public void insertNumbersSetsConsecutivesNumbersForBlocksAndSeriesNr() {
        
        List<DataBlock> blocks = new ArrayList();
        
        DataBlock block = new DataBlock();
        block.traces.add(new DataTrace());
        blocks.add(block);
        
        block = new DataBlock();
        block.traces.add(new DataTrace());
        block.traces.add(new DataTrace());
        blocks.add(block);        

        instance.insertNumbers(blocks);
        assertEquals(1,blocks.get(0).blockNr);
        assertEquals(2,blocks.get(1).blockNr);
        
        assertEquals(1,blocks.get(0).traces.get(0).traceNr);
        assertEquals(2,blocks.get(1).traces.get(0).traceNr);
        assertEquals(3,blocks.get(1).traces.get(1).traceNr);
        
    }

    @Test
    public void insertIdsUsesTracesNumbersAsDataIds() {
        
        List<DataBlock> blocks = new ArrayList();
        
        DataBlock block = new DataBlock();
        block.traces.add(new DataTrace());
        block.traces.add(new DataTrace());
        block.traces.get(0).traceNr = 2;
        block.traces.get(1).traceNr = 4;
        blocks.add(block);
        
        instance.insertIds(blocks);
        
        assertEquals(2L,blocks.get(0).traces.get(0).dataId);
        assertEquals(2L,blocks.get(0).traces.get(0).rawDataId);
        assertEquals(4L,blocks.get(0).traces.get(1).dataId);
        assertEquals(4L,blocks.get(0).traces.get(1).rawDataId);
        
    }
    
}
