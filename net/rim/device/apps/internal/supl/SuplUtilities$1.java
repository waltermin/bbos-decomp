package net.rim.device.apps.internal.supl;

import net.rim.device.api.ui.UiApplication;

final class SuplUtilities$1 implements Runnable {
   private final SuplUtilities this$0;

   SuplUtilities$1(SuplUtilities _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      UiApplication.getUiApplication().pushGlobalScreen(this.this$0.dialog, 50, 2);
   }
}
