package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.system.Application;

class PreviewManager$UpdateScroll implements Runnable {
   private final PreviewManager this$0;

   private PreviewManager$UpdateScroll(PreviewManager _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      synchronized (Application.getEventLock()) {
         this.this$0.updateClientScroll();
         this.this$0._invokeId = -1;
      }
   }

   PreviewManager$UpdateScroll(PreviewManager x0, PreviewManager$1 x1) {
      this(x0);
   }
}
