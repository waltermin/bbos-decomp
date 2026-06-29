package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.internal.crypto.fips.SelfTests;

final class SystemOnOffManager$1 implements Runnable {
   private final SystemOnOffManager this$0;

   SystemOnOffManager$1(SystemOnOffManager _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      new SelfTests(true).start();
   }
}
