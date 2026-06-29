package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.system.Application;

class PhoneCallCollection$1OrganizingCallsDialog$1 implements Runnable {
   private final PhoneCallCollection$1OrganizingCallsDialog this$1;

   PhoneCallCollection$1OrganizingCallsDialog$1(PhoneCallCollection$1OrganizingCallsDialog _1) {
      this.this$1 = _1;
   }

   @Override
   public void run() {
      this.this$1.val$updateRunnable.run();
      synchronized (Application.getEventLock()) {
         this.this$1.close();
      }
   }
}
