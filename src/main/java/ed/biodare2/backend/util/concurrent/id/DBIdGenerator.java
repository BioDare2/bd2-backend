/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.concurrent.id;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.springframework.dao.ConcurrencyFailureException;

/**
 *
 * @author tzielins
 */
public class DBIdGenerator implements IdGenerator  {
    
    final int ATTEMPT_MAX = 25;

    public static IdGenerator newInstance(String name, int increament,LongRecordManager recordManager) {
        return newInstance(name,increament,1,Long.MAX_VALUE,recordManager);
    }
    
    public static IdGenerator newInstance(String name, int increament,long START_VAL,long MAX_VAL,LongRecordManager recordManager) {
        
        recordManager.createRecordIfNotExists(name,START_VAL);
        return new DBIdGenerator(name, increament, MAX_VAL,recordManager);
    }
    
    
    final String name;
    final Queue<Long> ids;
    final int increament;
    final long MAXID;
    final LongRecordManager recordManager;
    
    protected DBIdGenerator(String name,int increament,long MAX_VAL,LongRecordManager recordManager) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
        if (increament < 5) throw new IllegalArgumentException("Increament must be > 5");
        
        this.recordManager = recordManager;
        this.name = name;
        this.ids = new ConcurrentLinkedQueue<>();
        this.increament = increament;
        this.MAXID = MAX_VAL;
        
        loadIds();
    }
    
    @Override
    public long next() {
        
        Long id = ids.poll();
        while (id == null) {
            loadIds();
            id = ids.poll();
        }
        if (id >= MAXID) throw new IllegalStateException("The id limit: "+MAXID+" has been reached");
        return id;          
    }

    protected void loadIds() {
        
        for (int i =0;i<ATTEMPT_MAX;i++) {
            try {
                List<Long> reserved = recordManager.reserveNextRecords(name,increament,MAXID);
                ids.addAll(reserved);
                return;
            } catch (ConcurrencyFailureException e) {
                // In case multiple threads try to update DB there will be cocurrence exception
            }
        }
        throw new RuntimeException("Cannot load ids due to concurrent db modifications");
    }

    protected void updateStart(long newStart) {
        
        if (newStart >= MAXID) throw new IllegalStateException("The id limit: "+MAXID+" has been reached");
        recordManager.updateStart(name, newStart);
        ids.clear();
    }

    
    
}
