package net.rim.ecmascript.runtime;

class ESStringPrototype$10 extends HostFunction {
   private final ESStringPrototype this$0;

   ESStringPrototype$10(ESStringPrototype _1, int x0, String x1, String x2, int x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      String str = ESStringPrototype.toString(this.getThis());
      String searchStr = Convert.toString(this.getParm(0));
      int pos = Convert.toInteger(this.getParm(1, Value.ZERO));
      int len = str.length();
      if (pos < 0) {
         pos = 0;
      }

      if (pos > len) {
         pos = len;
      }

      int searchLen = searchStr.length();
      return searchLen == 0 ? Value.makeIntegerValue(pos) : Value.makeIntegerValue(str.indexOf(searchStr, pos));
   }
}
