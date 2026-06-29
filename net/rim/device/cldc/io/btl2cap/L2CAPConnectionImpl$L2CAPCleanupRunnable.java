package net.rim.device.cldc.io.btl2cap;

final class L2CAPConnectionImpl$L2CAPCleanupRunnable implements Runnable {
   private final L2CAPConnectionImpl this$0;

   L2CAPConnectionImpl$L2CAPCleanupRunnable(L2CAPConnectionImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0._notifier != null) {
         try {
            this.this$0._notifier.close();
         } finally {
            return;
         }
      } else {
         this.this$0.cleanup(true);
      }
   }
}
