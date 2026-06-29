package net.rim.device.apps.internal.lbs;

import net.rim.device.apps.internal.lbs.resources.LBSResources;

final class MapField$9 extends LBSMenuItem {
   private final MapField this$0;

   MapField$9(MapField this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   public final boolean isVisible() {
      return this.this$0.getFocusLocation() != null || this.this$0.getRoute() != null;
   }

   @Override
   public final void run() {
      Location location = this.this$0.getFocusLocation();
      if (this.this$0.getRoute() != null && this.this$0.getRoute()._decisions.getFocus() != null) {
         this.this$0._latitude = this.this$0.getRoute()._decisions.getFocus()._latitude;
         this.this$0._longitude = this.this$0.getRoute()._decisions.getFocus()._longitude;
         if (this.this$0._zoom > 2) {
            this.this$0.setZoom(2);
         }
      } else {
         if (location == null) {
            return;
         }

         this.this$0._latitude = location._latitude;
         this.this$0._longitude = location._longitude;
         if (this.this$0._zoom > 2) {
            this.this$0.setZoom(2);
         }
      }

      this.this$0.updateScreenPosition();
      this.this$0.update(true);
      if (this.this$0._screen._following && this.this$0._screen._gpsLock) {
         this.this$0._screen._following = false;
         this.this$0.showHintLabel(LBSResources.getString(316));
      }
   }
}
