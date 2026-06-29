package net.rim.device.apps.internal.camera;

final class CameraMain$CameraCleanupRunnable implements Runnable {
   private final CameraMain this$0;

   private CameraMain$CameraCleanupRunnable(CameraMain _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      try {
         if (this.this$0._screen != null) {
            this.this$0._screen.suspendViewfinder(true);
            return;
         }
      } finally {
         return;
      }
   }

   CameraMain$CameraCleanupRunnable(CameraMain x0, CameraMain$1 x1) {
      this(x0);
   }
}
