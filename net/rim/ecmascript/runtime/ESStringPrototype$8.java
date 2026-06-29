package net.rim.ecmascript.runtime;

class ESStringPrototype$8 extends HostFunction {
   private final ESStringPrototype this$0;

   ESStringPrototype$8(ESStringPrototype _1, int x0, String x1, String x2, int x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      String str = ESStringPrototype.toString(this.getThis());
      int pos = Convert.toInteger(this.getParm(0));

      try {
         return Value.makeIntegerValue(str.charAt(pos));
      } finally {
         ;
      }
   }
}
