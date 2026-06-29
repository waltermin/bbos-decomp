package net.rim.ecmascript.runtime;

class ESStringPrototype$11 extends HostFunction {
   private final ESStringPrototype this$0;

   ESStringPrototype$11(ESStringPrototype _1, int x0, String x1, String x2, int x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      String str = ESStringPrototype.toString(this.getThis());
      String searchStr = Convert.toString(this.getParm(0));
      long value = Convert.toNumber(this.getParm(1));
      int pos = Integer.MAX_VALUE;
      if (value != Value.NaN) {
         pos = Convert.toInteger(value);
      }

      int len = str.length();
      if (pos < 0) {
         pos = 0;
      }

      if (pos > len) {
         pos = len;
      }

      int searchLen = searchStr.length();
      if (searchLen == 0) {
         return Value.makeIntegerValue(pos);
      }

      if (pos + searchLen > len) {
         pos = len - searchLen;
         if (pos < 0) {
            return Value.MINUS_ONE;
         }
      }

      label44:
      for (; pos >= 0; pos--) {
         for (int i = 0; i < searchLen; i++) {
            if (str.charAt(pos + i) != searchStr.charAt(i)) {
               continue label44;
            }
         }

         return Value.makeIntegerValue(pos);
      }

      return Value.MINUS_ONE;
   }
}
