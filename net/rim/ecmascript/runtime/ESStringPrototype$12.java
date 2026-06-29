package net.rim.ecmascript.runtime;

class ESStringPrototype$12 extends HostFunction {
   private final ESStringPrototype this$0;

   ESStringPrototype$12(ESStringPrototype _1, int x0, String x1, String x2, int x3) {
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
         start = 0;
      }

      if (end < 0) {
         end = 0;
      }

      if (start > strLen) {
         start = strLen;
      }

      if (end > strLen) {
         end = strLen;
      }

      if (start > end) {
         if (this.getVersion() == 120) {
            end = start;
         } else {
            int tmp = start;
            start = end;
            end = tmp;
         }
      }

      return Value.makeStringValue(str.substring(start, end));
   }
}
