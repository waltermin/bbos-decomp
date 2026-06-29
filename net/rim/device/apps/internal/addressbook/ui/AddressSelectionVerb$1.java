package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.Dialog;

final class AddressSelectionVerb$1 extends Dialog {
   private final AddressSelectionVerb this$0;

   AddressSelectionVerb$1(AddressSelectionVerb _1, String x0, Object[] x1, int[] x2, int x3, Bitmap x4, long x5) {
      super(x0, x1, x2, x3, x4, x5);
      this.this$0 = _1;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      int key = Keypad.key(keycode);
      switch (key) {
         case 17:
         default:
            this.select();
            return true;
         case 18:
            this.cancel();
         case 16:
            return super.keyDown(keycode, time);
      }
   }
}
