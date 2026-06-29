package net.rim.device.apps.api.reminders;

import net.rim.vm.Array;

final class ReminderManager$ReminderChangeNotificationThread extends Thread {
   Runnable[] _changeQueue = new Object[0];

   public final void addRunnable(Runnable r) {
      synchronized (this._changeQueue) {
         Array.resize(this._changeQueue, this._changeQueue.length + 1);
         this._changeQueue[this._changeQueue.length - 1] = r;
         this._changeQueue.notify();
      }
   }

   @Override
   public final void run() {
      while (true) {
         try {
            Runnable[] localchangeQueue;
            synchronized (this._changeQueue) {
               label96:
               try {
                  if (this._changeQueue.length == 0) {
                     this._changeQueue.wait();
                  }
               } finally {
                  break label96;
               }

               localchangeQueue = new Object[this._changeQueue.length];

               for (int i = 0; i < this._changeQueue.length; i++) {
                  localchangeQueue[i] = this._changeQueue[i];
               }

               Array.resize(this._changeQueue, 0);
            }

            for (int i = 0; i < localchangeQueue.length; i++) {
               Runnable r = localchangeQueue[i];
               r.run();
               r = null;
            }

            Array.resize(localchangeQueue, 0);
         } finally {
            continue;
         }
      }
   }
}
