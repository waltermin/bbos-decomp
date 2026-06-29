package net.rim.device.apps.internal.vad;

final class VADApplication$VADCleanupRunnable implements Runnable {
   private final VADApplication this$0;

   VADApplication$VADCleanupRunnable(VADApplication _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      try {
         if (this.this$0._manager != null && this.this$0._manager.getSession() != null) {
            this.this$0._manager.getSession().release();
            return;
         }
      } finally {
         return;
      }
   }
}
