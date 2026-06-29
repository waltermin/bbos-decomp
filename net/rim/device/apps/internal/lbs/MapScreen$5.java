package net.rim.device.apps.internal.lbs;

final class MapScreen$5 extends LBSMenuItem {
   private final MapScreen this$0;

   MapScreen$5(MapScreen this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   public final boolean isVisible() {
      return LBSOptions.POINTER_MODE && !this.this$0._mapField._pointerMode;
   }

   @Override
   public final void run() {
      this.this$0._mapField.pointerMode(true);
   }
}
