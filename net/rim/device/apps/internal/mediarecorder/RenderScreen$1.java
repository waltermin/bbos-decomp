package net.rim.device.apps.internal.mediarecorder;

class RenderScreen$1 extends Thread {
   private final RenderScreen this$0;

   RenderScreen$1(RenderScreen _1) {
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      try {
         this.this$0._content.finishLoading();
      } catch (Throwable var3) {
         e.printStackTrace();
         return;
      }
   }
}
