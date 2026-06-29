package net.rim.device.apps.internal.browser.plugin.media.field;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.internal.media.MediaOptionsRegistry;

final class MediaBrowserField$FullscreenRunnable implements Runnable {
   private int _id;
   private boolean _halted;
   private final MediaBrowserField this$0;

   final void halt() {
      synchronized (this) {
         if (this._id != -1) {
            UiApplication.getUiApplication().cancelInvokeLater(this._id);
            this._id = -1;
            this._halted = true;
         }
      }
   }

   final void queue() {
      this.queue(3000);
   }

   final void queue(long waitTime) {
      synchronized (this) {
         boolean fullscreen = MediaOptionsRegistry.getInstance().getBoolean(-4212305096992551720L);
         if (fullscreen && !this.this$0.isAudio() && !this.this$0._fullscreen) {
            if (this._id == -1) {
               this._id = UiApplication.getUiApplication().invokeLater(this, waitTime, false);
               this._halted = false;
            } else {
               this.reset(waitTime);
            }
         }
      }
   }

   final void reset() {
      this.reset(3000);
   }

   final void reset(long waitTime) {
      synchronized (this) {
         if (this._id != -1) {
            this.halt();
            this.queue(waitTime);
         }
      }
   }

   @Override
   public final void run() {
      synchronized (this) {
         if (!this._halted && this.this$0._player != null && this.this$0._player.getState() == 400) {
            this.this$0.toggleFullscreen();
            this._id = -1;
         }
      }
   }

   MediaBrowserField$FullscreenRunnable(MediaBrowserField _1) {
      this.this$0 = _1;
      this._id = -1;
   }
}
