/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.datahandling;

import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.data.NormalizationType;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.data.Timepoint;
import ed.robust.dom.util.Pair;
import ed.robust.util.timeseries.TSGenerator;
import ed.robust.util.timeseries.TimeSeriesOperations;
//import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tzielins
 */
public class TimeSeriesTrfImpTest {
    
    double EPS = 1e-6;
    
    public TimeSeriesTrfImpTest() {
    }
    
    TimeSeriesTransformer makeInstance() {
        return TimeSeriesTrfImp.getInstance();        
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testStandardise() throws IOException {
        System.out.println("Testing standarization");
        
        TimeSeriesTransformer transformer = makeInstance();
        
        TimeSeries data = new TimeSeries();
        TimeSeries out = transformer.standardise(data);
        assertNotNull(out);
        assertEquals(0, out.size());
        
        int N = 5*24;
        double step = 1;
        double period = 24;
        double phase = 5;
        double amp = 2;
        
        data = TSGenerator.makeCos(N, step, period,phase,amp);
        out = transformer.standardise(data);
        
        assertEquals(data,out);
        
        N = 16*24*10;
        step = 0.1;
        
        data = TSGenerator.makeCos(N, step, period,phase,amp);
        out = transformer.standardise(data);

        assertEquals(data.getAverageStep(), out.getAverageStep(),EPS);
        //List<TimeSeries> series = Arrays.asList(data,out);
        //TimeSeriesFileHandler.saveToText(series, new File("D:/Temp/std.csv"), ",", ROUNDING_TYPE.NO_ROUNDING);
        assertTrue(data.almostEquals(out, EPS));
        
        N = 16*24*20;
        step = 0.05;
        
        data = TSGenerator.makeCos(N, step, period,phase,amp);
        out = transformer.standardise(data);

        assertEquals(0.1, out.getAverageStep(),EPS);
        assertFalse(data.equals(out));
        
        {
            List<Timepoint> dataL = data.getTimepoints();
            List<Timepoint> outL = out.getTimepoints();
            assertEquals(dataL.get(0),outL.get(0));
            for (int i = 0;i<outL.size();i++) {
                assertTrue(dataL.get(i*2).almostEquals(outL.get(i),EPS));
            }
        }
        
        N = 35*24*20;
        step = 0.05;
        
        data = TSGenerator.makeCos(N, step, period,phase,amp);
        out = transformer.standardise(data);

        assertEquals(0.5, out.getAverageStep(),EPS);
        assertFalse(data.equals(out));
        
        {
            List<Timepoint> dataL = data.getTimepoints();
            List<Timepoint> outL = out.getTimepoints();
            assertEquals(dataL.get(0),outL.get(0));
            for (int i = 0;i<outL.size();i++) {
                assertTrue(dataL.get(i*10).almostEquals(outL.get(i),EPS));
            }
        }
        
        N = 60*24*20;
        step = 0.05;
        
        data = TSGenerator.makeCos(N, step, period,phase,amp);
        out = transformer.standardise(data);

        assertEquals(0.5, out.getAverageStep(),EPS);
        assertFalse(data.equals(out));
        
        {
            List<Timepoint> dataL = data.getTimepoints();
            List<Timepoint> outL = out.getTimepoints();
            assertEquals(dataL.get(0),outL.get(0));
            for (int i = 0;i<outL.size();i++) {
                assertTrue(dataL.get(i*10).almostEquals(outL.get(i),EPS));
            }
        }
        
        /* testing rounding whihc is not implemented
        List<TimeSeries> ser = new ArrayList<TimeSeries>();
        
        data = new TimeSeries();
        data.add(1.01,1);
        data.add(2,2);
        data.add(3.1,3);
        data.add(4.00001,4);

        TimeSeries rounded = new TimeSeries();
        for (Timepoint tp : data) rounded.add(DataRounder.round(tp.getTime(), ROUNDING_TYPE.DECY),tp.getValue());
        
        out = transformer.standardise(data);
        
        ser.add(data);
        ser.add(out);
        {
            List<Timepoint> dataL = rounded.getTimepoints();
            List<Timepoint> outL = out.getTimepoints();
            assertEquals(dataL.get(0),outL.get(0));
            for (int i = 0;i<outL.size();i++) {
                //assertTrue(dataL.get(i*10).almostEquals(outL.get(i),EPS));
                assertEquals(dataL.get(i),outL.get(i));
            }
        }
        
        
        data = new TimeSeries();
        data.add(10.01,1);
        data.add(10.211111111,2);
        data.add(10.3000001,3);
        data.add(10.47777,4);

        rounded = new TimeSeries();
        for (Timepoint tp : data) rounded.add(DataRounder.round(tp.getTime(), ROUNDING_TYPE.DECY),tp.getValue());
        
        out = transformer.standardise(data);
        
        ser.add(data);
        ser.add(out);
        
        {
            List<Timepoint> dataL = rounded.getTimepoints();
            List<Timepoint> outL = out.getTimepoints();
            assertEquals(dataL.get(0),outL.get(0));
            for (int i = 0;i<outL.size();i++) {
                //assertTrue(dataL.get(i*10).almostEquals(outL.get(i),EPS));
                assertEquals(dataL.get(i),outL.get(i));
            }
        }
        
    
        data = new TimeSeries();
        data.add(20.01,1);
        data.add(20.05111,2);
        data.add(20.066,3);
        data.add(20.07,4);
        
        System.out.println("AS: "+data.getAverageStep());

        rounded = new TimeSeries();
        for (Timepoint tp : data) rounded.add(DataRounder.round(tp.getTime(), ROUNDING_TYPE.MIL),tp.getValue());
        
        out = transformer.standardise(data);
        
        ser.add(data);
        ser.add(out);
        
        {
            List<Timepoint> dataL = rounded.getTimepoints();
            List<Timepoint> outL = out.getTimepoints();
            //assertEquals(dataL.get(0),outL.get(0));
            for (int i = 0;i<outL.size();i++) {
                //assertTrue(dataL.get(i*10).almostEquals(outL.get(i),EPS));
                //assertEquals(dataL.get(i),outL.get(i));
            }
        }
        
       data = new TimeSeries();
        data.add(30.0501,1);
        data.add(30.05111,2);
        data.add(30.066,3);
        data.add(30.0667,4);

        rounded = new TimeSeries();
        for (Timepoint tp : data) rounded.add(DataRounder.round(tp.getTime(), ROUNDING_TYPE.NO_ROUNDING),tp.getValue());
        
        out = transformer.standardise(data);
        
        ser.add(data);
        ser.add(out);
        
        {
            List<Timepoint> dataL = rounded.getTimepoints();
            List<Timepoint> outL = out.getTimepoints();
            //assertEquals(dataL.get(0),outL.get(0));
            for (int i = 0;i<outL.size();i++) {
                //assertTrue(dataL.get(i*10).almostEquals(outL.get(i),EPS));
                //assertEquals(dataL.get(i),outL.get(i));
            }
        }
        
        
        TimeSeriesFileHandler.saveToText(ser, new File("D:/Temp/round.csv"), ",", ROUNDING_TYPE.NO_ROUNDING);
        */
    }
    
    @Test
    public void testNormalization() {
        System.out.println("Testing normalization");
        
        
        TimeSeriesTransformer transformer = makeInstance();
        
        TimeSeries data = new TimeSeries();
        NormalizationType type = NormalizationType.NO_NORM;
        
        TimeSeries out = transformer.normalise(data, type);
        assertEquals(data, out);
        
        data.add(1,5);
        out = transformer.normalise(data, type);
        assertEquals(data, out);   
        
        data.add(Math.random()*100,System.currentTimeMillis());
        out = transformer.normalise(data, type);
        assertEquals(data, out);   
        
        data = new TimeSeries();
        type = NormalizationType.MAX_NORM;        
        
        out = transformer.normalise(data, type);
        assertEquals(data, out);        

        TimeSeries expected = new TimeSeries();
        
        data.add(1,5);
        expected.add(1,1);
        
        out = transformer.normalise(data, type);
        assertEquals(expected, out);   
        
        data = new TimeSeries();
        data.add(1,5);
        data.add(new Timepoint(2,10,5.0,3.0));
        
        expected = new TimeSeries();
        expected.add(1,0.5);
        expected.add(new Timepoint(2,1,0.5,0.3));
        
        out = transformer.normalise(data, type);
        assertEquals(expected, out);         
        
        type = NormalizationType.MEAN_NORM;        
        data = new TimeSeries();
        
        out = transformer.normalise(data, type);
        assertEquals(data, out);        

        expected = new TimeSeries();
        
        data.add(1,5);
        expected.add(1,1);
        
        out = transformer.normalise(data, type);
        assertEquals(expected, out);   
        
        data = new TimeSeries();
        data.add(1,2);
        data.add(new Timepoint(2,4,5.0,3.0));
        data.add(new Timepoint(3,0,4,Timepoint.STD_DEV));
        
        expected = new TimeSeries();
        expected.add(1,1);
        expected.add(new Timepoint(2,2,2.5,1.5));
        expected.add(new Timepoint(3,0,2,Timepoint.STD_DEV));
        
        out = transformer.normalise(data, type);
        assertEquals(expected, out);         
        
        
    }
    
    
    @Test
    public void testLineDetrending() {
        System.out.println("Testing no and lin detrending");
        
        Random rand = new Random();
        TimeSeriesTransformer transformer = makeInstance();
        
        DetrendingType type = DetrendingType.NO_DTR;
        
        TimeSeries data = new TimeSeries();
        TimeSeries out = transformer.detrend(data, type);
        assertTrue(data.equals(out));
        
        data.add(rand.nextDouble()*100,rand.nextDouble()*100);
        out = transformer.detrend(data, type);
        assertTrue(data.equals(out));

        data = makeRandom(50, true);
        out = transformer.detrend(data, type);
        assertTrue(data.equals(out));
        
        //lin trend
        type = DetrendingType.LIN_DTR;
        
        data = new TimeSeries();
        out = transformer.detrend(data, type);
        assertTrue(data.equals(out));
        
        data.add(rand.nextDouble()*100,rand.nextDouble()*100);
        out = transformer.detrend(data, type);
        assertTrue(data.equals(out));

        
        data = makeRandom(50, true);
        out = transformer.detrend(data, type);
        assertFalse(data.equals(out));
        
        data = new TimeSeries();
        data.add(new Timepoint(1,10,0.5,0.2));
        data.add(10,10);
        
        out = transformer.detrend(data, type);
        assertTrue(data.equals(out));        
        
        data = new TimeSeries();
        data.add(0,2);
        data.add(2,6);
        
        TimeSeries expected = new TimeSeries();
        expected.add(0,4);
        expected.add(2,4);
        
        out = transformer.detrend(data, type);
        assertTrue(expected.equals(out));
        
    }    

    
    @Test
    public void testPolyDetrending() throws IOException {
        System.out.println("Testing poly detrending");
        
        Random rand = new Random();
        TimeSeriesTransformer transformer = makeInstance();
        
        DetrendingType type = DetrendingType.POLY_DTR;
        
        TimeSeries data = new TimeSeries();
        TimeSeries out = transformer.detrend(data, type);
        assertTrue(data.equals(out));
        
        data.add(rand.nextDouble()*100,rand.nextDouble()*100);
        out = transformer.detrend(data, type);
        assertTrue(data.equals(out));

        data = makeRandom(50, true);
        out = transformer.detrend(data, type);
        assertFalse(data.equals(out));
        
 
        data = new TimeSeries();
        data.add(new Timepoint(1,10,0.5,0.2));
        data.add(10,10);
        
        out = transformer.detrend(data, type);
        assertTrue(data.almostEquals(out,EPS));        
        
        data = new TimeSeries();
        data.add(0,2);
        data.add(2,6);
        
        TimeSeries expected = new TimeSeries();
        expected.add(0,4);
        expected.add(2,4);
        
        out = transformer.detrend(data, type);
        assertTrue(expected.equals(out));

        //x3+2x2+x-10;
        data = new TimeSeries();
        double mean = 0;
        for (int i=0;i<5;i++) {
            double v = i*i*i+2*i*i+i-10;
            mean+=v;
            data.add(i,v);
        }
        expected = new TimeSeries();
        for (int i=0;i<data.size();i++) expected.add(i,mean/data.size());
        
        out = transformer.detrend(data, type);
        {
            List<TimeSeries> list = Arrays.asList(data,out,expected);
            //TimeSeriesFileHandler.saveToText(list, new File("D:/Temp/poly.csv"), ",");
        }
        assertTrue(expected.almostEquals(out,EPS));
        
        
        
    }    
    
    
    @Test
    public void testBaseDetrending() throws IOException {
        System.out.println("Testing base detrending");
        
        Random rand = new Random();
        TimeSeriesTransformer transformer = makeInstance();
        
        DetrendingType type = DetrendingType.BASE_DTR;
        
        TimeSeries data = new TimeSeries();
        TimeSeries out = transformer.detrend(data, type);
        assertTrue(data.equals(out));
        
        data.add(rand.nextDouble()*100,rand.nextDouble()*100);
        out = transformer.detrend(data, type);
        assertTrue(data.equals(out));

        data = makeRandom(50, true);
        out = transformer.detrend(data, type);
        assertFalse(data.equals(out));
        
 
        data = new TimeSeries();
        data.add(new Timepoint(1,10,0.5,0.2));
        data.add(15,10);
        data.add(20,10);
        data.add(25,10);
        data.add(30,10);
        
        out = transformer.detrend(data, type);
        /*{
            List<TimeSeries> list = Arrays.asList(data,out);
            TimeSeriesFileHandler.saveToText(list, new File("D:/Temp/base.csv"), ",");
        }*/
        
        assertTrue(data.almostEquals(out,1E-1));        
        
        //0.001*x2;
        data = new TimeSeries();
        double mean = 0;
        for (int i=0;i<100;i++) {
            double v = 0.001*i*i;
            mean+=v;
            data.add(i,v);
        }
        TimeSeries expected = new TimeSeries();
        for (int i=0;i<data.size();i++) expected.add(i,mean/data.size());
        
        out = transformer.detrend(data, type);
        /*{
            List<TimeSeries> list = Arrays.asList(data,out,expected);
            TimeSeriesFileHandler.saveToText(list, new File("D:/Temp/base.csv"), ",");
        }*/
        assertTrue(expected.almostEquals(out,0.5));
        
        data = new TimeSeries();
        mean = 0;
        for (int i=0;i<100;i++) {
            double v = 4*Math.sin(i*3.14/120);
            mean+=v;
            data.add(i,v);
        }
        expected = new TimeSeries();
        for (int i=0;i<data.size();i++) expected.add(i,mean/data.size());
        
        out = transformer.detrend(data, type);
        /*{
            List<TimeSeries> list = Arrays.asList(data,out,expected);
            TimeSeriesFileHandler.saveToText(list, new File("D:/Temp/base2.csv"), ",");
        }*/
        assertEquals(mean/data.size(),out.getMeanValue(),0.5);
        
    }    
    

    @Test
    public void testBaseAndAmpDetrending() throws IOException {
        System.out.println("Testing baseAndAmp detrending");
        
        Random rand = new Random();
        TimeSeriesTransformer transformer = makeInstance();
        
        DetrendingType type = DetrendingType.BAMP_DTR;
        
        TimeSeries data = new TimeSeries();
        TimeSeries out = transformer.detrend(data, type);
        assertTrue(data.equals(out));
        
        data.add(rand.nextDouble()*100,rand.nextDouble()*100);
        out = transformer.detrend(data, type);
        assertTrue(data.equals(out));

        data = makeRandom(50, true);
        out = transformer.detrend(data, type);
        assertFalse(data.equals(out));
        
 
        data = new TimeSeries();
        data.add(new Timepoint(1,10,0.5,0.2));
        data.add(15,10);
        data.add(20,10);        
        data.add(25,10);
        data.add(30,10);
        
        out = transformer.detrend(data, type);
        /*{
            List<TimeSeries> list = Arrays.asList(data,out);
            TimeSeriesFileHandler.saveToText(list, new File("D:/Temp/base.csv"), ",");
        }*/
        System.out.println(data.getFirst().getStdDev()+":"+data.getFirst().getStdError());
        System.out.println(out.getFirst().getStdDev()+":"+out.getFirst().getStdError());
        assertTrue(out.getFirst().hasError());
        assertArrayEquals(data.getTimes(), out.getTimes(),1E-6);
        assertArrayEquals(data.getValues(), out.getValues(),1E-1);
        //assertTrue(data.almostEquals(out,1E-1));        
        
        //0.001*x2;
        data = new TimeSeries();
        double mean = 0;
        for (int i=0;i<100;i++) {
            double v = 0.001*i*i;
            mean+=v;
            data.add(i,v);
        }
        TimeSeries expected = new TimeSeries();
        for (int i=0;i<data.size();i++) expected.add(i,mean/data.size());
        
        out = transformer.detrend(data, type);
        {
            List<TimeSeries> list = Arrays.asList(data,out,expected);
            //TimeSeriesFileHandler.saveToText(list, new File("D:/Temp/bamp.csv"), ",");
        }
        //assertTrue(expected.almostEquals(out,0.5));
        
        data = new TimeSeries();
        mean = 0;
        for (int i=0;i<100;i++) {
            double v = 4*Math.sin(i*3.14/120);
            mean+=v;
            data.add(i,v);
        }
        expected = new TimeSeries();
        for (int i=0;i<data.size();i++) expected.add(i,mean/data.size());
        
        out = transformer.detrend(data, type);
        {
            List<TimeSeries> list = Arrays.asList(data,out,expected);
            //TimeSeriesFileHandler.saveToText(list, new File("D:/Temp/bamp2.csv"), ",");
        }
        assertEquals(mean/data.size(),out.getMeanValue(),0.5);
        
    }    
    
    @Test
    public void testAllDetrendingTypes() throws IOException {
        
        System.out.println("Testing all detrending types");
        TimeSeriesTransformer transformer = makeInstance();
        for (DetrendingType type : DetrendingType.values()) {
            TimeSeries data = new TimeSeries();
            TimeSeries out = transformer.detrend(data, type);
            assertEquals(data, out);
            data.add(Math.random()*100,Math.random()*100);
            out = transformer.detrend(data, type);
            assertEquals(data, out);
            
            data = new TimeSeries();
            for (int i =  0;i<100;i++) {
                data.add(i,2);
            }
            out = transformer.detrend(data, type);
            /*{
                List<TimeSeries> ser = Arrays.asList(data,out);
                TimeSeriesFileHandler.saveToText(ser, new File("D:/Temp/"+type+"_d.csv"), ",");
            }*/
            double lEPS = EPS;
            if (type.equals(DetrendingType.BASE_DTR) || type.equals(DetrendingType.BAMP_DTR)) {
                out = out.subSeries(20, 80);
                data = data.subSeries(20, 80);
                lEPS = 0.2;
                if (type.equals(DetrendingType.BAMP_DTR)) lEPS = 0.4;
            }
            assertTrue("DT: "+type+", exp:"+data+", got:"+out,data.almostEquals(out,lEPS));
            
        }
    }
    
    @Test
    public void testAveraging() {
        System.out.println("Testing averaging same times");
        TimeSeriesTransformer transformer = makeInstance();
        
        List<TimeSeries> series = new ArrayList<TimeSeries>();
        
        
        TimeSeries out;
        
        try {
            out = transformer.average(series);
            fail("Exception expected for empty ts set");
        } catch (IllegalArgumentException e) {}
        
        TimeSeries data = makeRandom(50, true);
        series.add(data);
        
        out = transformer.average(series);
        assertEquals(data, out);
        
        series.clear();
        data = makeRandom(50, false);
        series.add(data);
        series.add(data);
        series.add(data);
   
        out = transformer.average(series);
        {
            List<Timepoint> dL = data.getTimepoints();
            List<Timepoint> oL = out.getTimepoints();
            
            assertEquals(dL.size(), oL.size());
            for (int i = 0;i<dL.size();i++) {
                Timepoint d = dL.get(i);
                Timepoint o = oL.get(i);
                
                assertEquals(d.getTime(), o.getTime(),1E-12);
                assertEquals(d.getValue(), o.getValue(),EPS);
                assertEquals(0, o.getStdError(),1E-12);
                assertEquals(0, o.getStdDev(),1E-12);
            }
        }
        
        series.clear();       
        data = new TimeSeries();
        data.add(0,0);
        data.add(5,-1);
        data.add(10,10);
        series.add(data);
        data = new TimeSeries();
        data.add(0,4);
        data.add(5,-1);
        data.add(10,20);
        series.add(data);
        data = new TimeSeries();
        data.add(0,2);
        data.add(5,2);
        data.add(10,30);
        series.add(data);
        
        TimeSeries exp = new TimeSeries();
        exp.add(0,2);
        exp.add(5,0);
        exp.add(10,20);
        
        out = transformer.average(series);
        {
            List<Timepoint> dL = exp.getTimepoints();
            List<Timepoint> oL = out.getTimepoints();
            
            assertEquals(dL.size(), oL.size());
            for (int i = 0;i<dL.size();i++) {
                Timepoint d = dL.get(i);
                Timepoint o = oL.get(i);
                
                assertEquals(d.getTime(), o.getTime(),1E-12);
                assertEquals(d.getValue(), o.getValue(),1E-12);
                //assertEquals(0, o.getStdError(),1E-12);
                //assertEquals(0, o.getStdDev(),1E-12);
            }
        }
                
        System.out.println("Testing averaging different times");
        
        TimeSeries joined = new TimeSeries();
        series.clear();
        
        data = new TimeSeries();
        data.add(0,1);
        data.add(1,1);
        data.add(2,1);
        
        series.add(data);
        joined.addAll(data.getTimepoints());
        
        data = new TimeSeries();
        data.add(5,2);
        data.add(6,2);
        data.add(7,2);
        
        series.add(data);
        joined.addAll(data.getTimepoints());
        
        data = new TimeSeries();
        data.add(10,4);
        data.add(11,4);
        data.add(12,4);
        
        series.add(data);
        joined.addAll(data.getTimepoints());
        
        out = transformer.average(series);
        {
            List<Timepoint> dL = joined.getTimepoints();
            List<Timepoint> oL = out.getTimepoints();
            
            assertEquals(dL.size(), oL.size());
            for (int i = 0;i<dL.size();i++) {
                Timepoint d = dL.get(i);
                Timepoint o = oL.get(i);
                
                assertEquals(d.getTime(), o.getTime(),1E-12);
                assertEquals(d.getValue(), o.getValue(),1E-12);
                assertEquals(0, o.getStdError(),1E-12);
                assertEquals(0, o.getStdDev(),1E-12);
            }
        }
        
        joined = new TimeSeries();
        series.clear();
        
        data = new TimeSeries();
        data.add(0,1);
         
        series.add(data);
        joined.addAll(data.getTimepoints());
        
        data = new TimeSeries();
        data.add(5,2);
        
        series.add(data);
        joined.addAll(data.getTimepoints());
        
        data = new TimeSeries();
        data.add(10,4);
        
        series.add(data);
        joined.addAll(data.getTimepoints());
        
        out = transformer.average(series);
        {
            List<Timepoint> dL = joined.getTimepoints();
            List<Timepoint> oL = out.getTimepoints();
            
            assertEquals(dL.size(), oL.size());
            for (int i = 0;i<dL.size();i++) {
                Timepoint d = dL.get(i);
                Timepoint o = oL.get(i);
                
                assertEquals(d.getTime(), o.getTime(),1E-12);
                assertEquals(d.getValue(), o.getValue(),1E-12);
                assertEquals(0, o.getStdError(),1E-12);
                assertEquals(0, o.getStdDev(),1E-12);
            }
        }
        
        
        series.clear();
        
        data = new TimeSeries();
        data.add(0,1);
        data.add(10,1);
        data.add(20,1);        
        data.add(30,1);        
        series.add(data);
        
        data = new TimeSeries();
        data.add(0,4);
        data.add(10,4);
        data.add(20,4);        
        series.add(data);
        
        data = new TimeSeries();
        data.add(0.1,1);
        data.add(10.1,1);
        data.add(20.1,1);        
        series.add(data);
        
        exp = new TimeSeries();
        exp.add(0,2);
        exp.add(10,2);
        exp.add(20,2);
        exp.add(30,1);
        
        out = transformer.average(series);
        {
            List<Timepoint> dL = exp.getTimepoints();
            List<Timepoint> oL = out.getTimepoints();
            
            assertEquals(dL.size(), oL.size());
            for (int i = 0;i<dL.size();i++) {
                Timepoint d = dL.get(i);
                Timepoint o = oL.get(i);
                
                assertEquals(d.getTime(), o.getTime(),1E-12);
                assertEquals("T: "+d.getTime(),d.getValue(), o.getValue(),d.getValue()/1000);
                //assertEquals(0, o.getStdError(),1E-12);
                //assertEquals(0, o.getStdDev(),1E-12);
            }
            //assertEquals(0,oL.get(oL.size()-1).getStdDev(),1E-12);
        }
        
       series.clear();
        
        data = new TimeSeries();
        data.add(0,1);
        data.add(0.1,1);
        data.add(10,1);
        data.add(20,1);        
        data.add(20.1,1);        
        data.add(30,1);        
        series.add(data);
        
        data = new TimeSeries();
        data.add(0,4);
        data.add(0.1,0.1*0.4+4);
        data.add(10,8);
        data.add(19,4+19*0.4);        
        data.add(20,4+20*0.4);        
        series.add(data);
        
        data = new TimeSeries();
        data.add(0.1,1);
        data.add(0.2,1);
        data.add(10.1,1);
        data.add(20,1);        
        data.add(20.1,1);        
        series.add(data);
        
        exp = new TimeSeries();
        exp.add(0,2.5);
        exp.add(0.1,6.04/3);
        exp.add(10,10.0/3.0);
        exp.add(20,14.0/3.0);
        exp.add(20.1,(2+20.1*0.4+4)/3.0);
        exp.add(30,1);
        
        out = transformer.average(series);
        {
            List<Timepoint> dL = exp.getTimepoints();
            List<Timepoint> oL = out.getTimepoints();
            
            assertEquals(dL.size(), oL.size());
            for (int i = 0;i<dL.size();i++) {
                Timepoint d = dL.get(i);
                Timepoint o = oL.get(i);
                
                assertEquals(d.getTime(), o.getTime(),1E-12);
                assertEquals("T: "+d.getTime(),d.getValue(), o.getValue(),d.getValue()/1000);
                //assertEquals(0, o.getStdError(),1E-12);
                //assertEquals(0, o.getStdDev(),1E-12);
            }
            assertEquals(0,oL.get(oL.size()-1).getStdDev(),1E-12);
        }
        
        
        
    }
    
    @Test
    public void stresTestAvergaing() {
        System.out.println("Stress test averaging same times");
        int N = 200;
        int days = 30;
        double f = 0.1;
        
        List<TimeSeries> series = new ArrayList<TimeSeries>();
        for (int i = 0;i<N;i++) {
            TimeSeries ser = TSGenerator.makeGausian((int)(days*24.0/f), f, Math.random()*12+15, 2, 5, 3);
            ser = TimeSeriesOperations.addWalkingNoise(ser, 0.2);
            series.add(ser);
        }
        
        TimeSeriesTransformer transformer = makeInstance();
        long sT = System.currentTimeMillis();
        TimeSeries out = transformer.average(series);
        System.out.println("Took: "+(System.currentTimeMillis()-sT));
        
        TimeSeries data = series.get(0);
        {
            List<Timepoint> dL = data.getTimepoints();
            List<Timepoint> oL = out.getTimepoints();
            
            assertEquals(dL.size(), oL.size());
            for (int i = 0;i<dL.size();i++) {
                Timepoint d = dL.get(i);
                Timepoint o = oL.get(i);
                
                assertEquals(d.getTime(), o.getTime(),1E-12);
                //assertEquals("T: "+d.getTime(),d.getValue(), o.getValue(),d.getValue()/1000);
                //assertEquals(0, o.getStdError(),1E-12);
                //assertEquals(0, o.getStdDev(),1E-12);
            }
            //assertEquals(0,oL.get(oL.size()-1).getStdDev(),1E-12);
        }
        
        
        System.out.println("Stress test averaging different times");

        double first = Double.POSITIVE_INFINITY;
        double last = Double.NEGATIVE_INFINITY;
        
        series = new ArrayList<TimeSeries>();
        for (int i = 0;i<N;i++) {
            TimeSeries ser = TSGenerator.makeGausian((int)(days*24.0/f), f, Math.random()*12+15, 2, 5, 3);
            ser = TimeSeriesOperations.addWalkingNoise(ser, 0.2);
            
            Pair<double[],double[]> TV = ser.getTimesAndValues();
            double shift = Math.random()*2;
            double[] times = TV.getLeft();
            for (int j = 0;j<times.length;j++) times[j]+=shift;
            ser = new TimeSeries(times,TV.getRight());
            series.add(ser);
            if (times[0] < first) first =times[0];
            if (times[times.length-1] > last) last = times[times.length-1];
        }
        
        sT = System.currentTimeMillis();
        out = transformer.average(series);
        System.out.println("Took: "+(System.currentTimeMillis()-sT));
        
        data = series.get(0);
        assertEquals(first,out.getFirst().getTime(),1E-12);
        assertEquals(last,out.getLast().getTime(),1E-12);
        assertTrue(out.size() >= data.size());
        
        /*{
            List<Timepoint> dL = data.getTimepoints();
            List<Timepoint> oL = out.getTimepoints();
            
            assertEquals(dL.size(), oL.size());
            for (int i = 0;i<dL.size();i++) {
                Timepoint d = dL.get(i);
                Timepoint o = oL.get(i);
                
                assertEquals(d.getTime(), o.getTime(),1E-12);
                //assertEquals("T: "+d.getTime(),d.getValue(), o.getValue(),d.getValue()/1000);
                //assertEquals(0, o.getStdError(),1E-12);
                //assertEquals(0, o.getStdDev(),1E-12);
            }
            //assertEquals(0,oL.get(oL.size()-1).getStdDev(),1E-12);
        }*/
        
        
    }
    
    TimeSeries makeRandom(int size,boolean withErrors) {
        Random rand = new Random();
        TimeSeries out = new TimeSeries();
        for (int i = 0;i<size;i++) {
            double t = rand.nextDouble()*1000;
            double v = rand.nextDouble()*100;
            Double stdE = null;
            Double stdD = null;
            if (withErrors) {
                if (rand.nextBoolean()) stdE = rand.nextDouble()*10;
                if (rand.nextBoolean()) stdD = rand.nextDouble()*10;
            }
            out.add(new Timepoint(t, v, stdE, stdD));
        }
        return out;
    }
    
}
