package net.rim.device.apps.internal.browser.plugin.media.field;

final class MediaBrowserField$2 extends Thread {
   private final Object val$lock;
   private final MediaBrowserField this$0;

   MediaBrowserField$2(MediaBrowserField _1, Object _2) {
      this.this$0 = _1;
      this.val$lock = _2;
   }

   @Override
   public final void run() {
      synchronized (this.val$lock) {
         label25:
         try {
            this.this$0._ds.connect();
            this.this$0._ds.start();
            this.this$0._screenManager.setOverlayText(899, 16777215, 0);
         } finally {
            break label25;
         }

         this.val$lock.notifyAll();
      }
   }
}
