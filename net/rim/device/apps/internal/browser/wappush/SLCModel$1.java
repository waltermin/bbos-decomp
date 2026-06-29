package net.rim.device.apps.internal.browser.wappush;

import net.rim.device.api.notification.NotificationsManager;

class SLCModel$1 implements Runnable {
   private final SLCModel this$0;

   SLCModel$1(SLCModel _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      NotificationsManager.cancelImmediateEvent(4665536253483290822L, 0, null, null);
   }
}
