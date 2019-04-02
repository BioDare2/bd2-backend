/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ed.biodare2.backend.util.concurrent.lock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author tzielins
 */
public class ResourceGuard<K> {
    
    final int SIZE;
    final Map<K, CountingLock> locks;
    //final AtomicInteger created = new AtomicInteger(0);
    final ReentrantLock global = new ReentrantLock();
    
    public ResourceGuard() {
        this(100);
    }
    
    public ResourceGuard(int SIZE) {
        if (SIZE < 1) throw new IllegalArgumentException("Size must be > 0");
        this.SIZE = SIZE;
        //locks = new ConcurrentHashMap<>(SIZE);
        locks = new HashMap<>(SIZE);
    }
    
    public void guard(K resourceId,Runnable code) {
        
        try {
            CountingLock lock = getAndLock(resourceId);
            try {
                code.run();
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted when locking resource: "+resourceId);
        }    
    }
    
    public void guard(K resourceId,Consumer<K> code) {
        
        
        try {
            CountingLock lock = getAndLock(resourceId);
            try {
                code.accept(resourceId);
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted when locking resource: "+resourceId);
        }
        
    }
    
    
    public <V> V guard(K resourceId,Function<K,V> code) {
        
        
        try {
            CountingLock lock = getAndLock(resourceId);
            try {
                return code.apply(resourceId);
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted when locking resource: "+resourceId);
        }
        
    }
    
    
    protected CountingLock getAndLock(K resourceId) throws InterruptedException {
        
        CountingLock lock = getAndMark(resourceId);
        lock.lock();
        
        return lock;
    }
    
    protected CountingLock getAndMark(K resourceId) throws InterruptedException {
        global.lockInterruptibly();
        try {
            CountingLock lock = locks.computeIfAbsent(resourceId, (id) -> {
                                        //created.incrementAndGet();
                                        return new CountingLock();
                        }); 
            lock.mark();
            
            //if (created.get() > 2*SIZE) {
            if (locks.size() > 2*SIZE) {
                cleanUnused();
            }  
            
            return lock;
        } finally {
            global.unlock();
        }
    }
    
    protected void cleanUnused() throws InterruptedException {
        
        List<K> toDelete = new ArrayList<>(2*SIZE);
        locks.forEach( (key,lock) -> {
            if (lock.isEmpty()) toDelete.add(key);
        });
        
        for (K key : toDelete) {
            CountingLock lock = locks.get(key);
            if (lock != null) {
                if (lock.isEmpty()) {
                        locks.remove(key);
                        /*if (locks.remove(key) != null) {
                           created.decrementAndGet();
                        }*/
                }
            }
        };
    }
    
    protected static class CountingLock {

        final AtomicInteger users = new AtomicInteger();
        final ReentrantLock lock = new ReentrantLock();

        public void mark() {
            users.incrementAndGet();
        }
        
        public void lock() throws InterruptedException {
            //users.incrementAndGet();
            lock.lockInterruptibly();
        }
        
        
        public void unlock() {
            lock.unlock();
            users.decrementAndGet();
        }
        
        
        public boolean isEmpty() {
            return users.get() < 1;
        }
        

        
    }
}
