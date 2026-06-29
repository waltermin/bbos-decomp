package net.rim.device.apps.internal.bis.utils;

import net.rim.device.api.browser.field.BrowserContent;

final class BrowserFieldRenderingApplication$1 extends Thread {
   private final BrowserContent val$contentField;
   private final BrowserFieldRenderingApplication this$0;

   BrowserFieldRenderingApplication$1(BrowserFieldRenderingApplication _1, BrowserContent _2) {
      this.this$0 = _1;
      this.val$contentField = _2;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      try {
         this.val$contentField.finishLoading();
      } catch (Throwable var3) {
         re.printStackTrace();
         return;
      }
   }
}
