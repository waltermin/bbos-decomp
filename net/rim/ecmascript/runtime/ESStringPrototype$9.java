package net.rim.ecmascript.runtime;

class ESStringPrototype$9 extends HostFunction {
   private final ESStringPrototype this$0;

   ESStringPrototype$9(ESStringPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      StringBuffer b = (StringBuffer)(new Object());
      b.append(ESStringPrototype.toString(this.getThis()));
      int nParms = this.getNumParms();

      for (int i = 0; i < nParms; i++) {
         b.append(Convert.toString(this.getParm(i)));
      }

      return Value.makeStringValue(b.toString());
   }
}
