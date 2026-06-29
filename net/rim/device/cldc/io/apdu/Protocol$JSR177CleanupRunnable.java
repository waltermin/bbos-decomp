package net.rim.device.cldc.io.apdu;

final class Protocol$JSR177CleanupRunnable implements Runnable {
   private final Protocol this$0;

   Protocol$JSR177CleanupRunnable(Protocol _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      try {
         this.this$0.close();
      } finally {
         return;
      }
   }
}
