package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.Alert;

final class RIMPhone$1 implements Runnable {
   private final RIMPhone this$0;

   RIMPhone$1(RIMPhone _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      Alert.startVibrate(2000);
   }
}
