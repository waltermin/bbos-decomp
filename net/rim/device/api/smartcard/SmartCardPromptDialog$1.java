package net.rim.device.api.smartcard;

class SmartCardPromptDialog$1 implements Runnable {
   private final SmartCardPromptDialog this$0;

   SmartCardPromptDialog$1(SmartCardPromptDialog _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      if (this.this$0._dialogOpen) {
         this.this$0.close(0, false);
      }
   }
}
