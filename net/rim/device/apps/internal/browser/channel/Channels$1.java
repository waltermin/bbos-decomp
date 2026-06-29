package net.rim.device.apps.internal.browser.channel;

import net.rim.device.apps.internal.browser.core.BrowserImpl;

class Channels$1 implements Runnable {
   private final BrowserImpl val$browser;
   private final ChannelModel val$channel;

   Channels$1(BrowserImpl _1, ChannelModel _2) {
      this.val$browser = _1;
      this.val$channel = _2;
   }

   @Override
   public void run() {
      this.val$browser.enqueRunnable(new Channels$1$1(this));
   }
}
