/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare2.backend.repo.isa_dom.dataimport.CellCoordinates;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRange;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRangeDescription;
import ed.biodare2.backend.repo.isa_dom.dataimport.CellRole;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBlock;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataBundle;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.ExcelTSImportParameters;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeColumnProperties;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeType;
import ed.robust.dom.data.TimeSeries;
import ed.synthsys.util.excel.ModernExcelView;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tzielins
 */
public class ExcelTableImporterTest {
    
    static final String fileName = "data-sheet.xlsx";
    static final double EPS = 1E-6;
    
    public ExcelTableImporterTest() {
    }

    ExcelTableImporter instance;
    
    @Before
    public void init() {
        instance = new ExcelTableImporter();
    }
    
   
    
    @Test
    public void processTimesValidatesTimes() {
        
        TimeColumnProperties prop = new TimeColumnProperties();
        prop.timeType = TimeType.TIME_IN_HOURS;
        prop.timeOffset = 2.0;
        
        List<Double> times = Collections.emptyList();
        try {
            instance.processTimes(times, prop);
            fail("Exception expected");
        } catch (ImportException e){}
        
        times = Arrays.asList(null,null);
        try {
            instance.processTimes(times, prop);
            fail("Exception expected");
        } catch (ImportException e){}
        
        times = Arrays.asList(1.0,null,2.0);
        try {
            instance.processTimes(times, prop);
            fail("Exception expected");
        } catch (ImportException e){}
        
        times = Arrays.asList(1.0,2.0);
        prop.timeOffset = -2;
        try {
            instance.processTimes(times, prop);
            fail("Exception expected");
        } catch (ImportException e){}
        
    }
    
    @Test
    public void processTimesConvertsTimes() throws ImportException {
        
        TimeColumnProperties prop = new TimeColumnProperties();
        prop.timeType = TimeType.IMG_NUMBER;
        prop.imgInterval = 2;
        prop.timeOffset = 1.0;
        
        List<Double> times = Arrays.asList(1.0,2.0,3.0);
        List<Double> exp = Arrays.asList(1.0,3.0,5.0);
        List<Double> res = instance.processTimes(times, prop);
        
        assertEquals(exp,res);
    }   
    
