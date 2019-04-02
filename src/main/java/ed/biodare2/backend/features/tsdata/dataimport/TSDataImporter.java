/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare2.backend.repo.isa_dom.dataimport.CellRole;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBlock;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author tzielins
 */
public class TSDataImporter {
    
    protected void insertNumbers(List<DataBlock> blocks) {
        
        int blockId = 1;
        int serId = 1;
        
        for (DataBlock block : blocks) {
            block.blockNr = blockId++;
            for (DataTrace trace : block.traces)
                trace.traceNr = serId++;
        }
    }
    
    protected void insertIds(List<DataBlock> blocks) {
        
        for (DataBlock block : blocks) {
            for (DataTrace trace : block.traces) {
                trace.dataId = trace.traceNr;
                trace.rawDataId = trace.dataId;
            }
        }
    }    

    protected DataBundle makeBundle(List<DataBlock> blocks) {
        
        DataBundle bundle = new DataBundle();
        bundle.blocks = blocks;
        
        bundle.backgrounds = blocks.stream().filter(b -> b.role.equals(CellRole.BACKGROUND))
                                            .flatMap(b -> b.traces.stream())
                                            .collect(Collectors.toList());
        
        bundle.data = blocks.stream().filter(b -> b.role.equals(CellRole.DATA))
                                            .flatMap(b -> b.traces.stream())
                                            .collect(Collectors.toList());
        return bundle;
    }    
}
