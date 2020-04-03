/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import ed.robust.dom.util.Pair;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class TextDataTableReader implements DataTableReader {

    
    final public Path file;
    final public String sep;
   
    Pair<Integer, Integer> rowsColsSize;
    
    public TextDataTableReader(Path file, String sep) {
        if (!Files.isRegularFile(file)) 
            throw new IllegalArgumentException("Missing input file: "+file);
        
        this.file = file;
        this.sep = sep;
    }
    
    
    
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
            
            skipLines(reader, from);
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
    
    /**
     * skips the next count lines
     * @param reader
     * @param count
     * @return number of lines skipped
     */
    static int skipLines(BufferedReader reader, int count) throws IOException {
        

        int skipped = 0;
        for (int i = 0; i< count; i++) {
            String line = reader.readLine();
            if (line == null) break;
            skipped++;
        }
        return skipped;
    }    

    @Override
    public Pair<Integer, Integer> rowsColsTableSize() throws IOException {
        
        if (rowsColsSize == null) {
            int cols = countCols();        
            int rows = countRows();
            rowsColsSize = new Pair<>(rows,cols);
        };
        return rowsColsSize;
    }

    int countCols() throws IOException {
        
        List<String> lines = readLines(file, 0, 20);
        
        return lines.stream()
                .mapToInt(line -> line.split(sep).length)
                .max().orElse(0);
    }

    int countRows() throws IOException {
        
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            
            int lines = 0;
            
            while( reader.readLine() != null) {
                lines++;
            }
            
            return lines;
        }        
    }

    @Override
    public List<List<Object>> readRecords(int firstRow, int size) throws IOException {
        
        List<String> lines = readLines(file, firstRow, size);
        
        Stream<String> line$ = size > 200 ? lines.parallelStream() : lines.stream();
        
        return line$.map( line -> lineToRecord(line))
                .collect(Collectors.toList());
        
    }

    List<Object> lineToRecord(String line) {
        
        return lineToRecord(line, sep);
    }
    
    static List<Object> lineToRecord(String line, String sep) {
        
        List<Object> items = new ArrayList<>();
        
        items.addAll(Arrays.asList(line.split(sep)));
        
        return items;
    } 
    
    @Override
    public OpennedReader openReader() throws IOException {
        return new OpennedReader(file, sep);
    }
    
    public static class OpennedReader implements SequentialReader {

        final BufferedReader reader;
        final String SEP;
        
        OpennedReader(Path file, String sep) throws IOException {
            reader = Files.newBufferedReader(file);
            this.SEP = sep;
        }
        
        @Override
        public int skipLines(int count) throws IOException {
            return TextDataTableReader.skipLines(reader, count);
        }
        
        @Override
        public Optional<List<Object>> readRecord() throws IOException {

            String line = reader.readLine();
            if (line == null) return Optional.empty();

            List<Object> record = lineToRecord(line, SEP);
            return Optional.of(record);
        }        
        
        @Override
        public void close() throws IOException {
            reader.close();
        }

    }
}
