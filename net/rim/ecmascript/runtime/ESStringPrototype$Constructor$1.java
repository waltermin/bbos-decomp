package net.rim.ecmascript.runtime;

class ESStringPrototype$Constructor$1 extends HostFunction {
   private final ESStringPrototype$Constructor this$0;

   ESStringPrototype$Constructor$1(ESStringPrototype$Constructor _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      StringBuffer b = (StringBuffer)(new Object());
      int nParms = this.getNumParms();

      for (int i = 0; i < nParms; i++) {
         b.append((char)Convert.toUint16(this.getParm(i)));
      }

      return Value.makeStringValue(b.toString());
   }
}
