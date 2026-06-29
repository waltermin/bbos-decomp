package net.rim.device.api.smartcard;

class SmartCardReaderSession$4 extends Function {
   private final SmartCardReaderSession this$0;

   SmartCardReaderSession$4(SmartCardReaderSession _1, Object x0) {
      super(x0);
      this.this$0 = _1;
   }

   @Override
   public void call() {
      this.this$0.negotiateProtocolImpl((SmartCardCapabilities)super._param1);
   }
}
