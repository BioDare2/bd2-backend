/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.concurrent.id;

import ed.biodare2.backend.util.concurrent.id.IdGenerator;
import ed.biodare2.backend.util.concurrent.id.LongRecordManager;
import ed.biodare2.backend.util.concurrent.id.IdGenerators;
import java.util.HashMap;
import java.util.Map;
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
public class IdGeneratorsTest {
    
    public IdGeneratorsTest() {
    }

    IdGenerators generators;
    LongRecordManager manager;
    Random random;
    
    @Before
    public void setUp() {
        manager = mock(LongRecordManager.class);
        generators = new IdGenerators(manager);
        random = new Random();
        
    }
    
    @After
    public void tearDown() {
    }
    
    /**
     * Test of initGenerator method, of class IdGenerators.
     */
    @Test
    public void testInitGenerator() {
        String name = "asdsadsa";
        int increament = 10;
        generators.initGenerator(name, increament);
        
        assertNotNull(generators.getGenerator(name));
    }
    
    @Test
    public void testInitGeneratorWithMax() {
        String name = "asdsadsa";
        int increament = 10;
        long max = 20;
        generators.initGenerator(name, increament,1,max);        
        assertNotNull(generators.getGenerator(name));
    }
    
    @Test
    public void initGeneratorOverridesExisting() {
        String name = "asdsadsa";
        int increament = 10;
        long max = 200;
        generators.initGenerator(name, increament,10,max);      
        IdGenerator gen = generators.getGenerator(name);
        assertNotNull(gen);
        generators.initGenerator(name, increament,100,max);      
        verify(manager).updateStart(eq(name), eq(100L));
    }    

    @Test
    public void testInitGenerators() {
        Map<String, Integer> conf = new HashMap<>();
        conf.put(""+hashCode(),10);
        conf.put("a"+hashCode(),10);
        
        generators.initGenerators(conf);
        for (String name : conf.keySet()) {
            assertNotNull(generators.getGenerator(name));
        }
    }
    
    

    /**
     * Test of getGenerator method, of class IdGenerators.
     */
    @Test
    public void testGetGenerator() {
        String name = "c"+hashCode();
        
        IdGenerator result;
        try {
            result = generators.getGenerator(name);
            fail("Excepiton expected not: "+result);
        } catch (IllegalArgumentException e) {};
        
        generators.initGenerator(name, 10);
        result = generators.getGenerator(name);
        assertNotNull(result);
        
        String name2 = name+"X";
        generators.initGenerator(name2, 10);
        
        IdGenerator result2 = generators.getGenerator(name2);
        assertFalse(result.equals(result2));
        assertNotSame(result,result2);
        
        result2 = generators.getGenerator(name);
        assertSame(result,result2);
        
    }
}