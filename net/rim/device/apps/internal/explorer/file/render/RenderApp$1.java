package net.rim.device.apps.internal.explorer.file.render;

final class RenderApp$1 implements Runnable {
   private final RenderApp this$0;

   RenderApp$1(RenderApp _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._renderScreen.close();
   }
}
