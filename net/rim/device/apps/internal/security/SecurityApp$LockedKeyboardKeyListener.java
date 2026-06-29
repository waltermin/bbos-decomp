package net.rim.device.apps.internal.security;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.Phone;
import net.rim.device.api.ui.Keypad;

final class SecurityApp$LockedKeyboardKeyListener implements KeyListener {
   private ApplicationManager _applicationManager;
   private Phone _phone;
   private StringBuffer _buffer;
   private final SecurityApp this$0;
   private static final int MAX_EMERGENCY_NUMBER_LENGTH = 6;

   SecurityApp$LockedKeyboardKeyListener(SecurityApp _1) {
      this.this$0 = _1;
      this._applicationManager = ApplicationManager.getApplicationManager();
      this._phone = Phone.getInstance();
      this._buffer = (StringBuffer)(new Object());
   }

   @Override
   public final boolean keyChar(char key, int status, int time) {
      return false;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      this.this$0._turnBacklightOff = false;
      if (this._applicationManager.isSystemLocked() && Application.getApplication().isForeground()) {
         char key = Keypad.map(keycode);
         if (!Character.isDigit(key) && key != 27 && key != '\n') {
            key = Keypad.getAltedChar(key);
         }

         if (Character.isDigit(key)) {
            this._buffer.append(key);
            int length = this._buffer.length();
            String number = this._buffer.toString();

            for (int i = 2; i < 6 && i <= length; i++) {
               if (this._phone.isEmergencyNumber(number.substring(length - i))) {
                  new SecurityApp$SendForEmergencyCallDialog(this.this$0).show();
                  this._buffer.setLength(0);
                  break;
               }
            }
         } else {
            this._buffer.setLength(0);
         }

         int length = this._buffer.length();
         if (length > 6) {
            this._buffer.delete(0, length - 6);
         }
      }

      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      if (this.this$0._turnBacklightOff && Keypad.key(keycode) == 20) {
         Application app = Application.getApplication();
         app.invokeLater(new SecurityApp$LockedKeyboardKeyListener$1(this), 800, false);
      }

      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      return false;
   }
}
