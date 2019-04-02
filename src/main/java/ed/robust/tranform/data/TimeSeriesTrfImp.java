/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.robust.tranform.data;

import ed.mesa.MesaLocalRegressionDetrending;
import ed.robust.dom.data.DetrendingType;
import ed.robust.dom.data.NormalizationType;
import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.data.Timepoint;
import ed.robust.dom.util.Pair;

import ed.robust.util.timeseries.BinningLinearTSInterpolator;
import ed.robust.util.timeseries.LocalRegressionDetrending;
import ed.robust.util.timeseries.ROUNDING_TYPE;
import ed.robust.util.timeseries.SplineTSInterpolator;
import ed.robust.util.timeseries.TimeSeriesInterpolator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
//import org.apache.commons.math3.optim.nonlinear.vector.jacobian.LevenbergMarquardtOptimizer;

/**
 *
 * @author tzielins
 */
public class TimeSeriesTrfImp implements TimeSeriesTransformer {

    static final TimeSeriesTrfImp INSTANCE = new TimeSeriesTrfImp();
    
    static final int LONG_DATA = 11*24;
    static final int VERY_LONG_DATA = 20*24;
    static final int EXT_HIGH_POINTS = 30*24*60;
    
    //private final LocalRegressionDetrending LRDetrending = new LocalRegressionDetrending();
    private final MesaLocalRegressionDetrending MESADetrending = new MesaLocalRegressionDetrending();
    
    public static TimeSeriesTransformer getInstance() {
        return INSTANCE;
    }
    
    private TimeSeriesTrfImp() {
        
    }
    
    @Override
    public TimeSeries standardise(TimeSeries org) {
        if (org.isEmpty()) return new TimeSeries();
        
        if (org.getDuration() <= LONG_DATA) return roundTimes(org);
        
        double minStep = 0.1;
        if (org.getDuration() > VERY_LONG_DATA) minStep = 0.5;
        
        if (org.getAverageStep() >= minStep) return roundTimes(org);
        
        TimeSeriesInterpolator interpolator;
        if (org.size() < EXT_HIGH_POINTS) interpolator = new SplineTSInterpolator(org, ROUNDING_TYPE.CENTY);
        else interpolator = new BinningLinearTSInterpolator(org, ROUNDING_TYPE.CENTY);
        
        return new TimeSeries(interpolator.makeInterpolation(minStep, ROUNDING_TYPE.DECY));
        
    }

    @Override
    public TimeSeries detrend(TimeSeries org, DetrendingType detrending) {
        
        if (org.size() < 2) return new TimeSeries(org);
        
        switch (detrending) {
            case NO_DTR: return new TimeSeries(org);
            case LIN_DTR: return lineDetrending(org);
            case POLY_DTR: return polyDetrending(org);
            case BASE_DTR: return baselineDetrending(org);
            case BAMP_DTR: return baseAndAmpDetrending(org);
            default: throw new UnsupportedOperationException("Detrending: "+detrending+" is not supported yet."); 
        }
        
    }

    @Override
    public TimeSeries normalise(TimeSeries org, NormalizationType normalization) {
        if (org.isEmpty()) return new TimeSeries();
        
        switch(normalization) {
            case NO_NORM : return new TimeSeries(org);
            case MAX_NORM : return divide(org,org.getMaxValue());
            case MEAN_NORM : return divide(org,org.getMeanValue());
            default: throw new UnsupportedOperationException("Normalization type: "+normalization+" Not supported yet."); 
        }        
    }

    @Override
    @SuppressWarnings("unchecked")
    public TimeSeries average(Collection<? extends TimeSeries> tsCollection) {
        
        if (tsCollection == null || tsCollection.isEmpty()) throw new IllegalArgumentException("Cannot averaged empty timeseries collection");
        
        List<TimeSeries> series;
        if (List.class.isInstance(tsCollection)) series = (List<TimeSeries>)(tsCollection);
        else series = new ArrayList<>(tsCollection);
        
        if (series.size() == 1) return new TimeSeries(series.get(0));
        
        //System.out.println("Will average: ");
        //for (TimeSeries s : series) System.out.println(s);
        
        TimeSeries out;
        if (haveSameTimes(series)) out = averageWithSameTimes(series);
        else out = averageGeneral(series);
        //out.getTimepoints();
        //System.out.println("AV: "+out);
        //System.out.println("---");
        return out;
        
    }

    /*@Override
    public TimeSeries average(Iterable<? extends TimeSeries> series) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }*/

