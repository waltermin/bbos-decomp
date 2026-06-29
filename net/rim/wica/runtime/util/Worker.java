package net.rim.wica.runtime.util;

import java.util.Vector;

public class Worker {
   private WorkerThread _thread;
   private Vector _queue = new Vector(4);

   public Worker() {
      this._thread = new WorkerThread(this._queue);
      this._thread.start();
   }

   public void addToQueue(Runnable runnable) {
      synchronized (this._queue) {
         this.checkIfRunning();
         this._queue.addElement(runnable);
         this._queue.notify();
      }
   }

   public void addPriorityToQueue(Runnable runnable) {
      synchronized (this._queue) {
         this.checkIfRunning();
         this._queue.insertElementAt(runnable, 0);
         this._queue.notify();
      }
   }

   public void addExclusiveToQueue(Runnable runnable) {
      synchronized (this._queue) {
         this.checkIfRunning();
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

   private void checkIfRunning() {
      if (!this._thread.isAlive()) {
         this._thread = new WorkerThread(this._queue);
         this._thread.start();
      }
   }
}
