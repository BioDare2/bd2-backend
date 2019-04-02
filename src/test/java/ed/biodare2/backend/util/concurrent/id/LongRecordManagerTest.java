/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.concurrent.id;

import ed.biodare2.backend.util.concurrent.id.LongRecordManager;
import ed.biodare2.backend.util.concurrent.id.db.LongRecord;
import ed.biodare2.backend.util.concurrent.id.db.LongRecordRep;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Zielu
 */
public class LongRecordManagerTest {
    
    public LongRecordManagerTest() {
    }
    
    LongRecordManager manager;
    LongRecordRep records;
    Random random;
    
    @Before
    public void setUp() {
        records = mock(LongRecordRep.class);
        manager = new LongRecordManager(records);
        random = new Random();
        
    }
    
    @After
    public void tearDown() {
    }

   /**
     * Test of reserveNextRecords method, of class LongRecordDAO.
     */
    @Test
    public void testReserveNextRecords() throws Exception {

        String name = ""+random.nextLong();
        
        
        LongRecord record = new LongRecord(name, 11);        
        when(records.findById(eq(name))).thenReturn(Optional.of(record));
        when(records.save(eq(record))).thenReturn(record);
        
        
        int size = 1;
        
        List expResult = Arrays.asList(11L);
        List result = manager.reserveNextRecords(name, size,Long.MAX_VALUE);
        assertEquals(expResult, result);
        
        size = 0;
        try {
            result = manager.reserveNextRecords(name, size,Long.MAX_VALUE);
            fail("Exception expected not: "+result);
        } catch(IllegalArgumentException e) {            
        }
        
        size = 3;
        expResult = Arrays.asList(12L,13L,14L);
        result = manager.reserveNextRecords(name, size,Long.MAX_VALUE);
        assertEquals(expResult, result);
        
    }
    
    
    @Test
    public void testReserveNextRecordsWithLimit() throws Exception {
        String name = ""+random.nextLong();
        long MAX = 12;
        
        
        LongRecord record = new LongRecord(name, 11);
        when(records.findById(eq(name))).thenReturn(Optional.of(record));
        when(records.save(eq(record))).thenReturn(record);
        
        int size = 1;
        
        List expResult = Arrays.asList(11L);
        List result = manager.reserveNextRecords(name, size,MAX);
        assertEquals(expResult, result);
        
        size = 0;
        try {
            result = manager.reserveNextRecords(name, size,MAX);
            fail("Exception expected not: "+result);
        } catch(IllegalArgumentException e) {            
        }
        
        size = 3;
        expResult = Arrays.asList(12L,13L,14L);
        try {
        result = manager.reserveNextRecords(name, size,MAX);
        assertEquals(expResult, result);
        fail("Exception expected");
        } catch (IllegalStateException e) {}
        
    }
    

    /**
     * Test of createRecordIfNotExists method, of class LongRecordDAO.
     */
    @Test
    public void createRecordIfNotExistsCreates() throws Exception {
        String name = ""+random.nextLong();
        
        manager.createRecordIfNotExists(name,5);
        LongRecord r = verify(records).save(any(LongRecord.class));
    }
    
    
    @Test
    public void createRecordIfNotExistsDoesNotCreatesIfExists() throws Exception {
        String name = ""+random.nextLong();
        
        LongRecord record = new LongRecord(name, 11);
        when(records.findById(eq(name))).thenReturn(Optional.of(record));
        when(records.save(eq(record))).thenReturn(record);        
        manager.createRecordIfNotExists(name,5);
        LongRecord r = verify(records,never()).save(any(LongRecord.class));
    }
    
    @Test
    public void updateStartSetsNewHighestValue() throws Exception {

        String name = ""+random.nextLong();
        
        
        LongRecord record = new LongRecord(name, 11);        
        when(records.findById(eq(name))).thenReturn(Optional.of(record));
        when(records.save(eq(record))).thenReturn(record);
        
        manager.updateStart(name,100);
        
        int size = 1;
        
        List expResult = Arrays.asList(100L);
        List result = manager.reserveNextRecords(name, size,Long.MAX_VALUE);
        assertEquals(expResult, result);
        
    }
    
    
    @Test
    public void updateStartIgnoreNewValuIfLowerThanExisting() throws Exception {

        String name = ""+random.nextLong();
        
        
        LongRecord record = new LongRecord(name, 201);        
        when(records.findById(eq(name))).thenReturn(Optional.of(record));
        when(records.save(eq(record))).thenReturn(record);
        
        manager.updateStart(name,100);
        
        int size = 1;
        
        List expResult = Arrays.asList(201L);
        List result = manager.reserveNextRecords(name, size,Long.MAX_VALUE);
        assertEquals(expResult, result);
        
    }
    
    
}
