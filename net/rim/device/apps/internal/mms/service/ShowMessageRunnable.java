package net.rim.device.apps.internal.mms.service;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Status;

class ShowMessageRunnable implements Runnable {
   private int _bitmapID;
   private String _message;

   public ShowMessageRunnable(String message, int bitmapID) {
      this._message = message;
      this._bitmapID = bitmapID;
   }

   @Override
   public void run() {
      System.out.println("MMS Service Test: " + this._message);
      Status.show(this._message, Bitmap.getPredefinedBitmap(this._bitmapID), 60000, 33554432, true, false, 50);
   }
}
