/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.concurrent.lock;

import ed.biodare2.backend.util.concurrent.lock.ResourceGuard;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author tzielins
 */
public class ResourceGuardTest {
    
    public ResourceGuardTest() {
    }
    
    ResourceGuard<Integer> guard;
    
    @Before
    public void init() {
        guard = new ResourceGuard<>(5);
    }

    @Test
    public void invokesTheInnerCode() {
        
        AtomicInteger counter = new AtomicInteger(0);
        
        guard.guard(1, ()-> {
            counter.incrementAndGet();
        });
        
        assertEquals(1,counter.get());
        
        Consumer<Integer> cons = i -> {
                counter.incrementAndGet();
            };
        
        guard.guard(1, (i)-> {
            counter.incrementAndGet();
        });

        long res = guard.guard(1, (i)-> {
            counter.incrementAndGet();
            return i*2L;
        });
        
        assertEquals(3,counter.get());
        assertEquals(2,res);
        
    }
    
    @Test
    public void guardsUsingIndependentLocks() throws Exception {
        
        final AtomicInteger counter1 = new AtomicInteger(0);
        final AtomicInteger counter2 = new AtomicInteger(0);
        
        final AtomicBoolean interupted = new AtomicBoolean(false);
        Runnable r1 = () -> {
            
            guard.guard(1, ()->{
                counter1.addAndGet(2);
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            });
        };
        
        Runnable r2 = () -> {
            
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
            
            try {
                guard.guard(2, ()->{
                    counter2.addAndGet(1);
                });
            } catch (RuntimeException e) {
                interupted.set(true);
            }
        };
        
        Thread first = new Thread(r1);
        Thread second = new Thread(r2);
        
        first.start();
        second.start();
        
        
        first.join();
        assertEquals(2,counter1.get());
        assertEquals(1,counter2.get());
        assertFalse(interupted.get());
    }
    
    
    @Test
    public void guardsAgainstSimultanousExecution() throws Exception {
        
        final AtomicInteger counter = new AtomicInteger(0);
        final AtomicBoolean interupted = new AtomicBoolean(false);
        Runnable r1 = () -> {
            
            guard.guard(1, ()->{
                counter.addAndGet(2);
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            });
        };
        
        Runnable r2 = () -> {
            
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
            
            try {
                guard.guard(1, ()->{
                    counter.addAndGet(1);
                });
            } catch (RuntimeException e) {
                interupted.set(true);
            }
        };
        
        Thread first = new Thread(r1);
        Thread second = new Thread(r2);
        
        first.start();
        second.start();
        
        Thread.sleep(150);
        second.interrupt();
        
        first.join();
        assertEquals(2,counter.get());
        assertTrue(interupted.get());
    }
    
    
    @Test
    public void cleaningIsTriggeredAndLeavesValidCreatedCounter() throws Exception {
        
        final AtomicInteger counter = new AtomicInteger(0);
        List<Callable<Integer>> runs = new ArrayList<>();
        for (int i = 0;i<100;i++) {
            final int id = i/3;
            runs.add( ()-> {
               
                guard.guard(id, ()->{
                    try {
                        Thread.sleep(10);
                        counter.incrementAndGet();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            return id;
            });
        };
        
        ExecutorService exec = Executors.newFixedThreadPool(5);
        exec.invokeAll(runs);
        
        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.MINUTES);
        
        assertEquals(100, counter.get());
        //assertEquals(guard.locks.size(),guard.created.get());
        System.out.println(guard.locks.size());
        assertTrue(guard.locks.size() <= 2*5);
        
        guard.locks.forEach( (k,v) -> {
            assertTrue(v.isEmpty());
        });
    }
    
    @Test
    public void guardsAgainsMultipleAccess() throws Exception {
        
        final AtomicInteger counter = new AtomicInteger(0);
        
        List<Callable<Integer>> runs = new ArrayList<>();
        for (int i = 0;i<50;i++) {
            final int id = 1;
            runs.add( ()-> {
               
                guard.guard(id, ()->{
                    try {
                        int v = counter.get();
                        Thread.sleep(20);
                        counter.set(v+1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            return id;
            });
        };
        
        ExecutorService exec = Executors.newFixedThreadPool(5);
        exec.invokeAll(runs);
        
        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.MINUTES);
        
        assertEquals(50, counter.get());
        //assertEquals(guard.locks.size(),guard.created.get());
        assertEquals(1,guard.locks.size());
        guard.locks.forEach( (k,v) -> {
            assertTrue(v.isEmpty());
        });
        
    }    
}
