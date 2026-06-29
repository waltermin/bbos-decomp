package net.rim.device.apps.internal.qm.peer.common;

import java.util.Vector;
import net.rim.device.api.system.Application;

final class QueueRunner implements Runnable {
   Application _application;
   Vector _queue;
   boolean _scheduled;

   QueueRunner(Application application) {
      this._application = application;
      this._queue = (Vector)(new Object());
   }

   final void addRunnable(Runnable element) {
      this._queue.addElement(element);
      this.schedule();
   }

   final synchronized void schedule() {
      if (!this._scheduled) {
         this._application.invokeLater(this);
         this._scheduled = true;
      }
   }

   @Override
   public final void run() {
      Runnable runnable = null;
      synchronized (this._queue) {
         if (this._queue.size() != 0) {
            runnable = (Runnable)this._queue.firstElement();
            this._queue.removeElementAt(0);
         }

         if (this._queue.size() != 0) {
            this._application.invokeLater(this);
         } else {
            this._scheduled = false;
         }
      }

      if (runnable != null) {
         runnable.run();
      }
   }
}
