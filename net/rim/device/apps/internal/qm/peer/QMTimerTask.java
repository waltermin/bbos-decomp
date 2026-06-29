package net.rim.device.apps.internal.qm.peer;

public class QMTimerTask implements Runnable {
   private long _expiryTime;
   private QMTimerTask _next;
   private boolean _scheduled;

   public boolean isScheduled() {
      return this._scheduled;
   }

   public void schedule(long time) {
      this._expiryTime = time;
      this._scheduled = true;
   }

   public void unschedule() {
      this._next = null;
      this._scheduled = false;
   }

   public long getExpiryTime() {
      return this._expiryTime;
   }

   public QMTimerTask getNext() {
      return this._next;
   }

   public void setNext(QMTimerTask next) {
      this._next = next;
   }

   @Override
   public void run() {
      throw null;
   }
}
