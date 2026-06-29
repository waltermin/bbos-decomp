package net.rim.device.api.system;

import net.rim.device.internal.system.InternalServices;

final class Application$TimerCleanupRunnable implements Runnable {
   private final Application this$0;

   Application$TimerCleanupRunnable(Application _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      for (int i = 0; i < 20; i++) {
         if (this.this$0._timedRunnables[i] != null) {
            InternalServices.killTimer(this.this$0._process.getOSTimerId(i));
         }
      }
   }
}
