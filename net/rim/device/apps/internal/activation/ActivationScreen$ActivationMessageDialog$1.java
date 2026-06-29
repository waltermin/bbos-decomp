package net.rim.device.apps.internal.activation;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.Dialog;

final class ActivationScreen$ActivationMessageDialog$1 extends Dialog {
   private final ActivationScreen$ActivationMessageDialog this$1;

   ActivationScreen$ActivationMessageDialog$1(ActivationScreen$ActivationMessageDialog _1, int x0, String x1, int x2, Bitmap x3, long x4) {
      super(x0, x1, x2, x3, x4);
      this.this$1 = _1;
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      if (key == 18) {
         this.close();
         return true;
      } else {
         return super.keyDown(keycode, time);
      }
   }
}
