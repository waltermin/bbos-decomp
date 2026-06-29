package net.rim.device.apps.internal.lbs;

final class MapScreen$13 extends LBSMenuItem {
   private final MapScreen this$0;

   MapScreen$13(MapScreen this$0, int x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = this$0;
   }

   @Override
   final boolean isVisible() {
      return LBSOptions.getBoolean(4717295063260546653L, false);
   }

   @Override
   public final void run() {
      this.this$0.getPOIs(this);
   }
}
