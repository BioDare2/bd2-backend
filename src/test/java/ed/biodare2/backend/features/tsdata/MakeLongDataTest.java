/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata;

import ed.robust.util.timeseries.TSGenerator;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class MakeLongDataTest {
    
    
    @Test
    @Ignore
    public void makeCSVColumnFile() throws Exception {
        
        int series = 100; //5000;
        int timepoints = 5*24*6; //5000; //5*24*10;
        int minutesUnit = 10; // minutes
        
        Map<String,double[]> patterns = new HashMap<>();
        patterns.put("22",makeCos(22, timepoints, minutesUnit));
        patterns.put("24",makeCos(24, timepoints, minutesUnit));
        patterns.put("26",makeCos(26, timepoints, minutesUnit));
        patterns.put("NO",new double[patterns.get("22").length]);
        
        List<String> keys = new ArrayList<>(patterns.keySet());
        List<String> selectedKeys = new ArrayList<>();
        
        Path file = Paths.get("E:/Temp/series_"+series+"x"+timepoints+"x"+minutesUnit+".csv");
        try (BufferedWriter out = Files.newBufferedWriter(file)) {
            Random r = new Random();
            List<String> row = new ArrayList<>(series+1);
            row.add("Time");
            for (int i = 0; i< series; i++) {
                String key = keys.get(r.nextInt(keys.size()));
                selectedKeys.add(key);
                String label = key+"_";
                row.add(label+r.nextInt(1+series/10));
            }
            
            String line = row.stream().collect(Collectors.joining(","));
            out.write(line);
            out.newLine();
            
            for (int i = 0; i< timepoints; i++) {
                row = new ArrayList<>(series+1);
                row.add(""+(i*minutesUnit)/60.0);
                for (int j = 0; j< series; j++) {
                    double[] pattern = patterns.get(selectedKeys.get(j));
                    double noise = 1;
                    double val = pattern[i]+noise*r.nextDouble();
                    val = Math.rint(val*10000)/1000;
                    row.add(""+val);
                    // row.add(""+Math.rint((1+r.nextDouble())*100000)/10000);
                    //row.add(""+Math.rint((1+r.nextDouble())*10000)/1000);
                }
                
                line = row.stream().collect(Collectors.joining(","));
                out.write(line);
                out.newLine();
            }
            
        }
    }
    
    @Test
    @Ignore
    public void makeCSVRowFile() throws Exception {
        
        int series = 2000; //5000;
        int timepoints = 20*24*6; //5000; //5*24*10;
        int minutesUnit = 10; // minutes
        
        Map<String,double[]> patterns = new HashMap<>();
        patterns.put("22",makeCos(22, timepoints, minutesUnit));
        patterns.put("24",makeCos(24, timepoints, minutesUnit));
        patterns.put("26",makeCos(26, timepoints, minutesUnit));
        patterns.put("NO",new double[patterns.get("22").length]);
        
        List<String> keys = new ArrayList<>(patterns.keySet());
        double[] levels = {0.5, 0.2, 0.1};
        
        Path file = Paths.get("E:/Temp/rows_series_"+series+"x"+timepoints+"x"+minutesUnit+".csv");
        try (BufferedWriter out = Files.newBufferedWriter(file)) {
            Random r = new Random();
            List<String> row = new ArrayList<>(timepoints+1);
            row.add("Time");
            for (int i = 0; i< timepoints; i++) {
                row.add(""+(i*minutesUnit)/60.0);
            }
            String line = row.stream().collect(Collectors.joining(","));
            out.write(line);
            out.newLine();            
            
            for (int j = 0; j< series; j++) {
                
                row = new ArrayList<>(timepoints+1);
                Noiser noiser = new Noiser();
                double level = levels[r.nextInt(levels.length)];
                String key = keys.get(r.nextInt(keys.size()));
                String label = key+"_"+level+"_";
                row.add(label+r.nextInt(1+series/10));
                
                double[] pattern = patterns.get(key);
                for (int i = 0; i< timepoints; i++) {
                    double val = level*pattern[i]+(1-level)*noiser.next();
                    val = Math.rint(val*10000)/1000;
                    row.add(""+val);                
                }
                
                line = row.stream().collect(Collectors.joining(","));
                out.write(line);
                out.newLine();                 
            }
            
            
        }
    }
    
    
    
    
    double[] makeCos(double period, int timepoints, int minutesUnit) {
        
        return TSGenerator.makeCos(timepoints, minutesUnit/60.0, period, 0.5).getValues();
    }
    
    static class Noiser {
        
        double[] previous = {Math.random(),Math.random(),Math.random(),Math.random()};
        Random r = new Random();
        
        public double next() {
            double n = r.nextDouble();
            previous[r.nextInt(previous.length)] = n;
            
            if (r.nextDouble() < 0.25) {
                for (int i = 0; i<3;i++) {
                    previous[r.nextInt(previous.length)] = n;
                }
            }
            
            n = 0;
            for (int i = 0;i<previous.length;i++) {
                n+=previous[i];
            }
            return n/previous.length;
        }
    }
    
}
