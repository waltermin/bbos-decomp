package net.rim.device.internal.synchronization.ota.util;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.CyclicQueue;

public final class ReusableObjectPool {
   private CyclicQueue _pool;

   public ReusableObjectPool() {
      this(0);
   }

   public ReusableObjectPool(int size) {
      this._pool = new CyclicQueue(size);
   }

   public static final ReusableObjectPool getSingletonInstance(long aGuid) {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      ReusableObjectPool xReusableObjectPool = (ReusableObjectPool)ar.getOrWaitFor(aGuid);
      if (xReusableObjectPool == null) {
         xReusableObjectPool = new ReusableObjectPool();
         ar.put(aGuid, xReusableObjectPool);
      }

      return xReusableObjectPool;
   }

   public final ReusableObject checkOut() {
      synchronized (this._pool) {
         return this._pool.isEmpty() ? null : (ReusableObject)this._pool.dequeue();
      }
   }

   public final void checkIn(ReusableObject aReusableObject) {
      aReusableObject.reset();
      synchronized (this._pool) {
         this._pool.enqueue(aReusableObject);
      }
   }

   public final void free() {
      synchronized (this._pool) {
         this._pool = new CyclicQueue(0);
      }
   }
}
