/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.features.tsdata.tableview;

import ed.robust.dom.util.Pair;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Tomasz Zielinski <tomasz.zielinski@ed.ac.uk>
 */
public abstract class TableRecordsReader {
    
    /**
     * 
     * @return pair with number or rows and columns in the table
     */
    public abstract Pair<Integer, Integer> tableSize() throws IOException;

    public abstract List<List<Object>> readRecords(int firstRow, int size) throws IOException;
    
    
}
