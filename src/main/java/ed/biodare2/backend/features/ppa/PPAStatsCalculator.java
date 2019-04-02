/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.ppa;

import ed.biodare2.backend.features.ppa.StatsUtil.StatsType;
import ed.robust.dom.tsprocessing.PPA;
import ed.robust.dom.tsprocessing.PPAResult;
import ed.robust.dom.tsprocessing.PPAStat;
import ed.robust.dom.tsprocessing.PPAStatSummary;
import ed.robust.dom.tsprocessing.PPAStats;
import ed.robust.dom.tsprocessing.PhaseType;
import ed.robust.dom.tsprocessing.Statistics;
import ed.robust.dom.tsprocessing.WeightedStat;
import ed.robust.dom.tsprocessing.WeightingType;
import ed.robust.util.timeseries.SmartDataRounder;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author tzielins
 */
public class PPAStatsCalculator {
    
    public static PPAStats calculateStats(List<PPAResult> results) {
        
        WeightedStat periodStats = calculateWeightedStat(results,new PeriodExtractor());
        
        Map<PhaseType,WeightedStat> ampStats = calculateAmpStats(results);
        Map<PhaseType,WeightedStat> phaseStats = calculatePhaseStats(results,periodStats);
        Map<PhaseType,WeightedStat> phaseStatsCirc = calculatePhaseStatsCirc(results,periodStats);
        
        
        int size = results.size();
        double[] joinedErrors = new double[size];
        double[] gofs = new double[size];
                
        int i = 0;
        for (PPAResult result : results) {
            joinedErrors[i] = result.getPPAMethodSpecific().getJoinedError();
            gofs[i] = result.getPPAMethodSpecific().getGOF();
            i++;
        }

        Statistics joinedError = StatsUtil.calculateStat(joinedErrors, StatsType.SIMPLE);
        Statistics gof = StatsUtil.calculateStat(gofs,StatsType.SIMPLE);
        
        PPAStats stats = new PPAStats();
        stats.setPeriodStats(periodStats);
        for (PhaseType type : PhaseType.values()) {
            stats.setAmpStats(ampStats.get(type), type);
            stats.setPhaseStats(phaseStats.get(type), type);
            stats.setPhaseStatsCirc(phaseStatsCirc.get(type), type);
        }
        
        stats.setJoinedError(joinedError);
        stats.setGOF(gof);
        
        return stats;
        
    }

    protected static Map<PhaseType,WeightedStat> calculatePhaseStats(List<PPAResult> results,WeightedStat periodStats) {
        
        Map<PhaseType,WeightedStat> phaseStats = new EnumMap<>(PhaseType.class);
        
        for (PhaseType type : PhaseType.values()) {
            WeightedStat orgStats = calculateWeightedStat(results,new SimplePhaseExtractor(type));
            WeightedStat rotStats = calculateWeightedStat(results,new RotatedPhaseExtractor(type,periodStats.getMean(WeightingType.None)));
            
            WeightedStat best = new WeightedStat();
            for (WeightingType weighting : WeightingType.values()) {
                Statistics orgStat = orgStats.getStat(weighting);
                Statistics rotStat = rotStats.getStat(weighting);
                
                Statistics stat = orgStat.getStdDev() <= rotStat.getStdDev() ? orgStat : rotStat;
                stat.setMean(SmartDataRounder.round(stat.getMean() % periodStats.getMean(weighting),periodStats.getMean(weighting)));
                
                best.setStat(stat, weighting);
            }
            
            phaseStats.put(type, best);
        }
        return phaseStats;
    }
    
    protected static Map<PhaseType,WeightedStat> calculatePhaseStatsCirc(List<PPAResult> results,WeightedStat periodStats) {
        
        Map<PhaseType,WeightedStat> phaseStats = new EnumMap<>(PhaseType.class);
        
        for (PhaseType type : PhaseType.values()) {
            WeightedStat orgStats = calculateWeightedStat(results,new SimpleCircPhaseExtractor(type));
            WeightedStat rotStats = calculateWeightedStat(results,new RotatedCircPhaseExtractor(type));
            
            WeightedStat best = new WeightedStat();
            for (WeightingType weighting : WeightingType.values()) {
                Statistics orgStat = orgStats.getStat(weighting);
                Statistics rotStat = rotStats.getStat(weighting);
                
                Statistics stat = orgStat.getStdDev() <= rotStat.getStdDev() ? orgStat : rotStat;
                stat.setMean(SmartDataRounder.round(stat.getMean() % 24,24));
                
                best.setStat(stat, weighting);
            }
            
            phaseStats.put(type, best);
        }
        return phaseStats;
    }
    
