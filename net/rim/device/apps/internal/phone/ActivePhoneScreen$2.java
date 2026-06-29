package net.rim.device.apps.internal.phone;

import net.rim.device.internal.system.AudioInternal;

final class ActivePhoneScreen$2 implements Runnable {
   private final ActivePhoneScreen this$0;

   ActivePhoneScreen$2(ActivePhoneScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      AudioInternal.stopTone();
   }
}
