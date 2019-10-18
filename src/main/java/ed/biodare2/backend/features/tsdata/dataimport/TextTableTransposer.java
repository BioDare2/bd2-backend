/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.dataimport;

import ed.biodare2.backend.features.tsdata.tableview.TextDataTableReader;
import ed.biodare2.backend.features.tsdata.tableview.TextDataTableReader.OpennedReader;
import ed.robust.dom.util.Pair;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public class TextTableTransposer {
    
    static int MAX_COLS = 1300;
    static int MAX_ROWS = 1300;
    
    public void transpose(Path inFile, String SEP, Path outFile) throws IOException {
        
        // if (Files.exists(outFile)) throw new IOException("Overwriting existing files is not allowed");
        
        TextDataTableReader reader = new TextDataTableReader(inFile, SEP);
        
        transpose(reader, outFile, SEP);
    }

    public void transpose(TextDataTableReader reader, Path outFile, String SEP) throws IOException {
        
        Pair<Integer, Integer> rowColSize = reader.tableSize();
        
        if (rowColSize.getLeft() > MAX_ROWS) 
            throw new IllegalArgumentException("Transposition supported only for tables with less than "+MAX_ROWS+" rows");
        if (rowColSize.getRight() > MAX_COLS) 
            throw new IllegalArgumentException("Transposition supported only for tables with less than "+MAX_COLS+" columns");
        
        transposeChunk(reader, 0, rowColSize.getRight(), outFile, SEP);
    }

    void transposeChunk(TextDataTableReader reader, int firstCol, int endCol, Path outFile, String SEP) throws IOException {
        
        List<List<Object>> newRows;
        
        try (TextDataTableReader.OpennedReader sequentialReader = reader.openReader()) {
            
            newRows = readTransposedChunk(sequentialReader, firstCol, endCol);
        }
        
        saveToTextTable(newRows, outFile, SEP);
    }
    
    List<List<Object>> readTransposedChunk(OpennedReader sequentialReader, int firstCol, int endCol) throws IOException {
        
        List<List<Object>> newRows = initListOfList(endCol-firstCol);
        int size = newRows.size();

        for (;;) {
            Optional<List<Object>> oldRowO = sequentialReader.readRecord();
            if (oldRowO.isEmpty()) break;

            List<Object> oldRow = oldRowO.get();
            List<Object> colsVals = subList(oldRow, firstCol, endCol);

            for (int i = 0; i< size; i++) {
                newRows.get(i).add(colsVals.get(i));
            }
        }

        return newRows;
    }    

    <T> List<List<T>> initListOfList(int size) {
        
        List<List<T>> list = new ArrayList<>(size);
        
        for (int i =0;i< size; i++) list.add(new ArrayList<>());
        
        return list;
    }

    <T> List<T> subList(List<T> list, int firstCol, int endCol) {
        
        int s = Math.min(firstCol, list.size());
        int e = Math.min(endCol, list.size());
        int size = endCol - firstCol;
        List<T> sub = list.subList(s,e);
        if (sub.size() < size) {
            sub = new ArrayList<>(sub);
            while(sub.size() < size) sub.add(null);
        }
        
        return sub;
    }

    void saveToTextTable(List<List<Object>> rows, Path outFile, String SEP) throws IOException {
        
        try (BufferedWriter out = Files.newBufferedWriter(outFile)) {
            
            for (int i = 0; i< rows.size(); i++) {
                
                String line = rows.get(i).stream()
                                    .map( o  -> o == null ? "" : o.toString())
                                    .collect(Collectors.joining(SEP));

                out.write(line);
                out.newLine();
            }
        }
        
    }
    
}