    protected static Map<PhaseType,WeightedStat> calculateAmpStats(List<PPAResult> results) {
        
        Map<PhaseType,WeightedStat> ampStats = new EnumMap<>(PhaseType.class);
        
        for (PhaseType type : PhaseType.values()) {
            WeightedStat stats = calculateWeightedStat(results,new AmpExtractor(type));
            
            ampStats.put(type, stats);
        }
        return ampStats;
    }
    
    protected static WeightedStat calculateWeightedStat(List<PPAResult> results,ValueExtractor extractor) {
        
        int size = results.size();
        double[] vals = new double[size];
        double[] valErrors = new double[size];
        double[] jERR = new double[size];
        double[] gofs = new double[size];
        
        int i = 0;
        for (PPAResult result : results) {
            vals[i] = extractor.getValue(result);
            valErrors[i] = extractor.getSpecError(result);
            jERR[i] = extractor.getJoinedError(result);
            gofs[i] = extractor.getGOF(result);
            i++;
        }
        
        return calculateWeightedStat(vals, valErrors, jERR, gofs);
    }
    
    protected static WeightedStat calculateWeightedStat(double[] vals,
        double[] valErrors,
        double[] jERR,
        double[] gofs) {       
        
        Statistics org = StatsUtil.calculateStat(vals, StatsType.FULL);
        Statistics gofW = StatsUtil.calculateErrorWeightedStat(vals, gofs, StatsType.SIMPLE);
        Statistics jERRW = StatsUtil.calculateErrorWeightedStat(vals, jERR, StatsType.SIMPLE);
        Statistics specW = StatsUtil.calculateErrorWeightedStat(vals, valErrors, StatsType.SIMPLE);
        
        WeightedStat stats = new WeightedStat();
        stats.setOrgStat(org);
        stats.setGofWeighted(gofW);
        stats.setJerrWeighted(jERRW);
        stats.setSpecWeighted(specW);
        
        return stats;
    }
    
    protected static PPAStatSummary calculateStatSummary(List<PPAResult> results, PPAExtractor extractor) {
        
        int size = results.size();
        double[] periods = new double[size];
        double[] phases = new double[size];
        double[] amps = new double[size];
        double[] joinedErrors = new double[size];
        double[] gofs = new double[size];
        
        double[] periodErrors = new double[size];
        double[] phaseErrors = new double[size];
        double[] ampErrors = new double[size];
        
        int i = 0;
        for (PPAResult result : results) {
            periods[i] = extractor.getPeriod(result);
            phases[i] = extractor.getPhase(result);
            amps[i] = extractor.getAmplitude(result);
            joinedErrors[i] = extractor.getJoinedError(result);
            gofs[i] = extractor.getGOF(result);
            periodErrors[i] = extractor.getPeriodError(result);
            phaseErrors[i] = extractor.getPhaseError(result);
            ampErrors[i] = extractor.getAmpError(result);
            i++;
        }

        Statistics joinedError = StatsUtil.calculateStat(joinedErrors, StatsType.SIMPLE);
        Statistics gof = StatsUtil.calculateStat(gofs, StatsType.SIMPLE);
        
        PPAStat orgStat = calculateStat(periods,phases,amps);
        
        PPAStat gofStat = calculateWeightedStat(periods,gofs,phases,gofs,amps,gofs);
        
        PPAStat jerrStat = calculateWeightedStat(periods,joinedErrors,phases,joinedErrors,amps,joinedErrors);

        PPAStat specStat = calculateWeightedStat(periods,periodErrors,phases,phaseErrors,amps,ampErrors);
        
        
        PPAStatSummary stat = new PPAStatSummary();
        stat.setOrgStat(orgStat);
        stat.setGofWeighted(gofStat);
        stat.setJerrWeighted(jerrStat);
        stat.setSpecWeighted(specStat);
        
        stat.setGOF(gof);
        stat.setJoinedError(joinedError);
        
        return stat;
    }

    protected static double[] rotatePhases(double[] phases, double period) {
        double[] rotated = new double[phases.length];
        double halfPeriod = period/2;
        
        for (int i=0;i<rotated.length;i++) {
            rotated[i] = phases[i];
            if (phases[i] < halfPeriod) rotated[i]+=period;
        }
        
        return rotated;
    }

