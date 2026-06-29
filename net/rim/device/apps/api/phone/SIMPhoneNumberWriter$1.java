package net.rim.device.apps.api.phone;

class SIMPhoneNumberWriter$1 implements Runnable {
   private final SIMPhoneNumberWriter this$0;

   SIMPhoneNumberWriter$1(SIMPhoneNumberWriter _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0._timeoutExpired = true;
      this.this$0.close();
   }
}
