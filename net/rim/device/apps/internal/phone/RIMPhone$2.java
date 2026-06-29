package net.rim.device.apps.internal.phone;

import net.rim.device.apps.internal.phone.api.PhoneUtilities;

final class RIMPhone$2 implements Runnable {
   private final RIMPhone this$0;

   RIMPhone$2(RIMPhone _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      boolean waiting = PhoneUtilities.getPrivateFlag(this.this$0._incomingCallContext, 23);
      if (this.this$0._incomingCallContext != null) {
         this.this$0.startRepeatingNotification(waiting);
      }
   }
}
