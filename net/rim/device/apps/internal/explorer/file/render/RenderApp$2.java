package net.rim.device.apps.internal.explorer.file.render;

final class RenderApp$2 extends Thread {
   private final Runnable val$runnable;
   private final RenderApp this$0;

   RenderApp$2(RenderApp _1, Runnable _2) {
      this.this$0 = _1;
      this.val$runnable = _2;
   }

   @Override
   public final void run() {
      this.val$runnable.run();
   }
}
