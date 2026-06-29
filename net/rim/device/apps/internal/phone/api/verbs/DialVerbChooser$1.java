package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.ui.Keypad;
import net.rim.device.apps.api.framework.verb.PopupVerbWrapperSelectionDialog;

class DialVerbChooser$1 extends PopupVerbWrapperSelectionDialog {
   private final DialVerbChooser this$0;

   DialVerbChooser$1(DialVerbChooser _1, String x0, String[] x1, int x2, boolean x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public boolean keyDown(int keycode, int time) {
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
