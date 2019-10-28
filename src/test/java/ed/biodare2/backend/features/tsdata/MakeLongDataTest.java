/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
    // @Ignore
    public void makeCSVColumnFile() throws Exception {
        
        int series = 10; //5000;
        int timepoints = 5*24*10; //5000; //5*24*10;
        int unit = 6; // minutes
        
        Path file = Paths.get("E:/Temp/columns_"+series+"x"+timepoints+".csv");
        try (BufferedWriter out = Files.newBufferedWriter(file)) {
            Random r = new Random();
            List<String> row = new ArrayList<>(series+1);
            row.add("Time");
            for (int i = 0; i< series; i++) {
                row.add("label"+r.nextInt(1+series/10));
            }
            
            String line = row.stream().collect(Collectors.joining(","));
            out.write(line);
            out.newLine();
            
            for (int i = 0; i< timepoints; i++) {
                row = new ArrayList<>(series+1);
                row.add(""+i*unit);
                for (int j = 0; j< series; j++) {
                    row.add(""+r.nextDouble());
                }
                
                line = row.stream().collect(Collectors.joining(","));
                out.write(line);
                out.newLine();
            }
            
        }
    }
    
}
