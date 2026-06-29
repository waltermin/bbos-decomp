package net.rim.device.apps.internal.explorer.content;

final class RenderScreen$1 implements Runnable {
   private final RenderScreen this$0;

   RenderScreen$1(RenderScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0.getDelegate().replace(this.this$0._field, this.this$0.createRenderErrorField());
      this.this$0._app.finish(6);
   }
}
