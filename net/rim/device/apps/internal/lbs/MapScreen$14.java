package net.rim.device.apps.internal.lbs;

final class MapScreen$14 extends LBSMenuItem {
   private final MapScreen this$0;

   MapScreen$14(MapScreen this$0, int x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      this.this$0.getDirections();
   }

   @Override
   final int getResourceId() {
      return this.this$0._mapField._directionsListScreen == null ? 319 : 320;
   }
}
