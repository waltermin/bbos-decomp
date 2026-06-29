package net.rim.device.apps.internal.lbs;

final class MapScreen$8 extends LBSMenuItem {
   private final MapScreen this$0;

   MapScreen$8(MapScreen this$0, int x0, int x1) {
      super(x0, x1);
      this.this$0 = this$0;
   }

   @Override
   final boolean isVisible() {
      return this.this$0._tracking || this.this$0._mapField.getRotation() != 0;
   }

   @Override
   public final void run() {
      this.this$0.setTrackUp(!this.this$0._trackUp);
      this.this$0._autoTrackUp = !this.this$0._autoTrackUp;
      LBSOptions.setBoolean(-4064050259441269877L, !LBSOptions.getBoolean(-4064050259441269877L, true));
   }

   @Override
   final int getResourceId() {
      return this.this$0._trackUp ? 87 : 86;
   }
}
