package net.rim.device.apps.internal.browser.plugin.media.field;

final class MediaBrowserField$3 extends Thread {
   private final MediaBrowserField val$mbf;
   private final MediaBrowserField this$0;

   MediaBrowserField$3(MediaBrowserField _1, MediaBrowserField _2) {
      this.this$0 = _1;
      this.val$mbf = _2;
   }

   @Override
   public final void run() {
      synchronized (this.val$mbf) {
         if (!this.this$0._isAudio && !this.this$0._visible) {
            this.this$0._screenManager.setOverlayText(-1, 0, 0);
         } else {
            try {
               if (this.this$0._player != null) {
                  this.this$0._player.start();
                  this.this$0._wasPlaying = false;
                  this.this$0._wasPlayingOnObscured = false;
                  this.this$0._queuedPlayback = false;
                  this.this$0.showPause();
               }
            } finally {
               return;
            }
         }
      }
   }
}
