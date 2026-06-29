package net.rim.ecmascript.runtime;

class ESStringPrototype$14 extends HostFunction {
   private final ESStringPrototype this$0;

   ESStringPrototype$14(ESStringPrototype _1, int x0, String x1, String x2, int x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      String str = ESStringPrototype.toString(this.getThis());
      int strLen = str.length();
      int start = Convert.toInteger(this.getParm(0));
      int end = Convert.toInteger(this.getParm(1, Value.makeIntegerValue(strLen)));
      if (start < 0) {
         start += strLen;
         if (start < 0) {
            start = 0;
         }
      } else if (start > strLen) {
         start = strLen;
      }

      if (end < 0) {
         end += strLen;
         if (end < 0) {
            end = 0;
         }
      } else if (end > strLen) {
         end = strLen;
      }

      int len = end - start;
      if (len < 0) {
         len = 0;
      }

      return len == 0 ? Value.makeStringValue("") : Value.makeStringValue(str.substring(start, start + len));
   }
}
