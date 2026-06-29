package net.rim.device.apps.internal.secureemail;

import net.rim.device.internal.ui.component.PleaseWaitWorkerThread;

final class SecureEmailOptionsItem$BackgroundOptionsWorkerThread extends PleaseWaitWorkerThread {
   private final SecureEmailOptionsItem this$0;

   SecureEmailOptionsItem$BackgroundOptionsWorkerThread(SecureEmailOptionsItem _1) {
      this.this$0 = _1;
   }

   @Override
   public final void doWork() {
      this.this$0.doBlocking(null);
   }
}
