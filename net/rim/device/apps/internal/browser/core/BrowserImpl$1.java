package net.rim.device.apps.internal.browser.core;

import net.rim.device.apps.internal.browser.stack.FetchRequest;

class BrowserImpl$1 implements Runnable {
   private final FetchRequest val$fetchRequest;
   private final BrowserImpl this$0;

   BrowserImpl$1(BrowserImpl _1, FetchRequest _2) {
      this.this$0 = _1;
      this.val$fetchRequest = _2;
   }

   @Override
   public void run() {
      this.this$0._navigationThread.requestFetchRequest(this.val$fetchRequest);
   }
}
