package net.rim.device.apps.internal.explorer.content;

final class RenderScreen$2 extends Thread {
   private final RenderScreen this$0;

   RenderScreen$2(RenderScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.loadContent();
   }
}
