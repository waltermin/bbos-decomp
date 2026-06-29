package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Branding;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.vm.Memory;

final class SplashScreen implements Runnable {
   private DisplayableSplashScreen _screen;
   private UiApplication _app;
   private Runnable _runWhenDone;
   private boolean _dismissScreen;
   private Bitmap _initialStartupImage;
   private int _initialStartupImageTimeout = 2000;
   private BitmapField _bitmapField;
   private static final int INTEL_SPLASH_TIME;
   private static final int EVAL_SPLASH_TIME;

   final synchronized boolean queue(boolean carrierImageOnly, Runnable runWhenDone, boolean dismissScreen) {
      if (this._runWhenDone != null) {
         this._app.invokeLater(this._runWhenDone);
         this._runWhenDone = null;
      }

      this._dismissScreen = dismissScreen;
      this._runWhenDone = runWhenDone;
      if (this._screen == null) {
         Bitmap bitmap = this.getInitialStartupImage();
         if (carrierImageOnly || bitmap == null) {
            bitmap = this.getCarrierBitmap();
            this._initialStartupImage = null;
         }

         if (bitmap != null) {
            Memory.supressHourglass(true);
            this._screen = new DisplayableSplashScreen();
            this._bitmapField = (BitmapField)(new Object(bitmap, 2305843009213693988L));
            this._screen.add(this._bitmapField);
            this._app.pushGlobalScreen(this._screen, -2147483642, 2);
            return true;
         }
      }

      return false;
   }

   final synchronized void setTimer() {
      if (this._screen != null) {
         int timeout = 3000;
         if (this._initialStartupImage != null) {
            timeout = this._initialStartupImageTimeout;
         } else {
            byte[] data = Branding.getData(1);
            if (data != null) {
               timeout = data[0] & 255 | (data[1] & 255) << 8;
            }
         }

         this._screen.setTimer(this._app, this, timeout);
      } else {
         if (this._runWhenDone != null) {
            this._app.invokeLater(this._runWhenDone);
            this._runWhenDone = null;
         }
      }
   }

   final boolean isRunning() {
      return this._screen != null;
   }

   final void dismissScreen() {
      if (this._screen != null) {
         this._app.popScreen(this._screen);
         this._screen = null;
         this._bitmapField = null;
         this._initialStartupImage = null;
         Memory.supressHourglass(false);
      }
   }

   @Override
   public final synchronized void run() {
      boolean checkForDismiss = false;
      if (this._screen != null) {
         if (this._initialStartupImage != null) {
            Bitmap bitmap = this.getCarrierBitmap();
            this._initialStartupImage = null;
            if (bitmap != null) {
               this._bitmapField.setBitmap(bitmap);
               this.setTimer();
            } else {
               checkForDismiss = true;
            }
         } else {
            checkForDismiss = true;
         }

         if (checkForDismiss) {
            if (this._dismissScreen || this._runWhenDone == null) {
               this.dismissScreen();
            }

            if (this._runWhenDone != null) {
               this._app.invokeLater(this._runWhenDone);
               this._runWhenDone = null;
            }
         }
      }
   }

   private final Bitmap getInitialStartupImage() {
      if (this._initialStartupImage == null) {
         this._initialStartupImageTimeout = 2000;
         byte[] data = Branding.getData(16898);
         if (data != null && data.length != 0 && data[0] == 0) {
         }
      }

      return this._initialStartupImage;
   }

   SplashScreen() {
      this._app = UiApplication.getUiApplication();
   }

   private final Bitmap getCarrierBitmap() {
      Bitmap bitmap = null;
      byte[] data = Branding.getData(0);
      if (data != null) {
         bitmap = Bitmap.createBitmapFromPNG(data, 0, data.length);
      }

      return bitmap;
   }
}
