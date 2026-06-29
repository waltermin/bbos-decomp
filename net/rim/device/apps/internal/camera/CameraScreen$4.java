package net.rim.device.apps.internal.camera;

import net.rim.device.apps.api.ui.PopupStatus;

final class CameraScreen$4 implements Runnable {
   private final CameraScreen this$0;

   CameraScreen$4(CameraScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      PopupStatus.show(CameraMain._rb.getString(29), 5000);
   }
}
