package net.rim.device.apps.internal.browser.push;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Status;

class BrowserChannelModel$1 implements Runnable {
   private final String val$notificationMsg;
   private final BrowserChannelModel this$0;

   BrowserChannelModel$1(BrowserChannelModel _1, String _2) {
      this.this$0 = _1;
      this.val$notificationMsg = _2;
   }

   @Override
   public void run() {
      Status.show(this.val$notificationMsg, Bitmap.getPredefinedBitmap(0), 60000, 33554432, true, false, 50);
   }
}
