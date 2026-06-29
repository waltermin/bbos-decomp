package net.rim.device.apps.internal.lbs.maplet;

final class MapletFooterProgressField$RepaintMe implements Runnable {
   private final MapletFooterProgressField this$0;

   MapletFooterProgressField$RepaintMe(MapletFooterProgressField this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      this.this$0._parent.invalidateProgressField();
   }
}
