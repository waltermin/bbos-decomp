package net.rim.device.apps.internal.security;

import net.rim.device.api.system.Backlight;

final class SecurityApp$LockedKeyboardKeyListener$1 implements Runnable {
   private final SecurityApp$LockedKeyboardKeyListener this$1;

   SecurityApp$LockedKeyboardKeyListener$1(SecurityApp$LockedKeyboardKeyListener _1) {
      this.this$1 = _1;
   }

   @Override
   public final void run() {
      if (this.this$1.this$0._turnBacklightOff) {
         this.this$1.this$0._turnBacklightOff = false;
         Backlight.enable(false);
      }
   }
}
