package net.rim.device.apps.internal.elt;

final class ETManager$GPSFixThread extends Thread {
   private ETManager _mgr;
   private boolean _waitingForGPS;
   private boolean _stop;
   private boolean _isFinished;
   private Object _lock = new Object();
   private static final int MAX_FIX_ATTEMPTS;

   ETManager$GPSFixThread(ETManager mgr) {
      this._mgr = mgr;
   }

   public final boolean isFinished() {
      return this._isFinished;
   }

   public final boolean isWaitingForGPS() {
      return this._waitingForGPS;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void stopAttempt() {
      synchronized (this._lock) {
         this._stop = true;

         try {
            this._lock.notify();
         } catch (Throwable var6) {
            Logger.logError(this, ((StringBuffer)(new Object("stopAttempt exception: "))).append(e).toString());
            return;
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      this._waitingForGPS = true;
      this._isFinished = false;
      int count = 0;
      Logger.logEvent(this, "to attempt lock.", false);
      synchronized (this._lock) {
         while (!this._stop) {
            Logger.logEvent(this, ((StringBuffer)(new Object("lock attempt: "))).append(count).toString(), false);
            if (this._mgr.updateLocation(true, -1) || count++ >= 3) {
               break;
            }

            if (!this._mgr._data.isEnabledByUser()) {
               this._stop = true;
            }
         }
      }

      this._waitingForGPS = false;
      Logger.logEvent(this, ((StringBuffer)(new Object("final attempts: "))).append(count).append(", stop=").append(this._stop).toString(), false);
      if (!this._stop) {
         this._isFinished = true;

         try {
            if (this._mgr._data.isEnabledByUser() && this._mgr._getLocationThread != null) {
               this._mgr._getLocationThread.notifyLock();
               return;
            }
         } catch (Throwable var6) {
            Logger.logError(this, ((StringBuffer)(new Object("notfy exception: "))).append(e).toString());
            return;
         }
      }
   }

   @Override
   public final String toString() {
      return ((StringBuffer)(new Object("GPSFixThread(")))
         .append(Integer.toHexString(this.hashCode()))
         .append("),waitingForGPS=")
         .append(this._waitingForGPS)
         .append(",stop=")
         .append(this._stop)
         .append(",isFinished=")
         .append(this._isFinished)
         .toString();
   }
}
