package net.rim.device.apps.internal.lbs;

import net.rim.device.apps.internal.lbs.finder.FindLocationScreen;
import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class MapScreen$12 extends LBSMenuItem {
   private final MapScreen this$0;

   MapScreen$12(MapScreen this$0, int x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      FindLocationScreen finder = new FindLocationScreen(this.this$0._gpsLocationData, this.this$0._mapField._zoom);
      Object returnValue = finder.go();
      if (returnValue instanceof Location) {
         this.this$0._mapField.zoom(true);
      } else {
         if (this.this$0._following && this.this$0._gpsLock) {
            this.this$0._following = false;
            this.this$0._mapField.setAutoPan(false);
            this.this$0._mapField.showHintLabel(LBSResources.getString(316));
         }
      }
   }
}
