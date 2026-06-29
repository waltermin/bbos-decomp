package net.rim.device.apps.internal.camera;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.internal.camera.Camera;
import net.rim.device.internal.system.ActiveMediaObservable;

final class ViewfinderField$ViewfinderStartRunnable implements Runnable {
   private final ViewfinderField this$0;

   private ViewfinderField$ViewfinderStartRunnable(ViewfinderField _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      boolean isOnTop = false;
      Screen screen = this.this$0.getScreen();
      if (screen != null) {
         isOnTop = !screen.isObscured() && screen.isUiEngineAttached();
      }

      if (isOnTop && this.this$0._vfState == 3) {
         XYRect xtnt = new XYRect(this.this$0.getExtent());
         this.this$0.getManager().transformToScreen(xtnt);

         try {
            this.this$0._vfHandle = Camera.openViewfinder(xtnt.x, xtnt.y, xtnt.width, xtnt.height);
            Camera.setZoomLevel(this.this$0._zoomLevels[this.this$0._zoomIndex]);
            this.this$0.setVfState(1);
         } finally {
            if (this.this$0._attemptCounter < 5) {
               this.this$0.setVfState(0);
               if (this.this$0._attemptCounter == 0) {
                  this.this$0._forceMedia = true;
                  ActiveMediaObservable.setInactive(this.this$0);
               }

               this.this$0.vfStart(this.this$0._attemptCounter + 1);
               return;
            } else {
               this.this$0.setVfState(4);
               ActiveMediaObservable.setInactive(this.this$0);
               Dialog.alert(CameraMain._rb.getString(28));
               if (screen != null) {
                  screen.close();
                  return;
               } else {
                  return;
               }
            }
         }
      } else {
         ActiveMediaObservable.setInactive(this.this$0);
         this.this$0.setVfState(0);
      }
   }

   ViewfinderField$ViewfinderStartRunnable(ViewfinderField x0, ViewfinderField$1 x1) {
      this(x0);
   }
}
