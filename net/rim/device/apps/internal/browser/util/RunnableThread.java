package net.rim.device.apps.internal.browser.util;

import java.util.Vector;
import net.rim.vm.Process;

public class RunnableThread extends Thread {
   protected Vector _queue = new Vector();
   private boolean _done;
   private boolean _killProcessIfThisThreadDies = true;

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

   public void setDone(boolean done) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void setKillProcessIfThisThreadDies(boolean killProcessIfThisThreadDies) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public void addToQueue(Object r) {
      synchronized (this._queue) {
         this._queue.addElement(r);
         this._queue.notify();
      }
   }

   public void addExclusiveToQueue(Object r) {
      synchronized (this._queue) {
         this._queue.removeAllElements();
         this._queue.addElement(r);
         this._queue.notify();
      }
   }

   @Override
   public void run() {
      Process.killProcessIfThisThreadDies(this._killProcessIfThisThreadDies);
      Object runMe = null;

      while (!this._done) {
         synchronized (this._queue) {
            while (this._queue.size() == 0) {
               try {
                  this._queue.wait();
               } finally {
                  continue;
               }
            }

            runMe = this._queue.elementAt(0);
            this._queue.removeElementAt(0);
         }

         this.runItem(runMe);
         runMe = null;
      }
   }

   protected void runItem(Object _1) {
      throw null;
   }
}
