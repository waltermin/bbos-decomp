package net.rim.device.internal.system;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.UiApplication;

class SIMCardEfHandler$1 implements Runnable {
   private final Application val$app;
   private final SIMCardEfHandler this$0;

   SIMCardEfHandler$1(SIMCardEfHandler _1, Application _2) {
      this.this$0 = _1;
      this.val$app = _2;
   }

   @Override
   public void run() {
      if (this.val$app instanceof UiApplication && this.this$0._dialog != null) {
         ((UiApplication)this.val$app).popScreen(this.this$0._dialog);
         this.this$0._dialog = null;
      }
   }
}
