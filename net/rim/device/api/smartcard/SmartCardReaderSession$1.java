package net.rim.device.api.smartcard;

class SmartCardReaderSession$1 extends Function {
   private final SmartCardReaderSession this$0;

   SmartCardReaderSession$1(SmartCardReaderSession _1) {
      this.this$0 = _1;
   }

   @Override
   public void call() {
      this.this$0.closeImpl();
   }
}
