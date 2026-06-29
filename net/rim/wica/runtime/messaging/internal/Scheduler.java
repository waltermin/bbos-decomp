package net.rim.wica.runtime.messaging.internal;

import net.rim.wica.runtime.messaging.internal.util.InternalLogger;
import net.rim.wica.runtime.messaging.internal.util.ThreadSafePriorityQueue;

public class Scheduler implements Runnable {
   private Thread _thread;
   private ThreadSafePriorityQueue _processors;
   private Object _END_THREAD = new Object();
   private boolean _running;

   Scheduler() {
      this._processors = new ThreadSafePriorityQueue();
   }

   void start(String name) {
      this._thread = (Thread)(new Object(this, name));
      this._running = true;
      this._thread.start();
   }

   void stop() {
      if (this._running) {
         this._running = false;
         this._processors.add(this._END_THREAD);

         try {
            this._thread.join();
         } finally {
            return;
         }
      }
   }

   public boolean isRunning() {
      return this._running;
   }

   public void schedule(Runnable processor) {
      this._processors.add(processor);
   }

   public void schedulePriority(Runnable processor) {
      this._processors.addPriority(processor);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      while (this._running) {
         Object o = this._processors.get();
         if (o != this._END_THREAD) {
            try {
               ((Runnable)o).run();
            } catch (Throwable var4) {
               InternalLogger.logError(this, null, t, o);
               continue;
            }
         }
      }
   }
}
