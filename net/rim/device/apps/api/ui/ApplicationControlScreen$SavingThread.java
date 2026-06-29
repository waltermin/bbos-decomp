package net.rim.device.apps.api.ui;

import net.rim.device.internal.ui.component.PleaseWaitWorkerThread;

class ApplicationControlScreen$SavingThread extends PleaseWaitWorkerThread {
   private final ApplicationControlScreen this$0;

   private ApplicationControlScreen$SavingThread(ApplicationControlScreen _1) {
      this.this$0 = _1;
   }

   @Override
   protected void doWork() {
      if (this.this$0._moduleHandles.length > 1) {
         this.this$0.commitApplicationSettings();
      } else {
         this.this$0.commitSettings(this.this$0._moduleHandles[0]);
      }
   }

   ApplicationControlScreen$SavingThread(ApplicationControlScreen x0, ApplicationControlScreen$1 x1) {
      this(x0);
   }
}
