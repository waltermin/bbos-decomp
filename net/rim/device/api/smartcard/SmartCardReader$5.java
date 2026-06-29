package net.rim.device.api.smartcard;

class SmartCardReader$5 extends Function {
   private final SmartCardReader this$0;

   SmartCardReader$5(SmartCardReader _1) {
      this.this$0 = _1;
   }

   @Override
   public void call() {
      super._objectResult = this.this$0.getLabelImpl();
   }
}
