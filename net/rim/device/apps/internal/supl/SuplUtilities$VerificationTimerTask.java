package net.rim.device.apps.internal.supl;

import java.util.TimerTask;
import net.rim.device.api.system.Application;

final class SuplUtilities$VerificationTimerTask extends TimerTask {
   private final SuplUtilities this$0;

   private SuplUtilities$VerificationTimerTask(SuplUtilities _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      synchronized (Application.getEventLock()) {
         this.this$0.dialog.select(this.this$0.defaultChoice);
      }
   }

   SuplUtilities$VerificationTimerTask(SuplUtilities x0, SuplUtilities$1 x1) {
      this(x0);
   }
}
