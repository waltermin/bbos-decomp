package net.rim.ecmascript.runtime;

class ESArrayPrototype$7 extends HostFunction {
   private final ESArrayPrototype this$0;

   ESArrayPrototype$7(ESArrayPrototype _1, String x0, String x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public long run() {
      ESObject thiz = this.getThis();
      long length = Convert.toUint32(thiz.getField("length"));
      ESArray a = new ESArray();
      long start = ESArrayPrototype.bound(Convert.toInteger(this.getParm(0, Value.ZERO)), length);
      long end = ESArrayPrototype.bound(Convert.toInteger(this.getParm(1, Value.makeLongValue(length))), length);
      long n = 0;

      while (start < end) {
         if (thiz.hasIndex(start)) {
            a.putIndex(n, thiz.getIndex(start));
         }

         n += 1;
         start += 1;
      }

      a.putField("length", n);
      return Value.makeObjectValue(a);
   }
}
