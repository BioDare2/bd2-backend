/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import static ed.biodare2.backend.features.tsdata.tableview.TextDataTableReader.lineToRecord;
import ed.robust.dom.util.Pair;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public interface DataTableReader {
    
    /**
     * 
     * @return pair with number or rows and columns in the table
     */
    public Pair<Integer, Integer> rowsColsTableSize() throws IOException;

    public List<List<Object>> readRecords(int firstRow, int size) throws IOException;
    
    public SequentialReader openReader() throws IOException;
    
    public static interface SequentialReader extends Closeable {
        
        public int skipLines(int count) throws IOException;
        
        public Optional<List<Object>> readRecord() throws IOException;
        
    }
}
