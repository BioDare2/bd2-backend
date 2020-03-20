/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare.data.topcount.TopCountReader;
import ed.biodare.data.topcount.err.FormatException;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellCoordinates;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRangeDescription;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRole;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBlock;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.dataimport.ExcelTSImportParameters;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeColumnProperties;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.util.Pair;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author tzielins
 */
public class TopCountImporter extends TSDataImporter {

    static final String ROWIDNUMBERS="_ABCDEFGHIJKLMN";
    
    final TopCountReader topcount = new TopCountReader(false);
    
    public DataBundle importTimeSeries(Path file, ExcelTSImportParameters parameters) throws ImportException {
        
        /* try {
            ObjectMapper mapper = new ObjectMapper();
            String param = mapper.writeValueAsString(parameters);
            System.out.println("Import top params:\n"+param);
        } catch (Exception e) {
            throw new ImportException("Topcount param error: "+e.getMessage(),e);
        } */
        try {
    
            Map<Pair<Integer, Integer>, TimeSeries> data = topcount.read(file);
            
            data = processTimes(data,(TimeColumnProperties)parameters.timeColumn.details);
            
            List<DataBlock> blocks = makeBlocks(data,parameters.dataBlocks);
            
            insertNumbers(blocks);
            insertIds(blocks);
        
            return makeBundle(blocks);
            
        } catch (FormatException e) {
            throw new ImportException("Topcount format error: "+e.getMessage(),e);
        } catch (IOException e) {
            throw new ServerSideException("Cannot read file: "+e.getMessage(),e);
        }
    }

    protected Map<Pair<Integer, Integer>, TimeSeries> processTimes(Map<Pair<Integer, Integer>, TimeSeries> data, TimeColumnProperties timeColumnProperties) {
        
        if (timeColumnProperties.timeOffset == 0) return data;
        
        Map<Pair<Integer, Integer>, TimeSeries> res = new HashMap<>(data.size());
        
        data.forEach( (key, series) -> {
            res.put(key, series.offsetTime(timeColumnProperties.timeOffset));
        });
        
        return res;
    }

    protected List<DataBlock> makeBlocks(Map<Pair<Integer, Integer>, TimeSeries> data, List<CellRangeDescription> blocksDescriptions) {
        
        List<DataBlock> blocks = blocksDescriptions.stream()
                .filter(dsc -> dsc.role.equals(CellRole.DATA) || dsc.role.equals(CellRole.BACKGROUND))
                .map( dsc -> makeBlock(data,dsc))
                .collect(Collectors.toList());

        return blocks;
    }

    protected DataBlock makeBlock(Map<Pair<Integer, Integer>, TimeSeries> data, CellRangeDescription dsc) {
        
        DataBlock block = new DataBlock();
        block.details = (DataColumnProperties)dsc.details;
        block.range = dsc.range;
        block.role = dsc.role;
        
        for (int col = dsc.range.first.col-1;col<dsc.range.last.col;col++) {
            
            Pair<Integer, Integer> key = colNrToCoordinates(col);
            
            
            TimeSeries series = data.get(key);
            if (series == null) throw new IllegalArgumentException("Missing timeseries for well: "+key+"; col:"+col);
            
            block.traces.add(makeTrace(series,block.details,dsc.role,key));
            
        }
        
        return block;        
    }

    protected DataTrace makeTrace(TimeSeries series, DataColumnProperties details, CellRole role, Pair<Integer, Integer> key) {
        DataTrace trace = new DataTrace();
        trace.coordinates = new CellCoordinates(key.getLeft(),key.getRight());
        trace.traceRef = coordinatesToWell(key);//trace.coordinates.columnLetter();
        trace.traceFullRef = trace.traceRef;
        trace.details = details;
        trace.role = role;
        trace.trace = series;
        return trace;    
    }

    protected String coordinatesToWell(Pair<Integer, Integer> key) {
        
        return ROWIDNUMBERS.substring(key.getLeft(),key.getLeft()+1)+key.getRight();
    }

    protected Pair<Integer, Integer> colNrToCoordinates(int nr) {
        int row = 1 + nr / 12;
        int col = 1 + (nr % 12);
        return new Pair<>(row,col);
    }
    
}
