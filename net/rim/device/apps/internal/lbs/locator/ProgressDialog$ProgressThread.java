package net.rim.device.apps.internal.lbs.locator;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Ui;

public final class ProgressDialog$ProgressThread implements Runnable {
   private ProgressDialog _progressDialog;
   private int _max;
   private int _min;
   private int _counter;
   private boolean _stop;
   private Object _lock;
   private final ProgressDialog this$0;

   ProgressDialog$ProgressThread(ProgressDialog this$0, ProgressDialog progressDialog, int min, int max) {
      this.this$0 = this$0;
      this._max = 30;
      this._progressDialog = progressDialog;
      this._lock = this._progressDialog._listener;
      if (max > 0) {
         this._max = max;
      }

      this._min = min;
   }

   public final void start() {
      this._stop = false;
      this._counter = this._min;
      new Thread(this).start();
   }

   public final void stopProgress() {
      if (!this._stop) {
         if (this._progressDialog.getUiEngine() != null) {
            synchronized (Application.getEventLock()) {
               Ui.getUiEngine().popScreen(this._progressDialog);
            }
         }

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
                  synchronized (Application.getEventLock()) {
                     this._progressDialog.close(false);
                  }

                  if (this._progressDialog.getUiEngine() != null) {
                     synchronized (Application.getEventLock()) {
                        Ui.getUiEngine().popScreen(this._progressDialog);
                     }
                  }

                  this._stop = true;
                  return;
               }

               this._progressDialog._statusGauge.setValue(this._counter);
               this._progressDialog.invalidate();
            }
         } finally {
            continue;
         }
      }
   }
}
