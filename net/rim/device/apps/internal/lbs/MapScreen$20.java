package net.rim.device.apps.internal.lbs;

final class MapScreen$20 extends LBSMenuItem {
   private final MapScreen this$0;

   MapScreen$20(MapScreen this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   public final boolean isVisible() {
      return true;
   }

   @Override
   public final void run() {
      Location location = this.this$0._mapField.getRoute();
      boolean showLocation = false;
      if (location == null) {
         location = this.this$0._mapField.getFocusLocation();
      }

      if (!(location instanceof Route) && (location == null || !this.this$0._mapField.isMarkerBubbleShowing())) {
         location = new Location(this.this$0._mapField._latitude, this.this$0._mapField._longitude, this.this$0._mapField._zoom);
         showLocation = true;
      }

      LocationEditDialog dialog = new LocationEditDialog(location);
      if (dialog.doModal() && showLocation) {
         this.this$0.showLocation(location);
      }
   }
}
