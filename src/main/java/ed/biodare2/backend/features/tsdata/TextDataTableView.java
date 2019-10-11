/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class TextDataTableView {
    
    public static boolean isSuitableFormat(Path file, String sep) throws IOException {
        
        List<String> lines = readLines(file,0,5);
        long separators = lines.stream().mapToInt(line -> countPresence(line, sep))
                .sum();
        
        return separators > 5;
    }

    static int countPresence(String line, String sep) {
        
        int count = 0;
        int from = 0;
        int ix;
        while( (ix = line.indexOf(sep, from)) >= 0) {
            count++;
            from = ix+1;
        }
        return count;
    }

    static List<String> readLines(Path file, int from, int count) throws IOException {
        
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            
            readLines(reader, from);
            return readLines(reader, count);
        }
    }

    /**
     * reads the next count lines
     * @param reader
     * @param count
     * @return 
     */
    static List<String> readLines(BufferedReader reader, int count) throws IOException {
        
        List<String> lines = new ArrayList<>(count);
        
        for (int i = 0; i< count; i++) {
            String line = reader.readLine();
            if (line == null) break;
            lines.add(line);
        }
        return lines;
    }
}
