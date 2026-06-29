package net.rim.device.apps.internal.lbs;

final class MapScreen$18 extends LBSMenuItem {
   private final MapScreen this$0;

   MapScreen$18(MapScreen this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   public final boolean isVisible() {
      return this.this$0._mapField.getRoute() != null;
   }

   @Override
   public final void run() {
      EmailUtilities.emailDirections(this.this$0._mapField.getRoute());
   }
}
