package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.component.SimpleInputDialog;

final class UssdInputDialog extends SimpleInputDialog {
   private static final int MIN_USSD_INPUT_LENGTH = 1;
   private static final int MAX_USSD_INPUT_LENGTH = 229;

   UssdInputDialog(String msg) {
      super(0, msg, 1, 229, 33554432);
      BasicEditField ef = this.getEditField();
      if (ef != null) {
         ef.setLabel(PhoneResources.getString(6261));
      }
   }

   @Override
   protected final boolean keyDown(int keycode, int time) {
      switch (Keypad.key(keycode)) {
         case 16:
            return super.keyDown(keycode, time);
         case 17:
         default:
            this.accept();
            return true;
         case 18:
            this.cancel();
            return true;
      }
   }
}
