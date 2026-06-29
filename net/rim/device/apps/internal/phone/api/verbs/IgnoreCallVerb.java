package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.system.Phone;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public class IgnoreCallVerb extends Verb {
   private int _callId;

   public IgnoreCallVerb(int callId) {
      super(0, PhoneResources.getResourceBundle(), 482);
      this._callId = callId;
   }

   @Override
   public Object invoke(Object context) {
      VoiceServices.broadcastEvent(2020, this._callId, null);

      try {
         if (!this.isCallAlreadyAnswered()) {
            PhoneUtilities.rejectCall(this._callId);
            return null;
         }

         Phone.getInstance().stopCall(this._callId);
      } finally {
         return null;
      }

      return null;
   }

   private boolean isCallAlreadyAnswered() {
      return PhoneUtilities.gsmTypeNetwork() ? Phone.getInstance().getCallState(this._callId) != 2 : false;
   }
}
