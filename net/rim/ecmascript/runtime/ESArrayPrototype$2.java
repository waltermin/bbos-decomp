package net.rim.ecmascript.runtime;

class ESArrayPrototype$2 extends HostFunction {
   private final ESArrayPrototype this$0;

   ESArrayPrototype$2(ESArrayPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      ESObject thiz = this.getThis();
      long length = Convert.toUint32(thiz.getField("length"));
      int nParms = this.getNumParms();
      long lastPushed = Value.UNDEFINED;

      for (int i = 0; i < nParms; i++) {
         lastPushed = this.getParm(i);
         thiz.putIndex(length, lastPushed);
         length += 1;
      }

      long value = Value.makeLongValue(length);
      thiz.putField("length", value);
      return this.getVersion() == 120 ? lastPushed : value;
   }
}
