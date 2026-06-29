package net.rim.device.cldc.io.file;

final class PosixFileInputStream$FileInputStreamCleanupRunnable implements Runnable {
   private final PosixFileInputStream this$0;

   PosixFileInputStream$FileInputStreamCleanupRunnable(PosixFileInputStream _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      try {
         this.this$0.closeInternal(false);
      } finally {
         return;
      }
   }
}
