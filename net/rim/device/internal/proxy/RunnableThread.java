package net.rim.device.internal.proxy;

import net.rim.device.api.util.CyclicQueue;

final class RunnableThread extends Thread {
   private CyclicQueue _queue = new CyclicQueue();

   final void add(Runnable runnable) {
      synchronized (this._queue) {
         this._queue.enqueue(runnable);
         this._queue.notify();
      }
   }

   @Override
   public final void run() {
      while (true) {
         try {
            Runnable runnable = null;
            synchronized (this._queue) {
               if (this._queue.isEmpty()) {
                  this._queue.wait();
               }

               runnable = (Runnable)this._queue.dequeue();
            }

            runnable.run();
         } catch (Throwable var5) {
         }
      }
   }
}
