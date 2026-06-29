package net.rim.device.apps.api.ui;

class SecurityDialog$USBPasswordRedirectDialog$1 implements Runnable {
   private final SecurityDialog$USBPasswordRedirectDialog this$0;

   SecurityDialog$USBPasswordRedirectDialog$1(SecurityDialog$USBPasswordRedirectDialog _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.close(1);
   }
}
