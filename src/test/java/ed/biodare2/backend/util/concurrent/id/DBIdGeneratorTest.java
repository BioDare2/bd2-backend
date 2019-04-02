/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.concurrent.id;


import ed.biodare2.backend.util.concurrent.id.IdGenerator;
import ed.biodare2.backend.util.concurrent.id.LongRecordManager;
import ed.biodare2.backend.util.concurrent.id.DBIdGenerator;
import ed.biodare2.backend.util.concurrent.id.db.LongRecord;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;

/**
 *
 * @author tzielins
 */
public class DBIdGeneratorTest {
    
    public DBIdGeneratorTest() {
    }
    
    LongRecordManager manager;

    Random random;
    
    @Before
    public void setUp() {
        manager = mock(LongRecordManager.class);
        random = new Random();
        
    }
    
    @After
    public void tearDown() {
    }
    

    /**
     * Test of newInstance method, of class DBIdGenerator.
     */
    @Test
    public void testNewInstance() throws Exception {
        String name = ""+random.nextLong();
        int increament = 5;
        long MAX = Long.MAX_VALUE;
        
        
        IdGenerator result = DBIdGenerator.newInstance(name, increament,1,MAX,manager);
        assertNotNull(result);
        
        verify(manager).createRecordIfNotExists(eq(name), eq(1L));
        
        
    }

    /**
     * Test of next method, of class DBIdGenerator.
     */
    @Test
    public void testNext() {
        System.out.println("next");
        
        String name = ""+random.nextLong();
        int increament = 5;
        int first = 13;
        long MAX = Long.MAX_VALUE;
        
        LongRecord record = new LongRecord(name,first);
        List<Long> ids = Arrays.asList(13L,14L,15L,16L,17L);
        
        when(manager.reserveNextRecords(eq(name), eq(increament), eq(MAX))).thenReturn(ids);
        
        
        IdGenerator instance = DBIdGenerator.newInstance(name, increament,1,MAX,manager);
        
        long expResult = first;
        assertEquals(expResult, instance.next());
        assertEquals(expResult+1, instance.next());
        
        
    }
    
    @Test
    public void updateStartUpdatesNextValue() {

        String name = ""+random.nextLong();
        int increament = 5;
        int first = 13;
        long MAX = Long.MAX_VALUE;
        
        LongRecord record = new LongRecord(name,first);
        List<Long> ids = Arrays.asList(13L,14L,15L,16L,17L);
        
        when(manager.reserveNextRecords(eq(name), eq(increament), eq(MAX))).thenReturn(ids);
        
        
        IdGenerator instance = DBIdGenerator.newInstance(name, increament,1,MAX,manager);
        
        long expResult = first;
        assertEquals(expResult, instance.next());
        
        ((DBIdGenerator)instance).updateStart(30L);
        verify(manager).updateStart(eq(name), eq(30L));
        ids = Arrays.asList(30L,31L);
        when(manager.reserveNextRecords(eq(name), eq(increament), eq(MAX))).thenReturn(ids);
        
        assertEquals(30, instance.next());
        
    }
    
    
    
}