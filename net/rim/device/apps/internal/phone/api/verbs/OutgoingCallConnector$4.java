package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.options.SSRequestStatusDialog;

class OutgoingCallConnector$4 extends SSRequestStatusDialog {
   private final OutgoingCallConnector this$0;

   OutgoingCallConnector$4(OutgoingCallConnector _1, boolean x0, boolean x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   protected void onEvent(int eventId, int param, Object context) {
      switch (eventId) {
         case 5000:
         case 5001:
         case 5002:
         case 5003:
         case 5004:
         case 5007:
         case 5008:
         case 5100:
            this.close(eventId);
            break;
         case 10100:
            if (context instanceof Object) {
               int newCallID = context;
               if (newCallID > 0) {
                  VoiceServices.broadcastEvent(1100, newCallID, null);
               }

               this.close(eventId);
               return;
            }
      }
   }
}