    protected TimeSeries roundTimes(TimeSeries org) {
        
        return new TimeSeries(org);
        /*
        Pair<Double,Double> mStep = calculateMeanStep(org);
        
        double step = mStep.getLeft()/10;
        if (step)
        TimeSeries out = new TimeSeries();
        ROUNDING_TYPE type = ROUNDING_TYPE.NO_ROUNDING;
        if (org.getAverageStep() > 0.9) type = ROUNDING_TYPE.DECY;
        else if (org.getAverageStep() > 0.09) type = ROUNDING_TYPE.DECY;
        else if (org.getAverageStep() > 0.009) type = ROUNDING_TYPE.CENTY;
        else if (org.getAverageStep() > 0.0009) type = ROUNDING_TYPE.MIL;
        
        DataRounder rounder = new DataRounder(type);
        
        for (Timepoint in : org) {
            Timepoint tp = in.changeTime(rounder.round(in.getTime()));
            out.add(tp);
        }
        
        return out;
        */
    }
    
    protected Pair<Double,Double> calculateMeanStep(TimeSeries data) {
        if (data.size() < 2) return new Pair<>(0.0,0.0);
        
        SummaryStatistics stat = new SummaryStatistics();
        List<Timepoint> list = data.getTimepoints();
        
        Timepoint prev = list.get(0);
        for (int i = 1;i<list.size();i++) {
            double step = list.get(i).getTime()- prev.getTime();
            prev = list.get(i);
            stat.addValue(step);
        }
        
        return new Pair<>(stat.getMean(),stat.getStandardDeviation());
    }

    protected TimeSeries divide(TimeSeries org, double factor) {
        if (factor == 0) return new TimeSeries(org);
        
        return org.factorise(factor);
    }

    protected TimeSeries lineDetrending(TimeSeries data) {
        
        Pair<Double,Double> trend = getLinTrendParams(data);
        
        return addTrend(data,-trend.getLeft(),-trend.getRight()+data.getMeanValue());        
    }

    protected Pair<Double, Double> getLinTrendParams(TimeSeries data) {
        SimpleRegression reg = new SimpleRegression();
        for (Timepoint tp: data) {
            reg.addData(tp.getTime(), tp.getValue());            
        }
        return new Pair<>(reg.getSlope(),reg.getIntercept());
    }

    protected TimeSeries addTrend(TimeSeries data,double a,double b) {
        TimeSeries out = new TimeSeries();
        for (Timepoint tp:data) {
            out.add(new Timepoint(tp.getTime(),tp.getValue()+tp.getTime()*a+b,tp.getStdError(), tp.getStdDev()));
        }
        return out;
    }

    protected TimeSeries polyDetrending(TimeSeries org) {
        
        double mean = org.getMeanValue();
        PolynomialFunction trend = getPolyTrendFunction(org, 3);
        
        TimeSeries out = new TimeSeries();
        for (Timepoint tp : org) {
            double t = tp.getTime();
            double v = tp.getValue()-trend.value(t)+mean;
            out.add(new Timepoint(t,v,tp.getStdError(),tp.getStdDev()));
        }
        return out;
    }
    
    
   /**
     * Finds polynomial function that best fits into the data. 
     * Implemented using apache math polynomial fitter. 
     * @param data for which polynomila function should be fitted
     * @param degree degree of polynomial, so highest power in the polynomial, for example 1 will give function y =ax+b
     * @return representation of polynomial function that best fits to the data
     */
    protected PolynomialFunction getPolyTrendFunction(TimeSeries data,int degree) {

        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);
        
        WeightedObservedPoints points = new WeightedObservedPoints();
        
        for (Timepoint tp : data) points.add(tp.getTime(), tp.getValue());
        
        double[] coeficients; // = new double[degree+1];
        //Arrays.fill(coeficients, 1.0);
        try {
            coeficients = fitter.fit(points.toList());
        } catch (Exception e) {
            //something went wrong lets do linear
            Pair<Double,Double> slopInter = getLinTrendParams(data);
            coeficients = new double[]{slopInter.getRight(),slopInter.getLeft()};
        }
        
        PolynomialFunction fit = new PolynomialFunction(coeficients);