    public static Path getExcelTestFile() {
        try {
            return Paths.get(ExcelTableImporterTest.class.getResource(fileName).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }
    
    @Test
    public void readTimesReadCorrectlyFromExcell() throws Exception {
        
        Path file = getExcelTestFile();

        CellRangeDescription timeColumn = new CellRangeDescription();
        timeColumn.range = new CellRange();
        timeColumn.range.first = new CellCoordinates(1,2);
        timeColumn.range.last = timeColumn.range.first;
        
        TimeColumnProperties prop = new TimeColumnProperties();
        prop.firstRow = 2;
        prop.timeType = TimeType.TIME_IN_HOURS;
        prop.timeOffset = 3;
        
        timeColumn.details = prop;
        timeColumn.role = CellRole.TIME;
        
        try(ModernExcelView excel = new ModernExcelView(file)) {
            
            List<Double> times = instance.readTimes(excel, timeColumn);
            assertEquals(3,times.get(0),EPS);
            assertEquals(3.5,times.get(1),EPS);
            assertEquals(144,times.get(times.size()-1),EPS);
                        
        }

    }
    
    @Test
    public void readTimesReadThrowsExceptionOnMissingColumn() throws Exception {
        
        Path file = getExcelTestFile();

        CellRangeDescription timeColumn = new CellRangeDescription();
        timeColumn.range = new CellRange();
        timeColumn.range.first = new CellCoordinates(700,2);
        timeColumn.range.last = timeColumn.range.first;
        
        TimeColumnProperties prop = new TimeColumnProperties();
        prop.firstRow = 2;
        prop.timeType = TimeType.TIME_IN_HOURS;
        prop.timeOffset = 3;
        
        timeColumn.details = prop;
        timeColumn.role = CellRole.TIME;
        
        try(ModernExcelView excel = new ModernExcelView(file)) {
            
            List<Double> times = instance.readTimes(excel, timeColumn);
            fail("ExceptionExpected");
                        
        } catch (ImportException e){};

    }    
    

    
    @Test
    public void readBlockReadsCorrectly() throws Exception {
        Path file = getExcelTestFile();
        try(ModernExcelView excel = new ModernExcelView(file)) {
            
            CellRangeDescription dsc = new CellRangeDescription();
            DataColumnProperties details = new DataColumnProperties();
            dsc.details = details;
            details.dataLabel = "ala";
            
            dsc.role = CellRole.DATA;
            dsc.range = new CellRange();
            dsc.range.first = new CellCoordinates(3, 0);
            dsc.range.last = new CellCoordinates(5, 0);
            
            int firstRow = 1;
            List<Double> times = Arrays.asList(1.0,2.0,3.0);
            DataBlock res = instance.readBlock(excel, dsc, times, firstRow);
            
            assertEquals(details,res.details);
            assertEquals(dsc.range, res.range);
            assertEquals(CellRole.DATA, res.role);
            assertEquals(3,res.traces.size());
            
            res.traces.forEach(trace -> {
                assertEquals(3,trace.trace.size());
                assertEquals(1.0,trace.trace.getFirst().getTime(),EPS);
                assertEquals(3.0,trace.trace.getLast().getTime(),EPS);
            });
            
            List<Double> fV = Arrays.asList(89.0263,59.8943,66.9469);
            List<Double> rFV = res.traces.stream().map(t -> t.trace.getFirst().getValue()).collect(Collectors.toList());
            assertEquals(fV,rFV);
            
        }
    }
    
    
    @Test
    public void importTimeSeries() throws Exception {
    
        Path file = getExcelTestFile();
        try(ModernExcelView excel = new ModernExcelView(file)) {
            
            ExcelTSImportParameters parameters = new ExcelTSImportParameters();
            
            CellRangeDescription timeColumn = new CellRangeDescription();
            timeColumn.range = new CellRange();
            timeColumn.range.first = new CellCoordinates(1,2);
            timeColumn.range.last = timeColumn.range.first;
        
            TimeColumnProperties prop = new TimeColumnProperties();
            prop.firstRow = 2;
            prop.timeType = TimeType.TIME_IN_HOURS;
            prop.timeOffset = 0;
            timeColumn.details = prop;
            timeColumn.role = CellRole.TIME;
            
            parameters.timeColumn = timeColumn;
            parameters.dataBlocks.add(timeColumn);
            
            CellRangeDescription dsc = new CellRangeDescription();
            DataColumnProperties details = new DataColumnProperties();
            dsc.details = details;
            details.dataLabel = "ala";
            
            dsc.role = CellRole.DATA;
            dsc.range = new CellRange();
            dsc.range.first = new CellCoordinates(2, 0);
            dsc.range.last = new CellCoordinates(4, 0);
            parameters.dataBlocks.add(dsc);
            
            dsc = new CellRangeDescription();            
            dsc.role = CellRole.BACKGROUND;
            dsc.range = new CellRange();
            dsc.range.first = new CellCoordinates(5, 0);
            dsc.range.last = new CellCoordinates(6, 0);            
            parameters.dataBlocks.add(dsc);            
            
            dsc = new CellRangeDescription();
            details = new DataColumnProperties();
            dsc.details = details;
            details.dataLabel = "WT";
            
            dsc.role = CellRole.DATA;
            dsc.range = new CellRange();
            dsc.range.first = new CellCoordinates(7, 0);
            dsc.range.last = new CellCoordinates(9, 0);
            parameters.dataBlocks.add(dsc);
            
            
            DataBundle bundle = instance.importTimeSeries(excel, parameters);
            assertEquals(3,bundle.blocks.size());
            assertEquals(2,bundle.backgrounds.size());
            assertEquals(6,bundle.data.size());
            
            bundle.data.stream().map( d -> d.trace.getLast().getTime()).allMatch(d -> d == 141.0);
            List<Integer> ids = bundle.data.stream().map( d -> d.traceNr).collect(Collectors.toList());
            List<Integer> expIds = Arrays.asList(1,2,3,6,7,8);
            assertEquals(expIds,ids);
            
            assertEquals(Arrays.asList(1L,2L,3L,6L,7L,8L),
                    bundle.data.stream().map( d -> d.dataId).collect(Collectors.toList()));
            
            List<String> refs = bundle.data.stream().map( d -> d.traceRef).collect(Collectors.toList());
            List<String> expR = Arrays.asList("B","C","D","G","H","I");
            assertEquals(expR,refs);
            
        }
        
    }
}
