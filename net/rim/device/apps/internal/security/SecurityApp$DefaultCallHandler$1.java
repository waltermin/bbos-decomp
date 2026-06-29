package net.rim.device.apps.internal.security;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.component.Dialog;

final class SecurityApp$DefaultCallHandler$1 extends Dialog {
   private final SecurityApp$DefaultCallHandler this$1;

   SecurityApp$DefaultCallHandler$1(SecurityApp$DefaultCallHandler _1, int x0, String x1, int x2, Bitmap x3, long x4) {
      super(x0, x1, x2, x3, x4);
      this.this$1 = _1;
   }

   @Override
   protected final void onHotkeySelected(char key) {
      String logString = "dlg hk slctd " + key;
      EventLogger.logEvent(-1148210079122251014L, logString.getBytes(), 0);
   }
}
