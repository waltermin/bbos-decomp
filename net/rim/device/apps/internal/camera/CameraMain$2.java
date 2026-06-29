package net.rim.device.apps.internal.camera;

final class CameraMain$2 implements Runnable {
   private final CameraMain this$0;

   CameraMain$2(CameraMain _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0._mediaDialog != null) {
         this.this$0.pushScreen(this.this$0._mediaDialog);
      }
   }
}
