package net.rim.device.api.applicationcontrol;

import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.ScreenUiEngineAttachedListener;

class ApplicationPermissionsManager$AppControlScreenClosedListener implements ScreenUiEngineAttachedListener {
   private final ApplicationPermissionsManager this$0;

   private ApplicationPermissionsManager$AppControlScreenClosedListener(ApplicationPermissionsManager _1) {
      this.this$0 = _1;
   }

   @Override
   public void onScreenUiEngineAttached(Screen applicationControlScreen, boolean attached) {
      if (!attached) {
         synchronized (this.this$0._closedSignal) {
            this.this$0._closedSignal.notifyAll();
         }
      }
   }

   ApplicationPermissionsManager$AppControlScreenClosedListener(ApplicationPermissionsManager x0, ApplicationPermissionsManager$1 x1) {
      this(x0);
   }
}
