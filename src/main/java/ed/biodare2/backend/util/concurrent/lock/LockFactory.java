/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ed.biodare2.backend.util.concurrent.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 *
 * @author tzielins
 */
class LockFactory extends BasePooledObjectFactory<ReentrantLock> {

    @Override
    public ReentrantLock create() {
	return new ReentrantLock();
    }

    @Override
    public PooledObject<ReentrantLock> wrap(ReentrantLock t) {
        return new DefaultPooledObject(t);
    }

}
