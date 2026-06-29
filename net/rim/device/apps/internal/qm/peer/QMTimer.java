package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.system.Application;

public final class QMTimer implements Runnable {
   private QMTimerTask _head;
   private int _invokeLaterId = -1;

   public final void schedule(QMTimerTask timer, long delay) {
      if (timer.isScheduled()) {
         throw new Object("Already scheduled");
      }

      long expiry = System.currentTimeMillis() + delay;
      timer.schedule(expiry);
      this.insertAndSchedule(timer);
   }

   public final synchronized void cancel(QMTimerTask timer) {
      if (timer == this._head) {
         this._head = this._head.getNext();
         this.setInvokeLater();
      } else if (this._head != null) {
         QMTimerTask cur = this._head;

         while (true) {
            QMTimerTask next = cur.getNext();
            if (next == null) {
               break;
            }

            if (next == timer) {
               cur.setNext(next.getNext());
               break;
            }

            cur = next;
         }
      }

      timer.unschedule();
   }

   private final synchronized void insertAndSchedule(QMTimerTask timer) {
      long expiry = timer.getExpiryTime();
      if (this._head != null && expiry >= this._head.getExpiryTime()) {
         QMTimerTask cur = this._head;

         while (true) {
            QMTimerTask next = cur.getNext();
            if (next == null || expiry < next.getExpiryTime()) {
               timer.setNext(next);
               cur.setNext(timer);
               return;
            }

            cur = next;
         }
      } else {
         timer.setNext(this._head);
         this._head = timer;
         this.setInvokeLater();
      }
   }

   private final synchronized void setInvokeLater() {
      Application app = Application.getApplication();
      if (this._invokeLaterId != -1) {
         app.cancelInvokeLater(this._invokeLaterId);
         this._invokeLaterId = -1;
      }

      if (this._head != null) {
         long delay = this._head.getExpiryTime() - System.currentTimeMillis();
         if (delay <= 0) {
            this.run();
            return;
         }

         this._invokeLaterId = app.invokeLater(this, delay, false);
      }
   }

   @Override
   public final void run() {
      this._invokeLaterId = -1;

      while (this._head != null) {
         QMTimerTask cur = this._head;
         if (cur.getExpiryTime() >= System.currentTimeMillis()) {
            break;
         }

         QMTimerTask next = cur.getNext();
         cur.unschedule();
         this._head = next;

         try {
            cur.run();
         } finally {
            continue;
         }
      }

      this.setInvokeLater();
   }
}
