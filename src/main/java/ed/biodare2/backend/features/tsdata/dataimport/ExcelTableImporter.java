/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ed.synthsys.util.excel.ExcelFormatException;
import ed.synthsys.util.excel.ModernExcelView;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author tzielins
 */
public class ExcelTableImporter extends TSDataImporter {
    
    
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
        times = trim(times);
        
        if (times.isEmpty()) 
            throw new ImportException("No time values found");
        
        if (times.stream().anyMatch( v -> v == null))
            throw new ImportException("Time values cannot contain gaps or non numerical values");
        
        times = convertTimes(times,timeProperties);
        
        if (timeProperties.timeOffset != 0)
            times = times.stream().map( v -> v + timeProperties.timeOffset).collect(Collectors.toList());
        
        if (times.stream().anyMatch( v -> v<0))
            throw new ImportException("Times values cannot be < 0");
        
        return times;
    }    

    protected List<Double> trim(List<Double> values) {
        
        if (values.isEmpty()) return values;
        
        int lastNotNull = values.size()-1;
        for (;lastNotNull>=0;lastNotNull--) {
            if (values.get(lastNotNull) != null) break;
        }
        
        return values.subList(0, lastNotNull+1);
    }

    protected List<Double> convertTimes(List<Double> times, TimeColumnProperties timeProperties) throws ImportException {
        
        switch (timeProperties.timeType) {
            case TIME_IN_HOURS: return times;
            case TIME_IN_MINUTES: return times.stream().map(v -> v != null ? v/60.0 : null).collect(Collectors.toList());
            case TIME_IN_SECONDS: return times.stream().map(v -> v != null ? v/(60.0*60) : null).collect(Collectors.toList());
            case IMG_NUMBER: {
                    if (times.get(0) < 1.0) throw new ImportException("Image nr must be 1-based");
                    if (timeProperties.imgInterval <= 0) throw new ImportException("Image interval must be >0, got: "+timeProperties.imgInterval);
                    return times.stream().map( nr -> nr != null ? (nr-1)*timeProperties.imgInterval :  null).collect(Collectors.toList());
                    }
            default: throw new ImportException("Unsuported time column type: "+timeProperties.timeType);
        }
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

    protected TimeSeries makeSerie(List<Double> times, List<Double> values) {
        

        final int size = Math.min(times.size(),values.size());
        
        TimeSeries serie = new TimeSeries();
        for (int i =0;i<size;i++) {
            Double v = values.get(i);
            if (v != null)
                serie.add(times.get(i),v);
        }
        return serie;
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
