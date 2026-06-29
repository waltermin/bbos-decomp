package net.rim.device.api.smartcard;

class SmartCardReader$2 extends Function {
   private final SmartCardReader this$0;

   SmartCardReader$2(SmartCardReader _1) {
      this.this$0 = _1;
   }

   @Override
   public void call() {
      super._booleanResult = this.this$0.isReaderPresentImpl();
   }
}
