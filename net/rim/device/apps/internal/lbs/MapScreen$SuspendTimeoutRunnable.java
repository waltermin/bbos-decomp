package net.rim.device.apps.internal.lbs;

import net.rim.device.api.ui.UiApplication;

final class MapScreen$SuspendTimeoutRunnable implements Runnable {
   private final MapScreen this$0;

   MapScreen$SuspendTimeoutRunnable(MapScreen this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      if (!UiApplication.getUiApplication().isForeground()) {
         this.this$0.onClose();
         this.this$0._suspendTimeoutPID = -1;
         System.exit(0);
      }
   }
}
