/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare2.backend.repo.isa_dom.dataimport.DataBlock;
import ed.biodare2.backend.repo.isa_dom.dataimport.DataTrace;
import ed.biodare2.backend.repo.isa_dom.dataimport.TimeType;
import ed.robust.dom.data.TimeSeries;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    
    @Test
    public void trimGivesEmptyForEmptyOrNullOnlyValues() {
        
        List<Double> values = Collections.emptyList();
        List<Double> exp = Collections.emptyList();
        List<Double> res = instance.trim(values);
        assertEquals(exp, res);
        
        values = Arrays.asList(null,null);
        res = instance.trim(values);
        assertEquals(exp, res);                
    }
    
    @Test
    public void trimRetainsInnerNulls() {
        
        List<Double> values = Arrays.asList(null,1.0,null,2.0);
        List<Double> res = instance.trim(values);
        assertEquals(values, res);
        
    }
    
    @Test
    public void trimRemovesTrailingNulls() {
        
        List<Double> values = Arrays.asList(null,1.0,null,2.0,null,null,null);
        List<Double> exp = Arrays.asList(null,1.0,null,2.0);
        List<Double> res = instance.trim(values);
        assertEquals(exp, res);
        
    }   
    
    @Test
    public void convertTimesReturnsOrginalIfTimeInHours() throws ImportException {
        
        List<Double> times = Arrays.asList(0.123,null,1.1,0.234);
        
        TimeType timeType = TimeType.TIME_IN_HOURS;
        
        List<Double> res = instance.convertTimes(times, timeType, 0);
        assertEquals(times,res);
    }

    @Test
    public void convertTimesConvertsMinutesToHoursIfTimeInMinutes() throws ImportException {
        
        List<Double> times = Arrays.asList(60.0,null,150.0,180.0);
        List<Double> exp = Arrays.asList(1.0,null,2.5,3.0);
        
        
        TimeType timeType = TimeType.TIME_IN_MINUTES;
        
        List<Double> res = instance.convertTimes(times, timeType, 0);
        assertEquals(exp,res);
    }    
    
    @Test
    public void convertTimesConvertsImageNrToHoursIfTimeInImages() throws ImportException {
        
        List<Double> times = Arrays.asList(1.0,null,2.0,3.0);
        List<Double> exp = Arrays.asList(0.0,null,2.0,4.0);
        
        
        TimeType timeType = TimeType.IMG_NUMBER;
        double imgInterval = 2.0;
        
        List<Double> res = instance.convertTimes(times, timeType, imgInterval);
        assertEquals(exp,res);
    }      
    
    @Test
    public void convertTimesThrowsExceptionForUnknowType() {
        
        List<Double> times = Arrays.asList(1.0,null,2.0,3.0);
        
        
        
        TimeType timeType = TimeType.NONE;

        try {
            List<Double> res = instance.convertTimes(times, timeType, 0);
            fail("Excpetion expected");
        } catch (ImportException e) {};
    }  
     
    @Test
    public void makeSerieCreatesTSIgnoringTheGaps() {
        
        List<Double> times = Arrays.asList(1.0,2.0,3.0,4.0,5.0);
        List<Double> values = Arrays.asList(null,1.5,2.5);
        
        TimeSeries exp = new TimeSeries();
        exp.add(2,1.5);
        exp.add(3,2.5);
        
        TimeSeries res = instance.makeSerie(times, values);
        assertEquals(exp, res);
        
        
    }    
    
    @Test
    public void processTimesValidatesTimes() {
        
        TimeType timeType  = TimeType.TIME_IN_HOURS;
        double timeOffset = 2.0;
        double imgInterval = 0;
        
        List<Double> times = Collections.emptyList();
        try {
            instance.processTimes(times, timeType,timeOffset, imgInterval);
            fail("Exception expected");
        } catch (ImportException e){}
        
        times = Arrays.asList(null,null);
        try {
            instance.processTimes(times, timeType,timeOffset, imgInterval);
            fail("Exception expected");
        } catch (ImportException e){}
        
        times = Arrays.asList(1.0,null,2.0);
        try {
            instance.processTimes(times, timeType,timeOffset, imgInterval);
            fail("Exception expected");
        } catch (ImportException e){}
        
        times = Arrays.asList(1.0,2.0);
        timeOffset = -2;
        try {
            instance.processTimes(times, timeType,timeOffset, imgInterval);
            fail("Exception expected");
        } catch (ImportException e){}
        
    }
    
    @Test
    public void processTimesConvertsTimes() throws ImportException {
        
        
        TimeType timeType = TimeType.IMG_NUMBER;
        double imgInterval = 2;
        double timeOffset = 1.0;
        
        List<Double> times = Arrays.asList(1.0,2.0,3.0);
        List<Double> exp = Arrays.asList(1.0,3.0,5.0);
        List<Double> res = instance.processTimes(times, timeType, timeOffset, imgInterval);
        
        assertEquals(exp,res);
    }   
    
    
    
}
