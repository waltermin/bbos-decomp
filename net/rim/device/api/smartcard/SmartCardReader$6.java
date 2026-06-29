package net.rim.device.api.smartcard;

class SmartCardReader$6 extends Function {
   private final SmartCardReader this$0;

   SmartCardReader$6(SmartCardReader _1) {
      this.this$0 = _1;
   }

   @Override
   public void call() {
      super._objectResult = this.this$0.getTypeImpl();
   }
}
