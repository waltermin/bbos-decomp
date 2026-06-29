package net.rim.device.api.ui.component;

import net.rim.device.api.ui.UiEngine;

class Status$PopScreenRunnable implements Runnable {
   private final Status this$0;

   Status$PopScreenRunnable(Status _1) {
      this.this$0 = _1;
   }

   public void init() {
   }

   @Override
   public void run() {
      UiEngine uiEngine = this.this$0.getUiEngine();
      if (uiEngine != null) {
         uiEngine.popScreen(this.this$0);
      }
   }
}
