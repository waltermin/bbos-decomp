package net.rim.device.api.smartcard;

class SmartCardReaderSession$6 extends Function {
   private final SmartCardReaderSession this$0;

   SmartCardReaderSession$6(SmartCardReaderSession _1) {
      this.this$0 = _1;
   }

   @Override
   public void call() {
      super._objectResult = this.this$0.getAnswerToResetImpl();
   }
}
