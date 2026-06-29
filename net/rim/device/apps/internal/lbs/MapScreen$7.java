package net.rim.device.apps.internal.lbs;

final class MapScreen$7 extends LBSMenuItem {
   private final MapScreen this$0;

   MapScreen$7(MapScreen this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   final boolean isVisible() {
      return !this.this$0._mapField._zoomMode;
   }

   @Override
   public final void run() {
      this.this$0._mapField.zoom(true);
   }
}
