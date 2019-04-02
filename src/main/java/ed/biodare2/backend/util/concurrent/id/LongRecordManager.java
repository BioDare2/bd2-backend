/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.concurrent.id;




import ed.biodare2.backend.util.concurrent.id.db.LongRecord;
import ed.biodare2.backend.util.concurrent.id.db.LongRecordRep;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author tzielins
 */
@Service
public class LongRecordManager {

    final Logger log = LoggerFactory.getLogger(this.getClass());
    //final int ATTEMPT_MAX = 20;
    
    LongRecordRep records;

    @Autowired
    public LongRecordManager(LongRecordRep records) {
        this.records = records;
    }
    
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)    
    public List<Long> reserveNextRecords(String name,int size,long MAXVAL) throws ConcurrencyFailureException {
        if (size <= 0) throw new IllegalArgumentException("Size must be > 0");
        
        
        //for(int attempt=1;;attempt++) {
            
            LongRecord record = records.findById(name)
                    .orElseThrow(() -> new IllegalStateException("Unkown long record: "+name));
            
            long first = record.getNextValue();
            if (first >= MAXVAL) throw new IllegalStateException("The record limit: "+MAXVAL+" has been reached for record: "+name);

            long nextFree = first+size;
            record.setNextValue(nextFree);
                
            /* it was for testing concurrent access
             try {
                EntityManager em = eMProvider.createEM();
                em.getTransaction().begin();
                LongRecord record2 = em.find(LongRecord.class,name);
                record2.setNextValue(record2.getNextValue()-1);
                em.getTransaction().commit();
            } catch (Exception e) {
                logger.info("Here did not expected exception: "+e.getMessage()+"; "+e.getClass().getName());
            }*/
            /*
            try {
                Thread.sleep(100);
            } catch (Exception e){};
            */
            
            save(record);

            List<Long> list = new ArrayList<>(size);
            for (long i = first;i<nextFree;i++) list.add(i);
            return list;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createRecordIfNotExists(String name,long START_VAL) {
        
        Optional<LongRecord> record = records.findById(name);
        if (record.isPresent()) return;
            
        insertRecord(name,START_VAL);            
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    protected LongRecord insertRecord(String name,long START_VAL) {
        
        LongRecord record = new LongRecord(name, START_VAL);
        return save(record);
        
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    protected LongRecord save(LongRecord record) {
        
        return records.save(record);
        
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStart(String name, long newStart) throws ConcurrencyFailureException {
        
            LongRecord record = records.findById(name)
                    .orElseThrow(() -> new IllegalStateException("Unkown long record: "+name));
            
            long current = record.getNextValue();
            if (current >= newStart) return; //throw new IllegalStateException("The record limit: "+MAXVAL+" has been reached for record: "+name);

            record.setNextValue(newStart);
                
            save(record);

    }
    
}
