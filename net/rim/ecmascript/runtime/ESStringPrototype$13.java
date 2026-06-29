package net.rim.ecmascript.runtime;

class ESStringPrototype$13 extends HostFunction {
   private final ESStringPrototype this$0;

   ESStringPrototype$13(ESStringPrototype _1, int x0, String x1, String x2, int x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      String str = ESStringPrototype.toString(this.getThis());
      int strLen = str.length();
      int start = Convert.toInteger(this.getParm(0));
      int length = Convert.toInteger(this.getParm(1, Value.makeIntegerValue(strLen)));
      if (start < 0) {
         start += strLen;
      }

      if (start < 0) {
         start = 0;
      }

      if (length < 0) {
         length = 0;
      }

      if (length > strLen - start) {
         length = strLen - start;
      }

      if (length <= 0) {
         str = "";
      } else {
         str = str.substring(start, start + length);
      }

      return Value.makeStringValue(str);
   }
}
