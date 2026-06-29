package net.rim.device.apps.internal.browser.plugin.media.field;

final class MediaBrowserField$1 implements Runnable {
   private final MediaBrowserField this$0;

   MediaBrowserField$1(MediaBrowserField _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.destroy();
   }
}
