package net.rim.device.apps.internal.browser.pme;

final class ZoomPanScreen$1 implements Runnable {
   private final ZoomPanScreen this$0;

   ZoomPanScreen$1(ZoomPanScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.updatePanZoom();
      this.this$0.updateFO();
      this.this$0.updateField();
      this.this$0.updateScreen();
      this.this$0.invalidate();
   }
}
