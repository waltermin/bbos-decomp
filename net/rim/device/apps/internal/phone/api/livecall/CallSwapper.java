package net.rim.device.apps.internal.phone.api.livecall;

import net.rim.device.apps.api.phone.VoiceServices;

class CallSwapper extends CallTask {
   private boolean _activeCallWasHeld;
   private boolean _heldCallWasResumed;

   public CallSwapper() {
   }

   @Override
   protected boolean isFinished() {
      return this._activeCallWasHeld && this._heldCallWasResumed;
   }

   @Override
   protected void execute() {
      VoiceServices.swapCalls();
   }

   @Override
   public void onPhoneEvent(int eventId, int callId, Object context) {
      switch (eventId) {
         case 1003:
         default:
            this._activeCallWasHeld = true;
            return;
         case 1004:
            this._heldCallWasResumed = true;
         case 1002:
      }
   }
}