        return fit;
    }

    protected TimeSeries baselineDetrending(TimeSeries org) {
        
        try {
            //LocalRegressionDetrending.TrendPack pack = LRDetrending.findSmartBaselineTrend(org);
            //LocalRegressionDetrending.TrendPack pack = MESADetrending.findSmartTrends(org,false,false);
            LocalRegressionDetrending.TrendPack pack = MESADetrending.findBaselineTrend(org);
            return LocalRegressionDetrending.removeTrend(org, pack);
        } catch (InterruptedException e) {
            throw new RuntimeException("Could not detrend as was interrupted "+e.getMessage(), e);
        }
        
    }

    protected TimeSeries baseAndAmpDetrending(TimeSeries org) {
        try {
            //LocalRegressionDetrending.TrendPack pack = LRDetrending.findSmartTrends(org,false);
            //LocalRegressionDetrending.TrendPack pack = MESADetrending.findSmartTrends(org,true,false);
            LocalRegressionDetrending.TrendPack pack = MESADetrending.findBaseAndAmpTrend(org);
            return LocalRegressionDetrending.removeTrend(org, pack);
        } catch (InterruptedException e) {
            throw new RuntimeException("Could not detrend as was interrupted "+e.getMessage(), e);
        }
    }

    protected boolean haveSameTimes(List<TimeSeries> series) {
        
        if (series.isEmpty()) return true;
        
        double[] template = series.get(0).getTimes();
        
        for (TimeSeries ser : series) {
            if (!Arrays.equals(template, ser.getTimes())) return false;
        }
        return true;
    }

    protected TimeSeries averageWithSameTimes(List<TimeSeries> series) {
        
        AverageTimeSeries avg = new AverageTimeSeries();
        for (TimeSeries ser : series) avg.add(ser);
        
        return avg.toTimeSeries();
    }

    protected TimeSeries averageGeneral(List<TimeSeries> series) {
        
        Set<Double> times = getJoinedTime(series);
        
        AverageTimeSeries avg = new AverageTimeSeries();
        
        for (TimeSeries ser : series) {
            if (ser.isEmpty()) continue;
            
            List<Timepoint> data = ser.getTimepoints();
            double start = data.get(0).getTime();
            double end = data.get(data.size()-1).getTime();
            if (data.size() > 1) {
                double interval = (data.get(1).getTime()-data.get(0).getTime())/2;
                start-=interval;
                interval = (data.get(data.size()-1).getTime()-data.get(data.size()-2).getTime())/2;
                end+=interval;

                TimeSeriesInterpolator inter = new SplineTSInterpolator(ser);
                //TimeSeriesInterpolator inter = new BinningLinearTSInterpolator(ser);
                for (double time : times) {
                    if (time >= start && time <= end) avg.add(time,inter.getValue(time));
                }
            } else {
                double val = data.get(0).getValue();
                //start-=1;
                //end+=1;
                for (double time : times) {
                    if (time >= start && time <= end) avg.add(time,val);
                }
            }
            
        }
        
        return avg.toTimeSeries();
    }

    protected Set<Double> getJoinedTime(List<TimeSeries> series) {
        
        List<TimeSeries> pieces = new ArrayList<>(series);
        Set<Double> times = new HashSet<>();
        
        while (!pieces.isEmpty()) {
            sortByNrOfPoints(pieces);
            TimeSeries fullest = pieces.remove(pieces.size()-1);
            
            times.addAll(fullest.getTimesList());
            pieces = cutOffPeriod(pieces,fullest);
        }
        
        return times;
    }

    protected void sortByNrOfPoints(List<TimeSeries> pieces) {
        Collections.sort(pieces, new TimeSeriesNrOfPointsComparator());
    }

    protected List<TimeSeries> cutOffPeriod(List<TimeSeries> series, TimeSeries period) {
        
        List<TimeSeries> pieces = new ArrayList<>();
        double start = period.getFirst().getTime();
        double end = period.getLast().getTime();
        for (TimeSeries ser : series) {
            if (ser.getFirst().getTime() < start) pieces.add(ser.subSeries(ser.getFirst().getTime(), start));
            if (ser.getLast().getTime() > end) pieces.add(ser.subSeries(end, ser.getLast().getTime()));
        }
        return pieces;
    }
    
    protected static class TimeSeriesNrOfPointsComparator implements Comparator<TimeSeries> {

        @Override
        public int compare(TimeSeries o1, TimeSeries o2) {
            if (o1.size() < o2.size()) return -1;
            if (o1.size() > o2.size()) return 1;
            //same length, lets have the latest first
            if (o1.getFirst().getTime() < o2.getFirst().getTime()) return 1;
            if (o1.getFirst().getTime() > o2.getFirst().getTime()) return -1;
            return 0;
        }
    
}
    
    protected static class AverageTimeSeries {
        
        Map<Double,SummaryStatistics> values = new HashMap<>();
        
        public void add(TimeSeries series) {
            for (Timepoint tp : series) add(tp.getTime(),tp.getValue());
        }
        
        public void add(double time,double value) {
            SummaryStatistics stat = values.get(time);
            if (stat == null) {
                stat = new SummaryStatistics();
                values.put(time,stat);
            }
            stat.addValue(value);
        }
        
        public TimeSeries toTimeSeries() {
            TimeSeries out = new TimeSeries();
            for (double time : values.keySet()) {
                SummaryStatistics stat = values.get(time);
                out.add(new Timepoint(time,stat.getMean(),stat.getStandardDeviation()/Math.sqrt(stat.getN()),stat.getStandardDeviation()));
            }
            return out;
        }
        
    }
}
