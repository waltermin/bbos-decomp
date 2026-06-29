package net.rim.device.apps.internal.camera;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;

final class CameraMain$1 implements Runnable {
   private final CameraMain this$0;

   CameraMain$1(CameraMain _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      Dialog d = (Dialog)(new Object(0, CameraMain._rb.getString(22), 0, null, 0));
      d.setIcon(ThemeManager.getThemeAwareImage("dialog_exclamation"));
      this.this$0.pushGlobalScreen(d, 50, 1);
      System.exit(0);
   }
}
