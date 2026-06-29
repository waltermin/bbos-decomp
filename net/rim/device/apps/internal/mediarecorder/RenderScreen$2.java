package net.rim.device.apps.internal.mediarecorder;

class RenderScreen$2 extends Thread {
   private final Runnable val$runnable;
   private final RenderScreen this$0;

   RenderScreen$2(RenderScreen _1, Runnable _2) {
      this.this$0 = _1;
      this.val$runnable = _2;
   }

   @Override
   public void run() {
      this.val$runnable.run();
   }
}
