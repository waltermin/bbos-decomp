package net.rim.device.apps.internal.lbs.maplet;

final class MapletMapField$1 implements Runnable {
   private final MapletMapField this$0;

   MapletMapField$1(MapletMapField this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      this.this$0.update(true);
   }
}
