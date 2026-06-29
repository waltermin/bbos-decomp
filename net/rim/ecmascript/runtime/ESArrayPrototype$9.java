package net.rim.ecmascript.runtime;

class ESArrayPrototype$9 extends HostFunction {
   private final ESArrayPrototype this$0;

   ESArrayPrototype$9(ESArrayPrototype _1, String x0, String x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      ESObject thiz = this.getThis();
      long length = Convert.toUint32(thiz.getField("length"));
      int nParms = this.getNumParms();
      long k = length - 1;

      for (long l = k + nParms; k >= 0; l -= 1) {
         if (thiz.hasIndex(k)) {
            thiz.putIndex(l, thiz.getIndex(k));
         } else {
            thiz.deleteIndex(l);
         }

         k -= 1;
      }

      for (int i = 0; i < nParms; i++) {
         thiz.putIndex(i, this.getParm(i));
      }

      length += nParms;
      thiz.putField("length", length);
      return Value.makeLongValue(length);
   }
}
