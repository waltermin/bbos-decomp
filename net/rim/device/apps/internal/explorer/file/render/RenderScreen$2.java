package net.rim.device.apps.internal.explorer.file.render;

final class RenderScreen$2 implements Runnable {
   private final RenderScreen this$0;

   RenderScreen$2(RenderScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.getDelegate().replace(this.this$0._field, this.this$0.createRenderErrorField());
   }
}
