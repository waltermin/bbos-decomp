package net.rim.device.api.smartcard;

class SmartCardReaderSession$3 extends Function {
   private final SmartCardReaderSession this$0;

   SmartCardReaderSession$3(SmartCardReaderSession _1, Object x0, Object x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   public void call() {
      this.this$0.sendAPDUsImpl((CommandAPDUGroup)super._param1, (ResponseAPDUGroup)super._param2);
   }
}
