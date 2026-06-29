package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.browser.field.BrowserContent;

class RenderingThread implements Runnable {
   BrowserContent _browserContent;

   RenderingThread(BrowserContent field) {
      this._browserContent = field;
   }

   @Override
   public void run() {
      try {
         this._browserContent.finishLoading();
      } finally {
         return;
      }
   }
}
