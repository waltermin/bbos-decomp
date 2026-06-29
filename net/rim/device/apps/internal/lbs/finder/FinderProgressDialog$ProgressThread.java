package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class FinderProgressDialog$ProgressThread implements Runnable {
   private int _max;
   private int _min;
   private int _counter;
   private Object _lock;
   private boolean _stop;
   private final FinderProgressDialog this$0;

   FinderProgressDialog$ProgressThread(FinderProgressDialog this$0, int min, int max) {
      this.this$0 = this$0;
      this._max = 30;
      this._lock = new Object();
      if (max > 0) {
         this._max = max;
      }

      this._min = min;
   }

   public final void start() {
      this._stop = false;
      this._counter = this._min;
      ((Thread)(new Object(this))).start();
   }

   final void stopProgress() {
      if (!this._stop) {
         this._stop = true;
         synchronized (this._lock) {
            this._lock.notify();
         }
      }
   }

   @Override
   public final void run() {
      while (!this._stop) {
         try {
            synchronized (this._lock) {
               this._lock.wait(1000);
               this._counter++;
               if (this._counter > this._max) {
                  UiApplication.getUiApplication().invokeAndWait(new MessageRunnable(LBSResources.getString(103)));
                  this._stop = true;
                  this.this$0.stop(true);
                  return;
               }

               this.this$0._statusGauge.setValue(this._counter);
               this.this$0.invalidate();
            }
         } finally {
            continue;
         }
      }
   }
}
