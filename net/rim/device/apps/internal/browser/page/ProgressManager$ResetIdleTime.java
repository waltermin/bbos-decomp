package net.rim.device.apps.internal.browser.page;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Backlight;
import net.rim.vm.Memory;

final class ProgressManager$ResetIdleTime implements Runnable {
   private final ProgressManager this$0;

   ProgressManager$ResetIdleTime(ProgressManager _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (System.currentTimeMillis() < this.this$0._startIdleKickTimestamp + 120000) {
         Memory.resetLastIdle();
         Backlight.resetElapsedTime();
      } else {
         Application.getApplication().cancelInvokeLater(this.this$0._resetIdleTimer);
         this.this$0._startIdleKickTimestamp = 0;
      }
   }
}
