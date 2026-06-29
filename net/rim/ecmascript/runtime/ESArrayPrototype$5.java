package net.rim.ecmascript.runtime;

class ESArrayPrototype$5 extends HostFunction {
   private final ESArrayPrototype this$0;

   ESArrayPrototype$5(ESArrayPrototype _1, String x0, String x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      ESObject thiz = this.getThis();
      long length = Convert.toUint32(thiz.getField("length"));
      if (length == 0) {
         thiz.putField("length", Value.ZERO);
         return Value.UNDEFINED;
      }

      long firstElement = thiz.getIndex(0);

      for (int k = 1; k < length; k++) {
         if (thiz.hasIndex(k)) {
            thiz.putIndex(k - 1, thiz.getIndex(k));
         } else {
            thiz.deleteIndex(k - 1);
         }
      }

      length -= 1;
      thiz.deleteIndex(length);
      thiz.putField("length", length);
      return firstElement;
   }
}
