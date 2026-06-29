package net.rim.device.apps.internal.camera;

import net.rim.device.apps.api.ui.PopupStatus;

final class CameraScreen$1 implements Runnable {
   private final CameraScreen this$0;

   CameraScreen$1(CameraScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      PopupStatus.show(CameraMain._rb.getString(27), 5000);
   }
}
