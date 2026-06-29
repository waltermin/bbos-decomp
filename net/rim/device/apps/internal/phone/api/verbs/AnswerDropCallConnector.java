package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.api.system.Phone;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.DroppedCallListener;

class AnswerDropCallConnector implements DroppedCallListener {
   protected int _numCalls;
   private int _callIdToAnswer = Phone.getInstance().getIncomingCallId();
   protected int[] _callIds;
   protected Object _connectionContext;

   public AnswerDropCallConnector(int callId, Object connectionContext) {
      this._connectionContext = ContextObject.clone(connectionContext);
   }

   protected void dropCalls() {
      throw null;
   }

   public void answer() {
      this.dropCalls();
   }

   private boolean findCallId(int callId) {
      if (this._callIds != null) {
         for (int i = this._callIds.length - 1; i >= 0; i--) {
            if (this._callIds[i] == callId) {
               return true;
            }
         }
      }

      return false;
   }

   protected void answerCall() {
      if (VoiceServices.getCallState(this._callIdToAnswer) == 2) {
         VoiceServices.answerCall(this._callIdToAnswer);
         VoiceServices.broadcastEvent(1110, this._callIdToAnswer, this._connectionContext);
      } else {
         VoiceServices.broadcastEvent(1140, this._callIdToAnswer, this._connectionContext);
      }
   }

   private void callEnded(int callId) {
      if (this.findCallId(callId)) {
         this._numCalls--;
         if (this._numCalls == 0) {
            this.answerCall();
         }
      }
   }

   @Override
   public void callDisconnected(int callId) {
      this.callEnded(callId);
   }

   @Override
   public void callFailed(int callId) {
      this.callEnded(callId);
   }
}
