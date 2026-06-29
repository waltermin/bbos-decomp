package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.Phone;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;

final class ActivePhoneScreen$AudioSourceRemover implements PhoneEventListener {
   private int _callIdToWaitFor;

   final void removeSource() {
      int callId = Phone.getInstance().getIncomingCallId();
      if (callId != 0) {
         this._callIdToWaitFor = callId;
         VoiceServices.addPhoneEventListener(this);
      } else {
         this.removeNow();
      }
   }

   private final void removeNow() {
      AudioRouter ar = AudioRouter.getInstance();
      ar.removeSource(0);
      ar.getAudioPathControl(0).resetAudioPath();
      System.out.println("Phone: audio source off");
   }

   @Override
   public final void phoneEventNotify(int eventId, int callId, Object context) {
      switch (eventId) {
         case 1001:
            VoiceServices.removePhoneEventListener(this);
            break;
         case 1002:
         case 1006:
            if (callId == this._callIdToWaitFor) {
               this.removeNow();
               VoiceServices.removePhoneEventListener(this);
               return;
            }
      }
   }
}
