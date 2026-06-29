package net.rim.device.apps.internal.activation;

import net.rim.device.internal.system.Security;

final class ActivationScreen$1 implements Runnable {
   private final ActivationScreen this$0;

   ActivationScreen$1(ActivationScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      Security.getInstance().deviceUnderAttack();
   }
}
