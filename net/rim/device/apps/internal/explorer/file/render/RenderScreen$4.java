package net.rim.device.apps.internal.explorer.file.render;

final class RenderScreen$4 extends Thread {
   private final RenderScreen this$0;

   RenderScreen$4(RenderScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.loadContent();
   }
}
