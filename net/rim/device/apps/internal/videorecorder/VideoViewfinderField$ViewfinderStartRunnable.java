package net.rim.device.apps.internal.videorecorder;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.internal.camera.Camera;

final class VideoViewfinderField$ViewfinderStartRunnable implements Runnable {
   private final VideoViewfinderField this$0;

   private VideoViewfinderField$ViewfinderStartRunnable(VideoViewfinderField _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      boolean isOnTop = false;
      Screen screen = this.this$0.getScreen();
      if (screen != null) {
         isOnTop = !screen.isObscured() && screen.isUiEngineAttached() && screen.isVisible();
      }

      if (isOnTop && this.this$0._vfState == 3) {
         XYRect xtnt = (XYRect)(new Object(this.this$0.getExtent()));
         this.this$0.getManager().transformToScreen(xtnt);

         try {
            this.this$0._vfHandle = Camera.openVideoViewfinder(xtnt.x, xtnt.y, xtnt.width, xtnt.height, xtnt.width, xtnt.height, 7, this.this$0._videoCodec);
            this.this$0.setVfState(1);
         } finally {
            if (this.this$0._attemptCounter < 5) {
               this.this$0.setVfState(0);
               this.this$0.vfStart(this.this$0._attemptCounter + 1);
               return;
            } else {
               this.this$0.setVfState(4);
               if (screen != null) {
                  screen.close();
                  return;
               } else {
                  return;
               }
            }
         }
      } else {
         this.this$0.setVfState(0);
      }
   }

   VideoViewfinderField$ViewfinderStartRunnable(VideoViewfinderField x0, VideoViewfinderField$1 x1) {
      this(x0);
   }
}
