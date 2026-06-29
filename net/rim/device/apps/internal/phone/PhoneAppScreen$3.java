package net.rim.device.apps.internal.phone;

final class PhoneAppScreen$3 implements Runnable {
   private final PhoneAppScreen this$0;

   PhoneAppScreen$3(PhoneAppScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._phoneNumberInput.clear(false);
   }
}
