/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare.data.excel.ExcelFormatException;
import ed.biodare.data.excel.ModernExcelView;
import ed.biodare2.backend.web.rest.ServerSideException;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellCoordinates;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRangeDescription;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRole;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBlock;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.dataimport.ExcelTSImportParameters;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeColumnProperties;
import ed.robust.dom.data.TimeSeries;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The implementation of Excel import that worked with the "old" import UI.
 * It is replaced by ExcelTableImporter, that can handle large files by converting them first to csv.
 * @author tzielins
 */
public class RegionBasedExcelTableImporter extends TSDataImporter {
    
    
    public DataBundle importTimeSeries(Path file,ExcelTSImportParameters parameters) throws ImportException {
        
        /* try {
            ObjectMapper mapper = new ObjectMapper();
            String param = mapper.writeValueAsString(parameters);
            System.out.println("Import excl params:\n"+param);
        } catch (Exception e) {
            throw new ImportException("Topcount param error: "+e.getMessage(),e);
        } */        
        try (ModernExcelView excel = new ModernExcelView(file)) {
            
            return importTimeSeries(excel,parameters);
        } catch (ExcelFormatException e) {
            throw new ImportException("Excell format error: "+e.getMessage(),e);
        } catch (IOException e) {
            throw new ServerSideException("Cannot read file: "+e.getMessage(),e);
        }
    }

    protected DataBundle importTimeSeries(ModernExcelView excel, ExcelTSImportParameters parameters) throws ImportException {
        
        List<Double> times = readTimes(excel,parameters.timeColumn);
        
        //excel is 0-based, imput is 1-based
        int firstRow = ((TimeColumnProperties)parameters.timeColumn.details).firstRow-1;
        
        List<DataBlock> blocks = readBlocks(excel,parameters.dataBlocks,times,firstRow);
        
        insertNumbers(blocks);
        insertIds(blocks);
        
        return makeBundle(blocks);
    }


    protected List<Double> readTimes(ModernExcelView excel, CellRangeDescription timeColumn) throws ImportException {
        
        if (timeColumn.details == null) throw new IllegalArgumentException("Missing time column details");
        if (!(timeColumn.details instanceof TimeColumnProperties)) throw new IllegalArgumentException("Unexpected type of time column details: "+timeColumn.details.getClass());

        TimeColumnProperties timeProperties = (TimeColumnProperties)timeColumn.details;
        
        //excel view is 0-based
        int colNr = timeColumn.range.first.col-1;
        int rowNr = timeProperties.firstRow-1;
        
        List<Double> times = excel.readDoubleColumn(colNr, rowNr);
        
        times = processTimes(times,timeProperties);

        return times;
    }
    
    protected List<Double> processTimes(List<Double> times, TimeColumnProperties timeProperties) throws ImportException {
        
        return processTimes(times,timeProperties.timeType,timeProperties.timeOffset,timeProperties.imgInterval);
    }    





    protected List<DataBlock> readBlocks(ModernExcelView excel, List<CellRangeDescription> blocksDescriptions, List<Double> times,int firstRow) {

        List<DataBlock> blocks = blocksDescriptions.stream()
                .filter(dsc -> dsc.role.equals(CellRole.DATA) || dsc.role.equals(CellRole.BACKGROUND))
                .map( dsc -> readBlock(excel,dsc,times,firstRow))
                .collect(Collectors.toList());
        

        return blocks;
        
    }

    protected DataBlock readBlock(ModernExcelView excel, CellRangeDescription dsc, List<Double> times,int firstRow) {
        
        DataBlock block = new DataBlock();
        block.details = (DataColumnProperties)dsc.details;
        block.range = dsc.range;
        block.role = dsc.role;
        
        for (int col = dsc.range.first.col-1;col<dsc.range.last.col;col++) {
            
            List<Double> values = excel.readDoubleColumn(col, firstRow);
            TimeSeries serie = makeSerie(times,values);
            
            block.traces.add(makeTrace(serie,block.details,dsc.role,col,firstRow));
            
        }
        
        return block;
    }



    protected DataTrace makeTrace(TimeSeries serie, DataColumnProperties details, CellRole role, int col, int row) {

        DataTrace trace = new DataTrace();
        trace.coordinates = new CellCoordinates(col+1,row+1);
        trace.traceRef = trace.coordinates.columnLetter();
        trace.traceFullRef = trace.traceRef;
        trace.details = details;
        trace.role = role;
        trace.trace = serie;
        return trace;
    }




}
