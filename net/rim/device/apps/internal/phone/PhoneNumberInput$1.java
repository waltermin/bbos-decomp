package net.rim.device.apps.internal.phone;

import net.rim.device.apps.internal.phone.api.PhoneLogger;
import net.rim.device.apps.internal.phone.api.verbs.DialVerb;

final class PhoneNumberInput$1 implements Runnable {
   private final String val$number;
   private final PhoneNumberInput this$0;

   PhoneNumberInput$1(PhoneNumberInput _1, String _2) {
      this.this$0 = _1;
      this.val$number = _2;
   }

   @Override
   public final void run() {
      PhoneLogger.log("startcall gsm230");
      new DialVerb(this.val$number, null).invoke(null);
   }
}
