package net.rim.device.apps.internal.secureemail;

import net.rim.device.internal.ui.component.PleaseWaitWorkerThread;

class SecureEmailProcessor$1 extends PleaseWaitWorkerThread {
   private final SecureEmailProcessor this$0;

   SecureEmailProcessor$1(SecureEmailProcessor _1) {
      this.this$0 = _1;
   }

   @Override
   public void doWork() {
      this.this$0.runProcessor(true);
   }
}
