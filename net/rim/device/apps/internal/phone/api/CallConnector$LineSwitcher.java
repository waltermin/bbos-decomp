package net.rim.device.apps.internal.phone.api;

import net.rim.device.api.system.Phone;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;

class CallConnector$LineSwitcher implements PhoneEventListener {
   int _originalLine = 1;

   public CallConnector$LineSwitcher(int line) {
      this._originalLine = line;
      VoiceServices.addPhoneEventListener(this);
   }

   @Override
   public void phoneEventNotify(int eventId, int callId, Object context) {
      switch (eventId) {
         case 1002:
         case 1006:
         case 3004:
            if (!Phone.getInstance().isActive()) {
               VoiceServices.removePhoneEventListener(this);
               PhoneUtilities.setCurrentLine(this._originalLine);
            }
      }
   }
}
