/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.concurrent.id;

import ed.biodare2.backend.util.concurrent.id.db.LongRecord;
import ed.biodare2.backend.util.concurrent.id.db.LongRecordRep;
import java.util.Arrays;
import java.util.List;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.ConcurrencyFailureException;
//import org.springframework.transaction.annotation.Transactional;
        
/**
 *
 * @author Zielu
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = {BioDare2TestConfiguration.class},webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class LongRecordManagerIntTest {

    
    @SpringBootApplication
    //@Import(EnvironmentConfiguration.class)
    //@ComponentScan
    public static class Config {
        
    } 
    
    @Autowired
    LongRecordManager manager;
    
    @Autowired
    LongRecordRep records; 
    
    LongRecord rec;
    
    @Before
    //@Transactional
    public void setup() {
        rec = new LongRecord("cos",2);
        rec = save(rec);
        
    }
    
    @Test
    @Ignore
    //@Transactional
    public void reserveNextRecordsUpdatesRepopsitory() throws Exception {

        
        int size = 2;
        
        List expResult = Arrays.asList(2L,3L);
        List result = manager.reserveNextRecords(rec.getRecordName(), size,Long.MAX_VALUE);
        assertEquals(expResult, result);        
        
        
    }
    
    @Test
    @Ignore
    //@Transactional
    public void updateStartsUpdatesRepopsitory() throws Exception {

        manager.updateStart(rec.getRecordName(), 200);
        int size = 2;
        
        List expResult = Arrays.asList(200L,201L);
        List result = manager.reserveNextRecords(rec.getRecordName(), size,Long.MAX_VALUE);
        assertEquals(expResult, result);        
        
        
    }
    
    
    @Test
    public void reserveNextRecordsUpdatesRepopsitoryConcurrentMod() throws Exception {

        LongRecord rec = new LongRecord("cos1",2);
        rec = save(rec);
        
        int size = 2;
        
        List expResult = Arrays.asList(2L,3L);
        List result = manager.reserveNextRecords(rec.getRecordName(), size,Long.MAX_VALUE);
        assertEquals(expResult, result);        
        
        rec.setNextValue(1);
        try {
            save(rec);
            fail("concurrent exception expected");
        } catch(ConcurrencyFailureException e) {};
        
    }
    
    
    @Test
    //@Transactional
    public void createRecordIfNotExistsCreates() throws Exception {
        String name = "ula"+Math.random();
        
        manager.createRecordIfNotExists(name,5);
        
        assertTrue(records.findById(name).isPresent());
    }    
    
    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    protected LongRecord save(LongRecord record) {
        
        return records.save(record);
        
    }

    
}
