package net.rim.device.apps.internal.camera;

import net.rim.device.apps.api.ui.PopupStatus;

final class CameraMain$3 implements Runnable {
   private final CameraMain this$0;

   CameraMain$3(CameraMain _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      PopupStatus.show(CameraMain._rb.getString(27), 5000);
   }
}
