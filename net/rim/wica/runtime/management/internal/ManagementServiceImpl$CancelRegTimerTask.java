package net.rim.wica.runtime.management.internal;

import java.util.TimerTask;
import net.rim.wica.runtime.logging.Logger;

final class ManagementServiceImpl$CancelRegTimerTask extends TimerTask {
   private final ManagementServiceImpl this$0;

   private ManagementServiceImpl$CancelRegTimerTask(ManagementServiceImpl this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      Logger.log(this.toString(), "Deactivating from CancelRegTimerTask", 4);
      this.this$0.cancelRegistration();
   }

   ManagementServiceImpl$CancelRegTimerTask(ManagementServiceImpl x0, ManagementServiceImpl$1 x1) {
      this(x0);
   }
}
