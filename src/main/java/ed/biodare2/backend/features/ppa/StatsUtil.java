/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.robust.dom.tsprocessing.Statistics;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import static org.apache.commons.math3.util.FastMath.*;
import ed.robust.util.timeseries.SmartDataRounder;
import org.apache.commons.math3.util.MathArrays;

/**
 *
 * @author tzielins
 */
public class StatsUtil {
    
    public enum StatsType {
        SIMPLE,
        MEDIUM,
        FULL        
    }
    
    public static final double ALMOST_ZERO = 1E-15;
    public static final double ALMOST_ZERO_WEIGHT = 1E30;
    
    public static Statistics calculateStat(Iterable<Double> values,StatsType statsType) {
        
        DescriptiveStatistics des = new DescriptiveStatistics();
        for (Double value : values) des.addValue(value);
        
        return extractStats(des,statsType);
    }
    
    public static Statistics calculateStat(double[] values,StatsType statsType) {
        
        DescriptiveStatistics des = new DescriptiveStatistics();
        for (double value : values) des.addValue(value);
        
        return extractStats(des,statsType);
        
    }
    
    protected static Statistics extractStats(DescriptiveStatistics des,StatsType statsType) {
        
        Statistics stat = new Statistics();
        
        stat.setN(des.getN());
        stat.setMean(SmartDataRounder.round(des.getMean()));
        
        stat.setStdErr(SmartDataRounder.round(des.getStandardDeviation()/sqrt(des.getN())));
        stat.setStdDev(SmartDataRounder.round(sqrt(des.getVariance())));
        
        if (!statsType.equals(StatsType.SIMPLE)) {
            stat.setMin(SmartDataRounder.round(des.getMin()));
            stat.setMax(SmartDataRounder.round(des.getMax()));
            stat.setSum(SmartDataRounder.round(des.getSum()));        
        }
        
        if (statsType.equals(StatsType.FULL)) {
            stat.setMedian(SmartDataRounder.round(des.getPercentile(0.50)));
            stat.setKurtosis(SmartDataRounder.round(des.getKurtosis()));
            stat.setSkewness(SmartDataRounder.round(des.getSkewness()));
        }
        return stat;
    }
    
    public static Statistics calculateErrorWeightedStat(double[] values,double[] errors,StatsType statsType) {
        
        double[] weights = new double[errors.length];
        for (int i = 0;i<errors.length;i++) {
            weights[i] = errors[i] > ALMOST_ZERO ? pow(1/errors[i], 2) : ALMOST_ZERO_WEIGHT;
        }
        return calculateWeightedStat(values, weights,statsType);
        
    }
    
    protected static Statistics emptyStats() {
        Statistics stats = new Statistics();
        stats.setN(0);
        stats.setKurtosis(Double.NaN);
        stats.setMax(Double.NaN);
        stats.setMean(Double.NaN);
        stats.setMedian(Double.NaN);
        stats.setMin(Double.NaN);
        stats.setSkewness(Double.NaN);
        stats.setStdDev(Double.NaN);
        stats.setStdErr(Double.NaN);
        stats.setSum(0);
        return stats;
    }
    
    public static Statistics calculateWeightedStat(double[] values,double[] weights,StatsType statsType) {
        
        if (values.length == 0) return emptyStats();
        
        Mean meanC = new Mean();
        double mean = meanC.evaluate(values, weights);
        
        double var = 0;
        if (values.length > 0) {
            Variance varC = new Variance();
            weights = MathArrays.normalizeArray(weights, values.length);
            var = varC.evaluate(values, weights, mean);
        }
        /*if (mean == Double.NaN || var == Double.NaN)
            throw new IllegalStateException("Got mean/var NaN: "+mean+","+var);
        */ 
        Statistics stat = calculateStat(values,statsType);
        
        /*if (var < 0) {
            System.out.println("V: "+Arrays.toString(values));
            System.out.println("W: "+Arrays.toString(weights));
            System.out.println("M: "+mean);            
            throw new IllegalStateException("Var is <=0: "+var);
        }*/
        //stat.n = values.length;
        
        stat.setMean(SmartDataRounder.round(mean));
        
        stat.setStdErr(SmartDataRounder.round(sqrt(var/stat.getN())));
        stat.setStdDev(SmartDataRounder.round(sqrt(var)));
        
        return stat;
    }
    
}
