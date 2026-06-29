package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.internal.system.Security;

final class CDMAServiceFilter$2 implements Runnable {
   private final CDMAServiceFilter this$0;

   CDMAServiceFilter$2(CDMAServiceFilter _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      Security.getInstance().deviceUnderAttack();
   }
}
