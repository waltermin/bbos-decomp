package net.rim.wica.runtime.util.internal;

import java.util.Vector;

public class BackgroundWorker extends Thread {
   protected Vector _queue = (Vector)(new Object(5));

   public BackgroundWorker() {
   }

   public BackgroundWorker(String name) {
      super(name);
   }

   public void addToQueue(Runnable runnable) {
      synchronized (this._queue) {
         this._queue.addElement(runnable);
         this._queue.notify();
      }
   }

   public void addPriorityToQueue(Runnable runnable) {
      synchronized (this._queue) {
         this._queue.insertElementAt(runnable, 0);
         this._queue.notify();
      }
   }

   public void addExclusiveToQueue(Runnable runnable) {
      synchronized (this._queue) {
         this._queue.removeAllElements();
         this._queue.addElement(runnable);
         this._queue.notify();
      }
   }

   public void clear() {
      synchronized (this._queue) {
         this._queue.removeAllElements();
      }
   }

   public int getCount() {
      synchronized (this._queue) {
         return this._queue.size();
      }
   }

   @Override
   public void run() {
      Runnable runnable = null;

      while (true) {
         synchronized (this._queue) {
            while (this._queue.size() == 0) {
               try {
                  this._queue.wait();
               } finally {
                  continue;
               }
            }

            runnable = (Runnable)this._queue.elementAt(0);
            this._queue.removeElementAt(0);
         }

         runnable.run();
         runnable = null;
      }
   }
}
