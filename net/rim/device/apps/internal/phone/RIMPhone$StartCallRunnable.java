package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;

final class RIMPhone$StartCallRunnable implements Runnable {
   private String _number;
   private int _callId;

   RIMPhone$StartCallRunnable(String number, int callId) {
      this._number = number;
      this._callId = callId;
   }

   @Override
   public final void run() {
      Object connectionParams = PhoneUtilities.getCallConnectionParameters(this._number, null, null, null);
      RIMGlobalMessagePoster.postGlobalEvent(-3502867315182341540L);
      VoiceServices.broadcastEvent(1100, this._callId, connectionParams);
   }
}
