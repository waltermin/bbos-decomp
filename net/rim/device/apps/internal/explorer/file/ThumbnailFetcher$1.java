package net.rim.device.apps.internal.explorer.file;

final class ThumbnailFetcher$1 implements Runnable {
   private final ThumbnailFetcher this$0;

   ThumbnailFetcher$1(ThumbnailFetcher _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      synchronized (this.this$0._gate) {
         this.this$0._gate.notify();
      }
   }
}
