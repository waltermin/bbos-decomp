package net.rim.device.apps.internal.elt;

import net.rim.device.api.system.Application;
import net.rim.vm.Process;

final class ETManager$GetLocationThread implements Runnable {
   private boolean _stop;
   private boolean _started;
   private Object _lock;
   private final ETManager this$0;

   public final void start() {
      Logger.logEvent(this, "start(), stopped:" + this._stop + ", started:" + this._started + " userStarted=" + this.this$0._data.isEnabledByUser(), false);
      if (!this._started) {
         this._stop = false;
         this._started = true;
         new Thread(this).start();
      }
   }

   public final void stop() {
      Logger.logEvent(this, "stop(), stopped:" + this._stop + ", started:" + this._started + " userStarted=" + this.this$0._data.isEnabledByUser(), false);
      if (!this._stop) {
         this._stop = true;
         this._started = false;
         this.notifyLock();
      }
   }

   final boolean isStarted() {
      return this._started;
   }

   public final void notifyLock() {
      Logger.logEvent(this, "notifyLock(), stopped:" + this._stop + ", started:" + this._started + " userStarted=" + this.this$0._data.isEnabledByUser(), false);
      if (this._started) {
         synchronized (this._lock) {
            this._lock.notify();
         }
      } else {
         this.start();
      }
   }

   final void killApp() {
      Application app = this.this$0._app;
      if (app != null) {
         Process p = Process.getProcess(app.getProcessId());
         Logger.logEvent(this, "killApp process=" + p, false);
         if (p != null) {
            p.destroy();
         }
      }
   }

   @Override
   public final void run() {
      if (this.this$0._data.isEnabledByUser()) {
         Logger.logEvent(this, "run() gpsFixThread=" + this.this$0._gpsFixThread, false);
         if (!this.this$0._gpsFixThread.isWaitingForGPS() || this.this$0._gpsFixThread.isFinished()) {
            this.this$0._gpsFixThread.stopAttempt();
            this.this$0._gpsFixThread = null;
            this.this$0._gpsFixThread = new ETManager$GPSFixThread(this.this$0);
         }

         int currentInterval = this.getNextCurrentInterval();
         long nextSyncInterval = this.this$0._syncTimestamp + this.this$0._syncBatchInterval;
         boolean invalidSyncTime = this.this$0._syncTimestamp == 0;
         if (invalidSyncTime || this.this$0._mgr == null) {
            this.this$0.registerSync();
         }

         int waitTime = this.this$0._syncBatchInterval - currentInterval * 1000;

         while (!this._stop) {
            if (waitTime < 0) {
               waitTime = this.getWaitTime(currentInterval);
            }

            Logger.logEvent(
               this,
               "wait="
                  + waitTime
                  + ", interval="
                  + currentInterval
                  + "sec, gpsFixTime="
                  + this.this$0._gpsFixTime
                  + ", enabledByUser="
                  + this.this$0._data.isEnabledByUser()
                  + ", enabledByITPolicy="
                  + this.this$0._data.isEnabledByITPolicy(),
               false
            );

            try {
               synchronized (this._lock) {
                  if (!this._stop && this.this$0._data.isEnabledByUser()) {
                     this._lock.wait(waitTime);
                  }

                  if (this._stop || !this.this$0._data.isEnabledByUser()) {
                     Logger.logEvent(this, "thread stop.", false);
                     break;
                  }

                  Logger.logEvent(this, "gpsFixThread=" + this.this$0._gpsFixThread, false);
                  if (this.this$0._gpsFixThread == null) {
                     this.this$0._gpsFixThread = new ETManager$GPSFixThread(this.this$0);
                  }

                  if (!this.this$0._gpsFixThread.isWaitingForGPS()) {
                     boolean gotLocation = this.this$0.updateLocation(false, currentInterval - 2);
                     if (!gotLocation) {
                        Logger.logEvent(this, "gotLocation false, current gpsFixThread=" + this.this$0._gpsFixThread, false);
                        if (!this.this$0._gpsFixThread.isWaitingForGPS()) {
                           if (this.this$0._gpsFixThread.isFinished()) {
                              this.this$0._gpsFixThread.stopAttempt();
                              this.this$0._gpsFixThread = null;
                              this.this$0._gpsFixThread = new ETManager$GPSFixThread(this.this$0);
                           }

                           this.this$0._gpsFixThread.start();
                        }
                     }
                  }

                  currentInterval = this.getNextCurrentInterval();
                  long currentTime = System.currentTimeMillis();

                  while (currentTime > nextSyncInterval) {
                     nextSyncInterval += this.this$0._syncBatchInterval;
                  }

                  if (!invalidSyncTime) {
                     waitTime = (int)(nextSyncInterval - currentTime) - currentInterval * 1000;
                  } else {
                     if (this.this$0._syncTimestamp > 0) {
                        invalidSyncTime = false;
                        nextSyncInterval = this.this$0._syncTimestamp + this.this$0._syncBatchInterval;
                     }

                     waitTime = this.getWaitTime(currentInterval);
                  }
               }
            } finally {
               continue;
            }
         }

         if (this.this$0._gpsFixThread != null) {
            this.this$0._gpsFixThread.stopAttempt();
         }

         this.this$0._gpsFixThread = null;
         this.killApp();
      }
   }

   private final int getWaitTime(int currentInterval) {
      currentInterval *= 1000;
      int wait = this.this$0._syncBatchInterval - currentInterval;
      if (wait < 0) {
         wait = 1000;
      }

      return wait;
   }

   private final int getNextCurrentInterval() {
      int fixTime = this.this$0._gpsFixTime;
      if (fixTime == 0) {
         fixTime = this.this$0._syncBatchInterval >> 1;
      }

      int currentInterval = Math.max(this.this$0._data.getGPSInterval(), fixTime / 1000);
      if (currentInterval - 30 >= this.this$0._syncBatchInterval / 1000) {
         currentInterval = this.this$0._syncBatchInterval / 1000 - 30;
      }

      return currentInterval;
   }

   ETManager$GetLocationThread(ETManager _1) {
      this.this$0 = _1;
      this._lock = new Object();
   }
}
