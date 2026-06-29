package net.rim.device.apps.internal.lbs.locator;

import net.rim.device.api.lbs.gps.GPSDevice;
import net.rim.device.api.lbs.gps.GPSProvider;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Ui;

public final class GPSFixProgressDialog$FixThread implements Runnable {
   private GPSDevice _device;
   private GPSFixProgressDialog _progressDialog;
   private final GPSFixProgressDialog this$0;

   public GPSFixProgressDialog$FixThread(GPSFixProgressDialog this$0, GPSDevice device, GPSFixProgressDialog progressDialog) {
      this.this$0 = this$0;
      this._device = device;
      this._progressDialog = progressDialog;
   }

   public final void start() {
      ((Thread)(new Object(this))).start();
   }

   @Override
   public final void run() {
      this.this$0._location = GPSProvider.getInstance().getSingleFix(this._device);
      synchronized (Application.getEventLock()) {
         if (this._progressDialog.getUiEngine() != null) {
            Ui.getUiEngine().popScreen(this._progressDialog);
         }
      }
   }
}
