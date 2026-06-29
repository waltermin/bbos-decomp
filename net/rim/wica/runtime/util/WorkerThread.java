package net.rim.wica.runtime.util;

import java.util.Vector;

final class WorkerThread extends Thread {
   private Vector _queue;
   private static final long THREAD_TIMEOUT;

   WorkerThread(Vector queue) {
      this._queue = queue;
   }

   private final boolean isEmpty() {
      return this._queue.size() == 0;
   }

   @Override
   public final void run() {
      Runnable runnable = null;

      while (true) {
         synchronized (this._queue) {
            while (this.isEmpty()) {
               try {
                  this._queue.wait(10000);
                  if (this.isEmpty()) {
                     break;
                  }
               } finally {
                  continue;
               }
            }

            if (this.isEmpty()) {
               return;
            }

            runnable = (Runnable)this._queue.elementAt(0);
            this._queue.removeElementAt(0);
         }

         label82:
         try {
            runnable.run();
         } finally {
            break label82;
         }

         runnable = null;
      }
   }
}