    /*protected static void setErrorStats(PPAStats stats, List<PPAResult> results) {
	
	double[] gofs = new double[results.size()];
	double[] errs = new double[results.size()];

        int i = 0;
        for (PPAResult result : results) {
	    gofs[i] = result.getGOF();
	    errs[i] = result.getPPAMethodSpecific().getGlobalError();
	    i++;
	}

	Statistics gof = StatsUtil.calculateStat(gofs);
	Statistics err = StatsUtil.calculateStat(errs);

	stats.setGOF(gof);
	stats.setERR(err);

    }*/

    private static PPAStat calculateStat(double[] periods, double[] phases, double[] amps) {
       
        Statistics period = StatsUtil.calculateStat(periods, StatsType.SIMPLE);
        Statistics amp = StatsUtil.calculateStat(amps, StatsType.SIMPLE);        
        Statistics orgPhase = StatsUtil.calculateStat(phases, StatsType.SIMPLE);        
        double[] rotPhases = rotatePhases(phases,period.getMean());        
        Statistics rotPhase = StatsUtil.calculateStat(rotPhases, StatsType.SIMPLE);
        Statistics phase = orgPhase.getStdDev() < rotPhase.getStdDev() ? orgPhase : rotPhase;
        phase.setMean(phase.getMean() % period.getMean());
        
        PPAStat ppaStat = new PPAStat();
        ppaStat.setPeriod(period);
        ppaStat.setPhase(phase);
        ppaStat.setAmplitude(amp);
        return ppaStat;
    }

    private static PPAStat calculateWeightedStat(double[] periods, double[] perErrs, double[] phases, double[] phaseErrs, double[] amps, double[] ampErr) {
        
        Statistics wPeriod = StatsUtil.calculateErrorWeightedStat(periods, perErrs, StatsType.SIMPLE);
        Statistics wAmp = StatsUtil.calculateErrorWeightedStat(amps, ampErr, StatsType.SIMPLE);
        Statistics wOrgPhase = StatsUtil.calculateErrorWeightedStat(phases, phaseErrs, StatsType.SIMPLE);
        double[] rotPhases = rotatePhases(phases,wPeriod.getMean());   
        Statistics wRotPhase = StatsUtil.calculateErrorWeightedStat(rotPhases, phaseErrs, StatsType.SIMPLE);
        Statistics wPhase = wOrgPhase.getStdDev() < wRotPhase.getStdDev() ? wOrgPhase : wRotPhase;
        wPhase.setMean(wPhase.getMean() % wPeriod.getMean());

        PPAStat ppaStat = new PPAStat();
        ppaStat.setPeriod(wPeriod);
        ppaStat.setPhase(wPhase);
        ppaStat.setAmplitude(wAmp);
        return ppaStat;
    }


    
    protected static interface PPAExtractor {
        double getPeriod(PPAResult res);
        double getPhase(PPAResult res);
        double getAmplitude(PPAResult res);
        double getJoinedError(PPAResult res);
        double getGOF(PPAResult res);

        public double getPeriodError(PPAResult result);

        public double getPhaseError(PPAResult result);

        public double getAmpError(PPAResult result);
    }
    
    protected static interface ValueExtractor {
        
        double getValue(PPAResult res);
        
        double getSpecError(PPAResult res);
        
        double getJoinedError(PPAResult res);
        
        double getGOF(PPAResult res);        
    }
    
    protected static class PeriodExtractor implements ValueExtractor {
        
        @Override
        public double getValue(PPAResult res) {
            return res.getPPAMethodSpecific().getPeriod();
        }
        
        @Override
        public double getSpecError(PPAResult res) {
            PPA ppa = res.getPPAMethodSpecific();
            if (ppa.hasPeriodError()) return ppa.getPeriodError();
            else return 1;
        }
        
        @Override
        public double getJoinedError(PPAResult res) {
            return res.getPPAMethodSpecific().getJoinedError();
        };
        
        @Override
        public double getGOF(PPAResult res) {
            return res.getPPAMethodSpecific().getGOF();
        };
    }
    
    
    protected static class AmpExtractor implements ValueExtractor {
        
        final PhaseType type;
        
        AmpExtractor(PhaseType type) {
            this.type = type;
        }

        @Override
        public double getValue(PPAResult res) {
            return res.getPPA(type).getAmplitude();
        }

        @Override
        public double getSpecError(PPAResult res) {
            PPA ppa =res.getPPA(type);
            if (ppa.hasAmplitudeError()) return ppa.getAmplitudeError();
            else return 1;
        }

