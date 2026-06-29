package net.rim.device.api.smartcard;

class SmartCardReaderSession$5 extends Function {
   private final SmartCardReaderSession this$0;

   SmartCardReaderSession$5(SmartCardReaderSession _1, int x0) {
      super(x0);
      this.this$0 = _1;
   }

   @Override
   public void call() {
      this.this$0.setProtocolImpl(super._paramInt);
   }
}
