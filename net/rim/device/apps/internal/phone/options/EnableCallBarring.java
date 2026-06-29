package net.rim.device.apps.internal.phone.options;

import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class EnableCallBarring extends SSRequest {
   private int _type;
   private String _password;
   private boolean _enable;

   EnableCallBarring(int type, String password, boolean enable) {
      this._type = type;
      this._enable = enable;
      this._password = password;
   }

   @Override
   protected final synchronized void runTask() {
      if (this._enable) {
         VoiceServices.activateCallBarring(this._type, this._password);
      } else {
         VoiceServices.deactivateCallBarring(this._type, this._password);
      }

      this.waitForNetworkResponse();
   }

   @Override
   protected final String getSSErrorString(int eventId, int reason) {
      String errorString = null;
      if (eventId != 5004 && (eventId != 5001 || reason != 6 && reason != 26)) {
         if (eventId == 5001) {
            switch (reason) {
               case 1:
                  return PhoneResources.getString(6112);
               case 12:
                  return PhoneResources.getString(9);
               default:
                  errorString = PhoneResources.getString(6122);
            }
         }

         return errorString;
      } else {
         return CommonResources.getString(6006);
      }
   }
}
