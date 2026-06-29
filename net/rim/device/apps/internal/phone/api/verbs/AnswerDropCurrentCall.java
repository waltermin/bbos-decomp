package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;

final class AnswerDropCurrentCall extends AnswerDropCallConnector {
   public AnswerDropCurrentCall(int callId, Object connectionContext) {
      super(callId, connectionContext);
   }

   @Override
   protected final void dropCalls() {
      Object call = VoiceServices.getVoiceApplication().getCurrentCall();
      if (!(call instanceof LiveCall)) {
         this.answerCall();
      } else {
         LiveCall liveCall = (LiveCall)call;
         ContextObject.put(super._connectionContext, 1714907342028355590L, this);
         PhoneUtilities.setPrivateFlag(super._connectionContext, 56);
         super._callIds = liveCall.getCallIds();
         super._numCalls = super._callIds.length;
         liveCall.end(super._connectionContext);
      }
   }
}