        @Override
        public double getJoinedError(PPAResult res) {
            return res.getPPA(type).getJoinedError();
        }

        @Override
        public double getGOF(PPAResult res) {
            return res.getPPA(type).getGOF();
        }
    }
    
    protected static class SimplePhaseExtractor implements ValueExtractor {
        
        final PhaseType type;
        
        SimplePhaseExtractor(PhaseType type) {
            this.type = type;
        }

        @Override
        public double getValue(PPAResult res) {
            return res.getPPA(type).getPhase();
        }

        @Override
        public double getSpecError(PPAResult res) {
            PPA ppa =res.getPPA(type);
            if (ppa.hasPhaseError()) return ppa.getPhaseError();
            else return 1;
        }

        @Override
        public double getJoinedError(PPAResult res) {
            return res.getPPA(type).getJoinedError();
        }

        @Override
        public double getGOF(PPAResult res) {
            return res.getPPA(type).getGOF();
        }
    }
    
    protected static class RotatedPhaseExtractor extends SimplePhaseExtractor {
        
        final double period;
        final double halfPeriod;
        
        RotatedPhaseExtractor(PhaseType type,double period) {
            super(type);
            this.period = period;
            halfPeriod = period/2;
        }

        @Override
        public double getValue(PPAResult res) {
            double phase = res.getPPA(type).getPhase();
            
            if (phase < halfPeriod) phase+=period;
            return phase;
        
        }

    }

    protected static class SimpleCircPhaseExtractor extends SimplePhaseExtractor {
        
        SimpleCircPhaseExtractor(PhaseType type) {
            super(type);
        }

        @Override
        public double getValue(PPAResult res) {
            return res.getPPA(type).getPhase()*24/res.getPeriod();
        }

    }
    
    protected static class RotatedCircPhaseExtractor extends SimplePhaseExtractor {
        
        final double period;
        final double halfPeriod;
        
        RotatedCircPhaseExtractor(PhaseType type) {
            super(type);
            this.period = 24;
            halfPeriod = 12;
        }

        @Override
        public double getValue(PPAResult res) {
            double phase = res.getPPA(type).getPhase();
            phase = phase*24/res.getPeriod();
            if (phase < halfPeriod) phase+=period;
            return phase;
        
        }

    }
    
    protected static abstract class PPAExtractorImp implements PPAExtractor {
        final PhaseType type;
        PPAExtractorImp(PhaseType type) {
            this.type = type;
        }
        
        @Override
        public double getPeriod(PPAResult res) {
            return res.getPPA(type).getPeriod();
        }

        @Override
        public double getPhase(PPAResult res) {
            return res.getPPA(type).getPhase();
        }

        @Override
        public double getAmplitude(PPAResult res) {
            return res.getPPA(type).getAmplitude();
        }

        @Override
        public double getJoinedError(PPAResult res) {
            return res.getPPA(type).getJoinedError();
        }        
        
        @Override
        public double getGOF(PPAResult res) {
            return res.getPPA(type).getGOF();
        } 
        
        @Override
        public double getPeriodError(PPAResult res) {
            PPA ppa = res.getPPA(type);
            if (ppa.hasPeriodError()) return ppa.getPeriodError();
            else return 1;
        };

        @Override
        public double getPhaseError(PPAResult res) {
            PPA ppa = res.getPPA(type);
            if (ppa.hasPhaseError()) return ppa.getPhaseError();
            else return 1;            
        };

        @Override
        public double getAmpError(PPAResult res) {
            PPA ppa = res.getPPA(type);
            if (ppa.hasAmplitudeError()) return ppa.getAmplitudeError();
            else return 1;            
        };
        
        
    }
    protected static class PPAByMethod extends PPAExtractorImp {

        public PPAByMethod() {
            super(PhaseType.ByMethod);
        }
        
        
    }
    
    static class PPAByFit extends PPAExtractorImp {

        public PPAByFit() {
            super(PhaseType.ByFit);
        }
        
        
    }
    
    static class PPAByFirstPeak extends PPAExtractorImp {

        public PPAByFirstPeak() {
            super(PhaseType.ByFirstPeak);
        }
        
        
    }
    
    
    static class PPAByAvgMax extends PPAExtractorImp {

        public PPAByAvgMax() {
            super(PhaseType.ByAvgMax);
        }
        
        
    }
    
 
    
}

