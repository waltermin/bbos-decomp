package net.rim.device.apps.internal.phone;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class ManageConferenceVerb extends Verb {
   private ConferenceCall _conferenceCall;

   public ManageConferenceVerb(int id, ConferenceCall conferenceCall) {
      super(70992, PhoneResources.getResourceBundle(), id);
      this._conferenceCall = conferenceCall;
   }

   @Override
   public final Object invoke(Object context) {
      new SelectCallDialog(this._conferenceCall, this).show();
      return null;
   }

   final void onCallSelected(LiveCall call) {
      switch (super._rbKey) {
         case 422:
         default:
            VoiceServices.removeCallFromConference(call.getCallId());
            return;
         case 423:
            call.end();
         case 421:
      }
   }
}
