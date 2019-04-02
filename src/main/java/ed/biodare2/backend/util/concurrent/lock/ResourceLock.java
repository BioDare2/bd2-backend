/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ed.biodare2.backend.util.concurrent.lock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tzielins
 */
public class ResourceLock<K> {


    protected static final boolean DEBUG = false;
    
    public static class RobustSystemError extends Exception {

	public RobustSystemError(String msg, Exception e) {
	    super(msg,e);
	}

	public RobustSystemError(String msg) {
	    super(msg);
	}
    }


    private final ReentrantLock mainLock = new ReentrantLock();
    private final Map<K,ReentrantLock> servedLocks = new HashMap<>();
    private final Map<ReentrantLock,Integer> locksCount = new HashMap<>();
    private final GenericObjectPool<ReentrantLock> pool;
    private final AtomicInteger cleanUpRequest = new AtomicInteger();
    private final int SIZE;
    private final int CLEANUP_TRESHOLD;
    private final Logger logger;

    public ResourceLock(int size) {
	if (size < 1) throw new IllegalArgumentException("Size must be >= 1");
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(2*size);
        config.setBlockWhenExhausted(false);
	pool = new GenericObjectPool<>(new LockFactory(),config);
	//pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_FAIL);
	SIZE = size;
        if (SIZE > 50)  CLEANUP_TRESHOLD = 20;
        else if (SIZE > 20) CLEANUP_TRESHOLD = 10;
        else if (SIZE > 10) CLEANUP_TRESHOLD = 5;
        else CLEANUP_TRESHOLD = 1;
        
        if (DEBUG) logger = LoggerFactory.getLogger(this.getClass());
        else logger = null;
    }

    public void lock(K resourceId) throws InterruptedException {
        
        if (DEBUG) logger.info("Thread "+Thread.currentThread().hashCode()+" locking "+resourceId);
        
	mainLock.lock();
	ReentrantLock lock;
	try {
	    lock = getLock(resourceId);
	    Integer count = locksCount.get(lock);
	    if (count == null) count = 0;
	    count++;
	    locksCount.put(lock, count);
	} finally {
	    mainLock.unlock();
	}
	lock.lockInterruptibly();
        
        if (DEBUG) logger.info("Thread "+Thread.currentThread().hashCode()+" locked "+resourceId);
    }
    

    public void unlock(K resourceId) throws IllegalMonitorStateException {

        if (DEBUG) logger.info("Thread "+Thread.currentThread().hashCode()+" unlocking "+resourceId);
        
	mainLock.lock();
	ReentrantLock lock;
	try {
	    lock = getLock(resourceId);
	    if (!lock.isHeldByCurrentThread()) throw new IllegalMonitorStateException("Cannot unlock resource which is not held by the caller");
	    Integer count = locksCount.get(lock);
	    count--;
	    locksCount.put(lock,count);
	} finally {
	    mainLock.unlock();
	}
	lock.unlock();
        
        if (DEBUG) logger.info("Thread "+Thread.currentThread().hashCode()+" unlocked "+resourceId);
        
    }


    protected ReentrantLock getLock(K id) throws IllegalStateException {
	mainLock.lock();
	try {
	    ReentrantLock lock = servedLocks.get(id);
	    if (lock == null) {

		try {
		    lock = pool.borrowObject();
		} catch (Exception e) {
		    throw new IllegalStateException("Cannot obtain new lock: "+e.getMessage(),e);
		}
		if (servedLocks.size() > SIZE)
		    if (cleanUpRequest.incrementAndGet() > CLEANUP_TRESHOLD) doCleanUp();

		servedLocks.put(id, lock);
	    }
	    return lock;
	} finally {
	    mainLock.unlock();
	}

    }

    protected void doCleanUp() throws IllegalStateException {
	mainLock.lock();
	try {

	  Iterator<Map.Entry<K,ReentrantLock>> iter = servedLocks.entrySet().iterator();
	  while (iter.hasNext()) {
	      Map.Entry<K,ReentrantLock> entry = iter.next();
	      ReentrantLock lock = entry.getValue();
	      Integer count = locksCount.get(lock);
	      if (count == null || count <= 0) {
		  locksCount.remove(lock);
		  iter.remove();
		  try {
		    pool.returnObject(lock);
		  } catch (Exception e) {
		      throw new IllegalStateException("Something wrong with the lock pool: "+e.getMessage(),e);
		  }
	      }
	  }
          cleanUpRequest.set(0);
	} finally {
	    mainLock.unlock();
	}
    }


}
