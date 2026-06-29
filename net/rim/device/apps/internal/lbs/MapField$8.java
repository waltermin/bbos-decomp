package net.rim.device.apps.internal.lbs;

final class MapField$8 extends LBSMenuItem {
   private final MapField this$0;

   MapField$8(MapField this$0, int x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public final boolean isVisible() {
      if (this.this$0._currentLocations._count > 0 && !(this.this$0._lastUsedScreen instanceof DirectionsListScreen)) {
         Location location = this.this$0.getFocusLocation();
         if (location != null) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final void run() {
      Location location = this.this$0.getFocusLocation();
      if (location != null) {
         new LocationDialog(location, this.this$0._currentLegalNotices).doModal();
      }
   }
}
