package net.rim.device.apps.internal.lbs;

final class MapField$10 extends LBSMenuItem {
   private final MapField this$0;

   MapField$10(MapField this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   public final boolean isVisible() {
      return this.this$0._currentLocations.hasLocations() || this.this$0.getRoute() != null;
   }

   @Override
   public final void run() {
      this.this$0._currentLocations.clear();
      this.this$0._currentLocations.clearRoutes();
      this.this$0._lastUsedScreen = null;
      this.this$0._directionsListScreen = null;
      this.this$0._locationsListScreen = null;
      this.this$0.update(true);
   }
}
