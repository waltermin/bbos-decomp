package net.rim.device.apps.internal.explorer.Media;

final class MediaLibraryScreen$ListUpdateRunnable implements Runnable {
   private final MediaLibraryScreen this$0;

   MediaLibraryScreen$ListUpdateRunnable(MediaLibraryScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      synchronized (this.this$0.ITEMS) {
         this.this$0._library.setSize(this.this$0.ITEMS.length);
         this.this$0.invalidate();
      }
   }
}
