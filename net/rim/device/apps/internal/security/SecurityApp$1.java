package net.rim.device.apps.internal.security;

final class SecurityApp$1 implements Runnable {
   private final SecurityApp this$0;

   SecurityApp$1(SecurityApp _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (!this.this$0._processPolicySetting) {
         this.this$0.processPolicySettings();
      }

      if (this.this$0._unlockOnStartup || SecurityApp.isSIMLocked()) {
         this.this$0._unlockOnStartup = false;
         this.this$0.unlock();
      }
   }
}
