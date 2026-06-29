package net.rim.device.apps.internal.lbs;

final class MapScreen$17 extends LBSMenuItem {
   private final MapScreen this$0;

   MapScreen$17(MapScreen this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      Location focused = this.this$0._mapField.getFocusLocation();
      if (focused != null && this.this$0._mapField._showCaption) {
         EmailUtilities.emailLocation(focused, this.this$0._mapField.getZoom());
      } else if (this.this$0._tracking && this.this$0._gpsLock) {
         EmailUtilities.emailLocation(this.this$0._mapField._marker._latitude, this.this$0._mapField._marker._longitude, this.this$0._mapField.getZoom(), true);
      } else {
         EmailUtilities.emailLocation(this.this$0._mapField.getLatitude(), this.this$0._mapField.getLongitude(), this.this$0._mapField.getZoom(), false);
      }
   }
}
