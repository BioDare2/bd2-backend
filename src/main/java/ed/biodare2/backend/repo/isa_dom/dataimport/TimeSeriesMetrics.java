/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.repo.isa_dom.dataimport;

import ed.robust.dom.data.TimeSeries;
import ed.robust.dom.data.Timepoint;
import java.io.Serializable;
import static java.lang.Math.*;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 *
 * @author tzielins
 */
public class TimeSeriesMetrics implements Serializable {
    
    public double maxDuration;
    public double minDuration;
    public double avgDuration;
    
    public int maxPoints;
    public int minPoints;
    public int avgPoints;
    
    public double maxStep;
    public double minStep;
    public double avgStep;
    
    public double maxPointsPerHour;
    public double minPointsPerHour;
    public double avgPointsPerHour;
    
    public double maxFirstTime;
    public double minFirstTime;
    public double avgFirstTime;
    
    public double maxLastTime;
    public double minLastTime;
    public double avgLastTime;    
    
    public double maxValue;
    public double minValue;
    public double avgValue;
    
    public int series;
    public boolean uniformMetrics;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + (int) (Double.doubleToLongBits(this.maxDuration) ^ (Double.doubleToLongBits(this.maxDuration) >>> 32));
        hash = 43 * hash + this.maxPoints;
        hash = 43 * hash + (int) (Double.doubleToLongBits(this.maxStep) ^ (Double.doubleToLongBits(this.maxStep) >>> 32));
        hash = 43 * hash + (int) (Double.doubleToLongBits(this.maxFirstTime) ^ (Double.doubleToLongBits(this.maxFirstTime) >>> 32));
        hash = 43 * hash + this.series;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TimeSeriesMetrics other = (TimeSeriesMetrics) obj;
        if (Double.doubleToLongBits(this.maxDuration) != Double.doubleToLongBits(other.maxDuration)) {
            return false;
        }
        if (Double.doubleToLongBits(this.minDuration) != Double.doubleToLongBits(other.minDuration)) {
            return false;
        }
        if (Double.doubleToLongBits(this.avgDuration) != Double.doubleToLongBits(other.avgDuration)) {
            return false;
        }
        if (this.maxPoints != other.maxPoints) {
            return false;
        }
        if (this.minPoints != other.minPoints) {
            return false;
        }
        if (this.avgPoints != other.avgPoints) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxStep) != Double.doubleToLongBits(other.maxStep)) {
            return false;
        }
        if (Double.doubleToLongBits(this.minStep) != Double.doubleToLongBits(other.minStep)) {
            return false;
        }
        if (Double.doubleToLongBits(this.avgStep) != Double.doubleToLongBits(other.avgStep)) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxPointsPerHour) != Double.doubleToLongBits(other.maxPointsPerHour)) {
            return false;
        }
        if (Double.doubleToLongBits(this.minPointsPerHour) != Double.doubleToLongBits(other.minPointsPerHour)) {
            return false;
        }
        if (Double.doubleToLongBits(this.avgPointsPerHour) != Double.doubleToLongBits(other.avgPointsPerHour)) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxFirstTime) != Double.doubleToLongBits(other.maxFirstTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.minFirstTime) != Double.doubleToLongBits(other.minFirstTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.avgFirstTime) != Double.doubleToLongBits(other.avgFirstTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxLastTime) != Double.doubleToLongBits(other.maxLastTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.minLastTime) != Double.doubleToLongBits(other.minLastTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.avgLastTime) != Double.doubleToLongBits(other.avgLastTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxValue) != Double.doubleToLongBits(other.maxValue)) {
            return false;
        }
        if (Double.doubleToLongBits(this.minValue) != Double.doubleToLongBits(other.minValue)) {
            return false;
        }
        if (Double.doubleToLongBits(this.avgValue) != Double.doubleToLongBits(other.avgValue)) {
            return false;
        }
        if (this.series != other.series) {
            return false;
        }
        if (this.uniformMetrics != other.uniformMetrics) {
            return false;
        }
        return true;
    }

    public boolean isSimilar(TimeSeriesMetrics other) {
        if (Double.doubleToLongBits(this.maxDuration) != Double.doubleToLongBits(other.maxDuration)) {
            return false;
        }
        if (Double.doubleToLongBits(this.minDuration) != Double.doubleToLongBits(other.minDuration)) {
            return false;
        }
        if (Double.doubleToLongBits(this.avgDuration) != Double.doubleToLongBits(other.avgDuration)) {
            return false;
        }
        if (this.maxPoints != other.maxPoints) {
            return false;
        }
        if (this.minPoints != other.minPoints) {
            return false;
        }
        if (this.avgPoints != other.avgPoints) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxStep) != Double.doubleToLongBits(other.maxStep)) {
            return false;
        }
        if (Double.doubleToLongBits(this.minStep) != Double.doubleToLongBits(other.minStep)) {
            return false;
        }
        if (Double.doubleToLongBits(this.avgStep) != Double.doubleToLongBits(other.avgStep)) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxPointsPerHour) != Double.doubleToLongBits(other.maxPointsPerHour)) {
            return false;
        }
        if (Double.doubleToLongBits(this.minPointsPerHour) != Double.doubleToLongBits(other.minPointsPerHour)) {
            return false;
        }
        if (Double.doubleToLongBits(this.avgPointsPerHour) != Double.doubleToLongBits(other.avgPointsPerHour)) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxFirstTime) != Double.doubleToLongBits(other.maxFirstTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.minFirstTime) != Double.doubleToLongBits(other.minFirstTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.avgFirstTime) != Double.doubleToLongBits(other.avgFirstTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxLastTime) != Double.doubleToLongBits(other.maxLastTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.minLastTime) != Double.doubleToLongBits(other.minLastTime)) {
            return false;
        }
        if (Double.doubleToLongBits(this.avgLastTime) != Double.doubleToLongBits(other.avgLastTime)) {
            return false;
        }
        if (this.uniformMetrics != other.uniformMetrics) {
            return false;
        }
        return true;
    }


    
    public static TimeSeriesMetrics fromTimeSeries(TimeSeries data) {
        
        TimeSeriesMetrics metrics = new TimeSeriesMetrics();
        metrics.series = 1;
        metrics.uniformMetrics = true;
        if (data.isEmpty()) return metrics;
        
        metrics.maxDuration = roundToMill(data.getDuration());
        metrics.minDuration = metrics.maxDuration;
        metrics.avgDuration = metrics.maxDuration;
        
        metrics.maxPoints = data.size();
        metrics.minPoints = metrics.maxPoints;
        metrics.avgPoints = metrics.maxPoints;
        
        metrics.maxFirstTime = roundToMill(data.getFirst().getTime());
        metrics.minFirstTime = metrics.maxFirstTime;
        metrics.avgFirstTime = metrics.maxFirstTime;
        
        metrics.maxLastTime = roundToMill(data.getLast().getTime());
        metrics.minLastTime = metrics.maxLastTime;
        metrics.avgLastTime = metrics.maxLastTime;
        
        metrics.maxValue = data.getMaxValue();
        metrics.minValue = data.getMinValue();
        metrics.avgValue = data.getMeanValue();         
        
        /*
        List<Timepoint> timepoints = data.getTimepoints();
        Timepoint prev = timepoints.get(0);
        
        SummaryStatistics steps = new SummaryStatistics();
        
        for (int ix = 1; ix < timepoints.size(); ix++) {
            Timepoint next = timepoints.get(ix);
            
            double step = roundToMill(next.getTime())-roundToMill(prev.getTime());
            steps.addValue(step);
            
            prev = next;
        }
        
        metrics.maxStep = steps.getMax();
        metrics.minStep = steps.getMin();
        metrics.avgStep = steps.getMean();
        */
        metrics.maxStep = metrics.maxDuration/(metrics.minPoints-1);
        metrics.minStep = metrics.maxStep;
        metrics.avgStep = metrics.maxStep;
        
        metrics.maxPointsPerHour = roundToMill((metrics.maxPoints-1)/metrics.minDuration);
        metrics.minPointsPerHour = metrics.maxPointsPerHour;
        metrics.avgPointsPerHour = metrics.maxPointsPerHour;
       
        return metrics;
    }
    
    static double roundToMill(double x) {
        return Math.rint(x*1000)/1000.0;
    }
    
    public static TimeSeriesMetrics reduce(TimeSeriesMetrics one, TimeSeriesMetrics other) {
       
        TimeSeriesMetrics metrics = new TimeSeriesMetrics();
        
        metrics.series = one.series+other.series;
        metrics.maxDuration = max(one.maxDuration, other.maxDuration);
        metrics.minDuration = min(one.maxDuration, other.maxDuration);
        metrics.avgDuration = roundToMill((one.avgDuration*one.series+other.avgDuration*other.series)/metrics.series);
        
        metrics.maxPoints = max(one.maxPoints, other.maxPoints);
        metrics.minPoints = min(one.minPoints, other.minPoints);
        metrics.avgPoints = (int)rint((one.avgPoints*one.series+other.avgPoints*other.series)/metrics.series);
        
        metrics.maxFirstTime = max(one.maxFirstTime, other.maxFirstTime);
        metrics.minFirstTime = min(one.minFirstTime, other.minFirstTime);
        metrics.avgFirstTime = roundToMill((one.avgFirstTime*one.series+other.avgFirstTime*other.series)/metrics.series);
        
        metrics.maxLastTime = max(one.maxLastTime, other.maxLastTime);
        metrics.minLastTime = min(one.minLastTime, other.minLastTime);
        metrics.avgLastTime = roundToMill((one.avgLastTime*one.series+other.avgLastTime*other.series)/metrics.series);
        
        metrics.maxValue = max(one.maxValue, other.maxValue);
        metrics.minValue = min(one.minValue, other.minValue);
        metrics.avgValue = roundToMill((one.avgValue*one.series+other.avgValue*other.series)/metrics.series);        
        
        metrics.maxStep = max(one.maxStep, other.maxStep);
        metrics.minStep = min(one.minStep, other.minStep);
        metrics.avgStep = ((one.avgStep*one.series+other.avgStep*other.series)/metrics.series);    
        
        metrics.maxPointsPerHour = max(one.maxPointsPerHour, other.maxPointsPerHour);
        metrics.minPointsPerHour = min(one.minPointsPerHour, other.minPointsPerHour);
        metrics.avgPointsPerHour = roundToMill((one.avgPointsPerHour*one.series+other.avgPointsPerHour*other.series)/metrics.series); 
       
        metrics.uniformMetrics = one.isSimilar(other);
        
        return metrics;        
    }
}
