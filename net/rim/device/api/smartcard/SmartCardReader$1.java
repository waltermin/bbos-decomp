package net.rim.device.api.smartcard;

class SmartCardReader$1 extends Function {
   private final SmartCardReader this$0;

   SmartCardReader$1(SmartCardReader _1) {
      this.this$0 = _1;
   }

   @Override
   public void call() {
      super._objectResult = this.this$0.openSessionImpl();
   }
}
