package net.rim.device.cldc.io.file;

final class PosixFileOutputStream$FileOutputStreamCleanupRunnable implements Runnable {
   private final PosixFileOutputStream this$0;

   PosixFileOutputStream$FileOutputStreamCleanupRunnable(PosixFileOutputStream _1) {
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
