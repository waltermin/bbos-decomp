package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;

class SSMessageDialog extends Dialog implements PhoneEventListener {
   SSMessageDialog(String msg, String iconName) {
      super(msg, null, null, 0, null, 33554432);
      this.setIcon(ThemeManager.getThemeAwareImage(iconName));
      VoiceServices.addPhoneEventListener(this);
   }

   @Override
   protected boolean trackwheelClick(int status, int time) {
      this.close();
      return true;
   }

   @Override
   public boolean dispatchKeyEvent(int event, char key, int keycode, int time) {
      switch (Keypad.key(keycode)) {
         case 16:
            return super.dispatchKeyEvent(event, key, keycode, time);
         case 18:
         default:
            this.close();
         case 17:
         case 19:
            return true;
      }
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      this.close();
      return true;
   }

   @Override
   public void phoneEventNotify(int eventId, int param1, Object param2) {
      if (eventId == 1000) {
         this.close();
      }
   }

   @Override
   public void close() {
      VoiceServices.removePhoneEventListener(this);
      super.close();
   }
}
