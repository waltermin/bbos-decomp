package net.rim.device.apps.internal.options.items;

final class ThemeOptionsScreen$2 implements Runnable {
   private final ThemeOptionsScreen this$0;

   ThemeOptionsScreen$2(ThemeOptionsScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      new ThemeOptionsScreen$ThumbnailLoaderThread(this.this$0).start();
   }
}
