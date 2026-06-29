package net.rim.device.apps.api.quickcontact;

import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.CharacterUtilities;

public final class ClickAndHoldKeyListener {
   private ClickAndHoldKeyListener$Callback _callback;
   private int _previousRepeatedKeycode;

   public ClickAndHoldKeyListener(ClickAndHoldKeyListener$Callback callback) {
      this._callback = callback;
   }

   public final boolean keyChar(char key, int status, int time) {
      this._previousRepeatedKeycode = 0;
      char lower = CharacterUtilities.toLowerCase(key, 1701707776);
      return lower >= 'a' && lower <= 'z';
   }

   public final boolean keyUp(int keycode, int time) {
      if (this._previousRepeatedKeycode != 0) {
         this._previousRepeatedKeycode = 0;
         return true;
      } else {
         return false;
      }
   }

   public final boolean keyRepeat(int keycode, int time) {
      if (Phone.getInstance().isActive() && RadioInfo.getNetworkType() == 4) {
         return false;
      }

      if (QuickContactList.isValidQuickContactKey(keycode)) {
         if (keycode != this._previousRepeatedKeycode) {
            this._previousRepeatedKeycode = keycode;
            if (this._callback != null) {
               this._callback.onKeyClickedAndHeld(keycode);
            }
         }

         return true;
      } else {
         if (Keypad.hasSendEndKeys()) {
            char key = Keypad.map(keycode);
            if (key == '0' || Keypad.getAltedChar(key) == '0') {
               if (this._callback != null) {
                  this._callback.onKeyClickedAndHeld(key);
               }

               return true;
            }
         }

         return false;
      }
   }
}
