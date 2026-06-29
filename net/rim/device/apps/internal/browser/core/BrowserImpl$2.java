package net.rim.device.apps.internal.browser.core;

import net.rim.device.apps.internal.browser.channel.Channels;

class BrowserImpl$2 implements Runnable {
   private final String val$finalId;
   private final BrowserImpl this$0;

   BrowserImpl$2(BrowserImpl _1, String _2) {
      this.this$0 = _1;
      this.val$finalId = _2;
   }

   @Override
   public void run() {
      Channels.openChannel(this.val$finalId);
   }
}
