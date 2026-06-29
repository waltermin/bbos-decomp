package net.rim.device.api.smartcard;

class SmartCardReaderSession$2 extends Function {
   private final SmartCardReaderSession this$0;

   SmartCardReaderSession$2(SmartCardReaderSession _1, Object x0, Object x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   public void call() {
      this.this$0.sendAPDUImpl((CommandAPDU)super._param1, (ResponseAPDU)super._param2);
   }
}
