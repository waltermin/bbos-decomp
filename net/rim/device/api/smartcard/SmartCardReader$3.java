package net.rim.device.api.smartcard;

class SmartCardReader$3 extends Function {
   private final SmartCardReader this$0;

   SmartCardReader$3(SmartCardReader _1) {
      this.this$0 = _1;
   }

   @Override
   public void call() {
      super._booleanResult = this.this$0.isSmartCardPresentImpl();
   }
}
