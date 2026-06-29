package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.apps.internal.phone.api.PhoneLogger;

class DialVerb$3 implements Runnable {
   private final DialVerb this$0;

   DialVerb$3(DialVerb _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      String logString = null;
      if (this.this$0._sendKeyInvoked) {
         logString = "dvSEND";
      } else if (this.this$0._invokedFromHyperlink) {
         logString = "dvHL";
      } else if (this.this$0._insideAddressCard) {
         logString = "dvAC";
      } else if (this.this$0._callLogInMsgList) {
         logString = "dvCLML";
      } else if (this.this$0._simPhoneBookInvoked) {
         logString = "dvSIMPB";
      } else if (this.this$0._speedDialInvoked) {
         logString = "dvSPD";
      }

      if (logString != null) {
         PhoneLogger.log(logString);
      }
   }
}
