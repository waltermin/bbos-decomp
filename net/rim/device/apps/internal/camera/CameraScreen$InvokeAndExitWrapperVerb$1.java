package net.rim.device.apps.internal.camera;

final class CameraScreen$InvokeAndExitWrapperVerb$1 implements Runnable {
   private final CameraScreen$InvokeAndExitWrapperVerb this$1;

   CameraScreen$InvokeAndExitWrapperVerb$1(CameraScreen$InvokeAndExitWrapperVerb _1) {
      this.this$1 = _1;
   }

   @Override
   public final void run() {
      this.this$1.this$0.close();
   }
}
